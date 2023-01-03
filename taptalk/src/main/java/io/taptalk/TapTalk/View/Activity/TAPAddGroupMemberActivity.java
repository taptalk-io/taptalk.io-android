package io.taptalk.TapTalk.View.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapContactListListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TapContactListAdapter;
import io.taptalk.TapTalk.View.Adapter.TapSelectedGroupMemberAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_ACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_ADD_MEMBER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SHORT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_SELECTABLE_CONTACT_LIST;

public class TAPAddGroupMemberActivity extends TAPBaseActivity {

    private ConstraintLayout clActionBar;
    private FrameLayout flButtonContinue;
    private LinearLayout llGroupMembers, llEmptyContact;
    private ImageView ivButtonBack, ivButtonSearch, ivButtonClearText, ivGlobalSearchLoading, ivButtonLoading;
    private TextView tvTitle, tvMemberCount, tvButtonText, tvInfoEmptyContact, tvButtonEmptyContact;
    private EditText etSearch;
    private RecyclerView rvContactList, rvGroupMembers;

    private TapContactListAdapter contactListAdapter;
    private TapSelectedGroupMemberAdapter selectedMembersAdapter;
    private TapContactListListener listener;
    private TAPContactListViewModel vm;

    // Create new group
    public static void start(
            Context context,
            String instanceKey
    ) {
        Intent intent = new Intent(context, TAPAddGroupMemberActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(GROUP_ACTION, CREATE_GROUP);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }
    }

    // Add group member
    public static void start(
            Activity context,
            String instanceKey,
            String roomID,
            ArrayList<TAPUserModel> groupMembers
    ) {
        Intent intent = new Intent(context, TAPAddGroupMemberActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(GROUP_ACTION, GROUP_ADD_MEMBER);
        intent.putExtra(ROOM_ID, roomID);
        intent.putParcelableArrayListExtra(GROUP_MEMBERS, groupMembers);
        context.startActivityForResult(intent, GROUP_ADD_MEMBER);
        context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_add_group_member);

        initViewModel();
        initListener();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFilteredContacts(etSearch.getText().toString().toLowerCase().trim());
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
                        finish();
                        break;
                }
            case RESULT_CANCELED:
                switch (requestCode) {
                    case CREATE_GROUP:
                        if (null != data) {
                            vm.setGroupName(data.getStringExtra(GROUP_NAME));
                            vm.setGroupImage(data.getParcelableExtra(GROUP_IMAGE));
                            vm.setGroupImageUri(data.getParcelableExtra(URI));
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
        TAPUserModel myUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
        vm = new ViewModelProvider(this,
                new TAPContactListViewModel.TAPContactListViewModelFactory(
                        getApplication(), instanceKey))
                .get(TAPContactListViewModel.class);

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
        TAPDataManager.getInstance(instanceKey).getMyContactList(new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(List<TAPUserModel> entities) {
                vm.setContactList(entities);
                vm.getContactList().removeAll(vm.getExistingMembers());
                vm.getFilteredContacts().addAll(vm.getContactList());
                vm.setSeparatedContactList(TAPUtils.generateContactListForRecycler(vm.getContactList(), TYPE_SELECTABLE_CONTACT_LIST, vm.getContactListPointer()));
                updateFilteredContacts(etSearch.getText().toString().toLowerCase().trim());

                // Put non-contact users from database to pointer
                TAPDataManager.getInstance(instanceKey).getNonContactUsersFromDatabase(new TAPDatabaseListener<TAPUserModel>() {
                    @Override
                    public void onSelectFinished(List<TAPUserModel> entities) {
                        for (TAPUserModel user : entities) {
                            ArrayList<String> blockedUserIDs = TAPDataManager.getInstance(instanceKey).getBlockedUserIds();
                            if (null != user.getUserID() && !user.getUserID().isEmpty() &&
                                null != user.getFullname() && !user.getFullname().isEmpty() &&
                                !blockedUserIDs.contains(user.getUserID())
                            ) {
                                TapContactListModel filteredContact = new TapContactListModel(user, TYPE_SELECTABLE_CONTACT_LIST);
                                vm.getContactListPointer().put(user.getUserID(), filteredContact);
                            }
                        }
                    }
                });
            }
        });

        vm.setRoomID(null == getIntent().getStringExtra(ROOM_ID) ? "" : getIntent().getStringExtra(ROOM_ID));
    }

