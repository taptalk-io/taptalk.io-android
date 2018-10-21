package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

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

        GlideApp.with(this).load(HpImageURL.BuilderDummy().getThumbnail()).centerCrop().into(civMyAvatar);
        GlideApp.with(this).load("https://cdn-images-1.medium.com/max/2000/1*bn2ci0duzDEfyVwlBjeM2Q.jpeg").centerCrop().into(civContactAvatar);

        civContactAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-291));
        civMyAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-30));
        cvResult.animate()
                .alpha(1f).withEndAction(
                        () -> {
                            civMyAvatar.animate().scaleY(0.8f).start();
                            civContactAvatar.animate().scaleY(0.8f).start();
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
