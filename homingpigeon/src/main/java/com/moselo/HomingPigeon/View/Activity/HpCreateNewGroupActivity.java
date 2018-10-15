package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.HomingPigeonDialog;
import com.moselo.HomingPigeon.Helper.HpHorizontalDecoration;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Interface.ContactListInterface;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpContactInitialAdapter;
import com.moselo.HomingPigeon.View.Adapter.HpContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.GROUP_IMAGE;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.GROUP_MEMBERS;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.GROUP_NAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.MY_ID;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.GROUP_MEMBER_LIMIT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.RequestCode.CREATE_GROUP;

public class HpCreateNewGroupActivity extends HpBaseActivity {

    LinearLayout llGroupMembers;
    ImageView ivButtonBack, ivButtonAction;
    TextView tvTitle, tvMemberCount;
    EditText etSearch;
    Button btnContinue;
    RecyclerView rvContactList, rvGroupMembers;

    HpContactInitialAdapter contactListAdapter;
    HpContactListAdapter selectedMembersAdapter;
    ContactListInterface listener;
    HpContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_create_new_group);

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
                        finish();
                        break;
                }
            case RESULT_CANCELED:
                switch (requestCode) {
                    case CREATE_GROUP:
                        if (null != data) {
                            vm.setGroupName(data.getStringExtra(GROUP_NAME));
                            vm.setGroupImage(data.getStringExtra(GROUP_IMAGE));
                        }
                        break;
                }
        }
    }

    private void initViewModel() {
        HpUserModel myUser = HpDataManager.getInstance().getActiveUser();
        vm = ViewModelProviders.of(this).get(HpContactListViewModel.class);
        vm.getSelectedContacts().add(myUser);
    }

    private void initListener() {
        listener = new ContactListInterface() {
            @Override
            public boolean onContactSelected(HpUserModel contact, boolean isSelected) {
                HpUtils.getInstance().dismissKeyboard(HpCreateNewGroupActivity.this);
                new Handler().post(waitAnimationsToFinishRunnable);
                if (isSelected) {
                    if (vm.getSelectedContacts().size() >= GROUP_MEMBER_LIMIT) {
                        // TODO: 20 September 2018 CHANGE DIALOG LISTENER
                        new HomingPigeonDialog.Builder(HpCreateNewGroupActivity.this)
                        .setTitle(getString(R.string.cannot_add_more_people))
                        .setMessage(getString(R.string.group_limit_reached))
                        .setPrimaryButtonTitle("OK")
                        .setPrimaryButtonListener(v -> {

                        })
                        .show();
                        return false;
                    }
                    vm.getSelectedContacts().add(contact);
                    selectedMembersAdapter.notifyItemInserted(vm.getSelectedContacts().size());
                    rvGroupMembers.scrollToPosition(vm.getSelectedContacts().size() - 1);
                    updateSelectedMemberDecoration();
                } else {
                    int index = vm.getSelectedContacts().indexOf(contact);
                    vm.getSelectedContacts().remove(contact);
                    selectedMembersAdapter.notifyItemRemoved(index);
                }
                if (vm.getSelectedContacts().size() > 1) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.group_member_count), vm.getSelectedContacts().size(), GROUP_MEMBER_LIMIT));
                return true;
            }

            @Override
            public void onContactRemoved(HpUserModel contact) {
                HpUtils.getInstance().dismissKeyboard(HpCreateNewGroupActivity.this);
                selectedMembersAdapter.removeItem(contact);
                new Handler().post(waitAnimationsToFinishRunnable);
                if (vm.getFilteredContacts().contains(contact)) {
                    int index = vm.getFilteredContacts().indexOf(contact);
                    vm.getFilteredContacts().get(index).setSelected(false);
                    contactListAdapter.notifyDataSetChanged();
                }
                if (vm.getSelectedContacts().size() > 1) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.group_member_count), vm.getSelectedContacts().size(), GROUP_MEMBER_LIMIT));
            }
        };
    }

    @Override
    protected void initView() {
        //Dummy Contacts
        if (vm.getContactList().size() == 0) {
            HpUserModel u0 = new HpUserModel("u0", "Ababa");
            HpUserModel u1 = new HpUserModel("u1", "Bambang 1");
            HpUserModel u2 = new HpUserModel("u2", "Bambang 2");
            HpUserModel u3 = new HpUserModel("u3", "Bambang 3");
            HpUserModel u4 = new HpUserModel("u4", "Caca");
            HpUserModel u5 = new HpUserModel("u5", "Coco");
            HpUserModel u6 = new HpUserModel("u6", "123asd");
            HpUserModel u7 = new HpUserModel("u7", "!!!11111!!!");
            HpUserModel u8 = new HpUserModel("u8", "!!wkwkwk!!");
            vm.getContactList().add(u0);
            vm.getContactList().add(u1);
            vm.getContactList().add(u2);
            vm.getContactList().add(u3);
            vm.getContactList().add(u4);
            vm.getContactList().add(u5);
            vm.getContactList().add(u6);
            vm.getContactList().add(u7);
            vm.getContactList().add(u8);
            vm.getFilteredContacts().addAll(vm.getContactList());
        }
        //End Dummy

        getWindow().setBackgroundDrawable(null);

        llGroupMembers = findViewById(R.id.ll_group_members);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonAction = findViewById(R.id.iv_button_action);
        tvTitle = findViewById(R.id.tv_title);
        tvMemberCount = findViewById(R.id.tv_member_count);
        tvMemberCount = findViewById(R.id.tv_member_count);
        etSearch = findViewById(R.id.et_search);
        btnContinue = findViewById(R.id.btn_continue);
        rvContactList = findViewById(R.id.rv_contact_list);
        rvGroupMembers = findViewById(R.id.rv_group_members);

        vm.setSeparatedContacts(HpUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));

        contactListAdapter = new HpContactInitialAdapter(HpContactListAdapter.SELECT, vm.getSeparatedContacts(), listener);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        selectedMembersAdapter = new HpContactListAdapter(HpContactListAdapter.SELECTED_MEMBER, vm.getSelectedContacts(), listener, vm.getSelectedContacts().get(0).getUserID());
        rvGroupMembers.setAdapter(selectedMembersAdapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        new Handler().post(waitAnimationsToFinishRunnable);

        etSearch.addTextChangedListener(searchTextWatcher);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            updateFilteredContacts(etSearch.getText().toString().toLowerCase());
            return true;
        });

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        ivButtonAction.setOnClickListener(v -> {
            if (vm.isSelecting()) {
                showToolbar();
            } else {
                showSearchBar();
            }
        });

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, HpGroupSubjectActivity.class);
            intent.putExtra(MY_ID, vm.getSelectedContacts().get(0).getUserID());
            intent.putParcelableArrayListExtra(GROUP_MEMBERS, new ArrayList<>(vm.getSelectedContacts()));
            if (null != vm.getGroupName()) intent.putExtra(GROUP_NAME, vm.getGroupName());
            if (null != vm.getGroupImage()) intent.putExtra(GROUP_IMAGE, vm.getGroupImage());
            startActivityForResult(intent, CREATE_GROUP);
        });
    }

    private void showToolbar() {
        vm.setSelecting(false);
        tvTitle.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.GONE);
        etSearch.setText("");
        etSearch.clearFocus();
        ivButtonAction.setImageResource(R.drawable.hp_ic_search_grey);
        HpUtils.getInstance().dismissKeyboard(this);
    }

    private void showSearchBar() {
        vm.setSelecting(true);
        tvTitle.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        ivButtonAction.setImageResource(R.drawable.hp_ic_close_grey);
        HpUtils.getInstance().showKeyboard(this, etSearch);
    }

    private void updateSelectedMemberDecoration() {
        if (rvGroupMembers.getItemDecorationCount() > 0) {
            rvGroupMembers.removeItemDecorationAt(0);
        }
        rvGroupMembers.addItemDecoration(new HpHorizontalDecoration(0, 0,
                0, HpUtils.getInstance().dpToPx(16), selectedMembersAdapter.getItemCount(),
                0, 0));
    }

    private void updateFilteredContacts(String searchKeyword) {
        vm.getSeparatedContacts().clear();
        vm.getFilteredContacts().clear();
        if (searchKeyword.trim().isEmpty()) {
            vm.getFilteredContacts().addAll(vm.getContactList());
        } else {
            List<HpUserModel> filteredContacts = new ArrayList<>();
            for (HpUserModel user : vm.getContactList()) {
                if (user.getName().toLowerCase().contains(searchKeyword)) {
                    filteredContacts.add(user);
                }
            }
            vm.getFilteredContacts().addAll(filteredContacts);
        }
        vm.setSeparatedContacts(HpUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));
        contactListAdapter.setItems(vm.getSeparatedContacts());
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            updateFilteredContacts(s.toString().toLowerCase());
            etSearch.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private Runnable waitAnimationsToFinishRunnable = new Runnable() {
        @Override
        public void run() {
            if (rvGroupMembers.isAnimating()) {
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
