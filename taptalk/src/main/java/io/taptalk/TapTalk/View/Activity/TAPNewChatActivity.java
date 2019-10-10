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
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TapContactListListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TapContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CONTACT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_ACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_CONTACT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_ADD_NEW_CONTACT;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_CREATE_NEW_GROUP;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_SCAN_QR_CODE;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_DEFAULT_CONTACT_LIST;

public class TAPNewChatActivity extends TAPBaseActivity {

    private static final String TAG = TAPNewChatActivity.class.getSimpleName();
    private LinearLayout llButtonSync, llConnectionStatus;
    private ImageView ivButtonClose, ivButtonSearch, ivConnectionStatus;
    private TextView tvTitle, tvConnectionStatus;
    private RecyclerView rvContactList;
    private FrameLayout flSyncStatus, flSync;

    private TapContactListAdapter adapter;
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
        if (vm.isFirstContactSyncDone()) {
            permissionCheckAndSyncContactList();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
    }

    private void permissionCheckAndSyncContactList() {
        if (!TapTalk.isAutoContactSyncEnabled()) {
            return;
        }
        if (!TAPContactManager.getInstance().isContactSyncPermissionAsked() &&
                !TAPUtils.getInstance().hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            showSyncContactPermissionDialog();
        } else if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            runOnUiThread(() -> flSync.setVisibility(View.VISIBLE));
        } else {
            syncContactList(false);
        }
    }

    private void permissionCheckAndGetContactListWhenSyncButtonClicked() {
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            showSyncContactPermissionDialog();
        } else {
            syncContactList(true);
        }
    }

    private void showSyncContactPermissionDialog() {
        runOnUiThread(() -> new TapTalkDialog.Builder(TAPNewChatActivity.this)
                .setTitle(getString(R.string.tap_contact_access))
                .setMessage(getString(R.string.tap_sync_contact_description))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_allow))
                .setPrimaryButtonListener(v -> ActivityCompat.requestPermissions(TAPNewChatActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACT))
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setSecondaryButtonListener(true, v -> flSync.setVisibility(View.VISIBLE))
                .show());
        TAPContactManager.getInstance().setAndSaveContactSyncPermissionAsked(true);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPContactListViewModel.class);
        setupMenuButtons();
        // Set up listener for Live Data
        vm.getContactListLive().observe(this, userModels -> {
            if (null != userModels) {
                vm.getContactList().clear();
                vm.getContactList().addAll(userModels);
                vm.setSeparatedContactList(TAPUtils.getInstance().generateContactListForRecycler(vm.getContactList(), TYPE_DEFAULT_CONTACT_LIST));
                refreshAdapterItems();
                runOnUiThread(() -> {
                    if (null != adapter) {
                        adapter.setItems(vm.getAdapterItems(), false);
                        Log.e(TAG, "setItems: " + adapter.getItemCount());
                    }
                });
            }
        });
    }

    private void setupMenuButtons() {
        TapContactListModel menuAddNewContact = new TapContactListModel(
                MENU_ID_ADD_NEW_CONTACT,
                getString(R.string.tap_new_contact),
                R.drawable.tap_ic_new_contact_orange);
        vm.getNewChatMenuList().add(menuAddNewContact);
        TapContactListModel menuScanQRCode = new TapContactListModel(
                MENU_ID_SCAN_QR_CODE,
                getString(R.string.tap_scan_qr_code),
                R.drawable.tap_ic_scan_qr_orange);
        vm.getNewChatMenuList().add(menuScanQRCode);
        TapContactListModel menuCreateNewGroup = new TapContactListModel(
                MENU_ID_CREATE_NEW_GROUP,
                getString(R.string.tap_new_group),
                R.drawable.tap_ic_new_group_orange);
        vm.getNewChatMenuList().add(menuCreateNewGroup);

        // TODO: 21 December 2018 TEMPORARILY DISABLED FEATURE
        //vm.setInfoLabelItem(new TapContactListModel(
        //        INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS,
        //        getString(R.string.tap_cant_find_contact),
        //        getString(R.string.tap_view_blocked_contacts)
        //));
    }

    public void refreshAdapterItems() {
        if (!vm.getAdapterItems().isEmpty()) {
            vm.getAdapterItems().clear();
        }
        vm.getAdapterItems().addAll(vm.getNewChatMenuList());
        vm.getAdapterItems().addAll(vm.getSeparatedContactList());
        if (null != vm.getInfoLabelItem()) {
            vm.getAdapterItems().add(vm.getInfoLabelItem());
        }
    }

    private void initView() {
        llButtonSync = findViewById(R.id.ll_btn_sync);
        llConnectionStatus = findViewById(R.id.ll_connection_status);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        ivConnectionStatus = findViewById(R.id.iv_connection_status);
        tvTitle = findViewById(R.id.tv_title);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);
        rvContactList = findViewById(R.id.rv_contact_list);
        flSyncStatus = findViewById(R.id.fl_sync_status);
        flSync = findViewById(R.id.fl_sync);

        new Thread(() -> TAPDataManager.getInstance().getMyContactListFromAPI(getContactView)).start();

        getWindow().setBackgroundDrawable(null);

        adapter = new TapContactListAdapter(vm.getSeparatedContactList(), contactListListener);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);

        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> searchContact());
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
                    //permissionCheckAndSyncContactList();
                    syncContactList(true);
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
        Intent intent = new Intent(this, TAPAddGroupMemberActivity.class);
        intent.putExtra(GROUP_ACTION, CREATE_GROUP);
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

    // Previously getContactList
    private void syncContactList(boolean showLoading) {
        if (!TapTalk.isAutoContactSyncEnabled()) {
            return;
        }
        if (showLoading) {
            showSyncLoading();
        }

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
        TAPDataManager.getInstance().addContactByPhone(newContactsPhoneNumbers, new TAPDefaultDataView<TAPAddContactByPhoneResponse>() {
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
                            if (showLoading) {
                                showSyncSuccessStatus(response.getUsers().size());
                            }
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
                            TAPContactManager.getInstance().updateUserData(users);
                            runOnUiThread(() -> {
                                //stopSyncLoading();
                                flSync.setVisibility(View.GONE);
                            });
                            if (showLoading) {
                                showSyncSuccessStatus(response.getUsers().size());
                            }
                            vm.setFirstContactSyncDone(true);
                        }).start();
                    } catch (Exception e) {
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
            if (0 == contactSynced) {
                tvConnectionStatus.setText(getString(R.string.tap_all_contacts_synced));
            } else {
                tvConnectionStatus.setText(String.format(getString(R.string.tap_synced_d_contacts), contactSynced));
            }
            ivConnectionStatus.setImageResource(R.drawable.tap_ic_checklist_pumpkin);
            ivConnectionStatus.setPadding(0, 0, 0, 0);
            ivConnectionStatus.clearAnimation();
            flSyncStatus.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> flSyncStatus.setVisibility(View.GONE), 1000L);
        });
    }

    private void showSyncLoadingStatus() {
        int padding = TAPUtils.getInstance().dpToPx(2);
        runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.tap_bg_status_connecting);
            tvConnectionStatus.setText(getString(R.string.tap_syncing_contacts));
            ivConnectionStatus.setImageResource(R.drawable.tap_ic_loading_progress_circle_white);
            ivConnectionStatus.setPadding(padding, padding, padding, padding);
            TAPUtils.getInstance().rotateAnimateInfinitely(this, ivConnectionStatus);
            llConnectionStatus.setVisibility(View.VISIBLE);
            flSyncStatus.setVisibility(View.VISIBLE);
        });
    }

    private TAPDefaultDataView<TAPContactResponse> getContactView = new TAPDefaultDataView<TAPContactResponse>() {
        @Override
        public void onSuccess(TAPContactResponse response) {
            try {
                // Insert contacts to database
                if (null == response.getContacts() || response.getContacts().isEmpty()) {
                    permissionCheckAndSyncContactList();
                    return;
                }
                new Thread(() -> {
                    List<TAPUserModel> users = new ArrayList<>();
                    for (TAPContactModel contact : response.getContacts()) {
                        TAPUserModel contactUserModel = contact.getUser().setUserAsContact();
                        users.add(contactUserModel);
                        TAPContactManager.getInstance().addUserMapByPhoneNumber(contactUserModel);
                        TAPContactManager.getInstance().updateUserData(contactUserModel);
                    }

                    TAPDataManager.getInstance().insertMyContactToDatabase(users);
                    TAPContactManager.getInstance().updateUserData(users);
                    permissionCheckAndSyncContactList();
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private TapContactListListener contactListListener = new TapContactListListener() {
        @Override
        public void onContactTapped(TapContactListModel contact) {
            TAPUserModel user = contact.getUser();
            if (null == user) {
                return;
            }
            if (!TAPChatManager.getInstance().getActiveUser().getUserID().equals(user.getUserID())) {
                // TODO: 25 October 2018 SET ROOM TYPE AND COLOR
                TAPUtils.getInstance().startChatActivity(
                        TAPNewChatActivity.this,
                        TAPChatManager.getInstance().arrangeRoomId(TAPChatManager.getInstance().getActiveUser().getUserID(), user.getUserID()),
                        user.getName(),
                        user.getAvatarURL(),
                        1,
                        /* TEMPORARY ROOM COLOR */TAPUtils.getInstance().getRandomColor(user.getName()) + "");
            }
        }

        @Override
        public void onMenuButtonTapped(int actionId) {
            switch (actionId) {
                case MENU_ID_ADD_NEW_CONTACT:
                    addNewContact();
                    break;
                case MENU_ID_SCAN_QR_CODE:
                    openQRScanner();
                    break;
                case MENU_ID_CREATE_NEW_GROUP:
                    createNewGroup();
                    break;
            }
        }

        @Override
        public void onInfoLabelButtonTapped(int actionId) {
            if (actionId == INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS) {
                viewBlockedContacts();
            }
        }
    };
}
