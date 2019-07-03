package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
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
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPMenuItem;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Adapter.TAPMediaListAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMenuButtonAdapter;
import io.taptalk.TapTalk.ViewModel.TAPProfileViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE;

public class TAPChatProfileActivity extends TAPBaseActivity {

    private static final String TAG = TAPChatProfileActivity.class.getSimpleName();

    private final int MENU_NOTIFICATION = 1;
    private final int MENU_ROOM_COLOR = 2;
    private final int MENU_BLOCK = 3;
    private final int MENU_CLEAR_CHAT = 4;
    private final int MENU_VIEW_MEMBERS = 5;
    private final int MENU_EXIT_GROUP = 6;

    private NestedScrollView nsvProfile;
    private LinearLayout llToolbarCollapsed, llReloadSharedMedia;
    private ImageView ivProfile, ivButtonBack, ivSharedMediaLoading, ivButtonEdit;
    private TextView tvFullName, tvCollapsedName, tvSharedMediaLabel;
    private View vGradient, vProfileSeparator;
    private RecyclerView rvMenuButtons, rvSharedMedia;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private TAPMenuButtonAdapter menuButtonAdapter;
    private TAPMediaListAdapter sharedMediaAdapter;
    private GridLayoutManager sharedMediaLayoutManager;
    private ViewTreeObserver.OnScrollChangedListener sharedMediaPagingScrollListener;

    private TAPProfileViewModel vm;

    private RequestManager glide;

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

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPProfileViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(ROOM));
        vm.getSharedMedias().clear();
    }

    private void initView() {
        nsvProfile = findViewById(R.id.nsv_profile);
        llToolbarCollapsed = findViewById(R.id.ll_toolbar_collapsed);
        llReloadSharedMedia = findViewById(R.id.ll_reload_shared_media);
        ivProfile = findViewById(R.id.iv_profile);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonEdit = findViewById(R.id.iv_button_edit);
        ivSharedMediaLoading = findViewById(R.id.iv_shared_media_loading);
        tvFullName = findViewById(R.id.tv_full_name);
        tvCollapsedName = findViewById(R.id.tv_collapsed_name);
        tvSharedMediaLabel = findViewById(R.id.tv_section_title);
        vGradient = findViewById(R.id.v_gradient);
        vProfileSeparator = findViewById(R.id.v_profile_separator);
        rvMenuButtons = findViewById(R.id.rv_menu_buttons);
        rvSharedMedia = findViewById(R.id.rv_shared_media);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);

        getWindow().setBackgroundDrawable(null);

        if (null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getFullsize().isEmpty()) {
            glide.load(vm.getRoom().getRoomImage().getFullsize())
                    .apply(new RequestOptions().placeholder(R.drawable.tap_bg_grey_e4))
                    .into(ivProfile);
        } else {
            ivProfile.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tapGrey9b)));
        }

        tvFullName.setText(vm.getRoom().getRoomName());
        tvCollapsedName.setText(vm.getRoom().getRoomName());

        // Set gradient for profile picture overlay
        vGradient.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                getResources().getColor(R.color.tapTransparentBlack40),
                getResources().getColor(R.color.tapTransparentBlack18),
                getResources().getColor(R.color.tapTransparentBlack),
                getResources().getColor(R.color.tapTransparentBlack40)}));

        // Initialize menus
        List<TAPMenuItem> menuItems = new ArrayList<>();
        TAPMenuItem menuNotification;
        if (vm.getRoom().isMuted()) {
            menuNotification = new TAPMenuItem(
                    MENU_NOTIFICATION,
                    R.drawable.tap_ic_notifications_grey,
                    R.color.tapIconChatProfileNotificationInactive,
                    R.style.tapChatProfileMenuLabelStyle,
                    true,
                    false,
                    getString(R.string.tap_notifications));
        } else {
            menuNotification = new TAPMenuItem(
                    MENU_NOTIFICATION,
                    R.drawable.tap_ic_notifications_pumpkin_orange,
                    R.color.tapIconChatProfileNotificationActive,
                    R.style.tapChatProfileMenuLabelStyle,
                    true,
                    true,
                    getString(R.string.tap_notifications));
        }
        TAPMenuItem menuRoomColor = new TAPMenuItem(
                MENU_ROOM_COLOR,
                R.drawable.tap_ic_color_grey,
                R.color.tapIconChatProfileConversationColor,
                R.style.tapChatProfileMenuLabelStyle,
                false,
                false,
                getString(R.string.tap_conversation_color));

        TAPMenuItem menuRoomSearchChat = new TAPMenuItem(
                MENU_ROOM_COLOR,
                R.drawable.tap_ic_search_grey,
                R.color.tapIconChatProfileSearchChat,
                R.style.tapChatProfileMenuLabelStyle,
                false,
                false,
                getString(R.string.tap_search_chat));

        // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
