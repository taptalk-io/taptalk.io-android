package com.moselo.HomingPigeon.View.Activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.Const.HpDefaultConstant;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpContactModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpContactResponse;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpContactInitialAdapter;
import com.moselo.HomingPigeon.View.Adapter.HpContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Const.HpDefaultConstant.CONTACT_LIST;
import static com.moselo.HomingPigeon.Const.HpDefaultConstant.PermissionRequest.PERMISSION_CAMERA;

public class HpNewChatActivity extends HpBaseActivity {

    private LinearLayout llButtonNewContact, llButtonScanQR, llButtonNewGroup, llBlockedContacts;
    private ImageView ivButtonBack, ivButtonSearch;
    private TextView tvTitle;
    private RecyclerView rvContactList;
    private NestedScrollView nsvNewChat;

    private HpContactInitialAdapter adapter;
    private HpContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_new_chat);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpContactListViewModel.class);
    }

    @Override
    protected void initView() {
        //call API
        HpDataManager.getInstance().getMyContactListFromAPI(getContactView);

        //setting up listener for Live Data
        vm.getContactListLive().observe(this, userModels -> {
            vm.getContactList().clear();
            vm.getContactList().addAll(userModels);
            vm.setSeparatedContacts(HpUtils.getInstance().separateContactsByInitial(vm.getContactList()));
            runOnUiThread(() -> adapter.setItems(vm.getSeparatedContacts()));
        });

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

        adapter = new HpContactInitialAdapter(HpContactListAdapter.CHAT, vm.getSeparatedContacts());
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> searchContact());
        llButtonNewContact.setOnClickListener(v -> addNewContact());
        llButtonScanQR.setOnClickListener(v -> openQRScanner());
        llButtonNewGroup.setOnClickListener(v -> createNewGroup());
        llBlockedContacts.setOnClickListener(v -> viewBlockedContacts());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_CAMERA:
                    openQRScanner();
                    break;
            }
        }
    }

    private void openQRScanner() {
        if (HpUtils.getInstance().hasPermissions(HpNewChatActivity.this, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(HpNewChatActivity.this, HpBarcodeScannerActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(HpNewChatActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
    }

    private void searchContact() {
        Intent intent = new Intent(this, HpSearchContactActivity.class);
        intent.putExtra(CONTACT_LIST, (ArrayList<HpUserModel>) vm.getContactList());
        startActivity(intent);
    }

    private void addNewContact() {
        Intent intent = new Intent(HpNewChatActivity.this, HpNewContactActivity.class);
        startActivity(intent);
    }

    private void createNewGroup() {
        Intent intent = new Intent(this, HpCreateNewGroupActivity.class);
        startActivity(intent);
    }

    private void viewBlockedContacts() {
        Intent intent = new Intent(this, HpBlockedListActivity.class);
        startActivity(intent);
    }

    HpDefaultDataView<HpContactResponse> getContactView = new HpDefaultDataView<HpContactResponse>() {
        @Override
        public void onSuccess(HpContactResponse response) {
            // Insert contacts to database
            List<HpUserModel> users = new ArrayList<>();
            for (HpContactModel contact : response.getContacts()) {
                users.add(contact.getUser().hpUserModelForAddToDB());
            }
            HpDataManager.getInstance().insertMyContactToDatabase(users);
        }
    };
}
