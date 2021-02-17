package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Adapter.TapContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_CONTACT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SHORT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.INFO_LABEL_ID_ADD_NEW_CONTACT;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_ADD_NEW_CONTACT;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_CREATE_NEW_GROUP;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_SCAN_QR_CODE;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_DEFAULT_CONTACT_LIST;

public class TAPNewChatActivity extends TAPBaseActivity {

    private static final String TAG = TAPNewChatActivity.class.getSimpleName();
    private ConstraintLayout clActionBar;
    private LinearLayout llButtonSync, llConnectionStatus;
    private ImageView ivButtonClose, ivButtonSearch, ivButtonClearText, ivConnectionStatus;
    private TextView tvTitle, tvConnectionStatus;
    private EditText etSearch;
    private RecyclerView rvContactList;
    private FrameLayout flSyncStatus, flSync;

    private TapContactListAdapter adapter;
    private TAPContactListViewModel vm;

    public static void start(
            Context context,
            String instanceKey
    ) {
        Intent intent = new Intent(context, TAPNewChatActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }
    }

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
        if (etSearch.getVisibility() == View.VISIBLE) {
            showToolbar();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
        }
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

    private void initViewModel() {
        vm = new ViewModelProvider(this,
                new TAPContactListViewModel.TAPContactListViewModelFactory(
                        getApplication(), instanceKey))
                .get(TAPContactListViewModel.class);
        setupMenuButtons();
        // Set up listener for Live Data
        vm.getContactListLive().observe(this, userModels -> {
            if (null != userModels) {
                vm.getContactList().clear();
                vm.getContactList().addAll(userModels);
                vm.setSeparatedContactList(TAPUtils.generateContactListForRecycler(vm.getContactList(), TYPE_DEFAULT_CONTACT_LIST));
                startSearch();
            }
        });
    }

    private void initView() {
        clActionBar = findViewById(R.id.cl_action_bar);
        llButtonSync = findViewById(R.id.ll_btn_sync);
        llConnectionStatus = findViewById(R.id.ll_connection_status);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        ivButtonClearText = findViewById(R.id.iv_button_clear_text);
        ivConnectionStatus = findViewById(R.id.iv_connection_status);
        tvTitle = findViewById(R.id.tv_title);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);
        etSearch = findViewById(R.id.et_search);
        rvContactList = findViewById(R.id.rv_contact_list);
        flSyncStatus = findViewById(R.id.fl_sync_status);
        flSync = findViewById(R.id.fl_sync);

        etSearch.addTextChangedListener(searchTextWatcher);

        new Thread(() -> TAPDataManager.getInstance(instanceKey).getMyContactListFromAPI(getContactView)).start();

        getWindow().setBackgroundDrawable(null);

        if (TapUI.getInstance(instanceKey).isAddContactDisabled()) {
            ivButtonSearch.setVisibility(View.INVISIBLE);
        }