//        menuItems.add(menuNotification);
//        menuItems.add(menuRoomColor);
//        menuItems.add(menuRoomSearchChat);

        menuButtonAdapter = new TAPMenuButtonAdapter(menuItems, profileMenuInterface);
        rvMenuButtons.setAdapter(menuButtonAdapter);
        rvMenuButtons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // TODO: 24 October 2018 CHECK IF ROOM TYPE IS GROUP
        if (vm.getRoom().getRoomType() == 1) {
            // 1-1 Room
            TAPMenuItem menuBlock = new TAPMenuItem(
                    MENU_BLOCK,
                    R.drawable.tap_ic_block_grey,
                    R.color.tapIconChatProfileBlockUser,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_block_user));
            TAPMenuItem menuClearChat = new TAPMenuItem(
                    MENU_CLEAR_CHAT,
                    R.drawable.tap_ic_delete_red,
                    R.color.tapIconChatProfileClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_clear_chat));
            // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
//            menuItems.add(2, menuBlock);
//            menuItems.add(menuClearChat);
        } else {
            // Group
            TAPMenuItem menuViewMembers = new TAPMenuItem(
                    MENU_VIEW_MEMBERS,
                    R.drawable.tap_ic_members_grey,
                    R.color.tapIconGroupProfileViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_view_members));
            TAPMenuItem menuExitGroup = new TAPMenuItem(
                    MENU_EXIT_GROUP,
                    R.drawable.tap_ic_exit_red,
                    R.color.tapIconChatProfileClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_exit_group));
            // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
