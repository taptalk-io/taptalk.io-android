package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.HpUserRoleModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

public class HpSearchContactActivity extends HpBaseActivity {

    private LinearLayout llAddNewContact;
    private ImageView ivButtonBack, ivButtonCancel;
    private EditText etSearch;
    private RecyclerView rvSearchResults;

    private HpContactListAdapter adapter;

    private HpContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_search_contact);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpContactListViewModel.class);

        setDummyData();
    }

    @Override
    protected void initView() {
        getWindow().setBackgroundDrawable(null);

        llAddNewContact = findViewById(R.id.ll_add_new_contact);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonCancel = findViewById(R.id.iv_button_cancel);
        etSearch = findViewById(R.id.et_search);
        rvSearchResults = findViewById(R.id.rv_search_results);

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new HpContactListAdapter(HpContactListAdapter.CHAT, vm.getFilteredContacts());
        rvSearchResults.setAdapter(adapter);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonCancel.setOnClickListener(v -> clearSearch());
        llAddNewContact.setOnClickListener(v -> openNewContactActivity());
    }

    private void clearSearch() {
        HpUtils.getInstance().dismissKeyboard(this);
        etSearch.setText("");
        etSearch.clearFocus();
    }

    private void openNewContactActivity() {
        Intent intent = new Intent(this, HpNewContactActivity.class);
        startActivity(intent);
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            vm.getFilteredContacts().clear();
            String searchKeyword = etSearch.getText().toString().toLowerCase().trim();
            if (searchKeyword.isEmpty()) {
                vm.getFilteredContacts().clear();
            } else {
                for (HpUserModel user : vm.getContactList()) {
                    if (user.getName().toLowerCase().contains(searchKeyword)) {
                        vm.getFilteredContacts().add(user);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // TODO: 28/09/18 Harus dihapus setelah fix
    private void setDummyData() {
        HpUserModel userRitchie = HpUserModel.Builder("1", "1", "Ritchie Nathaniel"
                , HpImageURL.BuilderDummy(), "ritchie", "ritchie@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538115801488")
                , Long.parseLong("0"));

        HpUserModel userDominic = HpUserModel.Builder("2", "2", "Dominic Vedericho"
                , HpImageURL.BuilderDummy(), "dominic", "dominic@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538115918918")
                , Long.parseLong("0"));

        HpUserModel userRionaldo = HpUserModel.Builder("3", "3", "Rionaldo Linggautama"
                , HpImageURL.BuilderDummy(), "rionaldo", "rionaldo@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116046534")
                , Long.parseLong("0"));

        HpUserModel userKevin = HpUserModel.Builder("4", "4", "Kevin Reynaldo"
                , HpImageURL.BuilderDummy(), "kevin", "kevin@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116099655")
                , Long.parseLong("0"));

        HpUserModel userWelly = HpUserModel.Builder("5", "5", "Welly Kencana"
                , HpImageURL.BuilderDummy(), "welly", "welly@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116147477")
                , Long.parseLong("0"));

        HpUserModel userJony = HpUserModel.Builder("6", "6", "Jony Lim"
                , HpImageURL.BuilderDummy(), "jony", "jony@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116249323")
                , Long.parseLong("0"));

        HpUserModel userMichael = HpUserModel.Builder("7", "7", "Michael Tansy"
                , HpImageURL.BuilderDummy(), "michael", "michael@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116355199")
                , Long.parseLong("0"));

        HpUserModel userRichard = HpUserModel.Builder("8", "8", "Richard Fang"
                , HpImageURL.BuilderDummy(), "richard", "richard@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116398588")
                , Long.parseLong("0"));

        HpUserModel userErwin = HpUserModel.Builder("9", "9", "Erwin Andreas"
                , HpImageURL.BuilderDummy(), "erwin", "erwin@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116452636")
                , Long.parseLong("0"));

        HpUserModel userJefry = HpUserModel.Builder("10", "10", "Jefry Lorentono"
                , HpImageURL.BuilderDummy(), "jefry", "jefry@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116490366")
                , Long.parseLong("0"));

        HpUserModel userCundy = HpUserModel.Builder("11", "11", "Cundy Sunardy"
                , HpImageURL.BuilderDummy(), "cundy", "cundy@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116531507")
                , Long.parseLong("0"));

        HpUserModel userRizka = HpUserModel.Builder("12", "12", "Rizka Fatmawati"
                , HpImageURL.BuilderDummy(), "rizka", "rizka@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116567527")
                , Long.parseLong("0"));

        HpUserModel userTest1 = HpUserModel.Builder("13", "13", "Test 1"
                , HpImageURL.BuilderDummy(), "test1", "test1@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116607839")
                , Long.parseLong("0"));

        HpUserModel userTest2 = HpUserModel.Builder("14", "14", "Test 2"
                , HpImageURL.BuilderDummy(), "test2", "test2@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116655845")
                , Long.parseLong("0"));

        HpUserModel userTest3 = HpUserModel.Builder("15", "15", "Test 3"
                , HpImageURL.BuilderDummy(), "test3", "test3@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116733822")
                , Long.parseLong("0"));

        HpUserModel userSanto = HpUserModel.Builder("17", "16", "Santo"
                , HpImageURL.BuilderDummy(), "santo", "santo@moselo.com", "08979809026"
                , new HpUserRoleModel(), Long.parseLong("0"), Long.parseLong("0"), false, Long.parseLong("1538116733822")
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
    }
}
