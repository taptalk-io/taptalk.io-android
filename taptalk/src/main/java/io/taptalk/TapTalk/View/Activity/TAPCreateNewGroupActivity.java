package io.taptalk.TapTalk.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkContactListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPContactInitialAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERSIDs;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SHORT_ANIMATION_TIME;

public class TAPCreateNewGroupActivity extends TAPBaseActivity {

    private ConstraintLayout clActionBar;
    private LinearLayout llGroupMembers;
    private ImageView ivButtonBack, ivButtonSearch, ivButtonClearText;
    private TextView tvTitle, tvMemberCount;
    private EditText etSearch;
    private Button btnContinue;
    private RecyclerView rvContactList, rvGroupMembers;

    private TAPContactInitialAdapter contactListAdapter;
    private TAPContactListAdapter selectedMembersAdapter;
    private TapTalkContactListInterface listener;
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
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
        }
    }

    private void initViewModel() {
        TAPUserModel myUser = TAPChatManager.getInstance().getActiveUser();
        vm = ViewModelProviders.of(this).get(TAPContactListViewModel.class);

        // Add self as selected group member
        vm.addSelectedContact(myUser);

        // Show users from contact list
        vm.getContactListLive().observe(this, userModels -> {
            vm.getContactList().clear();
            vm.getContactList().addAll(userModels);
            vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getContactList()));
            runOnUiThread(() -> contactListAdapter.setItems(vm.getSeparatedContacts()));
        });
        vm.getFilteredContacts().addAll(vm.getContactList());
    }

    private void initListener() {
        listener = new TapTalkContactListInterface() {
            @Override
            public boolean onContactSelected(TAPUserModel contact) {
                TAPUtils.getInstance().dismissKeyboard(TAPCreateNewGroupActivity.this);
                new Handler().post(waitAnimationsToFinishRunnable);
                if (!vm.getSelectedContacts().contains(contact)) {
                    if (vm.getSelectedContacts().size() >= TAPGroupManager.Companion.getGetInstance().getGroupMaxParticipants()) {
                        // TODO: 20 September 2018 CHANGE DIALOG LISTENER
                        new TapTalkDialog.Builder(TAPCreateNewGroupActivity.this)
                                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                .setTitle(getString(R.string.tap_cannot_add_more_people))
                                .setMessage(getString(R.string.tap_group_limit_reached))
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .setPrimaryButtonListener(v -> {

                                })
                                .show();
                        return false;
                    }
                    vm.addSelectedContact(contact);
                    selectedMembersAdapter.notifyItemInserted(vm.getSelectedContacts().size());
                    rvGroupMembers.scrollToPosition(vm.getSelectedContacts().size() - 1);
                    updateSelectedMemberDecoration();
                } else {
                    int index = vm.getSelectedContacts().indexOf(contact);
                    vm.removeSelectedContact(contact);
                    selectedMembersAdapter.notifyItemRemoved(index);
                }
                if (vm.getSelectedContacts().size() > 1) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.tap_selected_member_count), vm.getSelectedContacts().size(), TAPGroupManager.Companion.getGetInstance().getGroupMaxParticipants()));
                return true;
            }

            @Override
            public void onContactDeselected(TAPUserModel contact) {
                TAPUtils.getInstance().dismissKeyboard(TAPCreateNewGroupActivity.this);
                selectedMembersAdapter.removeItem(contact);
                new Handler().post(waitAnimationsToFinishRunnable);
                contactListAdapter.notifyDataSetChanged();
                if (vm.getSelectedContacts().size() > 1) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.tap_selected_member_count), vm.getSelectedContacts().size(), TAPGroupManager.Companion.getGetInstance().getGroupMaxParticipants()));
            }
        };
    }

    private void initView() {
        clActionBar = findViewById(R.id.cl_action_bar);
        llGroupMembers = findViewById(R.id.ll_group_members);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        ivButtonClearText = findViewById(R.id.iv_button_clear_text);
        tvTitle = findViewById(R.id.tv_title);
        tvMemberCount = findViewById(R.id.tv_member_count);
        etSearch = findViewById(R.id.et_search);
        btnContinue = findViewById(R.id.btn_continue);
        rvContactList = findViewById(R.id.rv_contact_list);
        rvGroupMembers = findViewById(R.id.rv_group_members);

        getWindow().setBackgroundDrawable(null);
        vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));

        // All contacts adapter
        contactListAdapter = new TAPContactInitialAdapter(TAPContactListAdapter.SELECT, vm.getSeparatedContacts(), vm.getSelectedContacts(), listener);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        // Selected members adapter
        selectedMembersAdapter = new TAPContactListAdapter(TAPContactListAdapter.SELECTED_MEMBER, vm.getSelectedContacts(), listener);
        rvGroupMembers.setAdapter(selectedMembersAdapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        new Handler().post(waitAnimationsToFinishRunnable);

        etSearch.addTextChangedListener(searchTextWatcher);
        etSearch.setOnEditorActionListener(searchEditorListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> showSearchBar());
        ivButtonClearText.setOnClickListener(v -> etSearch.setText(""));
        btnContinue.setOnClickListener(v -> openGroupSubjectActivity());

        rvContactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.getInstance().dismissKeyboard(TAPCreateNewGroupActivity.this);
            }
        });
    }

    private void showToolbar() {
        vm.setSelecting(false);
        TAPUtils.getInstance().dismissKeyboard(this);
        tvTitle.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.GONE);
        etSearch.setText("");
        ivButtonSearch.setVisibility(View.VISIBLE);
        ((TransitionDrawable) clActionBar.getBackground()).reverseTransition(SHORT_ANIMATION_TIME);
    }

    private void showSearchBar() {
        vm.setSelecting(true);
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
        intent.putStringArrayListExtra(GROUP_MEMBERSIDs, new ArrayList<>(vm.getSelectedContactsIds()));
        if (null != vm.getGroupName()) intent.putExtra(GROUP_NAME, vm.getGroupName());
        if (null != vm.getGroupImage()) intent.putExtra(GROUP_IMAGE, vm.getGroupImage());
        startActivityForResult(intent, CREATE_GROUP);
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
            vm.getFilteredContacts().addAll(vm.getContactList());
            vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));
            contactListAdapter.clearSectionTitles();
        } else {
            List<TAPUserModel> filteredContacts = new ArrayList<>();
            for (TAPUserModel user : vm.getContactList()) {
                if (user.getName().toLowerCase().contains(searchKeyword)) {
                    filteredContacts.add(user);
                }
            }
            vm.getFilteredContacts().addAll(filteredContacts);
            vm.getSeparatedContacts().add(vm.getFilteredContacts());
            contactListAdapter.setSectionTitles(generateSearchSectionTitles());
            // TODO: 6 September 2019 search username by API
        }
        //vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));
        contactListAdapter.setItems(vm.getSeparatedContacts());
    }

    private List<String> generateSearchSectionTitles() {
        List<String> sectionTitles = new ArrayList<>();
        sectionTitles.add(getString(R.string.tap_contacts));
        sectionTitles.add(getString(R.string.tap_global_search));
        return sectionTitles;
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            if (s.length() == 0) {
                ivButtonClearText.setVisibility(View.GONE);
            } else {
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
            TAPUtils.getInstance().dismissKeyboard(TAPCreateNewGroupActivity.this);
            return true;
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
