package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moselo.HomingPigeon.Helper.HorizontalDecoration;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.ContactListListener;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.ContactInitialAdapter;
import com.moselo.HomingPigeon.View.Adapter.ContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

public class CreateNewGroupActivity extends AppCompatActivity {

    LinearLayout llGroupMembers;
    ImageView ivButtonBack, ivButtonCancel;
    EditText etSearch;
    Button btnContinue;
    RecyclerView rvContactList, rvGroupMembers;

    ContactInitialAdapter contactListAdapter;
    ContactListAdapter selectedMembersAdapter;
    ContactListListener listener;
    ContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        initViewModel();
        initListener();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(ContactListViewModel.class);
    }

    private void initListener() {
        listener = new ContactListListener() {
            @Override
            public void onContactSelected(UserModel contact, boolean isSelected) {
                if (isSelected) {
                    vm.getSelectedContacts().add(contact);
                    selectedMembersAdapter.notifyItemInserted(vm.getSelectedContacts().size());
                    updateSelectedMemberDecoration(0);
                } else {
                    int index = vm.getSelectedContacts().indexOf(contact);
                    vm.getSelectedContacts().remove(contact);
                    selectedMembersAdapter.notifyItemRemoved(index);
                    updateSelectedMemberDecoration(1);
                }
                if (vm.getSelectedContacts().size() > 0) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onContactRemoved(UserModel contact) {
                int index = vm.getFilteredContacts().indexOf(contact);
                vm.getFilteredContacts().get(index).setSelected(false);
                contactListAdapter.notifyItemRangeChanged(0, vm.getFilteredContacts().size() - 1);
                if (vm.getSelectedContacts().size() > 0) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                    updateSelectedMemberDecoration(1);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
            }
        };
    }

    private void initView() {
        //Dummy Contacts
        if (vm.getContactList().size() == 0) {
            UserModel u0 = new UserModel("u0", "Ababa");
            UserModel u1 = new UserModel("u1", "Bambang 1");
            UserModel u2 = new UserModel("u2", "Bambang 2");
            UserModel u3 = new UserModel("u3", "Bambang 3");
            UserModel u4 = new UserModel("u4", "Caca");
            UserModel u5 = new UserModel("u5", "Coco");
            UserModel u6 = new UserModel("u6", "123asd");
            UserModel u7 = new UserModel("u7", "!!!11111!!!");
            vm.getContactList().add(u0);
            vm.getContactList().add(u1);
            vm.getContactList().add(u2);
            vm.getContactList().add(u3);
            vm.getContactList().add(u4);
            vm.getContactList().add(u5);
            vm.getContactList().add(u6);
            vm.getContactList().add(u7);
            vm.getFilteredContacts().addAll(vm.getContactList());
        }
        //End Dummy

        getWindow().setBackgroundDrawable(null);

        llGroupMembers = findViewById(R.id.ll_group_members);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonCancel = findViewById(R.id.iv_button_cancel);
        etSearch = findViewById(R.id.et_search);
        btnContinue = findViewById(R.id.btn_continue);
        rvContactList = findViewById(R.id.rv_contact_list);
        rvGroupMembers = findViewById(R.id.rv_group_members);

        // Separate contact list by initial
        int previousInitialIndexStart = 0;
        int size = vm.getFilteredContacts().size();
        for (int i = 1; i <= size; i++) {
            if (i == size ||
                    vm.getFilteredContacts().get(i).getName().charAt(0) !=
                            vm.getFilteredContacts().get(i - 1).getName().charAt(0)) {
                List<UserModel> contactSubList = vm.getFilteredContacts().subList(previousInitialIndexStart, i);
                vm.getSeparatedContacts().add(contactSubList);
                previousInitialIndexStart = i;
            }
        }

        etSearch.addTextChangedListener(searchTextWatcher);

        contactListAdapter = new ContactInitialAdapter(ContactListAdapter.SELECT, vm.getSeparatedContacts(), listener);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        selectedMembersAdapter = new ContactListAdapter(ContactListAdapter.SELECTED_MEMBER, vm.getSelectedContacts(), listener);
        rvGroupMembers.setAdapter(selectedMembersAdapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        updateSelectedMemberDecoration(0);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
    }

    private void updateSelectedMemberDecoration(int itemRemoved) {
        if (rvGroupMembers.getItemDecorationCount() > 0) {
            rvGroupMembers.removeItemDecorationAt(0);
        }
        rvGroupMembers.addItemDecoration(new HorizontalDecoration(0, 0,
                0, Utils.getInstance().dpToPx(16), selectedMembersAdapter.getItemCount() + itemRemoved,
                0, 0));
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
                vm.setFilteredContacts(vm.getContactList());
            } else {
                for (UserModel user : vm.getContactList()) {
                    if (user.getName().toLowerCase().contains(searchKeyword)) {
                        vm.getFilteredContacts().add(user);
                    }
                }
            }
            contactListAdapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
