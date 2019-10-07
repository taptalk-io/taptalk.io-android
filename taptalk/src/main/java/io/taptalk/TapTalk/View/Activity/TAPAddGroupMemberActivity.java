package io.taptalk.TapTalk.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapContactListListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPContactInitialAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPContactListAdapterOld;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_ACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBER_IDS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_ADD_MEMBER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SHORT_ANIMATION_TIME;

public class TAPAddGroupMemberActivity extends TAPBaseActivity {

    private ConstraintLayout clActionBar;
    private FrameLayout flButtonContinue;
    private LinearLayout llGroupMembers, llEmptyContact;
    private ImageView ivButtonBack, ivButtonSearch, ivButtonClearText, ivGlobalSearchLoading, ivButtonLoading;
    private TextView tvTitle, tvMemberCount, tvButtonText, tvInfoEmptyContact, tvButtonEmptyContact;
    private EditText etSearch;
    private RecyclerView rvContactList, rvGroupMembers;

    private TAPContactInitialAdapter contactListAdapter;
    private TAPContactListAdapterOld selectedMembersAdapter;
    private TapContactListListener listener;
    private TAPContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_create_new_group);

        initViewModel();
        initListener();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFilteredContacts(etSearch.getText().toString().toLowerCase());
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchTimer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case CREATE_GROUP:
                        onBackPressed();
                        break;
                }
            case RESULT_CANCELED:
                switch (requestCode) {
                    case CREATE_GROUP:
                        if (null != data) {
                            vm.setGroupName(data.getStringExtra(GROUP_NAME));
                            vm.setGroupImage(data.getParcelableExtra(GROUP_IMAGE));
                        }
                        break;
                }
        }
    }

    @Override
    public void onBackPressed() {
        if (vm.isSelecting()) {
            showToolbar();
        } else {
            super.onBackPressed();
            if (vm.getGroupAction() == CREATE_GROUP) {
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
            } else if (vm.getGroupAction() == GROUP_ADD_MEMBER) {
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
            }
        }
    }

    private void initViewModel() {
        TAPUserModel myUser = TAPChatManager.getInstance().getActiveUser();
        vm = ViewModelProviders.of(this).get(TAPContactListViewModel.class);

        vm.setGroupAction(getIntent().getIntExtra(GROUP_ACTION, CREATE_GROUP));

        if (vm.getGroupAction() == CREATE_GROUP) {
            // Add self as selected group member
            vm.addSelectedContact(myUser);
        }

        // Get existing members from group
        if (null != getIntent().getParcelableArrayListExtra(GROUP_MEMBERS)) {
            vm.setExistingMembers(getIntent().getParcelableArrayListExtra(GROUP_MEMBERS));
            vm.setInitialGroupSize(vm.getExistingMembers().size());
        }

        // Show users from contact list
        vm.getContactListLive().observe(this, userModels -> {
            boolean updateOnRefresh = false;
            if (vm.getContactList().isEmpty()) {
                updateOnRefresh = true;
            }
            if (userModels != null) {
                vm.getContactList().clear();
                vm.getContactList().addAll(userModels);
                vm.getContactList().removeAll(vm.getExistingMembers());
            }
            if (updateOnRefresh) {
                updateFilteredContacts(etSearch.getText().toString());
            }
//            vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getContactList()));
//            runOnUiThread(() -> contactListAdapter.setItems(vm.getSeparatedContacts()));
        });
        vm.getFilteredContacts().addAll(vm.getContactList());
        vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));

        vm.setRoomID(null == getIntent().getStringExtra(ROOM_ID) ? "" : getIntent().getStringExtra(ROOM_ID));
    }

    private void initListener() {
        listener = new TapContactListListener() {
            @Override
            public boolean onContactSelected(TAPUserModel contact) {
                TAPUtils.getInstance().dismissKeyboard(TAPAddGroupMemberActivity.this);
                new Handler().post(waitAnimationsToFinishRunnable);
                if (!vm.getSelectedContacts().contains(contact)) {
                    if (vm.getSelectedContacts().size() + vm.getInitialGroupSize() >= TAPGroupManager.Companion.getGetInstance().getGroupMaxParticipants()) {
                        // TODO: 20 September 2018 CHANGE DIALOG LISTENER
                        // Member count exceeds limit
                        new TapTalkDialog.Builder(TAPAddGroupMemberActivity.this)
                                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                .setTitle(getString(R.string.tap_cannot_add_more_people))
                                .setMessage(getString(R.string.tap_group_limit_reached))
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .setPrimaryButtonListener(v -> {

                                })
                                .show();
                        return false;
                    }
                    // Add selected member
                    vm.addSelectedContact(contact);
                    selectedMembersAdapter.notifyItemInserted(vm.getSelectedContacts().size());
                    rvGroupMembers.scrollToPosition(vm.getSelectedContacts().size() - 1);
                    updateSelectedMemberDecoration();
                } else {
                    // Remove selected member
                    int index = vm.getSelectedContacts().indexOf(contact);
                    vm.removeSelectedContact(contact);
                    selectedMembersAdapter.notifyItemRemoved(index);
                }
                if ((vm.getGroupAction() == CREATE_GROUP && vm.getSelectedContacts().size() > 1) ||
                        (vm.getGroupAction() == GROUP_ADD_MEMBER && vm.getSelectedContacts().size() > 0)) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.tap_selected_member_count), vm.getInitialGroupSize() + vm.getSelectedContacts().size(), TAPGroupManager.Companion.getGetInstance().getGroupMaxParticipants()));
                return true;
            }

            @Override
            public void onContactDeselected(TAPUserModel contact) {
                TAPUtils.getInstance().dismissKeyboard(TAPAddGroupMemberActivity.this);
                selectedMembersAdapter.removeItem(contact);
                new Handler().post(waitAnimationsToFinishRunnable);
                contactListAdapter.notifyDataSetChanged();
                if ((vm.getGroupAction() == CREATE_GROUP && vm.getSelectedContacts().size() > 1) ||
                        (vm.getGroupAction() == GROUP_ADD_MEMBER && vm.getSelectedContacts().size() > 0)) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.tap_selected_member_count), vm.getInitialGroupSize() + vm.getSelectedContacts().size(), TAPGroupManager.Companion.getGetInstance().getGroupMaxParticipants()));
            }
        };

        // Add socket listener for pending global search
        TAPConnectionManager.getInstance().addSocketListener(socketListener);
    }

    private void initView() {
        clActionBar = findViewById(R.id.cl_action_bar);
        flButtonContinue = findViewById(R.id.fl_button_continue);
        llGroupMembers = findViewById(R.id.ll_group_members);
        llEmptyContact = findViewById(R.id.ll_empty_contact);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        ivButtonClearText = findViewById(R.id.iv_button_clear_text);
        ivGlobalSearchLoading = findViewById(R.id.iv_global_search_loading);
        ivButtonLoading = findViewById(R.id.iv_button_loading);
        tvTitle = findViewById(R.id.tv_title);
        tvMemberCount = findViewById(R.id.tv_member_count);
        tvButtonText = findViewById(R.id.tv_button_text);
        tvInfoEmptyContact = findViewById(R.id.tv_info_empty_contact);
        tvButtonEmptyContact = findViewById(R.id.tv_button_empty_contact);
        etSearch = findViewById(R.id.et_search);
        rvContactList = findViewById(R.id.rv_contact_list);
        rvGroupMembers = findViewById(R.id.rv_group_members);

        getWindow().setBackgroundDrawable(null);

        // All contacts adapter
        contactListAdapter = new TAPContactInitialAdapter(TAPContactListAdapterOld.SELECT, vm.getSeparatedContacts(), vm.getSelectedContacts(), listener);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        // Selected members adapter
        selectedMembersAdapter = new TAPContactListAdapterOld(TAPContactListAdapterOld.SELECTED_MEMBER, vm.getSelectedContacts(), listener);
        rvGroupMembers.setAdapter(selectedMembersAdapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        new Handler().post(waitAnimationsToFinishRunnable);

        etSearch.addTextChangedListener(searchTextWatcher);
        etSearch.setOnEditorActionListener(searchEditorListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> showSearchBar());
        ivButtonClearText.setOnClickListener(v -> etSearch.setText(""));

        if (vm.getGroupAction() == CREATE_GROUP) {
            tvTitle.setText(getString(R.string.tap_new_group));
            ivButtonBack.setImageResource(R.drawable.tap_ic_chevron_left_white);
            ivButtonBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(TAPAddGroupMemberActivity.this, R.color.tapIconNavBarBackButton)));
            tvButtonText.setText(getString(R.string.tap_continue_s));
            flButtonContinue.setOnClickListener(v -> openGroupSubjectActivity());
        } else if (vm.getGroupAction() == GROUP_ADD_MEMBER) {
            tvTitle.setText(getString(R.string.tap_add_members));
            ivButtonBack.setImageResource(R.drawable.tap_ic_close_grey);
            ivButtonBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(TAPAddGroupMemberActivity.this, R.color.tapIconNavBarCloseButton)));
            tvButtonText.setText(getString(R.string.tap_add_members));
            flButtonContinue.setOnClickListener(v -> startAddMemberProcess());
        }

        rvContactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.getInstance().dismissKeyboard(TAPAddGroupMemberActivity.this);
            }
        });
    }

    private void showToolbar() {
        vm.setSelecting(false);
        TAPUtils.getInstance().dismissKeyboard(this);
        if (vm.getGroupAction() == GROUP_ADD_MEMBER) {
            ivButtonBack.setImageResource(R.drawable.tap_ic_close_grey);
        }
        tvTitle.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.GONE);
        etSearch.setText("");
        ivButtonSearch.setVisibility(View.VISIBLE);
        ((TransitionDrawable) clActionBar.getBackground()).reverseTransition(SHORT_ANIMATION_TIME);
    }

    private void showSearchBar() {
        vm.setSelecting(true);
        if (vm.getGroupAction() == GROUP_ADD_MEMBER) {
            ivButtonBack.setImageResource(R.drawable.tap_ic_chevron_left_white);
        }
        tvTitle.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        ivButtonSearch.setVisibility(View.GONE);
        TAPUtils.getInstance().showKeyboard(this, etSearch);
        ((TransitionDrawable) clActionBar.getBackground()).startTransition(SHORT_ANIMATION_TIME);
    }

    private void openGroupSubjectActivity() {
        Intent intent = new Intent(this, TAPGroupSubjectActivity.class);
        intent.putExtra(MY_ID, vm.getSelectedContacts().get(0).getUserID());
        intent.putParcelableArrayListExtra(GROUP_MEMBERS, new ArrayList<>(vm.getSelectedContacts()));
        intent.putStringArrayListExtra(GROUP_MEMBER_IDS, new ArrayList<>(vm.getSelectedContactsIds()));
        if (null != vm.getGroupName()) {
            intent.putExtra(GROUP_NAME, vm.getGroupName());
        }
        if (null != vm.getGroupImage()) {
            intent.putExtra(GROUP_IMAGE, vm.getGroupImage());
        }
        startActivityForResult(intent, CREATE_GROUP);
    }

    private void startAddMemberProcess() {
        if (!"".equals(vm.getRoomID())) {
            TAPDataManager.getInstance().addRoomParticipant(vm.getRoomID(), vm.getSelectedContactsIds(), addMemberView);
        }
    }

    private void updateSelectedMemberDecoration() {
        if (rvGroupMembers.getItemDecorationCount() > 0) {
            rvGroupMembers.removeItemDecorationAt(0);
        }
        rvGroupMembers.addItemDecoration(new TAPHorizontalDecoration(0, 0,
                0, TAPUtils.getInstance().dpToPx(16), selectedMembersAdapter.getItemCount(),
                0, 0));
    }

    private void updateFilteredContacts(String searchKeyword) {
        vm.getSeparatedContacts().clear();
        vm.getFilteredContacts().clear();
        if (searchKeyword.trim().isEmpty()) {
            // Show all contacts
            vm.getFilteredContacts().addAll(vm.getContactList());
            vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));
            contactListAdapter.clearSectionTitles();
            if (vm.getSeparatedContacts().isEmpty()) {
                tvInfoEmptyContact.setText(getString(R.string.tap_contact_list_empty));
                tvButtonEmptyContact.setText(getString(R.string.tap_add_new_contact));
                llEmptyContact.setOnClickListener(v -> openNewContactActivity());
                llEmptyContact.setVisibility(View.VISIBLE);
                rvContactList.setVisibility(View.GONE);
            } else {
                llEmptyContact.setVisibility(View.GONE);
                rvContactList.setVisibility(View.VISIBLE);
            }
        } else {
            // Show filtered contacts
            List<TAPUserModel> filteredContacts = new ArrayList<>();
            for (TAPUserModel user : vm.getContactList()) {
                if (user.getName().toLowerCase().contains(searchKeyword) || (null != user.getUsername() && user.getUsername().contains(searchKeyword))) {
                    filteredContacts.add(user);
                }
            }
            vm.getFilteredContacts().addAll(filteredContacts);
            if (vm.getFilteredContacts().isEmpty()) {
                contactListAdapter.setSectionTitles(getString(R.string.tap_global_search));
            } else {
                vm.getSeparatedContacts().add(vm.getFilteredContacts());
                contactListAdapter.setSectionTitles(getString(R.string.tap_contacts), getString(R.string.tap_global_search));
            }
            llEmptyContact.setVisibility(View.GONE);
            rvContactList.setVisibility(View.VISIBLE);
        }
        contactListAdapter.setItems(vm.getSeparatedContacts());
    }

    private void openNewContactActivity() {
        Intent intent = new Intent(this, TAPNewContactActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void showGlobalSearchLoading() {
        ivGlobalSearchLoading.setVisibility(View.VISIBLE);
        TAPUtils.getInstance().rotateAnimateInfinitely(TAPAddGroupMemberActivity.this, ivGlobalSearchLoading);
    }

    private void hideGlobalSearchLoading() {
        ivGlobalSearchLoading.clearAnimation();
        ivGlobalSearchLoading.setVisibility(View.GONE);
    }

    private void showButtonLoading() {
        tvButtonText.setVisibility(View.GONE);
        ivButtonLoading.setVisibility(View.VISIBLE);
        TAPUtils.getInstance().rotateAnimateInfinitely(this, ivButtonLoading);
    }

    private void hideButtonLoading() {
        ivButtonLoading.clearAnimation();
        ivButtonLoading.setVisibility(View.GONE);
        tvButtonText.setVisibility(View.VISIBLE);
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            TAPDataManager.getInstance().cancelUserSearchApiCall();
            hideGlobalSearchLoading();
            searchTimer.cancel();
            if (s.length() == 0) {
                vm.setPendingSearch("");
                ivButtonClearText.setVisibility(View.GONE);
            } else {
                searchTimer.start();
                ivButtonClearText.setVisibility(View.VISIBLE);
            }
            updateFilteredContacts(s.toString().toLowerCase());
            etSearch.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextView.OnEditorActionListener searchEditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            updateFilteredContacts(etSearch.getText().toString().toLowerCase());
            TAPDataManager.getInstance().cancelUserSearchApiCall();
            TAPDataManager.getInstance().getUserByUsernameFromApi(etSearch.getText().toString(), true, getUserView);
            TAPUtils.getInstance().dismissKeyboard(TAPAddGroupMemberActivity.this);
            return true;
        }
    };

    private CountDownTimer searchTimer = new CountDownTimer(300L, 100L) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            TAPDataManager.getInstance().getUserByUsernameFromApi(etSearch.getText().toString(), true, getUserView);
        }
    };

    TAPDefaultDataView<TAPGetUserResponse> getUserView = new TAPDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void startLoading() {
            showGlobalSearchLoading();
        }

        @Override
        public void endLoading() {
            hideGlobalSearchLoading();
        }

        @Override
        public void onSuccess(TAPGetUserResponse response) {
            TAPUserModel userResponse = response.getUser();
            TAPContactManager.getInstance().updateUserData(userResponse);
            if (!vm.getFilteredContacts().contains(userResponse) && !vm.getExistingMembers().contains(userResponse)) {
                List<TAPUserModel> globalSearchResult = new ArrayList<>();
                globalSearchResult.add(userResponse);
                vm.getSeparatedContacts().add(globalSearchResult);
                contactListAdapter.setItems(vm.getSeparatedContacts());
            } else {
                onError(new TAPErrorModel());
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            // User not found
            if (vm.getSeparatedContacts().isEmpty()) {
                runOnUiThread(() -> {
                    tvInfoEmptyContact.setText(String.format(getString(R.string.tap_no_result_found_for), etSearch.getText().toString()));
                    tvButtonEmptyContact.setText(getString(R.string.tap_try_different_search));
                    llEmptyContact.setOnClickListener(v -> etSearch.setText(""));
                    llEmptyContact.setVisibility(View.VISIBLE);
                    rvContactList.setVisibility(View.GONE);
                });
            }
            endLoading();
        }

        @Override
        public void onError(String errorMessage) {
            if (!TAPNetworkStateManager.getInstance().hasNetworkConnection(TAPAddGroupMemberActivity.this)) {
                // No internet connection
                vm.setPendingSearch(etSearch.getText().toString());
            }
        }
    };

    private TAPDefaultDataView<TAPCreateRoomResponse> addMemberView = new TAPDefaultDataView<TAPCreateRoomResponse>() {
        @Override
        public void startLoading() {
            showButtonLoading();
        }

        @Override
        public void onSuccess(TAPCreateRoomResponse response) {
            if (null == response.getParticipants()) {
                return;
            }
            TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);

            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(GROUP_MEMBERS, new ArrayList<>(response.getParticipants()));
            setResult(RESULT_OK, intent);
            hideButtonLoading();
            finish();
        }

        @Override
        public void onError(TAPErrorModel error) {
            hideButtonLoading();
            new TapTalkDialog.Builder(TAPAddGroupMemberActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            hideButtonLoading();
            new TapTalkDialog.Builder(TAPAddGroupMemberActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }
    };

    private TAPSocketListener socketListener = new TAPSocketListener() {
        @Override
        public void onSocketConnected() {
            // Resume pending search on connect
            if (null == vm.getPendingSearch() || vm.getPendingSearch().isEmpty()) {
                return;
            }
            TAPDataManager.getInstance().getUserByUsernameFromApi(vm.getPendingSearch(), true, getUserView);
            vm.setPendingSearch("");
        }
    };

    private Runnable waitAnimationsToFinishRunnable = new Runnable() {
        @Override
        public void run() {
            if (rvGroupMembers.isAnimating() && null != rvGroupMembers.getItemAnimator()) {
                // RecyclerView is still animating
                rvGroupMembers.getItemAnimator().isRunning(() -> new Handler().post(waitAnimationsToFinishRunnable));
            } else {
                // RecyclerView has finished animating
                selectedMembersAdapter.setAnimating(false);
                updateSelectedMemberDecoration();
            }
        }
    };
}
