package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.moselo.HomingPigeon.Helper.TapTalkDialog;
import com.moselo.HomingPigeon.Helper.TAPHorizontalDecoration;
import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Interface.TapTalkContactListInterface;
import com.moselo.HomingPigeon.Manager.TAPDataManager;
import com.moselo.HomingPigeon.Model.TAPImageURL;
import com.moselo.HomingPigeon.Model.TAPUserModel;
import com.moselo.HomingPigeon.Model.TAPUserRoleModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpContactInitialAdapter;
import com.moselo.HomingPigeon.View.Adapter.HpContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.GROUP_IMAGE;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.GROUP_NAME;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.MY_ID;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.GROUP_MEMBER_LIMIT;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;

public class HpCreateNewGroupActivity extends HpBaseActivity {

    private LinearLayout llGroupMembers;
    private ImageView ivButtonBack, ivButtonAction;
    private TextView tvTitle, tvMemberCount;
    private EditText etSearch;
    private Button btnContinue;
    private RecyclerView rvContactList, rvGroupMembers;

    private HpContactInitialAdapter contactListAdapter;
    private HpContactListAdapter selectedMembersAdapter;
    private TapTalkContactListInterface listener;
    private HpContactListViewModel vm;

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
                            vm.setGroupImage(data.getParcelableExtra(GROUP_IMAGE));
                        }
                        break;
                }
        }
    }

    private void initViewModel() {
        TAPUserModel myUser = TAPDataManager.getInstance().getActiveUser();
        vm = ViewModelProviders.of(this).get(HpContactListViewModel.class);
        vm.getSelectedContacts().add(myUser);

        setDummyData();
    }

    private void initListener() {
        listener = new TapTalkContactListInterface() {
            @Override
            public boolean onContactSelected(TAPUserModel contact) {
                TAPUtils.getInstance().dismissKeyboard(HpCreateNewGroupActivity.this);
                new Handler().post(waitAnimationsToFinishRunnable);
                if (!vm.getSelectedContacts().contains(contact)) {
                    if (vm.getSelectedContacts().size() >= GROUP_MEMBER_LIMIT) {
                        // TODO: 20 September 2018 CHANGE DIALOG LISTENER
                        new TapTalkDialog.Builder(HpCreateNewGroupActivity.this)
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
            public void onContactDeselected(TAPUserModel contact) {
                TAPUtils.getInstance().dismissKeyboard(HpCreateNewGroupActivity.this);
                selectedMembersAdapter.removeItem(contact);
                new Handler().post(waitAnimationsToFinishRunnable);
                contactListAdapter.notifyDataSetChanged();
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

        getWindow().setBackgroundDrawable(null);
        vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));

        // All contacts adapter
        contactListAdapter = new HpContactInitialAdapter(HpContactListAdapter.SELECT, vm.getSeparatedContacts(), vm.getSelectedContacts(), listener);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        // Selected members adapter
        selectedMembersAdapter = new HpContactListAdapter(HpContactListAdapter.SELECTED_MEMBER, vm.getSelectedContacts(), listener);
        rvGroupMembers.setAdapter(selectedMembersAdapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        new Handler().post(waitAnimationsToFinishRunnable);

        etSearch.addTextChangedListener(searchTextWatcher);
        etSearch.setOnEditorActionListener(searchEditorListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonAction.setOnClickListener(v -> toggleSearchBar());
        btnContinue.setOnClickListener(v -> openGroupSubjectActivity());
    }

    private void toggleSearchBar() {
        if (vm.isSelecting()) {
            // Show Toolbar
            vm.setSelecting(false);
            tvTitle.setVisibility(View.VISIBLE);
            etSearch.setVisibility(View.GONE);
            etSearch.setText("");
            etSearch.clearFocus();
            ivButtonAction.setImageResource(R.drawable.hp_ic_search_grey);
            TAPUtils.getInstance().dismissKeyboard(this);
        } else {
            // Show Search Bar
            vm.setSelecting(true);
            tvTitle.setVisibility(View.GONE);
            etSearch.setVisibility(View.VISIBLE);
            ivButtonAction.setImageResource(R.drawable.hp_ic_close_grey);
            TAPUtils.getInstance().showKeyboard(this, etSearch);
        }
    }

    private void openGroupSubjectActivity() {
        Intent intent = new Intent(this, HpGroupSubjectActivity.class);
        intent.putExtra(MY_ID, vm.getSelectedContacts().get(0).getUserID());
        intent.putParcelableArrayListExtra(GROUP_MEMBERS, new ArrayList<>(vm.getSelectedContacts()));
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
        } else {
            List<TAPUserModel> filteredContacts = new ArrayList<>();
            for (TAPUserModel user : vm.getContactList()) {
                if (user.getName().toLowerCase().contains(searchKeyword)) {
                    filteredContacts.add(user);
                }
            }
            vm.getFilteredContacts().addAll(filteredContacts);
        }
        vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));
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

    private TextView.OnEditorActionListener searchEditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            updateFilteredContacts(etSearch.getText().toString().toLowerCase());
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

    // TODO: 28/09/18 Harus dihapus setelah fix
    private void setDummyData() {
        if (vm.getContactList().size() > 0) return;

        TAPUserModel userRitchie = TAPUserModel.Builder("1", "1", "Ritchie Nathaniel"
                , TAPImageURL.BuilderDummy(), "ritchie", "ritchie@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538115801488")
                , Long.parseLong("0"));

        TAPUserModel userDominic = TAPUserModel.Builder("2", "2", "Dominic Vedericho"
                , TAPImageURL.BuilderDummy(), "dominic", "dominic@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538115918918")
                , Long.parseLong("0"));

        TAPUserModel userRionaldo = TAPUserModel.Builder("3", "3", "Rionaldo Linggautama"
                , TAPImageURL.BuilderDummy(), "rionaldo", "rionaldo@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116046534")
                , Long.parseLong("0"));

        TAPUserModel userKevin = TAPUserModel.Builder("4", "4", "Kevin Reynaldo"
                , TAPImageURL.BuilderDummy(), "kevin", "kevin@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116099655")
                , Long.parseLong("0"));

        TAPUserModel userWelly = TAPUserModel.Builder("5", "5", "Welly Kencana"
                , TAPImageURL.BuilderDummy(), "welly", "welly@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116147477")
                , Long.parseLong("0"));

        TAPUserModel userJony = TAPUserModel.Builder("6", "6", "Jony Lim"
                , TAPImageURL.BuilderDummy(), "jony", "jony@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116249323")
                , Long.parseLong("0"));

        TAPUserModel userMichael = TAPUserModel.Builder("7", "7", "Michael Tansy"
                , TAPImageURL.BuilderDummy(), "michael", "michael@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116355199")
                , Long.parseLong("0"));

        TAPUserModel userRichard = TAPUserModel.Builder("8", "8", "Richard Fang"
                , TAPImageURL.BuilderDummy(), "richard", "richard@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116398588")
                , Long.parseLong("0"));

        TAPUserModel userErwin = TAPUserModel.Builder("9", "9", "Erwin Andreas"
                , TAPImageURL.BuilderDummy(), "erwin", "erwin@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116452636")
                , Long.parseLong("0"));

        TAPUserModel userJefry = TAPUserModel.Builder("10", "10", "Jefry Lorentono"
                , TAPImageURL.BuilderDummy(), "jefry", "jefry@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116490366")
                , Long.parseLong("0"));

        TAPUserModel userCundy = TAPUserModel.Builder("11", "11", "Cundy Sunardy"
                , TAPImageURL.BuilderDummy(), "cundy", "cundy@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116531507")
                , Long.parseLong("0"));

        TAPUserModel userRizka = TAPUserModel.Builder("12", "12", "Rizka Fatmawati"
                , TAPImageURL.BuilderDummy(), "rizka", "rizka@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116567527")
                , Long.parseLong("0"));

        TAPUserModel userTest1 = TAPUserModel.Builder("13", "13", "Test 1"
                , TAPImageURL.BuilderDummy(), "test1", "test1@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116607839")
                , Long.parseLong("0"));

        TAPUserModel userTest2 = TAPUserModel.Builder("14", "14", "Test 2"
                , TAPImageURL.BuilderDummy(), "test2", "test2@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116655845")
                , Long.parseLong("0"));

        TAPUserModel userTest3 = TAPUserModel.Builder("15", "15", "Test 3"
                , TAPImageURL.BuilderDummy(), "test3", "test3@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116733822")
                , Long.parseLong("0"));

        TAPUserModel userSanto = TAPUserModel.Builder("17", "16", "Santo"
                , TAPImageURL.BuilderDummy(), "santo", "santo@moselo.com", "08979809026"
                , new TAPUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116733822")
                , Long.parseLong("0"));

        vm.getContactList().add(userCundy);

        vm.getContactList().add(userDominic);

        vm.getContactList().add(userErwin);

        vm.getContactList().add(userJony);
        vm.getContactList().add(userJefry);

        vm.getContactList().add(userKevin);

        vm.getContactList().add(userMichael);

        vm.getContactList().add(userRitchie);
        vm.getContactList().add(userRionaldo);
        vm.getContactList().add(userRichard);
        vm.getContactList().add(userRizka);

        vm.getContactList().add(userSanto);

        vm.getContactList().add(userWelly);

        vm.getContactList().add(userTest1);
        vm.getContactList().add(userTest2);
        vm.getContactList().add(userTest3);

        vm.getFilteredContacts().addAll(vm.getContactList());
    }
}
