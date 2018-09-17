package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.ContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.ContactListViewModel;

public class SearchContactActivity extends AppCompatActivity {

    LinearLayout llAddNewContact;
    ImageView ivButtonBack, ivButtonCancel;
    EditText etSearch;
    RecyclerView rvSearchResults;

    ContactListAdapter adapter;

    ContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(ContactListViewModel.class);

        //Dummy Contacts
        if (vm.getContactList().size() == 0) {
            UserModel u0 = new UserModel("u0", "Ababa");
            UserModel u1 = new UserModel("u1", "Bambang 1");
            UserModel u2 = new UserModel("u2", "Bambang 2");
            UserModel u3 = new UserModel("u3", "Bambang 3");
            UserModel u4 = new UserModel("u4", "Caca");
            UserModel u5 = new UserModel("u5", "Coco");
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

    private void initView() {
        getWindow().setBackgroundDrawable(null);

        llAddNewContact = findViewById(R.id.ll_add_new_contact);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonCancel = findViewById(R.id.iv_button_cancel);
        etSearch = findViewById(R.id.et_search);
        rvSearchResults = findViewById(R.id.rv_search_results);

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new ContactListAdapter(vm.getFilteredContacts());
        rvSearchResults.setAdapter(adapter);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        ivButtonCancel.setOnClickListener(v -> {
            if (etSearch.getText().toString().isEmpty()) {
                onBackPressed();
            } else {
                etSearch.setText("");
                etSearch.clearFocus();
                Utils.getInstance().dismissKeyboard(this);
            }
        });

        llAddNewContact.setOnClickListener(v -> {

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
                for (UserModel user : vm.getContactList()) {
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
