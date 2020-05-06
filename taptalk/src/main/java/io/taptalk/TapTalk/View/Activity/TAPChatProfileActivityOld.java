package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Adapter.TapChatProfileAdapter;
import io.taptalk.TapTalk.ViewModel.TAPProfileViewModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_ADD_TO_CONTACTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_BLOCK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_CLEAR_CHAT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_DELETE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_DEMOTE_ADMIN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_EXIT_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_NOTIFICATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_PROMOTE_ADMIN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_REMOVE_MEMBER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_ROOM_COLOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_ROOM_SEARCH_CHAT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_SEND_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_VIEW_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.CLOSE_ACTIVITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_ADMIN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_OPEN_MEMBER_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_UPDATE_DATA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_GROUP_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_MEMBER_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_LOADING_LAYOUT;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_MEDIA_THUMBNAIL;

@Deprecated
public class TAPChatProfileActivityOld extends TAPBaseActivity {

    private static final String TAG = TAPChatProfileActivityOld.class.getSimpleName();

    private LinearLayout llToolbarCollapsed;
    private FrameLayout flLoading;
    private ImageView ivProfile, ivButtonBack, ivButtonEdit, ivSaving;
    private TextView tvFullName, tvCollapsedName, tvLoadingText;
    private View vGradient;
    private RecyclerView rvChatProfile;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private TapChatProfileAdapter adapter;
    private GridLayoutManager glm;
    private ViewTreeObserver.OnScrollChangedListener sharedMediaPagingScrollListener;

    private TAPProfileViewModel vm;

    private RequestManager glide;

    public static void start(
            Activity context,
            String instanceKey,
            TAPRoomModel room,
            @Nullable TAPUserModel user
    ) {
        start(context, instanceKey, room, user, null);
    }

