package io.taptalk.TapTalk.View.Activity;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMenuItem;
import io.taptalk.TapTalk.View.Adapter.TAPImageListAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMenuButtonAdapter;
import io.taptalk.TapTalk.ViewModel.TAPProfileViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;

public class TAPProfileActivity extends TAPBaseActivity {

    private static final String TAG = TAPProfileActivity.class.getSimpleName();

    private final int MENU_NOTIFICATION = 1;
    private final int MENU_ROOM_COLOR = 2;
    private final int MENU_BLOCK = 3;
    private final int MENU_CLEAR_CHAT = 4;
    private final int MENU_VIEW_MEMBERS = 5;
    private final int MENU_EXIT_GROUP = 6;

    private LinearLayout llToolbarCollapsed;
    private ImageView ivProfile, ivButtonBack;
    private TextView tvFullName, tvCollapsedName, tvSharedMediaLabel;
    private View vGradient, vProfileSeparator;
    private RecyclerView rvMenuButtons, rvSharedMedia;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private TAPMenuButtonAdapter menuButtonAdapter;
    private TAPImageListAdapter sharedMediaAdapter;

    private TAPProfileViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_profile);

        initViewModel();
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPProfileViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(ROOM));
    }

    private void initView() {
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
            Glide.with(this).load(vm.getRoom().getRoomImage().getFullsize()).into(ivProfile);
        } else {
            ivProfile.setBackgroundTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getRoom().getRoomName())));
        }

        tvFullName.setText(vm.getRoom().getRoomName());
        tvCollapsedName.setText(vm.getRoom().getRoomName());

        // Set gradient for profile picture overlay
        vGradient.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[] {
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
                    false,
                    false,
                    getString(R.string.tap_exit_group));
            menuItems.add(menuViewMembers);
            menuItems.add(menuExitGroup);
        }

        // TODO: 23 October 2018 GET SHARED MEDIA

        // Dummy media
        TAPImageURL dummyImage = vm.getRoom().getRoomImage();
        dummyImage = null == dummyImage ? new TAPImageURL() : dummyImage;
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        vm.getSharedMedias().add(dummyImage);
        // End dummy

        if (vm.getSharedMedias().size() > 0) {
            // Has shared media
            tvSharedMediaLabel.setText(getString(R.string.tap_shared_media));
            sharedMediaAdapter = new TAPImageListAdapter(vm.getSharedMedias());
            rvSharedMedia.setAdapter(sharedMediaAdapter);
            rvSharedMedia.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            tvSharedMediaLabel.setVisibility(View.GONE);
            rvSharedMedia.setVisibility(View.GONE);
        }

        appBarLayout.addOnOffsetChangedListener(offsetChangedListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
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
}
