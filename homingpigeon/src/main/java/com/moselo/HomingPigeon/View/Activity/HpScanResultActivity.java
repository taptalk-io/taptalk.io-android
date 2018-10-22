package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ViewModel.HpScanResultViewModel;

public class HpScanResultActivity extends HpBaseActivity {

    private HpScanResultViewModel mViewModel;

    private CardView cvResult;
    private ConstraintLayout clContainer;
    private CircleImageView civContactAvatar;
    private CircleImageView civMyAvatar;
    private LinearLayout llButton;
    private LinearLayout llTextUsername;
    private LinearLayout llAddSuccess;
    private ImageView ivButtonIcon;
    private TextView tvButtonTitle;
    private TextView tvAlreadyContact;

    public static HpScanResultActivity newInstance() {
        return new HpScanResultActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_scan_result_activity);
        mViewModel = ViewModelProviders.of(this).get(HpScanResultViewModel.class);
        initView();
    }

    public void initView() {
        cvResult = findViewById(R.id.cv_result);
        clContainer = findViewById(R.id.cl_container);
        civContactAvatar = findViewById(R.id.civ_contact_avatar);
        civMyAvatar = findViewById(R.id.civ_my_avatar);
        llButton = findViewById(R.id.ll_button);
        llTextUsername = findViewById(R.id.ll_text_username);
        llAddSuccess = findViewById(R.id.ll_add_success);
        ivButtonIcon = findViewById(R.id.iv_button_icon);
        tvButtonTitle = findViewById(R.id.tv_button_title);
        tvAlreadyContact = findViewById(R.id.tv_already_contact);

        GlideApp.with(this).load(HpImageURL.BuilderDummy().getThumbnail()).centerCrop().into(civMyAvatar);
        GlideApp.with(this).load("https://cdn-images-1.medium.com/max/2000/1*bn2ci0duzDEfyVwlBjeM2Q.jpeg").centerCrop().into(civContactAvatar);
        animateAlreadyContact();
//        animateAddSuccess();
    }

    public void animateAlreadyContact() {
        civContactAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-291));
        civMyAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-30));
        cvResult.animate()
                .alpha(1f).withEndAction(
                () -> {
                    civMyAvatar.animate().scaleY(0.8f).start();
                    civContactAvatar.animate().scaleY(0.8f).start();
                    llTextUsername.animate().alpha(0f).withEndAction(() -> {
                        llTextUsername.setVisibility(View.GONE);
                        tvAlreadyContact.setScaleY(0.8f);
                        tvAlreadyContact.setVisibility(View.VISIBLE);
                        tvAlreadyContact.animate().alpha(1f).start();
                    }).start();
                    tvButtonTitle.setScaleY(0.8f);
                    tvButtonTitle.setVisibility(View.VISIBLE);
                    tvButtonTitle.animate().alpha(1f).start();
                    ivButtonIcon.setScaleY(0.8f);
                    ivButtonIcon.setVisibility(View.VISIBLE);
                    ivButtonIcon.animate().alpha(1f).start();
                    llButton.setVisibility(View.VISIBLE);
                    llButton.animate().alpha(1f).start();
                    cvResult.animate().scaleY(1.25f).withEndAction(
                            () -> civContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(HpUtils.getInstance().dpToPx(-24)).withEndAction(
                                    () -> {
                                        civContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(HpUtils.getInstance().dpToPx(10)).start();
                                        civMyAvatar.animate().setDuration(100).translationX(HpUtils.getInstance().dpToPx(0)).withEndAction(
                                                () -> civMyAvatar.animate().setDuration(100).translationX(-50).start()).start();
                                    }).start()).start();
                }).start();
    }

    public void animateAddSuccess() {
        civContactAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-291));
        civMyAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-30));
        cvResult.animate()
                .alpha(1f).withEndAction(
                () -> {
                    civMyAvatar.animate().scaleY(0.8f).start();
                    civContactAvatar.animate().scaleY(0.8f).start();
                    llTextUsername.animate().alpha(0f).withEndAction(() -> {
                        llTextUsername.setVisibility(View.GONE);
                        llAddSuccess.setScaleY(0.8f);
                        llAddSuccess.setVisibility(View.VISIBLE);
                        llAddSuccess.animate().alpha(1f).start();
                    }).start();
                    tvButtonTitle.setScaleY(0.8f);
                    tvButtonTitle.setVisibility(View.VISIBLE);
                    tvButtonTitle.animate().alpha(1f).start();
                    ivButtonIcon.setScaleY(0.8f);
                    ivButtonIcon.setVisibility(View.VISIBLE);
                    ivButtonIcon.animate().alpha(1f).start();
                    llButton.setVisibility(View.VISIBLE);
                    llButton.animate().alpha(1f).start();
                    cvResult.animate().scaleY(1.25f).withEndAction(
                            () -> civContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(HpUtils.getInstance().dpToPx(-24)).withEndAction(
                                    () -> {
                                        civContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(HpUtils.getInstance().dpToPx(10)).start();
                                        civMyAvatar.animate().setDuration(100).translationX(HpUtils.getInstance().dpToPx(0)).withEndAction(
                                                () -> civMyAvatar.animate().setDuration(100).translationX(-50).start()).start();
                                    }).start()).start();
                }).start();
    }
}
