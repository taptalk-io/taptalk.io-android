package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPContactInitialAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CONTACT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_CONTACT;

public class TAPNewChatActivity extends TAPBaseActivity {

    private static final String TAG = TAPNewChatActivity.class.getSimpleName();
    private LinearLayout llBlockedContacts, llButtonSync, llConnectionStatus;
    private ImageView ivButtonClose, ivButtonSearch, ivConnectionStatus;
    private TextView tvTitle, tvConnectionStatus;
    private RecyclerView rvContactList;
    private NestedScrollView nsvNewChat;
    private FrameLayout flSyncStatus, flSync;
    private ProgressBar pbConnecting;
    private ConstraintLayout clButtonNewContact, clButtonScanQR, clButtonNewGroup;

    private TAPContactInitialAdapter adapter;
    private TAPContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_new_chat);

        initViewModel();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vm.isFirstContactSyncDone()) permissionCheckAndGetContactList();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
    }

    private void permissionCheckAndGetContactList() {
        if (!TAPContactManager.getInstance().isContactSyncPermissionAsked() &&
                !TAPUtils.getInstance().hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            showPermissionDialog();
        } else if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            runOnUiThread(() -> flSync.setVisibility(View.VISIBLE));
        } else {
            //getContactList(false);
            getContactList(false);
        }
    }

    private void permissionCheckAndGetContactListWhenSyncButtonClicked() {
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            showPermissionDialog();
        } else {
            getContactList(true);
        }
    }

    private void showPermissionDialog() {
        runOnUiThread(() -> new TapTalkDialog.Builder(TAPNewChatActivity.this)
                .setTitle("Contact Access")
                .setMessage("We need your permission to access your contact, we will sync your contact to our server and automatically find your friend so it is easier for you to find your friends.")
                .setCancelable(false)
                .setPrimaryButtonTitle("Allow")
                .setPrimaryButtonListener(v -> ActivityCompat.requestPermissions(TAPNewChatActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACT))
                .setSecondaryButtonTitle("Cancel")
                .setSecondaryButtonListener(true, v -> flSync.setVisibility(View.VISIBLE))
                .show());
        TAPContactManager.getInstance().setAndSaveContactSyncPermissionAsked(true);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPContactListViewModel.class);
        //setting up listener for Live Data
        vm.getContactListLive().observe(this, userModels -> {
            if (null != userModels) {
                vm.getContactList().clear();
                vm.getContactList().addAll(userModels);
                vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getContactList()));
                runOnUiThread(() -> {
                    if (null != adapter) {
                        adapter.setItems(vm.getSeparatedContacts(), true);
                    }
                        //adapter.updateAdapterData(vm.getSeparatedContacts());
                });
            }
        });
    }

    private void initView() {
        clButtonNewContact = findViewById(R.id.cl_button_new_contact);
        clButtonScanQR = findViewById(R.id.cl_button_scan_qr);
        clButtonNewGroup = findViewById(R.id.cl_button_new_group);
        llBlockedContacts = findViewById(R.id.ll_blocked_contacts);
        llButtonSync = findViewById(R.id.ll_btn_sync);
        llConnectionStatus = findViewById(R.id.ll_connection_status);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        ivConnectionStatus = findViewById(R.id.iv_connection_status);
        tvTitle = findViewById(R.id.tv_title);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);
        rvContactList = findViewById(R.id.rv_contact_list);
        nsvNewChat = findViewById(R.id.nsv_new_chat);
        flSyncStatus = findViewById(R.id.fl_sync_status);
        flSync = findViewById(R.id.fl_sync);
        pbConnecting = findViewById(R.id.pb_connecting);

        pbConnecting.setVisibility(View.GONE);
        ivConnectionStatus.setVisibility(View.VISIBLE);

        new Thread(() -> TAPDataManager.getInstance().getMyContactListFromAPI(getContactView)).start();

        getWindow().setBackgroundDrawable(null);

        OverScrollDecoratorHelper.setUpOverScroll(nsvNewChat);

        adapter = new TAPContactInitialAdapter(TAPContactListAdapter.CHAT, vm.getSeparatedContacts());
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);


        // TODO: 21 December 2018 TEMPORARILY DISABLED FEATURE
        llBlockedContacts.setVisibility(View.GONE);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> searchContact());
        clButtonNewContact.setOnClickListener(v -> addNewContact());
        clButtonScanQR.setOnClickListener(v -> openQRScanner());
        clButtonNewGroup.setOnClickListener(v -> createNewGroup());
        llBlockedContacts.setOnClickListener(v -> viewBlockedContacts());
        llButtonSync.setOnClickListener(v -> permissionCheckAndGetContactListWhenSyncButtonClicked());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_CAMERA_CAMERA:
                    openQRScanner();
                    break;
                case PERMISSION_READ_CONTACT:
                    //permissionCheckAndGetContactList();
                    getContactList(true);
                    break;
            }
        } else {
            switch (requestCode) {
                case PERMISSION_READ_CONTACT:
                    flSync.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void openQRScanner() {
        if (TAPUtils.getInstance().hasPermissions(TAPNewChatActivity.this, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(TAPNewChatActivity.this, TAPBarcodeScannerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        } else {
            ActivityCompat.requestPermissions(TAPNewChatActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CAMERA);
        }
    }

    private void searchContact() {
        Intent intent = new Intent(this, TAPSearchContactActivity.class);
        intent.putExtra(CONTACT_LIST, (ArrayList<TAPUserModel>) vm.getContactList());
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void addNewContact() {
        Intent intent = new Intent(TAPNewChatActivity.this, TAPNewContactActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void createNewGroup() {
        Intent intent = new Intent(this, TAPCreateNewGroupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void viewBlockedContacts() {
        Intent intent = new Intent(this, TAPBlockedListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void showSyncLoading() {
        showSyncLoadingStatus();
    }

    private void stopSyncLoading() {
        llConnectionStatus.setVisibility(View.GONE);
    }

    private void getContactList(boolean showLoading) {
        if (showLoading) showSyncLoading();

        new Thread(() -> {
            List<String> newContactsPhoneNumbers = new ArrayList<>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC");
            if ((null != cur ? cur.getCount() : 0) > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);

                        if (null != pCur) {
                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String phoneNumb = TAPContactManager.getInstance().convertPhoneNumber(phoneNo);
                                if (!"".equals(phoneNumb) && !TAPContactManager.getInstance()
                                        .isUserPhoneNumberAlreadyExist(phoneNumb) && !newContactsPhoneNumbers.contains(phoneNumb)) {
                                    newContactsPhoneNumbers.add(phoneNumb);
                                }
                            }
                            pCur.close();
                        }
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }

            callAddContactsByPhoneApi(newContactsPhoneNumbers, showLoading);

        }).start();
    }

    private void callAddContactsByPhoneApi(List<String> newContactsPhoneNumbers, boolean showLoading) {
        TAPDataManager.getInstance().addContactByPhone(newContactsPhoneNumbers, new TapDefaultDataView<TAPAddContactByPhoneResponse>() {
            @Override
            public void onSuccess(TAPAddContactByPhoneResponse response) {
                new Thread(() -> {
                    try {
                        // Insert contacts to database
                        if (null == response.getUsers() || response.getUsers().isEmpty()) {
                            runOnUiThread(() -> {
                                //stopSyncLoading();
                                flSync.setVisibility(View.GONE);
                            });
                            if (showLoading)
                                showSyncSuccessStatus(response.getUsers().size());
                            vm.setFirstContactSyncDone(true);
                            return;
                        }
                        new Thread(() -> {
                            List<TAPUserModel> users = new ArrayList<>();
                            for (TAPUserModel contact : response.getUsers()) {
                                contact.setUserAsContact();
                                users.add(contact);
                            }
                            TAPDataManager.getInstance().insertMyContactToDatabase(users);
                            TAPContactManager.getInstance().updateUserDataMap(users);
                            runOnUiThread(() -> {
                                //stopSyncLoading();
                                flSync.setVisibility(View.GONE);
                            });
                            if (showLoading)
                                showSyncSuccessStatus(response.getUsers().size());

                            vm.setFirstContactSyncDone(true);
                        }).start();
                    } catch (Exception e) {
                        Log.e(TAG, "initViewModel: ", e);
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onError(TAPErrorModel error) {
                super.onError(error);
                runOnUiThread(() -> stopSyncLoading());
            }

            @Override
            public void onError(String errorMessage) {
                super.onError(errorMessage);
                runOnUiThread(() -> stopSyncLoading());
            }
        });
    }

    private void showSyncSuccessStatus(int contactSynced) {
        runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.tap_bg_status_connected);
            if (0 == contactSynced)
                tvConnectionStatus.setText("All contacts synced");
            else tvConnectionStatus.setText("Synced " + contactSynced + " Contacts");
            pbConnecting.setVisibility(View.GONE);
            ivConnectionStatus.setVisibility(View.VISIBLE);
            ivConnectionStatus.setImageResource(R.drawable.tap_ic_connected_white);
            flSyncStatus.setVisibility(View.VISIBLE);
            Log.e(TAG, "showSyncSuccessStatus: ");

            new Handler().postDelayed(() -> flSyncStatus.setVisibility(View.GONE), 1000L);
        });
    }

    private void showSyncLoadingStatus() {
        runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.tap_bg_status_connecting);
            tvConnectionStatus.setText("Syncing Contacts");
            ivConnectionStatus.setVisibility(View.GONE);
            pbConnecting.setVisibility(View.VISIBLE);
            llConnectionStatus.setVisibility(View.VISIBLE);
            flSyncStatus.setVisibility(View.VISIBLE);
        });
    }

    private TapDefaultDataView<TAPContactResponse> getContactView = new TapDefaultDataView<TAPContactResponse>() {
        @Override
        public void onSuccess(TAPContactResponse response) {
            try {
                // Insert contacts to database
                if (null == response.getContacts() || response.getContacts().isEmpty()) {
                    permissionCheckAndGetContactList();
                    return;
                }
                new Thread(() -> {
                    List<TAPUserModel> users = new ArrayList<>();
                    for (TAPContactModel contact : response.getContacts()) {
                        TAPUserModel contactUserModel = contact.getUser().setUserAsContact();
                        users.add(contactUserModel);
                        TAPContactManager.getInstance().addUserMapByPhoneNumber(contactUserModel);
                        TAPContactManager.getInstance().updateUserDataMap(contactUserModel);
                    }

                    TAPDataManager.getInstance().insertMyContactToDatabase(users);
                    TAPContactManager.getInstance().updateUserDataMap(users);
                    permissionCheckAndGetContactList();
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
