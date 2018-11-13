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

import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Model.TAPUserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.TAPContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.CONTACT_LIST;

public class TAPSearchContactActivity extends TAPBaseActivity {

    private LinearLayout llAddNewContact;
    private ImageView ivButtonBack, ivButtonCancel;
    private EditText etSearch;
    private RecyclerView rvSearchResults;

    private TAPContactListAdapter adapter;

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
    }

    @Override
    protected void initView() {
        getWindow().setBackgroundDrawable(null);

        //ini buat set data yang di pass dari new chat activity
        setData();
        
        llAddNewContact = findViewById(R.id.ll_add_new_contact);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonCancel = findViewById(R.id.iv_button_cancel);
        etSearch = findViewById(R.id.et_search);
        rvSearchResults = findViewById(R.id.rv_search_results);

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new TAPContactListAdapter(TAPContactListAdapter.CHAT, vm.getFilteredContacts());
        rvSearchResults.setAdapter(adapter);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonCancel.setOnClickListener(v -> clearSearch());
        llAddNewContact.setOnClickListener(v -> openNewContactActivity());
    }

    private void clearSearch() {
        TAPUtils.getInstance().dismissKeyboard(this);
        etSearch.setText("");
        etSearch.clearFocus();
    }

    private void openNewContactActivity() {
        Intent intent = new Intent(this, TAPNewContactActivity.class);
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
                for (TAPUserModel user : vm.getContactList()) {
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

    private void setData() {
        vm.setContactList(getIntent().getParcelableArrayListExtra(CONTACT_LIST));
    }
}
