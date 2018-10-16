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

import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.HpUserRoleModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpContactInitialAdapter;
import com.moselo.HomingPigeon.View.Adapter.HpContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.PermissionRequest.PERMISSION_CAMERA;

public class HpNewChatActivity extends HpBaseActivity {

    LinearLayout llButtonNewContact, llButtonScanQR, llButtonNewGroup, llBlockedContacts;
    ImageView ivButtonBack, ivButtonSearch;
    TextView tvTitle;
    RecyclerView rvContactList;
    NestedScrollView nsvNewChat;

    HpContactInitialAdapter adapter;
    HpContactListViewModel vm;

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
        //Dummy Contacts
        setDummyData();
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

        adapter = new HpContactInitialAdapter(HpContactListAdapter.CHAT, vm.getSeparatedContacts());
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        ivButtonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, HpSearchContactActivity.class);
            startActivity(intent);
        });

        llButtonNewContact.setOnClickListener(v -> {
            openNewUsername();
        });

        llButtonScanQR.setOnClickListener(v -> openQRScanner());

        llButtonNewGroup.setOnClickListener(v -> {
            Intent intent = new Intent(this, HpCreateNewGroupActivity.class);
            startActivity(intent);
        });

        llBlockedContacts.setOnClickListener(v -> {
            Intent intent = new Intent(this, HpBlockedListActivity.class);
            startActivity(intent);
        });
    }

    private void openQRScanner() {
        if (HpUtils.getInstance().hasPermissions(HpNewChatActivity.this, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(HpNewChatActivity.this, HpBarcodeScannerActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(HpNewChatActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
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

    private void openNewUsername() {
        Intent intent = new Intent(HpNewChatActivity.this, HpNewContactActivity.class);
        startActivity(intent);
    }

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
        List<HpUserModel> userModels = new ArrayList<>();
        userModels.add(userCundy);
        userModels.add(userDominic);
        userModels.add(userErwin);
        userModels.add(userJony);
        userModels.add(userJefry);
        userModels.add(userKevin);
        userModels.add(userMichael);
        userModels.add(userRitchie);
        userModels.add(userRionaldo);
        userModels.add(userRichard);
        userModels.add(userRizka);
        userModels.add(userSanto);
        userModels.add(userTest1);
        userModels.add(userTest2);
        userModels.add(userTest3);
        userModels.add(userWelly);

        HpDataManager.getInstance().insertAndGetMyContact(userModels, new HpDatabaseListener<HpUserModel>() {
            @Override
            public void onSelectFinished(List<HpUserModel> entities) {
                vm.getContactList().clear();
                vm.getContactList().addAll(entities);
                vm.setSeparatedContacts(HpUtils.getInstance().separateContactsByInitial(vm.getContactList()));
                runOnUiThread(() -> adapter.setItems(vm.getSeparatedContacts()));
            }
        });
    }
}