    private void initListener() {
        listener = new TapContactListListener() {
            @Override
            public boolean onContactSelected(TapContactListModel contact) {
                TAPUtils.dismissKeyboard(TAPAddGroupMemberActivity.this);
                new Handler().post(waitAnimationsToFinishRunnable);
                if (!vm.getSelectedContactList().contains(contact)) {
                    if (vm.getSelectedContactList().size() + vm.getInitialGroupSize() > TAPGroupManager.Companion.getInstance(instanceKey).getGroupMaxParticipants()) {
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
                    selectedMembersAdapter.notifyItemInserted(vm.getSelectedContactList().size());
                    rvGroupMembers.scrollToPosition(vm.getSelectedContactList().size() - 1);
                    updateSelectedMemberDecoration();
                } else {
                    // Remove selected member
                    int index = vm.getSelectedContactList().indexOf(contact);
                    vm.removeSelectedContact(contact);
                    selectedMembersAdapter.notifyItemRemoved(index);
                }
                if ((vm.getGroupAction() == CREATE_GROUP && vm.getSelectedContactList().size() > 1) ||
                        (vm.getGroupAction() == GROUP_ADD_MEMBER && vm.getSelectedContactList().size() > 0)) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.tap_format_dd_selected_member_count), vm.getInitialGroupSize() + vm.getSelectedContactList().size(), TAPGroupManager.Companion.getInstance(instanceKey).getGroupMaxParticipants()));
                return true;
            }