    public static void start(
            Activity context,
            String instanceKey,
            TAPRoomModel room,
            @Nullable TAPUserModel user,
            @Nullable Boolean isAdmin
    ) {
        if (null != user && TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(user.getUserID())) {
            return;
        }
        Intent intent = new Intent(context, TAPChatProfileActivityOld.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(ROOM, room);
        if (null != isAdmin) {
            intent.putExtra(K_USER, user);
            intent.putExtra(IS_ADMIN, isAdmin);
            context.startActivityForResult(intent, GROUP_OPEN_MEMBER_PROFILE);
        } else if (room.getRoomType() == TYPE_PERSONAL) {
            context.startActivity(intent);
        } else if (room.getRoomType() == TYPE_GROUP && null != user) {
            intent.putExtra(K_USER, user);
            context.startActivityForResult(intent, OPEN_MEMBER_PROFILE);
        } else if (room.getRoomType() == TYPE_GROUP) {
            context.startActivityForResult(intent, OPEN_GROUP_PROFILE);
        }
        context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_chat_profile);

        glide = Glide.with(this);
        initViewModel();
        initView();
        TAPBroadcastManager.register(this, downloadProgressReceiver, DownloadProgressLoading, DownloadFinish, DownloadFailed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TAPBroadcastManager.unregister(this, downloadProgressReceiver);
    }

    @Override
    public void onBackPressed() {
        if (vm.isApiCallOnProgress()) {
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE:
                    startVideoDownload(vm.getPendingDownloadMessage());
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case GROUP_UPDATE_DATA:
                    vm.setGroupDataFromManager(TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(vm.getRoom().getRoomID()));
                    if (null == data) {
                        return;
                    }

                    if (null != data.getParcelableExtra(ROOM)) {
                        vm.setRoom(data.getParcelableExtra(ROOM));
                        updateView();
                    }

                    if (data.getBooleanExtra(CLOSE_ACTIVITY, false)) {
                        Intent intent = new Intent();
                        intent.putExtra(CLOSE_ACTIVITY, true);
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                    break;
            }
        }
    }

    private void initViewModel() {
        vm = new ViewModelProvider(this).get(TAPProfileViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(ROOM));
        if (null == vm.getRoom()) {
            finish();
        }
        vm.setGroupMemberUser(getIntent().getParcelableExtra(K_USER));
        if (null != vm.getGroupMemberUser()) {
            vm.setGroupMemberProfile(true);
            vm.setGroupAdmin(getIntent().getBooleanExtra(IS_ADMIN, false));
            vm.setUserDataFromManager(TAPContactManager.getInstance(instanceKey).getUserData(vm.getGroupMemberUser().getUserID()));
        } else if (vm.getRoom().getRoomType() == TYPE_PERSONAL) {
            vm.setUserDataFromManager(TAPContactManager.getInstance(instanceKey).getUserData(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(vm.getRoom().getRoomID())));
        } else if (vm.getRoom().getRoomType() == TYPE_GROUP) {
            vm.setGroupDataFromManager(TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(vm.getRoom().getRoomID()));
        }
        vm.getSharedMedias().clear();
    }

    private void initView() {
        llToolbarCollapsed = findViewById(R.id.ll_toolbar_collapsed);
        flLoading = findViewById(R.id.fl_loading);
        ivProfile = findViewById(R.id.iv_profile);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonEdit = findViewById(R.id.iv_button_edit);
        ivSaving = findViewById(R.id.iv_loading_image);
        tvFullName = findViewById(R.id.tv_full_name);
        tvCollapsedName = findViewById(R.id.tv_collapsed_name);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        vGradient = findViewById(R.id.v_gradient);
        rvChatProfile = findViewById(R.id.rv_chat_profile);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);

        getWindow().setBackgroundDrawable(null);

        updateView();

        // Set gradient for profile picture overlay
        vGradient.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                ContextCompat.getColor(this, R.color.tapTransparentBlack40),
                ContextCompat.getColor(this, R.color.tapTransparentBlack18),
                ContextCompat.getColor(this, R.color.tapTransparentBlack),
                ContextCompat.getColor(this, R.color.tapTransparentBlack40)}));

        if (!vm.isGroupMemberProfile()) {
            // Show loading on start
            vm.setLoadingItem(new TapChatProfileItemModel(TYPE_LOADING_LAYOUT));
            vm.getAdapterItems().add(vm.getLoadingItem());

            // Load shared medias
            new Thread(() -> TAPDataManager.getInstance(instanceKey).getRoomMedias(0L, vm.getRoom().getRoomID(), sharedMediaListener)).start();
        }

        // Setup recycler view
        adapter = new TapChatProfileAdapter(instanceKey, vm.getAdapterItems(), chatProfileInterface, glide);
        glm = new GridLayoutManager(this, 3) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemAt(position).getType() == TYPE_MEDIA_THUMBNAIL) {
                    return 1;
                } else {
                    return 3;
                }
            }
        });
        rvChatProfile.setAdapter(adapter);
        rvChatProfile.setLayoutManager(glm);
        SimpleItemAnimator recyclerAnimator = (SimpleItemAnimator) rvChatProfile.getItemAnimator();
        if (null != recyclerAnimator) {
            recyclerAnimator.setSupportsChangeAnimations(false);
        }

        appBarLayout.addOnOffsetChangedListener(offsetChangedListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonEdit.setOnClickListener(v -> openEditGroup());
        flLoading.setOnClickListener(v -> {
        });

        // Update room data
        if (vm.getRoom().getRoomType() == TYPE_PERSONAL) {
            TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(
                    TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(vm.getRoom().getRoomID()),
                    getUserView);
        } else if (vm.getRoom().getRoomType() == TYPE_GROUP) {
            TAPDataManager.getInstance(instanceKey).getChatRoomData(vm.getRoom().getRoomID(), getRoomView);
        }
    }

    private void updateView() {
        // Update room image
        if (null != vm.getUserDataFromManager() &&
                null != vm.getUserDataFromManager().getAvatarURL() &&
                !vm.getUserDataFromManager().getAvatarURL().getFullsize().isEmpty()) {
            // Load image from contact manager
            glide.load(vm.getUserDataFromManager().getAvatarURL().getFullsize())
                    .apply(new RequestOptions().placeholder(R.drawable.tap_bg_grey_e4))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            runOnUiThread(() -> showDefaultAvatar());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivProfile);
        } else if (null != vm.getGroupDataFromManager() &&
                null != vm.getGroupDataFromManager().getRoomImage() &&
                !vm.getGroupDataFromManager().getRoomImage().getFullsize().isEmpty()) {
            // Load image from group manager
            glide.load(vm.getGroupDataFromManager().getRoomImage().getFullsize())
                    .apply(new RequestOptions().placeholder(R.drawable.tap_bg_grey_e4))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            runOnUiThread(() -> showDefaultAvatar());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivProfile);
        } else if (vm.isGroupMemberProfile() && null != vm.getGroupMemberUser().getAvatarURL()) {
            // Load member image from intent
            glide.load(vm.getGroupMemberUser().getAvatarURL().getFullsize())
                    .apply(new RequestOptions().placeholder(R.drawable.tap_bg_grey_e4))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            runOnUiThread(() -> showDefaultAvatar());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivProfile);
        } else if (!vm.isGroupMemberProfile() &&
                null != vm.getRoom().getRoomImage() &&
                !vm.getRoom().getRoomImage().getFullsize().isEmpty()) {
            // Load room image from intent
            glide.load(vm.getRoom().getRoomImage().getFullsize())
                    .apply(new RequestOptions().placeholder(R.drawable.tap_bg_grey_e4))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            runOnUiThread(() -> showDefaultAvatar());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivProfile);
        } else {
            showDefaultAvatar();
        }

        // Update room name
        if (null != vm.getUserDataFromManager() &&
                !vm.getUserDataFromManager().getName().isEmpty()) {
            // Set name from contact manager
            tvFullName.setText(vm.getUserDataFromManager().getName());
            tvCollapsedName.setText(vm.getUserDataFromManager().getName());
        } else if (null != vm.getGroupDataFromManager() &&
                !vm.getGroupDataFromManager().getRoomName().isEmpty()) {
            // Set name from group manager
            tvFullName.setText(vm.getGroupDataFromManager().getRoomName());
            tvCollapsedName.setText(vm.getGroupDataFromManager().getRoomName());
        } else if (vm.isGroupMemberProfile()) {
            // Set name from passed member profile intent
            tvFullName.setText(vm.getGroupMemberUser().getName());
            tvCollapsedName.setText(vm.getGroupMemberUser().getName());
        } else {
            // Set name from passed room intent
            tvFullName.setText(vm.getRoom().getRoomName());
            tvCollapsedName.setText(vm.getRoom().getRoomName());
        }

        // Show / hide edit group button
        if (!vm.isGroupMemberProfile() && null != vm.getRoom() &&
                TYPE_GROUP == vm.getRoom().getRoomType() && null != vm.getRoom().getAdmins() &&
                vm.getRoom().getAdmins().contains(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
            ivButtonEdit.setVisibility(View.VISIBLE);
        } else {
            ivButtonEdit.setVisibility(View.GONE);
        }

        // Update room menu
        vm.getAdapterItems().removeAll(vm.getMenuItems());
        vm.setMenuItems(generateChatProfileMenu());
        vm.getAdapterItems().addAll(0, vm.getMenuItems());
        if (null != adapter) {
            adapter.setItems(vm.getAdapterItems());
            adapter.notifyDataSetChanged();
        }
    }

    private void showDefaultAvatar() {
        if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType()) {
            // Show default group avatar
            ivProfile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_img_default_group_avatar));
        } else {
            // Show default user avatar
            ivProfile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_img_default_avatar));
        }
    }

    private List<TapChatProfileItemModel> generateChatProfileMenu() {
        List<TapChatProfileItemModel> menuItems = new ArrayList<>();
        if (!vm.isGroupMemberProfile()) {
            // Notification
            TapChatProfileItemModel menuNotification = new TapChatProfileItemModel(
                    MENU_NOTIFICATION,
                    getString(R.string.tap_notifications),
                    R.drawable.tap_ic_notification_orange,
                    R.color.tapIconChatProfileMenuNotificationInactive,
                    R.style.tapChatProfileMenuLabelStyle);
            menuNotification.setChecked(!vm.getRoom().isMuted());

            // Room color
            TapChatProfileItemModel menuRoomColor = new TapChatProfileItemModel(
                    MENU_ROOM_COLOR,
                    getString(R.string.tap_conversation_color),
                    R.drawable.tap_ic_color_grey,
                    R.color.tapIconChatProfileMenuConversationColor,
                    R.style.tapChatProfileMenuLabelStyle);

            // Search chat
            TapChatProfileItemModel menuRoomSearchChat = new TapChatProfileItemModel(
                    MENU_ROOM_SEARCH_CHAT,
                    getString(R.string.tap_search_chat),
                    R.drawable.tap_ic_search_grey_small,
                    R.color.tapIconChatProfileMenuSearchChat,
                    R.style.tapChatProfileMenuLabelStyle);

            // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
//        menuItems.add(menuNotification);
//        menuItems.add(menuRoomColor);
//        menuItems.add(menuRoomSearchChat);

            if (vm.getRoom().getRoomType() == TYPE_PERSONAL) {
                // Personal chat room

                // Add to contacts
                TAPUserModel contact = TAPContactManager.getInstance(instanceKey).getUserData(
                        TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(vm.getRoom().getRoomID()));
                if (null == contact || null == contact.getIsContact() || contact.getIsContact() == 0) {
                    TapChatProfileItemModel menuAddToContact = new TapChatProfileItemModel(
                            MENU_ADD_TO_CONTACTS,
                            getString(R.string.tap_add_to_contacts),
                            R.drawable.tap_ic_add_circle_orange,
                            R.color.tapIconGroupMemberProfileMenuAddToContacts,
                            R.style.tapChatProfileMenuLabelStyle);
                    menuItems.add(menuAddToContact);
                }

                // Block user
                TapChatProfileItemModel menuBlock = new TapChatProfileItemModel(
                        MENU_BLOCK,
                        getString(R.string.tap_block_user),
                        R.drawable.tap_ic_block_red,
                        R.color.tapIconChatProfileMenuBlockUser,
                        R.style.tapChatProfileMenuLabelStyle);

                // Clear chat
                TapChatProfileItemModel menuClearChat = new TapChatProfileItemModel(
                        MENU_CLEAR_CHAT,
                        getString(R.string.tap_clear_chat),
                        R.drawable.tap_ic_delete_red,
                        R.color.tapIconChatProfileMenuClearChat,
                        R.style.tapChatProfileMenuDestructiveLabelStyle);

                // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
//            menuItems.add(2, menuBlock);
//            menuItems.add(menuClearChat);
            } else if (vm.getRoom().getRoomType() == TYPE_GROUP &&
                    null != vm.getRoom().getGroupParticipants() &&
                    1 < vm.getRoom().getGroupParticipants().size()) {
                // Group chat with more than 1 member

                // View members
                TapChatProfileItemModel menuViewMembers = new TapChatProfileItemModel(
                        MENU_VIEW_MEMBERS,
                        getString(R.string.tap_view_members),
                        R.drawable.tap_ic_members_orange,
                        R.color.tapIconGroupProfileMenuViewMembers,
                        R.style.tapChatProfileMenuLabelStyle);
                menuItems.add(menuViewMembers);

                // Exit group
                TapChatProfileItemModel menuExitGroup = new TapChatProfileItemModel(
                        MENU_EXIT_GROUP,
                        getString(R.string.tap_leave_group),
                        R.drawable.tap_ic_logout_red,
                        R.color.tapIconChatProfileMenuClearChat,
                        R.style.tapChatProfileMenuDestructiveLabelStyle);
                menuItems.add(menuExitGroup);
            } else if (vm.getRoom().getRoomType() == TYPE_GROUP &&
                    null != vm.getRoom().getAdmins() &&
                    vm.getRoom().getAdmins().contains(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                // Group chat where the active user is admin

                // View members
                TapChatProfileItemModel menuViewMembers = new TapChatProfileItemModel(
                        MENU_VIEW_MEMBERS,
                        getString(R.string.tap_view_members),
                        R.drawable.tap_ic_members_orange,
                        R.color.tapIconGroupProfileMenuViewMembers,
                        R.style.tapChatProfileMenuLabelStyle);
                menuItems.add(menuViewMembers);

                // Delete group
                TapChatProfileItemModel menuDeleteGroup = new TapChatProfileItemModel(
                        MENU_DELETE_GROUP,
                        getString(R.string.tap_delete_group),
                        R.drawable.tap_ic_delete_red,
                        R.color.tapIconChatProfileMenuClearChat,
                        R.style.tapChatProfileMenuDestructiveLabelStyle);
                menuItems.add(menuDeleteGroup);
            }
        } else {
            // Group chat member profile

            // Add to contacts
            TAPUserModel contact = TAPContactManager.getInstance(instanceKey).getUserData(vm.getGroupMemberUser().getUserID());
            if (null == contact || null == contact.getIsContact() || contact.getIsContact() == 0) {
                TapChatProfileItemModel menuAddToContact = new TapChatProfileItemModel(
                        MENU_ADD_TO_CONTACTS,
                        getString(R.string.tap_add_to_contacts),
                        R.drawable.tap_ic_add_circle_orange,
                        R.color.tapIconGroupMemberProfileMenuAddToContacts,
                        R.style.tapChatProfileMenuLabelStyle);
                menuItems.add(menuAddToContact);
            }

            // Send message
            TapChatProfileItemModel menuSendMessage = new TapChatProfileItemModel(
                    MENU_SEND_MESSAGE,
                    getString(R.string.tap_send_message),
                    R.drawable.tap_ic_send_message_orange,
                    R.color.tapIconGroupMemberProfileMenuSendMessage,
                    R.style.tapChatProfileMenuLabelStyle);
            menuItems.add(menuSendMessage);

            // Promote admin
            if (null != vm.getRoom().getAdmins() &&
                    vm.getRoom().getAdmins().contains(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                    !vm.getRoom().getAdmins().contains(vm.getGroupMemberUser().getUserID())) {
                TapChatProfileItemModel menuPromoteAdmin = new TapChatProfileItemModel(
                        MENU_PROMOTE_ADMIN,
                        getString(R.string.tap_promote_admin),
                        R.drawable.tap_ic_appoint_admin,
                        R.color.tapIconGroupMemberProfileMenuPromoteAdmin,
                        R.style.tapChatProfileMenuLabelStyle);
                menuItems.add(menuPromoteAdmin);
            }
            // Demote admin
            else if (null != vm.getRoom().getAdmins() &&
                    vm.getRoom().getAdmins().contains(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                TapChatProfileItemModel menuDemoteAdmin = new TapChatProfileItemModel(
                        MENU_DEMOTE_ADMIN,
                        getString(R.string.tap_demote_admin),
                        R.drawable.tap_ic_remove_circle_grey,
                        R.color.tapIconGroupMemberProfileMenuDemoteAdmin,
                        R.style.tapChatProfileMenuLabelStyle);
                menuItems.add(menuDemoteAdmin);
            }

            // Remove member
            if (null != vm.getRoom().getAdmins() &&
                    vm.getRoom().getAdmins().contains(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                TapChatProfileItemModel menuRemoveMember = new TapChatProfileItemModel(
                        MENU_REMOVE_MEMBER,
                        getString(R.string.tap_remove_group_member),
                        R.drawable.tap_ic_delete_red,
                        R.color.tapIconGroupMemberProfileMenuRemoveMember,
                        R.style.tapChatProfileMenuDestructiveLabelStyle);
                menuItems.add(menuRemoveMember);
            }
        }
        return menuItems;
    }

    private void toggleNotification(boolean isNotificationOn) {
        Log.e(TAG, "toggleNotification: " + isNotificationOn);
    }

    private void changeRoomColor() {
        Log.e(TAG, "changeRoomColor: ");
    }

    private void openEditGroup() {
        TAPEditGroupSubjectActivity.start(this, instanceKey, vm.getRoom());
    }

    private void searchChat() {
        Log.e(TAG, "searchChat: ");
    }

    private void blockUser() {
        Log.e(TAG, "blockUser: ");
    }

    private void clearChat() {
        Log.e(TAG, "clearChat: ");
    }

    private void viewMembers() {
        TAPGroupMemberListActivity.Companion.start(TAPChatProfileActivityOld.this, instanceKey, vm.getRoom());
    }

    private void showExitChatDialog() {
        new TapTalkDialog.Builder(this)
                .setTitle(this.getString(R.string.tap_clear_and_exit_chat))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setMessage(this.getString(R.string.tap_leave_group_confirmation))
                .setPrimaryButtonTitle(this.getString(R.string.tap_ok))
                .setPrimaryButtonListener(v -> {
                    vm.setLoadingStartText(getString(R.string.tap_loading));
                    vm.setLoadingEndText(getString(R.string.tap_left_group));
                    TAPDataManager.getInstance(instanceKey).leaveChatRoom(vm.getRoom().getRoomID(), deleteRoomView);
                })
                .setSecondaryButtonTitle(this.getString(R.string.tap_cancel))
                .setSecondaryButtonListener(v -> {
                })
                .show();
    }

    private void addToContacts() {
        if (vm.isGroupMemberProfile()) {
            TAPDataManager.getInstance(instanceKey).addContactApi(vm.getGroupMemberUser().getUserID(), addContactView);
        } else if (vm.getRoom().getRoomType() == TYPE_PERSONAL) {
            TAPDataManager.getInstance(instanceKey).addContactApi(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(vm.getRoom().getRoomID()), addContactView);
        }
    }

    private void openChatRoom(TAPUserModel userModel) {
        TapUIChatActivity.start(
                this,
                instanceKey,
                TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                        TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                        userModel.getUserID()),
                userModel.getName(),
                userModel.getAvatarURL(),
                TYPE_PERSONAL,
                ""); // TODO: 15 October 2019 SET ROOM COLOR
        Intent intent = new Intent();
        intent.putExtra(CLOSE_ACTIVITY, true);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    private void promoteAdmin() {
        vm.setLoadingStartText(getString(R.string.tap_updating));
        vm.setLoadingEndText(getString(R.string.tap_promoted_admin));
        TAPDataManager.getInstance(instanceKey).promoteGroupAdmins(vm.getRoom().getRoomID(),
                Arrays.asList(vm.getGroupMemberUser().getUserID()), userActionView);
    }

    private void showDemoteAdminDialog() {
        new TapTalkDialog.Builder(this)
                .setTitle(getString(R.string.tap_demote_admin))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setMessage(getString(R.string.tap_demote_admin_confirmation))
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener(v -> {
                    vm.setLoadingStartText(getString(R.string.tap_updating));
                    vm.setLoadingEndText(getString(R.string.tap_demoted_admin));
                    TAPDataManager.getInstance(instanceKey).demoteGroupAdmins(
                            vm.getRoom().getRoomID(),
                            Arrays.asList(vm.getGroupMemberUser().getUserID()),
                            userActionView);
                })
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setSecondaryButtonListener(view -> {
                })
                .show();
    }

    private void showRemoveMemberDialog() {
        new TapTalkDialog.Builder(this)
                .setTitle(getString(R.string.tap_remove_group_member))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setMessage(getString(R.string.tap_remove_member_confirmation))
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener(v -> {
                    vm.setLoadingStartText(getString(R.string.tap_removing));
                    vm.setLoadingEndText(getString(R.string.tap_removed_member));
                    TAPDataManager.getInstance(instanceKey).removeRoomParticipant(
                            vm.getRoom().getRoomID(),
                            Arrays.asList(vm.getGroupMemberUser().getUserID()),
                            userActionView);
                })
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setSecondaryButtonListener(view -> {
                })
                .show();
    }

    private void showDeleteChatRoomDialog() {
        new TapTalkDialog.Builder(this)
                .setTitle(this.getString(R.string.tap_delete_chat_room))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setMessage(this.getString(R.string.tap_delete_group_confirmation))
                .setPrimaryButtonTitle(this.getString(R.string.tap_ok))
                .setPrimaryButtonListener(v -> {
                    vm.setLoadingStartText(getString(R.string.tap_loading));
                    vm.setLoadingEndText(getString(R.string.tap_group_deleted));
                    TAPDataManager.getInstance(instanceKey).deleteChatRoom(vm.getRoom(), deleteRoomView);
                })
                .setSecondaryButtonTitle(this.getString(R.string.tap_cancel))
                .setSecondaryButtonListener(v -> {
                })
                .show();
    }

    private void startVideoDownload(TAPMessageModel message) {
        if (!TAPUtils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm.setPendingDownloadMessage(message);
            ActivityCompat.requestPermissions(
                    TAPChatProfileActivityOld.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE);
        } else {
            // Download file
            vm.setPendingDownloadMessage(null);
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message);
        }
        notifyItemChanged(message);
    }

    private void notifyItemChanged(TAPMessageModel mediaMessage) {
        runOnUiThread(() -> adapter.notifyItemChanged(vm.getMenuItems().size() + 1 + vm.getSharedMedias().indexOf(mediaMessage)));
    }

    private void showSharedMediaLoading() {
        if (vm.getAdapterItems().contains(vm.getLoadingItem())) {
            return;
        }
        vm.getAdapterItems().add(vm.getLoadingItem());
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    private void hideSharedMediaLoading() {
        if (!vm.getAdapterItems().contains(vm.getLoadingItem())) {
            return;
        }
        int index = vm.getAdapterItems().indexOf(vm.getLoadingItem());
        vm.getAdapterItems().remove(index);
        adapter.notifyItemRemoved(index);
    }

    private void showLoadingPopup(String message) {
        vm.setApiCallOnProgress(true);
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivSaving.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(this, ivSaving);
            }
            tvLoadingText.setText(message);
            flLoading.setVisibility(View.VISIBLE);
        });
    }

    private void hideLoadingPopup(String message) {
        //vm.setApiCallOnProgress(false);
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_checklist_pumpkin));
            ivSaving.clearAnimation();
            tvLoadingText.setText(message);
            flLoading.setOnClickListener(v -> hideLoadingPopup());

            new Handler().postDelayed(this::hideLoadingPopup, 1000L);
        });
    }

    private void hideLoadingPopup() {
        vm.setApiCallOnProgress(false);
        flLoading.setVisibility(View.GONE);
    }

    private void showErrorDialog(String title, String message) {
        new TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .show();
    }

    private AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        private boolean isShowing;
        private int scrollRange = -1;
        private int nameTranslationY = TAPUtils.dpToPx(8);
        private int scrimHeight;

        private ValueAnimator transitionToCollapse, transitionToExpand;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (scrollRange == -1) {
                // Initialize
                scrimHeight = llToolbarCollapsed.getLayoutParams().height * 3 / 2;
                scrollRange = appBarLayout.getTotalScrollRange() - scrimHeight;
                collapsingToolbarLayout.setScrimVisibleHeightTrigger(scrimHeight);
            }

            if (Math.abs(verticalOffset) >= scrollRange && !isShowing) {
                // Show Toolbar
                isShowing = true;
                llToolbarCollapsed.setVisibility(View.VISIBLE);
                llToolbarCollapsed.animate()
                        .alpha(1f)
                        .setDuration(DEFAULT_ANIMATION_TIME)
                        .start();
                tvCollapsedName.setTranslationY(nameTranslationY);
                tvCollapsedName.animate()
                        .translationY(0)
                        .alpha(1f)
                        .setDuration(DEFAULT_ANIMATION_TIME)
                        .start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getTransitionToExpand().cancel();
                    getTransitionToCollapse().start();
                } else {
                    ImageViewCompat.setImageTintList(ivButtonBack, ColorStateList.valueOf(ContextCompat.
                            getColor(TAPChatProfileActivityOld.this, R.color.tapIconNavigationBarBackButton)));
                    ImageViewCompat.setImageTintList(ivButtonEdit, ColorStateList.valueOf(ContextCompat.
                            getColor(TAPChatProfileActivityOld.this, R.color.tapIconNavigationBarBackButton)));
                }
            } else if (Math.abs(verticalOffset) < scrollRange && isShowing) {
                // Hide Toolbar
                isShowing = false;
                llToolbarCollapsed.animate()
                        .alpha(0f)
                        .setDuration(DEFAULT_ANIMATION_TIME)
                        .withEndAction(() -> llToolbarCollapsed.setVisibility(View.GONE))
                        .start();
                tvCollapsedName.animate()
                        .translationY(nameTranslationY)
                        .alpha(0f)
                        .setDuration(DEFAULT_ANIMATION_TIME)
                        .start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getTransitionToCollapse().cancel();
                    getTransitionToExpand().start();
                } else {
                    ImageViewCompat.setImageTintList(ivButtonBack, ColorStateList.valueOf(ContextCompat.
                            getColor(TAPChatProfileActivityOld.this, R.color.tapIconTransparentBackgroundBackButton)));
                    ImageViewCompat.setImageTintList(ivButtonEdit, ColorStateList.valueOf(ContextCompat.
                            getColor(TAPChatProfileActivityOld.this, R.color.tapIconTransparentBackgroundBackButton)));
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private ValueAnimator getTransitionToCollapse() {
            if (null == transitionToCollapse) {
                transitionToCollapse = ValueAnimator.ofArgb(
                        ContextCompat.getColor(TAPChatProfileActivityOld.this, R.color.tapIconTransparentBackgroundBackButton),
                        ContextCompat.getColor(TAPChatProfileActivityOld.this, R.color.tapIconNavigationBarBackButton));
                transitionToCollapse.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToCollapse.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                transitionToCollapse.addUpdateListener(valueAnimator -> ivButtonEdit.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToCollapse;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private ValueAnimator getTransitionToExpand() {
            if (null == transitionToExpand) {
                transitionToExpand = ValueAnimator.ofArgb(
                        ContextCompat.getColor(TAPChatProfileActivityOld.this, R.color.tapIconNavigationBarBackButton),
                        ContextCompat.getColor(TAPChatProfileActivityOld.this, R.color.tapIconTransparentBackgroundBackButton));
                transitionToExpand.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToExpand.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                transitionToExpand.addUpdateListener(valueAnimator -> ivButtonEdit.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToExpand;
        }
    };

    private TAPChatProfileActivity.ChatProfileInterface chatProfileInterface = new TAPChatProfileActivity.ChatProfileInterface() {
        @Override
        public void onMenuClicked(TapChatProfileItemModel item) {
            switch (item.getMenuId()) {
                case MENU_NOTIFICATION:
                    toggleNotification(item.isChecked());
                    break;
                case MENU_ROOM_COLOR:
                    changeRoomColor();
                    break;
                case MENU_ROOM_SEARCH_CHAT:
                    searchChat();
                    break;
                case MENU_BLOCK:
                    blockUser();
                    break;
                case MENU_CLEAR_CHAT:
                    clearChat();
                    break;
                case MENU_VIEW_MEMBERS:
                    viewMembers();
                    break;
                case MENU_EXIT_GROUP:
                    showExitChatDialog();
                    break;
                case MENU_ADD_TO_CONTACTS:
                    addToContacts();
                    break;
                case MENU_SEND_MESSAGE:
                    openChatRoom(vm.getGroupMemberUser());
                    break;
                case MENU_PROMOTE_ADMIN:
                    promoteAdmin();
                    break;
                case MENU_DEMOTE_ADMIN:
                    showDemoteAdminDialog();
                    break;
                case MENU_REMOVE_MEMBER:
                    showRemoveMemberDialog();
                    break;
                case MENU_DELETE_GROUP:
                    showDeleteChatRoomDialog();
                    break;
            }
        }

        @Override
        public void onMediaClicked(TAPMessageModel item, ImageView ivThumbnail, boolean isMediaReady) {
            if (item.getType() == TYPE_IMAGE && isMediaReady) {
                // Preview image detail
                TAPImageDetailPreviewActivity.start(TAPChatProfileActivityOld.this, instanceKey, item, ivThumbnail);
            } else if (item.getType() == TYPE_IMAGE) {
                // Download image
                TAPFileDownloadManager.getInstance(instanceKey).downloadImage(TAPChatProfileActivityOld.this, item);
                notifyItemChanged(item);
            } else if (item.getType() == TYPE_VIDEO && isMediaReady && null != item.getData()) {
                Uri videoUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(item.getRoom().getRoomID(), (String) item.getData().get(FILE_ID));
                if (null == videoUri) {
                    // Prompt download
                    String fileID = (String) item.getData().get(FILE_ID);
                    TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache(fileID);
                    notifyItemChanged(item);
                    new TapTalkDialog.Builder(TAPChatProfileActivityOld.this)
                            .setTitle(getString(R.string.tap_error_could_not_find_file))
                            .setMessage(getString(R.string.tap_error_redownload_file))
                            .setCancelable(true)
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setPrimaryButtonListener(v -> startVideoDownload(item))
                            .show();
                } else {
                    // Open video player
                    TAPVideoPlayerActivity.start(TAPChatProfileActivityOld.this, instanceKey, videoUri, item);
                }
            } else if (item.getType() == TYPE_VIDEO) {
                // Download video
                startVideoDownload(item);
            }
        }

        @Override
        public void onCancelDownloadClicked(TAPMessageModel item) {
            TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(item.getLocalID());
            notifyItemChanged(item);
        }

        @Override
        public void onReloadSharedMedia() {
            // TODO: 15 October 2019
        }
    };

    private TAPDefaultDataView<TAPCreateRoomResponse> getRoomView = new TAPDefaultDataView<TAPCreateRoomResponse>() {
        @Override
        public void onSuccess(TAPCreateRoomResponse response) {
            vm.setRoom(response.getRoom());
            vm.getRoom().setGroupParticipants(response.getParticipants());
            vm.getRoom().setAdmins(response.getAdmins());

            TAPGroupManager.Companion.getInstance(instanceKey).addGroupData(vm.getRoom());
            updateView();
        }
    };

    private TAPDefaultDataView<TAPGetUserResponse> getUserView = new TAPDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void onSuccess(TAPGetUserResponse response) {
            TAPUserModel user = response.getUser();
            TAPContactManager.getInstance(instanceKey).updateUserData(user);
            vm.getRoom().setRoomImage(user.getAvatarURL());
            vm.getRoom().setRoomName(user.getName());
            updateView();
        }
    };

    private TAPDefaultDataView<TAPCommonResponse> deleteRoomView = new TAPDefaultDataView<TAPCommonResponse>() {
        @Override
        public void startLoading() {
            showLoadingPopup(vm.getLoadingStartText());
        }

        @Override
        public void onSuccess(TAPCommonResponse response) {
            if (response.getSuccess()) {
                TAPOldDataManager.getInstance(instanceKey).cleanRoomPhysicalData(vm.getRoom().getRoomID(), new TAPDatabaseListener() {
                    @Override
                    public void onDeleteFinished() {
                        TAPDataManager.getInstance(instanceKey).deleteMessageByRoomId(vm.getRoom().getRoomID(), new TAPDatabaseListener() {
                            @Override
                            public void onDeleteFinished() {
                                //hideLoadingPopup(vm.getLoadingEndText());
                                TAPGroupManager.Companion.getInstance(instanceKey).removeGroupData(vm.getRoom().getRoomID());
                                TAPGroupManager.Companion.getInstance(instanceKey).setRefreshRoomList(true);
                                runOnUiThread(() -> {
                                    ivSaving.setImageDrawable(ContextCompat.getDrawable(TAPChatProfileActivityOld.this, R.drawable.tap_ic_checklist_pumpkin));
                                    ivSaving.clearAnimation();
                                    tvLoadingText.setText(vm.getLoadingEndText());
                                    new Handler().postDelayed(() -> {
                                        vm.setApiCallOnProgress(false);
                                        flLoading.setVisibility(View.GONE);
                                        setResult(RESULT_OK);
                                        finish();
                                        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
                                    }, 1000L);
                                });

                            }
                        });
                    }
                });
            } else {
                hideLoadingPopup();
                new TapTalkDialog.Builder(TAPChatProfileActivityOld.this)
                        .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                        .setTitle(getString(R.string.tap_failed))
                        .setMessage(null != response.getMessage() ? response.getMessage()
                                : getResources().getString(R.string.tap_error_message_general))
                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                        .show();
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            hideLoadingPopup();
            showErrorDialog(getString(R.string.tap_error), error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            hideLoadingPopup();
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general));
        }
    };

    private TAPDefaultDataView<TAPAddContactResponse> addContactView = new TAPDefaultDataView<TAPAddContactResponse>() {
        @Override
        public void startLoading() {
            showLoadingPopup(getString(R.string.tap_adding));
        }

        @Override
        public void onSuccess(TAPAddContactResponse response) {
            TAPUserModel newContact = response.getUser().setUserAsContact();
            //TAPDataManager.getInstance(instanceKey).insertMyContactToDatabase(new TAPDatabaseListener<TAPUserModel>() {
            //}, newContact);
            TAPContactManager.getInstance(instanceKey).updateUserData(newContact);
            hideLoadingPopup(getString(R.string.tap_added_contact));
            updateView();
        }

        @Override
        public void onError(TAPErrorModel error) {
            hideLoadingPopup();
            showErrorDialog(getString(R.string.tap_error), error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            hideLoadingPopup();
            showErrorDialog(getString(R.string.tap_error), errorMessage);
        }
    };

    private TAPDefaultDataView<TAPCreateRoomResponse> userActionView = new TAPDefaultDataView<TAPCreateRoomResponse>() {
        @Override
        public void startLoading() {
            showLoadingPopup(vm.getLoadingStartText());
        }

        @Override
        public void onSuccess(TAPCreateRoomResponse response) {
            vm.setRoom(response.getRoom());
            vm.getRoom().setGroupParticipants(response.getParticipants());
            vm.getRoom().setAdmins(response.getAdmins());

            TAPGroupManager.Companion.getInstance(instanceKey).addGroupData(vm.getRoom());

            hideLoadingPopup(vm.getLoadingEndText());

            new Handler().postDelayed(() -> {
                Intent intent = new Intent();
                intent.putExtra(ROOM, vm.getRoom());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
            }, 1000L);
        }

        @Override
        public void onError(TAPErrorModel error) {
            hideLoadingPopup();
            showErrorDialog(getString(R.string.tap_error), error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            hideLoadingPopup();
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general));
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> sharedMediaListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            new Thread(() -> {
                if (0 == entities.size() && 0 == vm.getSharedMedias().size()) {
                    // No shared media
                    vm.setFinishedLoadingSharedMedia(true);
                    runOnUiThread(() -> rvChatProfile.post(() -> hideSharedMediaLoading()));
                } else {
                    // Has shared media
                    int previousSize = vm.getSharedMedias().size();
                    if (0 == previousSize) {
                        // First load
                        vm.setSharedMediaSectionTitle(new TapChatProfileItemModel(getString(R.string.tap_shared_media)));
                        vm.getAdapterItems().add(vm.getSharedMediaSectionTitle());
                        runOnUiThread(() -> {
                            if (MAX_ITEMS_PER_PAGE <= entities.size()) {
                                sharedMediaPagingScrollListener = () -> {
                                    if (!vm.isFinishedLoadingSharedMedia() && glm.findLastVisibleItemPosition() > (vm.getMenuItems().size() + vm.getSharedMedias().size() - (MAX_ITEMS_PER_PAGE / 2))) {
                                        // Load more if view holder is visible
                                        if (!vm.isLoadingSharedMedia()) {
                                            vm.setLoadingSharedMedia(true);
                                            showSharedMediaLoading();
                                            new Thread(() -> TAPDataManager.getInstance(instanceKey).getRoomMedias(vm.getLastSharedMediaTimestamp(), vm.getRoom().getRoomID(), sharedMediaListener)).start();
                                        }
                                    }
                                };
                                rvChatProfile.getViewTreeObserver().addOnScrollChangedListener(sharedMediaPagingScrollListener);
                            }
                        });
                    }
                    if (MAX_ITEMS_PER_PAGE > entities.size()) {
                        // No more medias in database
                        // TODO: 10 May 2019 CALL API BEFORE?
                        vm.setFinishedLoadingSharedMedia(true);
                        runOnUiThread(() -> rvChatProfile.getViewTreeObserver().removeOnScrollChangedListener(sharedMediaPagingScrollListener));
                    }
                    for (TAPMessageEntity entity : entities) {
                        TAPMessageModel mediaMessage = TAPChatManager.getInstance(instanceKey).convertToModel(entity);
                        vm.addSharedMedia(mediaMessage);
                        vm.getAdapterItems().add(new TapChatProfileItemModel(mediaMessage));
                    }
                    vm.setLastSharedMediaTimestamp(vm.getSharedMedias().get(vm.getSharedMedias().size() - 1).getCreated());
                    vm.setLoadingSharedMedia(false);
                    runOnUiThread(() -> rvChatProfile.post(() -> {
                        hideSharedMediaLoading();
                        rvChatProfile.post(() -> adapter.notifyItemRangeInserted(vm.getMenuItems().size() + 1 + previousSize, entities.size()));
                    }));
                }
            }).start();
        }
    };

    private BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String localID = intent.getStringExtra(DownloadLocalID);
            if (null == action || null == localID) {
                return;
            }
            switch (action) {
                case DownloadProgressLoading:
                case DownloadFinish:
                case DownloadFailed:
                    runOnUiThread(() -> notifyItemChanged(vm.getSharedMedia(localID)));
                    break;
            }
        }
    };
}
