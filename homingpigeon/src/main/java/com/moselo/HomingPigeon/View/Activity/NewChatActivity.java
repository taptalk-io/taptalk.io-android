package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.ContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

public class NewChatActivity extends AppCompatActivity {

    LinearLayout llButtonNewContact, llButtonScanQR, llButtonNewGroup, llBlockedContacts;
    ImageView ivButtonBack, ivButtonSearch;
    TextView tvTitle;
    RecyclerView rvContactList;

    ContactListAdapter adapter;
    ContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(ContactListViewModel.class);
    }

    private void initView() {
        //Dummy Contacts
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
        //End Dummy

        getWindow().setBackgroundDrawable(null);

        llButtonNewContact = findViewById(R.id.ll_button_new_contact);
        llButtonScanQR = findViewById(R.id.ll_button_scan_qr);
        llButtonNewGroup = findViewById(R.id.ll_button_new_group);
        llBlockedContacts = findViewById(R.id.ll_blocked_contacts);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        tvTitle = findViewById(R.id.tv_title);
        rvContactList = findViewById(R.id.rv_contact_list);

        // Separate contact list by initial
        List<List<UserModel>> contactList = new ArrayList<>();
        int previousInitialIndexStart = 0;
        int size = vm.getContactList().size();
        for (int i = 1; i <= size; i++) {
            if (i == size ||
                    vm.getContactList().get(i).getName().charAt(0) !=
                    vm.getContactList().get(i - 1).getName().charAt(0)) {
                List<UserModel> contactSubList = vm.getContactList().subList(previousInitialIndexStart, i);
                contactList.add(contactSubList);
                previousInitialIndexStart = i;
            }
        }

        adapter = new ContactListAdapter(contactList);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        ivButtonSearch.setOnClickListener(v -> {

        });

        llButtonNewContact.setOnClickListener(v -> {

        });

        llButtonScanQR.setOnClickListener(v -> {

        });

        llButtonNewGroup.setOnClickListener(v -> {

        });

        llBlockedContacts.setOnClickListener(v -> {

        });
    }
}