            @Override
            public void onContactDeselected(TapContactListModel contact) {
                TAPUtils.dismissKeyboard(TAPAddGroupMemberActivity.this);
                int index = vm.getSelectedContactList().indexOf(contact);
                vm.removeSelectedContact(contact);
                selectedMembersAdapter.notifyItemRemoved(index);
                new Handler().post(waitAnimationsToFinishRunnable);
                contactListAdapter.notifyDataSetChanged();
                if ((vm.getGroupAction() == CREATE_GROUP && vm.getSelectedContactList().size() > 1) ||
                        (vm.getGroupAction() == GROUP_ADD_MEMBER && vm.getSelectedContactList().size() > 0)) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.tap_format_dd_selected_member_count), vm.getInitialGroupSize() + vm.getSelectedContactList().size(), TAPGroupManager.Companion.getInstance(instanceKey).getGroupMaxParticipants()));
            }
        };

        // Add socket listener for pending global search
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener);
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
        contactListAdapter = new TapContactListAdapter(instanceKey, vm.getAdapterItems(), listener);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        // Selected members adapter
        selectedMembersAdapter = new TapSelectedGroupMemberAdapter(instanceKey, vm.getSelectedContactList(), listener);
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
            ivButtonBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white));
            ImageViewCompat.setImageTintList(ivButtonBack, ColorStateList.valueOf(ContextCompat.
                    getColor(TAPAddGroupMemberActivity.this, R.color.tapIconNavigationBarBackButton)));
            tvButtonText.setText(getString(R.string.tap_continue));
            flButtonContinue.setOnClickListener(v -> openGroupSubjectActivity());
        } else if (vm.getGroupAction() == GROUP_ADD_MEMBER) {
            tvTitle.setText(getString(R.string.tap_add_members));
            ivButtonBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_grey));
            ImageViewCompat.setImageTintList(ivButtonBack, ColorStateList.valueOf(ContextCompat.
                    getColor(TAPAddGroupMemberActivity.this, R.color.tapIconNavBarCloseButton)));
            tvButtonText.setText(getString(R.string.tap_add_members));
            flButtonContinue.setOnClickListener(v -> startAddMemberProcess());
        }

        rvContactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.dismissKeyboard(TAPAddGroupMemberActivity.this);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            flButtonContinue.setBackground(getDrawable(R.drawable.tap_bg_button_active_ripple));
        }
    }

    private void showToolbar() {
        vm.setSelecting(false);
        TAPUtils.dismissKeyboard(this);
        if (vm.getGroupAction() == GROUP_ADD_MEMBER) {
            ivButtonBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_grey));
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
            ivButtonBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white));
        }
        tvTitle.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        ivButtonSearch.setVisibility(View.GONE);
        TAPUtils.showKeyboard(this, etSearch);
        ((TransitionDrawable) clActionBar.getBackground()).startTransition(SHORT_ANIMATION_TIME);
    }

    private void openGroupSubjectActivity() {
        TAPEditGroupSubjectActivity.start(
                this,
                instanceKey,
                new ArrayList<>(vm.getSelectedContacts()),
                new ArrayList<>(vm.getSelectedContactsIds()),
                vm.getGroupName(),
                vm.getGroupImage(),
                vm.getGroupImageUri());
    }

    private void startAddMemberProcess() {
        if (!"".equals(vm.getRoomID())) {
            TAPDataManager.getInstance(instanceKey).addRoomParticipant(vm.getRoomID(), vm.getSelectedContactsIds(), addMemberView);
        }
    }

    private void updateSelectedMemberDecoration() {
        if (rvGroupMembers.getItemDecorationCount() > 0) {
            rvGroupMembers.removeItemDecorationAt(0);
        }
        rvGroupMembers.addItemDecoration(new TAPHorizontalDecoration(0, 0,
                0, TAPUtils.dpToPx(16), selectedMembersAdapter.getItemCount(),
                0, 0));
    }

    private void updateFilteredContacts(String searchKeyword) {
        vm.getAdapterItems().clear();
        vm.getFilteredContacts().clear();
        vm.getContactSearchResult().clear();
        vm.getNonContactSearchResult().clear();
        vm.getContactListSearchResult().clear();
        vm.getNonContactListSearchResult().clear();
        vm.setContactQueryFinished(false);
        vm.setNonContactQueryFinished(false);

        if (searchKeyword.isEmpty()) {
            // Show all contacts
            vm.getFilteredContacts().addAll(vm.getContactList());
            vm.getAdapterItems().addAll(vm.getSeparatedContactList());
            vm.setNeedToCallGetUserApi(false);
            if (vm.getAdapterItems().isEmpty()) {
                tvInfoEmptyContact.setText(getString(R.string.tap_contact_list_empty));
                if (!TapUI.getInstance(instanceKey).isAddContactDisabled() &&
                        TapUI.getInstance(instanceKey).isNewContactMenuButtonVisible()) {
                    tvButtonEmptyContact.setText(getString(R.string.tap_add_new_contact));
                    tvButtonEmptyContact.setVisibility(View.VISIBLE);
                    llEmptyContact.setOnClickListener(v -> openNewContactActivity());
                } else {
                    tvButtonEmptyContact.setVisibility(View.GONE);
                }
                llEmptyContact.setVisibility(View.VISIBLE);
                rvContactList.setVisibility(View.GONE);
            } else {
                llEmptyContact.setVisibility(View.GONE);
                rvContactList.setVisibility(View.VISIBLE);
            }
            contactListAdapter.setItems(vm.getAdapterItems());
        } else {
            // Show filtered contacts
            vm.setNeedToCallGetUserApi(true);

            ArrayList<String> blockedUserIDs = TAPDataManager.getInstance(instanceKey).getBlockedUserIds();

            // Search matching contacts from database
            TAPDataManager.getInstance(instanceKey).searchContactsByNameAndUsername(searchKeyword, new TAPDatabaseListener<TAPUserModel>() {
                @Override
                public void onSelectFinished(List<TAPUserModel> entities) {
                    if (null != entities && !entities.isEmpty()) {
                        for (TAPUserModel contact : entities) {
                            if (vm.getContactListPointer().containsKey(contact.getUserID()) &&
                                !vm.getExistingMembers().contains(contact) &&
                                !blockedUserIDs.contains(contact.getUserID())
                            ) {
                                vm.getContactSearchResult().add(contact);
                                vm.getContactListSearchResult().add(vm.getContactListPointer().get(contact.getUserID()));
                                if (searchKeyword.equalsIgnoreCase(contact.getUsername())) {
                                    vm.setNeedToCallGetUserApi(false);
                                }
                            }
                        }
                    }
                    vm.setContactQueryFinished(true);
                    if (vm.isNonContactQueryFinished()) {
                        showFilteredContactFromDatabaseAndCallApi();
                    }
                }
            });

            // Search matching non-contact users from database
            TAPDataManager.getInstance(instanceKey).searchNonContactUsersFromDatabase(searchKeyword, new TAPDatabaseListener<TAPUserModel>() {
                @Override
                public void onSelectFinished(List<TAPUserModel> entities) {
                    if (null != entities && !entities.isEmpty()) {
                        for (TAPUserModel user : entities) {
                            if (vm.getContactListPointer().containsKey(user.getUserID()) &&
                                !vm.getExistingMembers().contains(user) &&
                                !blockedUserIDs.contains(user.getUserID())
                            ) {
                                vm.getNonContactSearchResult().add(user);
                                vm.getNonContactListSearchResult().add(vm.getContactListPointer().get(user.getUserID()));
                                if (searchKeyword.equalsIgnoreCase(user.getUsername())) {
                                    vm.setNeedToCallGetUserApi(false);
                                }
                            }
                        }
                    }
                    vm.setNonContactQueryFinished(true);
                    if (vm.isContactQueryFinished()) {
                        showFilteredContactFromDatabaseAndCallApi();
                    }
                }
            });
        }
    }

    private synchronized void showFilteredContactFromDatabaseAndCallApi() {
        // Update view after both contact and non-contact database query is finished
        if (!vm.getAdapterItems().isEmpty()) {
            return;
        }
        if (!vm.getContactSearchResult().isEmpty()) {
            // Add contact search result to view
            vm.getAdapterItems().add(new TapContactListModel(getString(R.string.tap_contacts)));
            vm.getFilteredContacts().addAll(vm.getContactSearchResult());
            vm.getAdapterItems().addAll(vm.getContactListSearchResult());
        }
        if (!vm.getNonContactSearchResult().isEmpty()) {
            // Add non-contact search result to view
            vm.getAdapterItems().add(new TapContactListModel(getString(R.string.tap_global_search)));
            vm.getFilteredContacts().addAll(vm.getNonContactSearchResult());
            vm.getAdapterItems().addAll(vm.getNonContactListSearchResult());
        }

        if (vm.isNeedToCallGetUserApi()) {
            // Call get user API if no matching username is found in database result
            TAPDataManager.getInstance(instanceKey).getUserByUsernameFromApi(etSearch.getText().toString(), true, getUserView);
        }

        runOnUiThread(() -> {
            llEmptyContact.setVisibility(View.GONE);
            rvContactList.setVisibility(View.VISIBLE);
            contactListAdapter.setItems(vm.getAdapterItems());
        });
    }

    private void openNewContactActivity() {
        TAPNewContactActivity.start(this, instanceKey);
    }

    private void showGlobalSearchLoading() {
        runOnUiThread(() -> {
            ivGlobalSearchLoading.setVisibility(View.VISIBLE);
            TAPUtils.rotateAnimateInfinitely(TAPAddGroupMemberActivity.this, ivGlobalSearchLoading);
        });
    }

    private void hideGlobalSearchLoading() {
        runOnUiThread(() -> {
            ivGlobalSearchLoading.clearAnimation();
            ivGlobalSearchLoading.setVisibility(View.GONE);
        });
    }

    private void showButtonLoading() {
        runOnUiThread(() -> {
            tvButtonText.setVisibility(View.GONE);
            ivButtonLoading.setVisibility(View.VISIBLE);
            TAPUtils.rotateAnimateInfinitely(this, ivButtonLoading);
        });
    }

    private void hideButtonLoading() {
        runOnUiThread(() -> {
            ivButtonLoading.clearAnimation();
            ivButtonLoading.setVisibility(View.GONE);
            tvButtonText.setVisibility(View.VISIBLE);
        });
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            TAPDataManager.getInstance(instanceKey).cancelUserSearchApiCall();
            hideGlobalSearchLoading();
            searchTimer.cancel();
            if (s.length() == 0) {
                vm.setPendingSearch("");
                ivButtonClearText.setVisibility(View.GONE);
                updateFilteredContacts(etSearch.getText().toString().toLowerCase().trim());
            } else {
                searchTimer.start();
                ivButtonClearText.setVisibility(View.VISIBLE);
            }
            etSearch.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextView.OnEditorActionListener searchEditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            updateFilteredContacts(etSearch.getText().toString().toLowerCase().trim());
            TAPDataManager.getInstance(instanceKey).cancelUserSearchApiCall();
            TAPDataManager.getInstance(instanceKey).getUserByUsernameFromApi(etSearch.getText().toString(), true, getUserView);
            TAPUtils.dismissKeyboard(TAPAddGroupMemberActivity.this);
            return true;
        }
    };

    private CountDownTimer searchTimer = new CountDownTimer(300L, 100L) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            updateFilteredContacts(etSearch.getText().toString().toLowerCase().trim());
        }
    };

    TAPDefaultDataView<TAPGetUserResponse> getUserView = new TAPDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void startLoading() {
            vm.setNeedToCallGetUserApi(false);
            showGlobalSearchLoading();
        }

        @Override
        public void endLoading() {
            hideGlobalSearchLoading();
        }

        @Override
        public void onSuccess(TAPGetUserResponse response) {
            TAPUserModel userResponse = response.getUser();

            // Save user response to database
            TAPContactManager.getInstance(instanceKey).updateUserData(userResponse);

            if (!vm.getContactListPointer().containsKey(userResponse.getUserID())) {
                // Add user response to pointer
                TapContactListModel contactListResponse = new TapContactListModel(userResponse, TYPE_SELECTABLE_CONTACT_LIST);
                vm.getContactListPointer().put(userResponse.getUserID(), contactListResponse);
            }

            if (!vm.getFilteredContacts().contains(userResponse) && !vm.getExistingMembers().contains(userResponse)) {
                // Add user response to view
                if (vm.getNonContactSearchResult().isEmpty()) {
                    vm.getAdapterItems().add(new TapContactListModel(getString(R.string.tap_global_search)));
                }
                vm.getAdapterItems().add(vm.getContactListPointer().get(userResponse.getUserID()));
                contactListAdapter.setItems(vm.getAdapterItems());
            } else {
                onError(new TAPErrorModel());
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            // User not found
            if (vm.getAdapterItems().isEmpty()) {
                runOnUiThread(() -> {
                    tvInfoEmptyContact.setText(String.format(getString(R.string.tap_format_s_no_result_found_for), etSearch.getText().toString()));
                    tvButtonEmptyContact.setText(getString(R.string.tap_try_different_search));
                    llEmptyContact.setOnClickListener(v -> {
                        TAPUtils.showKeyboard(TAPAddGroupMemberActivity.this, etSearch);
                        etSearch.setSelection(etSearch.getText().length());
                    });
                    llEmptyContact.setVisibility(View.VISIBLE);
                    rvContactList.setVisibility(View.GONE);
                });
            }
            endLoading();
        }

        @Override
        public void onError(String errorMessage) {
            if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TAPAddGroupMemberActivity.this)) {
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
            TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);

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
            TAPDataManager.getInstance(instanceKey).getUserByUsernameFromApi(vm.getPendingSearch(), true, getUserView);
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
