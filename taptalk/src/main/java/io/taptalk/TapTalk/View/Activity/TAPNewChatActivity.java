package io.taptalk.TapTalk.View.Activity;

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

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPContactInitialAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CONTACT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA;

public class TAPNewChatActivity extends TAPBaseActivity {

    private LinearLayout llButtonNewContact, llButtonScanQR, llButtonNewGroup, llBlockedContacts;
    private ImageView ivButtonBack, ivButtonSearch;
    private TextView tvTitle;
    private RecyclerView rvContactList;
    private NestedScrollView nsvNewChat;

    private TAPContactInitialAdapter adapter;
    private TAPContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_new_chat);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPContactListViewModel.class);
    }

    @Override
    protected void initView() {
        //call API
        TAPDataManager.getInstance().getMyContactListFromAPI(getContactView);

        //setting up listener for Live Data
        vm.getContactListLive().observe(this, userModels -> {
            vm.getContactList().clear();
            vm.getContactList().addAll(userModels);
            vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getContactList()));
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

        adapter = new TAPContactInitialAdapter(TAPContactListAdapter.CHAT, vm.getSeparatedContacts());
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
        if (TAPUtils.getInstance().hasPermissions(TAPNewChatActivity.this, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(TAPNewChatActivity.this, TAPBarcodeScannerActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(TAPNewChatActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
    }

    private void searchContact() {
        Intent intent = new Intent(this, TAPSearchContactActivity.class);
        intent.putExtra(CONTACT_LIST, (ArrayList<TAPUserModel>) vm.getContactList());
        startActivity(intent);
    }

    private void addNewContact() {
        Intent intent = new Intent(TAPNewChatActivity.this, TAPNewContactActivity.class);
        startActivity(intent);
    }

    private void createNewGroup() {
        Intent intent = new Intent(this, TAPCreateNewGroupActivity.class);
        startActivity(intent);
    }

    private void viewBlockedContacts() {
        Intent intent = new Intent(this, TAPBlockedListActivity.class);
        startActivity(intent);
    }

    TapDefaultDataView<TAPContactResponse> getContactView = new TapDefaultDataView<TAPContactResponse>() {
        @Override
        public void onSuccess(TAPContactResponse response) {
            // Insert contacts to database
            List<TAPUserModel> users = new ArrayList<>();
            for (TAPContactModel contact : response.getContacts()) {
                users.add(contact.getUser().hpUserModelForAddToDB());
            }
            TAPDataManager.getInstance().insertMyContactToDatabase(users);
        }
    };
}
