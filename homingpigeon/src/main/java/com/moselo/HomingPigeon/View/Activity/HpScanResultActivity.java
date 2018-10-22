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

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.SCAN_RESULT;

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
    private ImageView ivButtonClose;
    private TextView tvButtonTitle;
    private TextView tvAlreadyContact;
    private TextView tvThisIsYou;

    private String scanResult;

    public static HpScanResultActivity newInstance() {
        return new HpScanResultActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_scan_result_activity);
        mViewModel = ViewModelProviders.of(this).get(HpScanResultViewModel.class);
        scanResult = "";
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
        ivButtonClose = findViewById(R.id.iv_button_close);
        tvButtonTitle = findViewById(R.id.tv_button_title);
        tvAlreadyContact = findViewById(R.id.tv_already_contact);
        tvThisIsYou = findViewById(R.id.tv_this_is_you);

        scanResult = getIntent().getStringExtra(SCAN_RESULT);

        GlideApp.with(this).load(HpImageURL.BuilderDummy().getThumbnail()).centerCrop().into(civMyAvatar);
        GlideApp.with(this).load("https://cdn-images-1.medium.com/max/2000/1*bn2ci0duzDEfyVwlBjeM2Q.jpeg").centerCrop().into(civContactAvatar);

        ivButtonClose.setOnClickListener(v -> onBackPressed());

        if ("old".equals(scanResult.toLowerCase()))
            animateAlreadyContact();
        else if ("me".equals(scanResult.toLowerCase()))
            viewThisIsYou();
        else
            llButton.setOnClickListener(v -> animateAddSuccess());
    }

    public void viewThisIsYou() {
        llButton.setVisibility(View.GONE);
        tvThisIsYou.setVisibility(View.VISIBLE);
    }

    public void animateAlreadyContact() {
        civContactAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-291));
        civMyAvatar.setTranslationX(0);
        cvResult.animate()
                .alpha(1f).withEndAction(
                () -> {
                    llButton.animate().alpha(0f).start();
                    llTextUsername.animate().alpha(0f).withEndAction(() -> {
                        llTextUsername.setVisibility(View.GONE);
                        tvAlreadyContact.setVisibility(View.VISIBLE);
                        llButton.setVisibility(View.VISIBLE);
                        ivButtonIcon.setImageResource(R.drawable.hp_ic_chat_white);
                        tvButtonTitle.setText("Chat Now");
                        llButton.animate().alpha(1f).start();
                        civContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(HpUtils.getInstance().dpToPx(-54)).withEndAction(
                                () -> {
                                    civContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(HpUtils.getInstance().dpToPx(-24)).start();
                                    civMyAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(150).translationX(HpUtils.getInstance().dpToPx(48)).withEndAction(
                                            () -> civMyAvatar.animate().setInterpolator(new AccelerateInterpolator()).setDuration(150).translationX(24).start()).start();
                                }).start();
                    }).start();
                }).start();
    }

    public void animateAddSuccess() {
        civContactAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-291));
        civMyAvatar.setTranslationX(0);
        cvResult.animate()
                .alpha(1f).withEndAction(
                () -> {
                    llButton.animate().alpha(0f).start();
                    llTextUsername.animate().alpha(0f).withEndAction(() -> {
                        llTextUsername.setVisibility(View.GONE);
                        llAddSuccess.setVisibility(View.VISIBLE);
                        llButton.setVisibility(View.VISIBLE);
                        ivButtonIcon.setImageResource(R.drawable.hp_ic_chat_white);
                        tvButtonTitle.setText("Chat Now");
                        llButton.animate().alpha(1f).start();
                        civContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(HpUtils.getInstance().dpToPx(-54)).withEndAction(
                                () -> {
                                    civContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(HpUtils.getInstance().dpToPx(-24)).start();
                                    civMyAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(150).translationX(HpUtils.getInstance().dpToPx(48)).withEndAction(
                                            () -> civMyAvatar.animate().setInterpolator(new AccelerateInterpolator()).setDuration(150).translationX(24).start()).start();
                                }).start();
                    }).start();
                }).start();
    }
}
