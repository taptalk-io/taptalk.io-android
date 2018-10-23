package com.moselo.HomingPigeon.View.Activity;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ViewModel.HpProfileViewModel;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ROOM;

public class HpProfileActivity extends HpBaseActivity {

    LinearLayout llToolbarCollapsed;
    ImageView ivProfile, ivButtonBack;
    TextView tvFullName, tvCollapsedName;
    View vGradient;

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    RecyclerView rvProfile;
    GridLayoutManager glm;

//    TransitionDrawable backButtonTransitionDrawable;

    HpProfileViewModel vm;

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
        llToolbarCollapsed = findViewById(R.id.ll_toolbar_collapsed);
        ivProfile = findViewById(R.id.iv_profile);
        ivButtonBack = findViewById(R.id.iv_button_back);
        tvFullName = findViewById(R.id.tv_full_name);
        tvCollapsedName = findViewById(R.id.tv_collapsed_name);
        vGradient = findViewById(R.id.v_gradient);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);
//        rvProfile = findViewById(R.id.rv_profile);

        if (null != vm.getRoom().getRoomImage()) {
            GlideApp.with(this).load(vm.getRoom().getRoomImage().getFullsize()).into(ivProfile);
        }

        tvFullName.setText(vm.getRoom().getRoomName());
        tvCollapsedName.setText(vm.getRoom().getRoomName());

        // Set gradient for profile picture overlay
        vGradient.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(this, R.color.transparent_black_40),
                        ContextCompat.getColor(this, R.color.transparent_black_18),
                        ContextCompat.getColor(this, R.color.transparent_black),
                        ContextCompat.getColor(this, R.color.transparent_black_40)}));

        // Custom span count for recycler view
        glm = new GridLayoutManager(this, 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < 5) {
                    return 1;
                } else {
                    return 3;
                }
            }
        });

        collapsingToolbarLayout.setScrimVisibleHeightTrigger(0);
        appBarLayout.addOnOffsetChangedListener(offsetChangedListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
    }

    AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        boolean isShowing;
        int scrollRange = -1;
        int nameTranslationY = HpUtils.getInstance().dpToPx(8);
        final int animationDuration = 200;

        ValueAnimator transitionToGreen, transitionToWhite;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (scrollRange == -1) {
                // Initialize
                scrollRange = appBarLayout.getTotalScrollRange() - tvCollapsedName.getLayoutParams().height;
                transitionToGreen = ValueAnimator.ofArgb(
                        ivButtonBack.getSolidColor(),
                        getResources().getColor(R.color.greenBlue));
                transitionToGreen.setDuration(animationDuration);
                transitionToGreen.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                transitionToWhite = ValueAnimator.ofArgb(
                        ivButtonBack.getSolidColor(),
                        getResources().getColor(R.color.white));
                transitionToWhite.setDuration(animationDuration);
                transitionToWhite.addUpdateListener(valueAnimator -> ivButtonBack.setColorFilter(
                        (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
            }

            if (Math.abs(verticalOffset) >= scrollRange && !isShowing) {
                // Show Toolbar
                isShowing = true;
                llToolbarCollapsed.setVisibility(View.VISIBLE);
                llToolbarCollapsed.animate()
                        .alpha(1)
                        .setDuration(animationDuration)
                        .start();
                tvCollapsedName.setTranslationY(nameTranslationY);
                tvCollapsedName.animate()
                        .translationY(0)
                        .alpha(1f)
                        .start();
//                backButtonTransitionDrawable.startTransition(1000);
                transitionToWhite.cancel();
                transitionToGreen.start();
            } else if (Math.abs(verticalOffset) < scrollRange && isShowing) {
                // Hide Toolbar
                isShowing = false;
                llToolbarCollapsed.animate()
                        .alpha(0)
                        .setDuration(animationDuration)
                        .withEndAction(() -> llToolbarCollapsed.setVisibility(View.GONE))
                        .start();
                tvCollapsedName.animate()
                        .translationY(nameTranslationY)
                        .alpha(0f)
                        .start();
//                backButtonTransitionDrawable.startTransition(0);
//                backButtonTransitionDrawable.reverseTransition(1000);
                transitionToGreen.cancel();
                transitionToWhite.start();
            }
        }
    };
}