        adapter = new TapContactListAdapter(instanceKey, vm.getSeparatedContactList(), contactListListener);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);

        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> showSearchBar());
        ivButtonClearText.setOnClickListener(v -> etSearch.setText(""));
        llButtonSync.setOnClickListener(v -> permissionCheckAndGetContactListWhenSyncButtonClicked());

        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            TAPUtils.dismissKeyboard(TAPNewChatActivity.this);
            return false;
        });

        rvContactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.dismissKeyboard(TAPNewChatActivity.this);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llButtonSync.setBackground(getDrawable(R.drawable.tap_bg_button_active_ripple));
        }
    }

    private void setupMenuButtons() {
        if (!TapUI.getInstance(instanceKey).isAddContactDisabled() &&
                TapUI.getInstance(instanceKey).isNewContactMenuButtonVisible()) {
            TapContactListModel menuAddNewContact = new TapContactListModel(
                    MENU_ID_ADD_NEW_CONTACT,
                    getString(R.string.tap_new_contact),
                    R.drawable.tap_ic_new_contact_orange);
            vm.getMenuButtonList().add(menuAddNewContact);
        }
        if (!TapUI.getInstance(instanceKey).isAddContactDisabled() &&
                TapUI.getInstance(instanceKey).isScanQRMenuButtonVisible()) {
            TapContactListModel menuScanQRCode = new TapContactListModel(
                    MENU_ID_SCAN_QR_CODE,
                    getString(R.string.tap_scan_qr_code),
                    R.drawable.tap_ic_scan_qr_orange);
            vm.getMenuButtonList().add(menuScanQRCode);
        }
        if (TapUI.getInstance(instanceKey).isNewGroupMenuButtonVisible()) {
            TapContactListModel menuCreateNewGroup = new TapContactListModel(
                    MENU_ID_CREATE_NEW_GROUP,
                    getString(R.string.tap_new_group),
                    R.drawable.tap_ic_new_group_orange);
            vm.getMenuButtonList().add(menuCreateNewGroup);
        }
    }

    private void showToolbar() {
        TAPUtils.dismissKeyboard(this);
        ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_grey));
        tvTitle.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.GONE);
        etSearch.setText("");
        ivButtonSearch.setVisibility(View.VISIBLE);
        ((TransitionDrawable) clActionBar.getBackground()).reverseTransition(SHORT_ANIMATION_TIME);
    }

    private void showSearchBar() {
        ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white));
        tvTitle.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        ivButtonSearch.setVisibility(View.GONE);
        TAPUtils.showKeyboard(this, etSearch);
        ((TransitionDrawable) clActionBar.getBackground()).startTransition(SHORT_ANIMATION_TIME);
    }

    private void startSearch() {
        if (etSearch.getText().toString().equals(" ")) {
            // Clear keyword when EditText only contains a space
            etSearch.setText("");
            return;
        }
        String searchKeyword = etSearch.getText().toString().toLowerCase().trim();
        if (searchKeyword.isEmpty()) {
            showMenuButtonsAndContactList();
            ivButtonClearText.setVisibility(View.GONE);
        } else {
            showFilteredContacts(searchKeyword);
            ivButtonClearText.setVisibility(View.VISIBLE);
        }
    }

    private void showMenuButtonsAndContactList() {
        if (!vm.getAdapterItems().isEmpty()) {
            vm.getAdapterItems().clear();
        }
        vm.getAdapterItems().addAll(vm.getMenuButtonList());
        vm.getAdapterItems().addAll(vm.getSeparatedContactList());

        // TODO: 11 October 2019 TEMPORARILY DISABLED FEATURE
        // TODO: 29 October 2019 CHECK IF VIEW BLOCKED CONTACTS MENU IS VISIBLE IN TAP UI
        //setViewBlockedContactsInfoLabelItem();
        //vm.getAdapterItems().add(vm.getInfoLabelItem());

        runOnUiThread(() -> {
            if (null != adapter) {
                adapter.setItems(vm.getAdapterItems(), false);
            }
        });
    }

    private void showFilteredContacts(String searchKeyword) {
        vm.getAdapterItems().clear();
        for (TapContactListModel contact : vm.getSeparatedContactList()) {
            TAPUserModel user = contact.getUser();
            if (null != user && (user.getName().toLowerCase().contains(searchKeyword) ||
                    (null != user.getUsername() && user.getUsername().contains(searchKeyword)))) {
                vm.getAdapterItems().add(contact);
            }
        }

        if (TapUI.getInstance(instanceKey).isNewContactMenuButtonVisible()) {
            setAddNewContactInfoLabelItem();
            vm.getAdapterItems().add(vm.getInfoLabelItem());
        }

        if (null != adapter) {
            adapter.setItems(vm.getAdapterItems(), false);
        }
    }

    private void setViewBlockedContactsInfoLabelItem() {
        if (null == vm.getInfoLabelItem()) {
            vm.setInfoLabelItem(new TapContactListModel(
                    INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS,
                    getString(R.string.tap_cant_find_contact),
                    getString(R.string.tap_view_blocked_contacts)
            ));
        } else {
            vm.getInfoLabelItem().setActionId(INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS);
            vm.getInfoLabelItem().setTitle(getString(R.string.tap_cant_find_contact));
            vm.getInfoLabelItem().setButtonText(getString(R.string.tap_view_blocked_contacts));
        }
    }

    private void setAddNewContactInfoLabelItem() {
        if (null == vm.getInfoLabelItem()) {
            vm.setInfoLabelItem(new TapContactListModel(
                    INFO_LABEL_ID_ADD_NEW_CONTACT,
                    getString(R.string.tap_cant_find_person),
                    getString(R.string.tap_add_new_contact)
            ));
        } else {
            vm.getInfoLabelItem().setActionId(INFO_LABEL_ID_ADD_NEW_CONTACT);
            vm.getInfoLabelItem().setTitle(getString(R.string.tap_cant_find_person));
            vm.getInfoLabelItem().setButtonText(getString(R.string.tap_add_new_contact));
        }
    }

    private void permissionCheckAndSyncContactList() {
        if (!TapTalk.isAutoContactSyncEnabled(instanceKey)) {
            return;
        }
        if (!TAPContactManager.getInstance(instanceKey).isContactSyncPermissionAsked() &&
                !TAPUtils.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            showSyncContactPermissionDialog();
            TAPContactManager.getInstance(instanceKey).setAndSaveContactSyncPermissionAsked(true);
            TAPContactManager.getInstance(instanceKey).setAndSaveContactSyncAllowedByUser(false);
        } else if (!TAPUtils.hasPermissions(this, Manifest.permission.READ_CONTACTS) ||
                !TAPContactManager.getInstance(instanceKey).isContactSyncAllowedByUser()) {
            runOnUiThread(() -> flSync.setVisibility(View.VISIBLE));
        } else if (TAPContactManager.getInstance(instanceKey).isContactSyncAllowedByUser()) {
            syncContactList(false);
        }
    }

    private void permissionCheckAndGetContactListWhenSyncButtonClicked() {
        if (!TAPUtils.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            showSyncContactPermissionDialog();
        } else {
            syncContactList(true);
        }
    }

    private void showSyncContactPermissionDialog() {
        if (!isFinishing()) {
            runOnUiThread(() -> new TapTalkDialog.Builder(TAPNewChatActivity.this)
                    .setTitle(getString(R.string.tap_contact_access))
                    .setMessage(getString(R.string.tap_sync_contact_description))
                    .setCancelable(false)
                    .setPrimaryButtonTitle(getString(R.string.tap_allow))
                    .setPrimaryButtonListener(v -> ActivityCompat.requestPermissions(TAPNewChatActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACT))
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setSecondaryButtonListener(true, v -> flSync.setVisibility(View.VISIBLE))
                    .show());
        }
    }

    private void openQRScanner() {
        if (TAPUtils.hasPermissions(TAPNewChatActivity.this, Manifest.permission.CAMERA)) {
            TAPBarcodeScannerActivity.start(TAPNewChatActivity.this, instanceKey);
        } else {
            ActivityCompat.requestPermissions(TAPNewChatActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CAMERA);
        }
    }

    private void addNewContact() {
        TAPNewContactActivity.start(this, instanceKey);
    }

    private void createNewGroup() {
        TAPAddGroupMemberActivity.start(this, instanceKey);
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

    private void syncContactList(boolean showLoading) {
        if (!TapTalk.isAutoContactSyncEnabled(instanceKey)) {
            return;
        }
        if (showLoading) {
            showSyncLoading();
        }
        TAPContactManager.getInstance(instanceKey).setAndSaveContactSyncAllowedByUser(true);

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
                                String phoneNumb = TAPContactManager.getInstance(instanceKey).convertPhoneNumber(phoneNo);
                                if (!"".equals(phoneNumb) && !TAPContactManager.getInstance(instanceKey)
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
        TAPDataManager.getInstance(instanceKey).addContactByPhone(newContactsPhoneNumbers, new TAPDefaultDataView<TAPAddContactByPhoneResponse>() {
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
                                if (!contact.getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                                    contact.setUserAsContact();
                                    users.add(contact);
                                }
                            }
                            TAPContactManager.getInstance(instanceKey).saveContactListToDatabase(users);
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
            llConnectionStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_status_connected));
            if (0 == contactSynced) {
                tvConnectionStatus.setText(getString(R.string.tap_all_contacts_synced));
            } else {
                tvConnectionStatus.setText(String.format(getString(R.string.tap_format_d_synced_contacts), contactSynced));
            }
            ivConnectionStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_checklist_pumpkin));
            ivConnectionStatus.setPadding(0, 0, 0, 0);
            ivConnectionStatus.clearAnimation();
            flSyncStatus.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> flSyncStatus.setVisibility(View.GONE), 1000L);
        });
    }

    private void showSyncLoadingStatus() {
        int padding = TAPUtils.dpToPx(2);
        runOnUiThread(() -> {
            llConnectionStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_status_connecting));
            tvConnectionStatus.setText(getString(R.string.tap_syncing_contacts));
            ivConnectionStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            ivConnectionStatus.setPadding(padding, padding, padding, padding);
            TAPUtils.rotateAnimateInfinitely(this, ivConnectionStatus);
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
                        users.add(contact.getUser().setUserAsContact());
                    }
                    TAPContactManager.getInstance(instanceKey).saveContactListToDatabase(users);
                    permissionCheckAndSyncContactList();
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            startSearch();
            etSearch.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TapContactListListener contactListListener = new TapContactListListener() {
        @Override
        public void onContactTapped(TapContactListModel contact) {
            TAPUserModel user = contact.getUser();
            if (null == user) {
                return;
            }
            if (!TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(user.getUserID())) {
                // TODO: 25 October 2018 SET ROOM COLOR
                TapUIChatActivity.start(
                        TAPNewChatActivity.this,
                        instanceKey,
                        TAPChatManager.getInstance(instanceKey).arrangeRoomId(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(), user.getUserID()),
                        user.getName(),
                        user.getAvatarURL(),
                        TYPE_PERSONAL,
                        /* TEMPORARY ROOM COLOR */TAPUtils.getRandomColor(TAPNewChatActivity.this, user.getName()) + "");
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
            switch (actionId) {
                case INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS:
                    viewBlockedContacts();
                    break;
                case INFO_LABEL_ID_ADD_NEW_CONTACT:
                    addNewContact();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TAPDataManager.getInstance(instanceKey).unsubscribeContactListFromAPI();
    }
}
