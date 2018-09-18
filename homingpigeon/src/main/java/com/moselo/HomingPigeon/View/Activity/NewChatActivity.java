package com.moselo.HomingPigeon.View.Activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.ContactInitialAdapter;
import com.moselo.HomingPigeon.View.Adapter.ContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.PermissionRequest.CAMERA_PERMISSION;

public class NewChatActivity extends AppCompatActivity {

    LinearLayout llButtonNewContact, llButtonScanQR, llButtonNewGroup, llBlockedContacts;
    ImageView ivButtonBack, ivButtonSearch;
    TextView tvTitle;
    RecyclerView rvContactList;
    NestedScrollView nsvNewChat;

    ContactInitialAdapter adapter;
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
        if (vm.getContactList().size() == 0) {
            UserModel u0 = new UserModel("u0", "Ababa");
            UserModel u1 = new UserModel("u1", "Bambang 1");
            UserModel u2 = new UserModel("u2", "Bambang 2");
            UserModel u3 = new UserModel("u3", "Bambang 3");
            UserModel u4 = new UserModel("u4", "Caca");
            UserModel u5 = new UserModel("u5", "Coco");
            UserModel u6 = new UserModel("u6", "123asd");
            UserModel u7 = new UserModel("u7", "!!!11111!!!");
            UserModel u8 = new UserModel("u8", "!!wkwkwk!!");
            vm.getContactList().add(u0);
            vm.getContactList().add(u1);
            vm.getContactList().add(u2);
            vm.getContactList().add(u3);
            vm.getContactList().add(u4);
            vm.getContactList().add(u5);
            vm.getContactList().add(u6);
            vm.getContactList().add(u7);
            vm.getContactList().add(u8);
        }
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
        nsvNewChat = findViewById(R.id.nsv_new_chat);

        OverScrollDecoratorHelper.setUpOverScroll(nsvNewChat);

        vm.setSeparatedContacts(Utils.getInstance().separateContactsByInitial(vm.getContactList()));

        adapter = new ContactInitialAdapter(ContactListAdapter.CHAT, vm.getSeparatedContacts());
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        ivButtonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchContactActivity.class);
            startActivity(intent);
        });

        llButtonNewContact.setOnClickListener(v -> {
            openNewUsername();
        });

        llButtonScanQR.setOnClickListener(v -> openQRScanner());

        llButtonNewGroup.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateNewGroupActivity.class);
            startActivity(intent);
        });

        llBlockedContacts.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlockedListActivity.class);
            startActivity(intent);
        });
    }

    private void openQRScanner() {
        if (Utils.getInstance().hasPermissions(NewChatActivity.this, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(NewChatActivity.this, BarcodeScannerActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(NewChatActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CAMERA_PERMISSION:
                    openQRScanner();
                    break;
            }
        }
    }

    private void openNewUsername() {
        Intent intent = new Intent(NewChatActivity.this, NewContactActivity.class);
        startActivity(intent);
    }
}