//            menuItems.add(2, menuViewMembers);
//            menuItems.add(menuExitGroup);
        }

        TAPUtils.getInstance().rotateAnimateInfinitely(TAPChatProfileActivity.this, ivSharedMediaLoading);
        tvSharedMediaLabel.setVisibility(View.GONE);
        new Thread(() -> TAPDataManager.getInstance().getRoomMedias(0L, vm.getRoom().getRoomID(), sharedMediaListener)).start();

        appBarLayout.addOnOffsetChangedListener(offsetChangedListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        if (vm.getRoom().getRoomType() == 1) {
            TAPDataManager.getInstance().getUserByIdFromApi(
                    TAPChatManager.getInstance().getOtherUserIdFromRoom(vm.getRoom().getRoomID()),
                    getUserView);
        }
    }

    private void setNotification(boolean isNotificationOn) {
        Log.e(TAG, "setNotification: " + isNotificationOn);
    }

    private void changeRoomColor() {
        Log.e(TAG, "changeRoomColor: ");
    }

    private void blockUser() {
        Log.e(TAG, "blockUser: ");
    }

    private void clearChat() {
        Log.e(TAG, "clearChat: ");
    }

    private void viewMembers() {
        Log.e(TAG, "viewMembers: ");
    }

    private void exitGroup() {
        Log.e(TAG, "exitGroup: ");
    }

    private void startVideoDownload(TAPMessageModel message) {
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm.setPendingDownloadMessage(message);
            ActivityCompat.requestPermissions(
                    TAPChatProfileActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE);
        } else {
            // Download file
            vm.setPendingDownloadMessage(null);
            TAPFileDownloadManager.getInstance().downloadFile(TAPChatProfileActivity.this, message);
        }
        runOnUiThread(() -> sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(message)));
    }

    private AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        private boolean isShowing;
        private int scrollRange = -1;
        private int nameTranslationY = TAPUtils.getInstance().dpToPx(8);
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
                vProfileSeparator.animate()
                        .alpha(1f)
                        .setDuration(DEFAULT_ANIMATION_TIME)
                        .start();
                getTransitionToExpand().cancel();
                getTransitionToCollapse().start();
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
                vProfileSeparator.animate()
                        .alpha(0f)
                        .setDuration(DEFAULT_ANIMATION_TIME)
                        .start();
                getTransitionToCollapse().cancel();
                getTransitionToExpand().start();
            }
        }

        private ValueAnimator getTransitionToCollapse() {
            if (null == transitionToCollapse) {
                transitionToCollapse = ValueAnimator.ofArgb(
                        getResources().getColor(R.color.tapIconTransparentBackgroundBackButton),
                        getResources().getColor(R.color.tapIconNavBarBackButton));
                transitionToCollapse.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToCollapse.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToCollapse;
        }

        private ValueAnimator getTransitionToExpand() {
            if (null == transitionToExpand) {
                transitionToExpand = ValueAnimator.ofArgb(
                        getResources().getColor(R.color.tapIconNavBarBackButton),
                        getResources().getColor(R.color.tapIconTransparentBackgroundBackButton));
                transitionToExpand.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToExpand.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToExpand;
        }
    };

    public interface ProfileMenuInterface {
        void onMenuClicked(TAPMenuItem menuItem);
    }

    private ProfileMenuInterface profileMenuInterface = menuItem -> {
        switch (menuItem.getMenuID()) {
            case MENU_NOTIFICATION:
                setNotification(menuItem.isChecked());
                break;
            case MENU_ROOM_COLOR:
                changeRoomColor();
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
                exitGroup();
                break;
        }
    };

    public interface MediaInterface {
        void onMediaClicked(TAPMessageModel item, ImageView ivThumbnail, boolean isMediaReady);

        void onCancelDownloadClicked(TAPMessageModel item);
    }

    private MediaInterface mediaInterface = new MediaInterface() {
        @Override
        public void onMediaClicked(TAPMessageModel item, ImageView ivThumbnail, boolean isMediaReady) {
            if (item.getType() == TYPE_IMAGE && isMediaReady) {
                // Preview image detail
                Intent intent = new Intent(TAPChatProfileActivity.this, TAPImageDetailPreviewActivity.class);
                intent.putExtra(MESSAGE, item);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        TAPChatProfileActivity.this,
                        ivThumbnail,
                        getString(R.string.tap_transition_view_image));
                startActivity(intent, options.toBundle());
            } else if (item.getType() == TYPE_IMAGE) {
                // Download image
                TAPFileDownloadManager.getInstance().downloadImage(TAPChatProfileActivity.this, item);
                sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(item));
            } else if (item.getType() == TYPE_VIDEO && isMediaReady && null != item.getData()) {
                Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), (String) item.getData().get(FILE_ID));
                if (null == videoUri) {
                    // Prompt download
                    String fileID = (String) item.getData().get(FILE_ID);
                    TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache(fileID);
                    sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(item));
                    new TapTalkDialog.Builder(TAPChatProfileActivity.this)
                            .setTitle(getString(R.string.tap_error_could_not_find_file))
                            .setMessage(getString(R.string.tap_error_redownload_file))
                            .setCancelable(true)
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setPrimaryButtonListener(v -> startVideoDownload(item))
                            .show();
                } else {
                    // Open video player
                    TAPUtils.getInstance().openVideoPreview(TAPChatProfileActivity.this, videoUri, item);
                }
            } else if (item.getType() == TYPE_VIDEO) {
                // Download video
                startVideoDownload(item);
            }
        }

        @Override
        public void onCancelDownloadClicked(TAPMessageModel item) {
            TAPFileDownloadManager.getInstance().cancelFileDownload(item.getLocalID());
            sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(item));
        }
    };

    private TAPDefaultDataView<TAPGetUserResponse> getUserView = new TAPDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void onSuccess(TAPGetUserResponse response) {
            glide.load(response.getUser().getAvatarURL().getFullsize())
                    .apply(new RequestOptions().placeholder(ivProfile.getDrawable()))
                    .into(ivProfile);
            String name = response.getUser().getName();
            tvFullName.setText(name);
            tvCollapsedName.setText(name);
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> sharedMediaListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            new Thread(() -> {
                Log.e(TAG, "onSelectFinished: " + entities.size());
                if (0 == entities.size() && 0 == vm.getSharedMedias().size()) {
                    // No shared media
                    Log.e(TAG, "onSelectFinished: No shared media");
                    vm.setFinishedLoadingSharedMedia(true);
                    runOnUiThread(() -> ivSharedMediaLoading.setVisibility(View.GONE));
                } else {
                    // Has shared media
                    int previousSize = vm.getSharedMedias().size();
                    if (0 == previousSize) {
                        // First load
                        Log.e(TAG, "onSelectFinished: First load");
                        runOnUiThread(() -> {
                            tvSharedMediaLabel.setText(getString(R.string.tap_shared_media));
                            sharedMediaAdapter = new TAPMediaListAdapter(vm.getSharedMedias(), mediaInterface, glide);
                            sharedMediaLayoutManager = new GridLayoutManager(TAPChatProfileActivity.this, 3) {
                                @Override
                                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                                    try {
                                        super.onLayoutChildren(recycler, state);
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            rvSharedMedia.setAdapter(sharedMediaAdapter);
                            rvSharedMedia.setLayoutManager(sharedMediaLayoutManager);
                            SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvSharedMedia.getItemAnimator();
                            if (null != messageAnimator) {
                                messageAnimator.setSupportsChangeAnimations(false);
                            }
                            if (MAX_ITEMS_PER_PAGE <= entities.size()) {
                                sharedMediaPagingScrollListener = () -> {
                                    // Get coordinates of view holder (last index - half of max item per load)
                                    View view = sharedMediaLayoutManager.findViewByPosition(sharedMediaAdapter.getItemCount() - (MAX_ITEMS_PER_PAGE / 2));
                                    if (null != view) {
                                        int[] location = new int[2];
                                        view.getLocationOnScreen(location);
                                        if (!vm.isFinishedLoadingSharedMedia() && location[1] < TAPUtils.getInstance().getScreenHeight()) {
                                            // Load more if view holder is visible
                                            if (!vm.isLoadingSharedMedia()) {
                                                vm.setLoadingSharedMedia(true);
                                                ivSharedMediaLoading.setVisibility(View.VISIBLE);
                                                new Thread(() -> TAPDataManager.getInstance().getRoomMedias(vm.getLastSharedMediaTimestamp(), vm.getRoom().getRoomID(), sharedMediaListener)).start();
                                            }
                                        }
                                    }
                                };
                                nsvProfile.getViewTreeObserver().addOnScrollChangedListener(sharedMediaPagingScrollListener);
                            }
                        });
                    }
                    if (MAX_ITEMS_PER_PAGE > entities.size()) {
                        // No more medias in database
                        // TODO: 10 May 2019 CALL API BEFORE?
                        Log.e(TAG, "onSelectFinished: No more medias in database");
                        vm.setFinishedLoadingSharedMedia(true);
                        runOnUiThread(() -> nsvProfile.getViewTreeObserver().removeOnScrollChangedListener(sharedMediaPagingScrollListener));
                    }
                    for (TAPMessageEntity entity : entities) {
                        vm.addSharedMedia(TAPChatManager.getInstance().convertToModel(entity));
                    }
                    vm.setLastSharedMediaTimestamp(vm.getSharedMedias().get(vm.getSharedMedias().size() - 1).getCreated());
                    vm.setLoadingSharedMedia(false);
                    runOnUiThread(() -> rvSharedMedia.post(() -> {
                        sharedMediaAdapter.notifyItemRangeInserted(previousSize, entities.size());
                        ivSharedMediaLoading.setVisibility(View.GONE);
                        if (0 == previousSize) {
                            tvSharedMediaLabel.setVisibility(View.VISIBLE);
                            rvSharedMedia.setVisibility(View.VISIBLE);
                        }
                    }));
                }
            }).start();
        }
    };

    private BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null == action) {
                return;
            }
            switch (action) {
                case DownloadProgressLoading:
                case DownloadFinish:
                case DownloadFailed:
                    if (null != sharedMediaAdapter) {
                        String localID = intent.getStringExtra(DownloadLocalID);
                        runOnUiThread(() -> sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(vm.getSharedMedia(localID))));
                    }
                    break;
            }
        }
    };
}
