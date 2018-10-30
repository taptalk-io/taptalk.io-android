package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moselo.HomingPigeon.API.DefaultSubscriber;
import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpCommonResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetUserResponse;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ViewModel.HpScanResultViewModel;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ADDED_CONTACT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.SCAN_RESULT;

public class HpScanResultActivity extends HpBaseActivity {

    private HpScanResultViewModel mViewModel;

    private CardView cvResult;
    private ProgressBar pbLoading;
    private ProgressBar pbAddLoading;
    private ConstraintLayout clContainer;
    private CircleImageView civMyUserAvatar;
    private CircleImageView civTheirContactAvatar;
    private LinearLayout llButton;
    private LinearLayout llTextUsername;
    private LinearLayout llAddSuccess;
    private ImageView ivButtonIcon;
    private ImageView ivButtonClose;
    private TextView tvButtonTitle;
    private TextView tvAlreadyContact;
    private TextView tvThisIsYou;
    private TextView tvContactUsername;
    private TextView tvContactFullname;
    private TextView tvAddSuccess;

    private HpUserModel addedContactUserModel;
    private String scanResult;
    private HpUserModel myUserModel;
    private HpUserModel contactModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_scan_result_activity);
        mViewModel = ViewModelProviders.of(this).get(HpScanResultViewModel.class);
        initView();
    }

    public void initView() {
        cvResult = findViewById(R.id.cv_result);
        pbLoading = findViewById(R.id.pb_loading);
        pbAddLoading = findViewById(R.id.pb_add_loading);
        clContainer = findViewById(R.id.cl_container);
        civMyUserAvatar = findViewById(R.id.civ_contact_avatar);
        civTheirContactAvatar = findViewById(R.id.civ_my_avatar);
        llButton = findViewById(R.id.ll_button);
        llTextUsername = findViewById(R.id.ll_text_username);
        llAddSuccess = findViewById(R.id.ll_add_success);
        ivButtonIcon = findViewById(R.id.iv_button_icon);
        ivButtonClose = findViewById(R.id.iv_button_close);
        tvButtonTitle = findViewById(R.id.tv_button_title);
        tvAlreadyContact = findViewById(R.id.tv_already_contact);
        tvThisIsYou = findViewById(R.id.tv_this_is_you);
        tvContactFullname = findViewById(R.id.tv_contact_fullname);
        tvContactUsername = findViewById(R.id.tv_contact_username);
        tvAddSuccess = findViewById(R.id.tv_add_success);

        ivButtonClose.setOnClickListener(v -> onBackPressed());

        addedContactUserModel = getIntent().getParcelableExtra(ADDED_CONTACT);
        scanResult = getIntent().getStringExtra(SCAN_RESULT);
        myUserModel = HpDataManager.getInstance().getActiveUser();

        if (null != addedContactUserModel) setUpFromNewContact();
        else if (null != scanResult) setUpFromScanQR();

//        if ("old".equals(scanResult.toLowerCase()))
//            animateAlreadyContact();
//        else if ("me".equals(scanResult.toLowerCase()))
//            viewThisIsYou();
//        else
//            llButton.setOnClickListener(v -> animateAddSuccess());
    }

    private void setUpFromNewContact() {
        pbLoading.setVisibility(View.GONE);
        cvResult.setVisibility(View.VISIBLE);
        civMyUserAvatar.setFillColor(getResources().getColor(R.color.vibrantGreen));
        civTheirContactAvatar.setFillColor(getResources().getColor(R.color.brightBlue));
        GlideApp.with(this).load("https://img.uefa.com/imgml/uefacom/ucl/social/og-default.jpg")
                .centerCrop().into(civTheirContactAvatar);
        GlideApp.with(this).load("https://images.performgroup.com/di/library/GOAL/d5/f8/champions-league-2017-18-ball-adidas-finale_124lp0wu9rvqf1suvrvzmya9m8.jpg?t=1630593109")
                .centerCrop().into(civMyUserAvatar);
        //GlideApp.with(this).load(addedContactUserModel.getAvatarURL().getThumbnail())
        //      .centerCrop().into(civTheirContactAvatar);
        //GlideApp.with(this).load(myUserModel.getAvatarURL().getThumbnail())
        //        .centerCrop().into(civMyUserAvatar);
        tvContactFullname.setText(addedContactUserModel.getName());
        tvContactUsername.setText(addedContactUserModel.getUsername());
        animateAddSuccess(addedContactUserModel);
        llButton.setOnClickListener(v -> {
            HpUtils.getInstance().startChatActivity(HpScanResultActivity.this,
                HpChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), addedContactUserModel.getUserID()),
                addedContactUserModel.getName(),
                addedContactUserModel.getAvatarURL(), 1, "#2eccad");
            finish();
        });
    }

    private void setUpFromScanQR() {
        HpDataManager.getInstance().getUserByIdFromApi(scanResult, getUserView);
    }

    private void validateScanResult(HpUserModel userModel) {
        cvResult.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        contactModel = userModel;
        GlideApp.with(this).load("https://img.uefa.com/imgml/uefacom/ucl/social/og-default.jpg")
                .centerCrop().into(civTheirContactAvatar);
        GlideApp.with(this).load("https://images.performgroup.com/di/library/GOAL/d5/f8/champions-league-2017-18-ball-adidas-finale_124lp0wu9rvqf1suvrvzmya9m8.jpg?t=1630593109")
                .centerCrop().into(civMyUserAvatar);
        //GlideApp.with(this).load(addedContactUserModel.getAvatarURL().getThumbnail())
        //      .centerCrop().into(civTheirContactAvatar);
        //GlideApp.with(this).load(myUserModel.getAvatarURL().getThumbnail())
        //        .centerCrop().into(civMyUserAvatar);
        tvContactFullname.setText(userModel.getName());
        tvContactUsername.setText(userModel.getUsername());

        if (scanResult.equals(myUserModel.getUserID())) {
            viewThisIsYou();
        } else {
            checkIsInContactAndSetUpAnimation();
        }
    }

    private void checkIsInContactAndSetUpAnimation() {
        HpDataManager.getInstance().checkUserInMyContacts(contactModel.getUserID(), new HpDatabaseListener<HpUserModel>() {
            @Override
            public void onContactCheckFinished(int isContact) {
                if (isContact != 0) animateAlreadyContact();
                else handleWhenUserNotInContact();
            }
        });
    }

    private void handleWhenUserNotInContact() {
        llButton.setOnClickListener(v -> HpDataManager.getInstance().addContactApi(contactModel.getUserID(), addContactView));
    }

    HpDefaultDataView<HpCommonResponse> addContactView = new HpDefaultDataView<HpCommonResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
            tvButtonTitle.setVisibility(View.GONE);
            ivButtonIcon.setVisibility(View.GONE);
            pbAddLoading.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(HpCommonResponse response) {
            super.onSuccess(response);
            HpDataManager.getInstance().insertMyContactToDatabase(contactModel);
            tvButtonTitle.setVisibility(View.VISIBLE);
            ivButtonIcon.setVisibility(View.VISIBLE);
            pbAddLoading.setVisibility(View.GONE);
            animateAddSuccess(contactModel);
            llButton.setOnClickListener(v -> {
                HpUtils.getInstance().startChatActivity(HpScanResultActivity.this,
                    HpChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), contactModel.getUserID()),
                    contactModel.getName(), contactModel.getAvatarURL(),
                    1, "#2eccad");
                finish();
            });
        }

        @Override
        public void onError(HpErrorModel error) {
            super.onError(error);
        }
    };

    HpDefaultDataView<HpGetUserResponse> getUserView = new HpDefaultDataView<HpGetUserResponse>() {
        @Override
        public void onSuccess(HpGetUserResponse response) {
            super.onSuccess(response);
            validateScanResult(response.getUser());
        }

        @Override
        public void onError(HpErrorModel error) {
            super.onError(error);
        }
    };

    public void viewThisIsYou() {
        runOnUiThread(() -> {
            llButton.setVisibility(View.GONE);
            tvThisIsYou.setVisibility(View.VISIBLE);
        });
    }

    public void animateAlreadyContact() {
        runOnUiThread(() -> {
            civMyUserAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
            llButton.setOnClickListener(v -> HpUtils.getInstance().startChatActivity(HpScanResultActivity.this,
                    HpChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), contactModel.getUserID()),
                    contactModel.getName(), contactModel.getAvatarURL(), 1, "#2eccad"));
            tvAlreadyContact.setText(Html.fromHtml("<b>"+contactModel.getName()+"</b> "
                    +getResources().getString(R.string.is_already_in_your_contacts)));
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
                            civMyUserAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(HpUtils.getInstance().dpToPx(-54)).withEndAction(
                                    () -> {
                                        civMyUserAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(HpUtils.getInstance().dpToPx(-24)).start();
                                        civTheirContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(150).translationX(HpUtils.getInstance().dpToPx(48)).withEndAction(
                                                () -> civTheirContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).setDuration(150).translationX(24).start()).start();
                                    }).start();
                        }).start();
                    }).start();
        });
    }

    public void animateAddSuccess(HpUserModel contactModel) {
        runOnUiThread(() -> {
            tvAddSuccess.setText(Html.fromHtml(getResources().getString(R.string.you_have_added)
                    +" <b>"+contactModel.getName()+"</b> "
                    +getResources().getString(R.string.to_your_contacts)));
            civMyUserAvatar.setTranslationX(HpUtils.getInstance().dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
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
                        civMyUserAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(HpUtils.getInstance().dpToPx(-54)).withEndAction(
                                () -> {
                                    civMyUserAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(HpUtils.getInstance().dpToPx(-24)).start();
                                    civTheirContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(150).translationX(HpUtils.getInstance().dpToPx(48)).withEndAction(
                                            () -> civTheirContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).setDuration(150).translationX(24).start()).start();
                                }).start();
                    }).start();
                }).start();
        });

    }
}
