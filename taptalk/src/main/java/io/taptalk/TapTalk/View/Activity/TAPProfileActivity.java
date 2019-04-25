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

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE;

public class TAPProfileActivity extends TAPBaseActivity {

    private static final String TAG = TAPProfileActivity.class.getSimpleName();

    private final int MENU_NOTIFICATION = 1;
    private final int MENU_ROOM_COLOR = 2;
    private final int MENU_BLOCK = 3;
    private final int MENU_CLEAR_CHAT = 4;
    private final int MENU_VIEW_MEMBERS = 5;
    private final int MENU_EXIT_GROUP = 6;

    private NestedScrollView nsvProfile;
    private LinearLayout llToolbarCollapsed;
    private ImageView ivProfile, ivButtonBack;
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
        setContentView(R.layout.tap_activity_profile);

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
    }

    private void initView() {
        nsvProfile = findViewById(R.id.nsv_profile);
        llToolbarCollapsed = findViewById(R.id.ll_toolbar_collapsed);
        ivProfile = findViewById(R.id.iv_profile);
        ivButtonBack = findViewById(R.id.iv_button_back);
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
            glide.load(vm.getRoom().getRoomImage().getFullsize()).into(ivProfile);
        } else {
            ivProfile.setBackgroundTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getRoom().getRoomName())));
        }

        tvFullName.setText(vm.getRoom().getRoomName());
        tvCollapsedName.setText(vm.getRoom().getRoomName());

        // Set gradient for profile picture overlay
        vGradient.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                getResources().getColor(R.color.tap_transparent_black_40),
                getResources().getColor(R.color.tap_transparent_black_18),
                getResources().getColor(R.color.tap_transparent_black),
                getResources().getColor(R.color.tap_transparent_black_40)}));

        // Initialize menus
        List<TAPMenuItem> menuItems = new ArrayList<>();
        TAPMenuItem menuNotification;
        if (vm.getRoom().isMuted()) {
            menuNotification = new TAPMenuItem(
                    MENU_NOTIFICATION,
                    R.drawable.tap_ic_notifications_grey,
                    true,
                    false,
                    getString(R.string.tap_notifications));
        } else {
            menuNotification = new TAPMenuItem(
                    MENU_NOTIFICATION,
                    R.drawable.tap_ic_notifications_green,
                    true,
                    true,
                    getString(R.string.tap_notifications));
        }
        TAPMenuItem menuRoomColor = new TAPMenuItem(
                MENU_ROOM_COLOR,
                R.drawable.tap_ic_color_grey,
                false,
                false,
                getString(R.string.tap_conversation_color));
        menuItems.add(menuNotification);
        menuItems.add(menuRoomColor);

        menuButtonAdapter = new TAPMenuButtonAdapter(menuItems, profileMenuInterface);
        rvMenuButtons.setAdapter(menuButtonAdapter);
        rvMenuButtons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // TODO: 24 October 2018 CHECK IF ROOM TYPE IS GROUP
        if (vm.getRoom().getRoomType() == 1) {
            // 1-1 Room
            TAPMenuItem menuBlock = new TAPMenuItem(
                    MENU_BLOCK,
                    R.drawable.tap_ic_block_grey,
                    false,
                    false,
                    getString(R.string.tap_block_user));
            TAPMenuItem menuClearChat = new TAPMenuItem(
                    MENU_CLEAR_CHAT,
                    R.drawable.tap_ic_delete_red,
                    R.color.tap_tomato,
                    false,
                    false,
                    getString(R.string.tap_clear_chat));
            menuItems.add(menuBlock);
            menuItems.add(menuClearChat);
        } else {
            // Group
            TAPMenuItem menuViewMembers = new TAPMenuItem(
                    MENU_VIEW_MEMBERS,
                    R.drawable.tap_ic_members_grey,
                    false,
                    false,
                    getString(R.string.tap_view_members));
            TAPMenuItem menuExitGroup = new TAPMenuItem(
                    MENU_EXIT_GROUP,
                    R.drawable.tap_ic_exit_red,
                    R.color.tap_tomato,
                    false,
                    false,
                    getString(R.string.tap_exit_group));
            menuItems.add(menuViewMembers);
            menuItems.add(menuExitGroup);
        }

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
                    TAPProfileActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE);
        } else {
            // Download file
            vm.setPendingDownloadMessage(null);
            TAPFileDownloadManager.getInstance().downloadFile(TAPProfileActivity.this, message);
        }
        sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(message));
    }

    private AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        private boolean isShowing;
        private int scrollRange = -1;
        private int nameTranslationY = TAPUtils.getInstance().dpToPx(8);
        private int scrimHeight;

        private ValueAnimator transitionToGreen, transitionToWhite;

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
                getTransitionWhite().cancel();
                getTransitionGreen().start();
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
                getTransitionGreen().cancel();
                getTransitionWhite().start();
            }
        }

        private ValueAnimator getTransitionGreen() {
            if (null == transitionToGreen) {
                transitionToGreen = ValueAnimator.ofArgb(
                        getResources().getColor(R.color.tap_white),
                        getResources().getColor(R.color.tap_greenBlue));
                transitionToGreen.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToGreen.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToGreen;
        }

        private ValueAnimator getTransitionWhite() {
            if (null == transitionToWhite) {
                transitionToWhite = ValueAnimator.ofArgb(
                        getResources().getColor(R.color.tap_greenBlue),
                        getResources().getColor(R.color.tap_white));
                transitionToWhite.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToWhite.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToWhite;
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
                Intent intent = new Intent(TAPProfileActivity.this, TAPImageDetailPreviewActivity.class);
                intent.putExtra(MESSAGE, item);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        TAPProfileActivity.this,
                        ivThumbnail,
                        getString(R.string.tap_transition_view_image));
                startActivity(intent, options.toBundle());
            } else if (item.getType() == TYPE_IMAGE) {
                // Download image
                TAPFileDownloadManager.getInstance().downloadImage(TAPProfileActivity.this, item);
                sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(item));
            } else if (item.getType() == TYPE_VIDEO && isMediaReady) {
                // Open video player
                if (null == item.getData()) {
                    return;
                }
                Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), (String) item.getData().get(FILE_ID));
                if (null == videoUri) {
                    new TapTalkDialog.Builder(TAPProfileActivity.this)
                            .setTitle(getString(R.string.tap_error_could_not_find_file))
                            .setMessage(getString(R.string.tap_error_redownload_file))
                            .setCancelable(true)
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setPrimaryButtonListener(v -> startVideoDownload(item))
                            .show();
                    return;
                }
                Intent intent = new Intent(TAPProfileActivity.this, TAPVideoPlayerActivity.class);
                intent.putExtra(URI, videoUri.toString());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
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

    private TapDefaultDataView<TAPGetUserResponse> getUserView = new TapDefaultDataView<TAPGetUserResponse>() {
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
            if (0 == entities.size() && 0 == vm.getSharedMedias().size()) {
                // No shared media
                vm.setFinishedLoadingSharedMedia(true);
                tvSharedMediaLabel.setVisibility(View.GONE);
                rvSharedMedia.setVisibility(View.GONE);
            } else {
                // Has shared media
                int previousSize = vm.getSharedMedias().size();
                if (0 == previousSize) {
                    // First load
                    tvSharedMediaLabel.setText(getString(R.string.tap_shared_media));
                    sharedMediaAdapter = new TAPMediaListAdapter(vm.getSharedMedias(), mediaInterface, glide);
                    sharedMediaLayoutManager = new GridLayoutManager(TAPProfileActivity.this, 3) {
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
                            int[] location = new int[2];
                            if (null != view) {
                                view.getLocationOnScreen(location);
                                if (!vm.isFinishedLoadingSharedMedia() && location[1] < TAPUtils.getInstance().getScreenHeight()) {
                                    // Load more if view holder is visible
                                    if (!vm.isLoadingSharedMedia()) {
                                        vm.setLoadingSharedMedia(true);
                                        new Thread(() -> TAPDataManager.getInstance().getRoomMedias(vm.getLastSharedMediaTimestamp(), vm.getRoom().getRoomID(), sharedMediaListener)).start();
                                    }
                                }
                            }
                        };
                        nsvProfile.getViewTreeObserver().addOnScrollChangedListener(sharedMediaPagingScrollListener);
                    }
                }
                if (MAX_ITEMS_PER_PAGE > entities.size()) {
                    // No more medias in database
                    vm.setFinishedLoadingSharedMedia(true);
                    nsvProfile.getViewTreeObserver().removeOnScrollChangedListener(sharedMediaPagingScrollListener);
                }
                for (TAPMessageEntity entity : entities) {
                    vm.addSharedMedia(TAPChatManager.getInstance().convertToModel(entity));
                }
                vm.setLastSharedMediaTimestamp(vm.getSharedMedias().get(vm.getSharedMedias().size() - 1).getCreated());
                vm.setLoadingSharedMedia(false);
                runOnUiThread(() -> rvSharedMedia.post(() -> sharedMediaAdapter.notifyItemRangeInserted(previousSize, entities.size())));
            }
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
                    String localID = intent.getStringExtra(DownloadLocalID);
                    runOnUiThread(() -> sharedMediaAdapter.notifyItemChanged(sharedMediaAdapter.getItems().indexOf(vm.getSharedMedia(localID))));
                    break;
            }
        }
    };
}
