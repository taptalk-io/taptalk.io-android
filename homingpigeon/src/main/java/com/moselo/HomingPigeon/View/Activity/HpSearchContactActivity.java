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
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

public class HpSearchContactActivity extends HpBaseActivity {

    LinearLayout llAddNewContact;
    ImageView ivButtonBack, ivButtonCancel;
    EditText etSearch;
    RecyclerView rvSearchResults;

    HpContactListAdapter adapter;

    HpContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_search_contact);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpContactListViewModel.class);

        //Dummy Contacts
        if (vm.getContactList().size() == 0) {
            HpUserModel u0 = new HpUserModel("u0", "Ababa");
            HpUserModel u1 = new HpUserModel("u1", "Bambang 1");
            HpUserModel u2 = new HpUserModel("u2", "Bambang 2");
            HpUserModel u3 = new HpUserModel("u3", "Bambang 3");
            HpUserModel u4 = new HpUserModel("u4", "Caca");
            HpUserModel u5 = new HpUserModel("u5", "Coco");
            vm.getContactList().add(u0);
            vm.getContactList().add(u1);
            vm.getContactList().add(u2);
            vm.getContactList().add(u3);
            vm.getContactList().add(u4);
            vm.getContactList().add(u5);
//            vm.getFilteredContacts().addAll(vm.getContactList());
        }
        //End Dummy
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

        ivButtonCancel.setOnClickListener(v -> {
//            if (etSearch.getText().toString().isEmpty()) {
//                onBackPressed();
//            } else {
                etSearch.setText("");
                etSearch.clearFocus();
                HpUtils.getInstance().dismissKeyboard(this);
//            }
        });

        llAddNewContact.setOnClickListener(v -> {
            Intent intent = new Intent(this, HpNewContactActivity.class);
            startActivity(intent);
        });
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
}
