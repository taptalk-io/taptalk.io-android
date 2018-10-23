package com.moselo.HomingPigeon.View.Activity;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpImageListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpProfileViewModel;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DEFAULT_ANIMATION_TIME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ROOM;

public class HpProfileActivity extends HpBaseActivity {

    private ConstraintLayout clButtonNotifications;
    private LinearLayout llToolbarCollapsed, llButtonConversationColor, llButtonBlockUser, llButtonClearChat;
    private ImageView ivProfile, ivButtonBack, ivNotifications;
    private TextView tvFullName, tvCollapsedName, tvSharedMediaLabel;
    private View vGradient, vProfileSeparator;
    private Switch swNotifications;
    private RecyclerView rvProfile;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private HpImageListAdapter sharedMediaAdapter;

    private HpProfileViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_profile);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpProfileViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(K_ROOM));
    }

    @Override
    protected void initView() {
        clButtonNotifications = findViewById(R.id.cl_button_notifications);
        llToolbarCollapsed = findViewById(R.id.ll_toolbar_collapsed);
        llButtonConversationColor = findViewById(R.id.ll_button_conversation_color);
        llButtonBlockUser = findViewById(R.id.ll_button_block_user);
        llButtonClearChat = findViewById(R.id.ll_button_clear_chat);
        ivProfile = findViewById(R.id.iv_profile);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivNotifications = findViewById(R.id.iv_notifications);
        tvFullName = findViewById(R.id.tv_full_name);
        tvCollapsedName = findViewById(R.id.tv_collapsed_name);
        tvSharedMediaLabel = findViewById(R.id.tv_section_title);
        vGradient = findViewById(R.id.v_gradient);
        vProfileSeparator = findViewById(R.id.v_profile_separator);
        swNotifications = findViewById(R.id.sw_notifications);
        rvProfile = findViewById(R.id.rv_profile);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);

        getWindow().setBackgroundDrawable(null);

        if (null != vm.getRoom().getRoomImage()) {
            GlideApp.with(this).load(vm.getRoom().getRoomImage().getFullsize()).into(ivProfile);
        }

        tvFullName.setText(vm.getRoom().getRoomName());
        tvCollapsedName.setText(vm.getRoom().getRoomName());

        // Set gradient for profile picture overlay
        vGradient.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[] {
                        getResources().getColor(R.color.transparent_black_40),
                        getResources().getColor(R.color.transparent_black_18),
                        getResources().getColor(R.color.transparent_black),
                        getResources().getColor(R.color.transparent_black_40)}));

        swNotifications.setChecked(!vm.getRoom().isMuted());
        swNotifications.setOnCheckedChangeListener(notificationCheckListener);

        // TODO: 23 October 2018 GET SHARED MEDIA

        // Dummy media
        HpImageURL dummyImage = vm.getRoom().getRoomImage();
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
            tvSharedMediaLabel.setText(getString(R.string.shared_media));
            sharedMediaAdapter = new HpImageListAdapter(vm.getSharedMedias());
            rvProfile.setAdapter(sharedMediaAdapter);
            rvProfile.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            tvSharedMediaLabel.setVisibility(View.GONE);
            rvProfile.setVisibility(View.GONE);
        }

        appBarLayout.addOnOffsetChangedListener(offsetChangedListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        clButtonNotifications.setOnClickListener(v -> onNotificationClicked());
        llButtonConversationColor.setOnClickListener(v -> changeConversationColor());
        llButtonBlockUser.setOnClickListener(v -> blockUser());
        llButtonClearChat.setOnClickListener(v -> clearChatRoom());
    }

    private void onNotificationClicked() {
        swNotifications.setChecked(!swNotifications.isChecked());
    }

    private void changeConversationColor() {
        // TODO: 23 October 2018 CHANGE CONVERSATION COLOR
    }

    private void blockUser() {
        // TODO: 23 October 2018 BLOCK USER
    }

    private void clearChatRoom() {
        // TODO: 23 October 2018 DELETE MESSAGES FROM ROOM
    }

    private AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        private boolean isShowing;
        private int scrollRange = -1;
        private int nameTranslationY = HpUtils.getInstance().dpToPx(8);
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
                        getResources().getColor(R.color.white),
                        getResources().getColor(R.color.greenBlue));
                transitionToGreen.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToGreen.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToGreen;
        }

        private ValueAnimator getTransitionWhite() {
            if (null == transitionToWhite) {
                transitionToWhite = ValueAnimator.ofArgb(
                        getResources().getColor(R.color.greenBlue),
                        getResources().getColor(R.color.white));
                transitionToWhite.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToWhite.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToWhite;
        }
    };

    private CompoundButton.OnCheckedChangeListener notificationCheckListener = new CompoundButton.OnCheckedChangeListener() {

        private ValueAnimator transitionToGreen, transitionToGrey;

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            // TODO: 23 October 2018 TURN NOTIFICATIONS ON/OFF
            if (isChecked) {
                // Turn notifications ON
                getTransitionGrey().cancel();
                getTransitionGreen().start();
            } else {
                // Turn notifications OFF
                getTransitionGreen().cancel();
                getTransitionGrey().start();
            }
        }

        private ValueAnimator getTransitionGreen() {
            if (null == transitionToGreen) {
                transitionToGreen = ValueAnimator.ofArgb(
                        getResources().getColor(R.color.grey_9b),
                        getResources().getColor(R.color.greenBlue));
                transitionToGreen.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToGreen.addUpdateListener(valueAnimator -> ivNotifications.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToGreen;
        }

        private ValueAnimator getTransitionGrey() {
            if (null == transitionToGrey) {
                transitionToGrey = ValueAnimator.ofArgb(
                        getResources().getColor(R.color.greenBlue),
                        getResources().getColor(R.color.grey_9b));
                transitionToGrey.setDuration(DEFAULT_ANIMATION_TIME);
                transitionToGrey.addUpdateListener(valueAnimator -> ivNotifications.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }
            return transitionToGrey;
        }
    };
}
