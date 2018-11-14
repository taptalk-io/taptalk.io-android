package io.taptalk.TapTalk.View.Activity;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.ViewModel.TAPScanResultViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ADDED_CONTACT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SCAN_RESULT;

public class TAPScanResultActivity extends TAPBaseActivity {

    private TAPScanResultViewModel mViewModel;

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

    private TAPUserModel addedContactUserModel;
    private String scanResult;
    private TAPUserModel myUserModel;
    private TAPUserModel contactModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_scan_result_activity);
        mViewModel = ViewModelProviders.of(this).get(TAPScanResultViewModel.class);
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
        myUserModel = TAPDataManager.getInstance().getActiveUser();

        if (null != addedContactUserModel) setUpFromNewContact();
        else if (null != scanResult) setUpFromScanQR();
    }

    private void setUpFromNewContact() {
        pbLoading.setVisibility(View.GONE);
        cvResult.setVisibility(View.VISIBLE);
        civMyUserAvatar.setFillColor(getResources().getColor(R.color.vibrantGreen));
        civTheirContactAvatar.setFillColor(getResources().getColor(R.color.brightBlue));
        Glide.with(this).load("https://img.uefa.com/imgml/uefacom/ucl/social/og-default.jpg")
                .apply(new RequestOptions().centerCrop()).into(civTheirContactAvatar);
        Glide.with(this).load("https://images.performgroup.com/di/library/GOAL/d5/f8/champions-league-2017-18-ball-adidas-finale_124lp0wu9rvqf1suvrvzmya9m8.jpg?t=1630593109")
                .apply(new RequestOptions().centerCrop()).into(civMyUserAvatar);
        //Glide.with(this).load(addedContactUserModel.getAvatarURL().getThumbnail())
        //    .apply(new RequestOptions().centerCrop()).into(civTheirContactAvatar);
        //Glide.with(this).load(myUserModel.getAvatarURL().getThumbnail())
        //        .apply(new RequestOptions().centerCrop()).into(civMyUserAvatar);
        tvContactFullname.setText(addedContactUserModel.getName());
        tvContactUsername.setText(addedContactUserModel.getUsername());
        animateAddSuccess(addedContactUserModel);
        llButton.setOnClickListener(v -> {
            TAPUtils.getInstance().startChatActivity(TAPScanResultActivity.this,
                TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), addedContactUserModel.getUserID()),
                addedContactUserModel.getName(),
                addedContactUserModel.getAvatarURL(), 1, "#2eccad");
            finish();
        });
    }

    private void setUpFromScanQR() {
        TAPDataManager.getInstance().getUserByIdFromApi(scanResult, getUserView);
    }

    private void validateScanResult(TAPUserModel userModel) {
        cvResult.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        contactModel = userModel;
        Glide.with(this).load("https://img.uefa.com/imgml/uefacom/ucl/social/og-default.jpg")
                .apply(new RequestOptions()).into(civTheirContactAvatar);
        Glide.with(this).load("https://images.performgroup.com/di/library/GOAL/d5/f8/champions-league-2017-18-ball-adidas-finale_124lp0wu9rvqf1suvrvzmya9m8.jpg?t=1630593109")
                .apply(new RequestOptions().centerCrop()).into(civMyUserAvatar);
        //Glide.with(this).load(addedContactUserModel.getAvatarURL().getThumbnail())
        //      .apply(new RequestOptions().centerCrop()).into(civTheirContactAvatar);
        //Glide.with(this).load(myUserModel.getAvatarURL().getThumbnail())
        //        .apply(new RequestOptions().centerCrop()).into(civMyUserAvatar);
        tvContactFullname.setText(userModel.getName());
        tvContactUsername.setText(userModel.getUsername());

        if (scanResult.equals(myUserModel.getUserID())) {
            viewThisIsYou();
        } else {
            checkIsInContactAndSetUpAnimation();
        }
    }

    private void checkIsInContactAndSetUpAnimation() {
        TAPDataManager.getInstance().checkUserInMyContacts(contactModel.getUserID(), new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onContactCheckFinished(int isContact) {
                if (isContact != 0) animateAlreadyContact();
                else handleWhenUserNotInContact();
            }
        });
    }

    private void handleWhenUserNotInContact() {
        llButton.setOnClickListener(v -> TAPDataManager.getInstance().addContactApi(contactModel.getUserID(), addContactView));
    }

    TapDefaultDataView<TAPCommonResponse> addContactView = new TapDefaultDataView<TAPCommonResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
            tvButtonTitle.setVisibility(View.GONE);
            ivButtonIcon.setVisibility(View.GONE);
            pbAddLoading.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(TAPCommonResponse response) {
            super.onSuccess(response);
            TAPDataManager.getInstance().insertMyContactToDatabase(contactModel.hpUserModelForAddToDB());
            tvButtonTitle.setVisibility(View.VISIBLE);
            ivButtonIcon.setVisibility(View.VISIBLE);
            pbAddLoading.setVisibility(View.GONE);
            animateAddSuccess(contactModel);
            llButton.setOnClickListener(v -> {
                TAPUtils.getInstance().startChatActivity(TAPScanResultActivity.this,
                    TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), contactModel.getUserID()),
                    contactModel.getName(), contactModel.getAvatarURL(),
                    1, "#2eccad");
                finish();
            });
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            tvButtonTitle.setVisibility(View.VISIBLE);
            ivButtonIcon.setVisibility(View.VISIBLE);
            pbAddLoading.setVisibility(View.GONE);
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setTitle("Error")
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle("OK")
                    .setPrimaryButtonListener(v -> {
                    }).show();
        }

        @Override
        public void onError(Throwable throwable) {
            super.onError(throwable);
            tvButtonTitle.setVisibility(View.VISIBLE);
            ivButtonIcon.setVisibility(View.VISIBLE);
            pbAddLoading.setVisibility(View.GONE);
            // TODO: 31/10/18 ini textnya masih dummy
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setTitle("Error")
                    .setMessage(getString(R.string.api_call_return_error))
                    .setPrimaryButtonTitle("OK")
                    .setPrimaryButtonListener(v -> {}).show();
        }
    };

    TapDefaultDataView<TAPGetUserResponse> getUserView = new TapDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void onSuccess(TAPGetUserResponse response) {
            super.onSuccess(response);
            validateScanResult(response.getUser());
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setTitle("Error")
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle("OK")
                    .setPrimaryButtonListener(v -> {
                        onBackPressed();
                    }).show();
        }

        @Override
        public void onError(Throwable throwable) {
            super.onError(throwable);
            // TODO: 31/10/18 ini textnya masih dummy
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setTitle("Error")
                    .setMessage(getString(R.string.api_call_return_error))
                    .setPrimaryButtonTitle("OK")
                    .setPrimaryButtonListener(v -> {
                        onBackPressed();
                    }).show();
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
            civMyUserAvatar.setTranslationX(TAPUtils.getInstance().dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
            llButton.setOnClickListener(v -> TAPUtils.getInstance().startChatActivity(TAPScanResultActivity.this,
                    TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), contactModel.getUserID()),
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
                            ivButtonIcon.setImageResource(R.drawable.tap_ic_chat_white);
                            tvButtonTitle.setText("Chat Now");
                            llButton.animate().alpha(1f).start();
                            civMyUserAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(TAPUtils.getInstance().dpToPx(-54)).withEndAction(
                                    () -> {
                                        civMyUserAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(TAPUtils.getInstance().dpToPx(-24)).start();
                                        civTheirContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(150).translationX(TAPUtils.getInstance().dpToPx(48)).withEndAction(
                                                () -> civTheirContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).setDuration(150).translationX(24).start()).start();
                                    }).start();
                        }).start();
                    }).start();
        });
    }

    public void animateAddSuccess(TAPUserModel contactModel) {
        runOnUiThread(() -> {
            tvAddSuccess.setText(Html.fromHtml(getResources().getString(R.string.you_have_added)
                    +" <b>"+contactModel.getName()+"</b> "
                    +getResources().getString(R.string.to_your_contacts)));
            civMyUserAvatar.setTranslationX(TAPUtils.getInstance().dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
            cvResult.animate()
                .alpha(1f).withEndAction(
                () -> {
                    llButton.animate().alpha(0f).start();
                    llTextUsername.animate().alpha(0f).withEndAction(() -> {
                        llTextUsername.setVisibility(View.GONE);
                        llAddSuccess.setVisibility(View.VISIBLE);
                        llButton.setVisibility(View.VISIBLE);
                        ivButtonIcon.setImageResource(R.drawable.tap_ic_chat_white);
                        tvButtonTitle.setText("Chat Now");
                        llButton.animate().alpha(1f).start();
                        civMyUserAvatar.animate().setInterpolator(new AccelerateInterpolator()).translationX(TAPUtils.getInstance().dpToPx(-54)).withEndAction(
                                () -> {
                                    civMyUserAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).translationX(TAPUtils.getInstance().dpToPx(-24)).start();
                                    civTheirContactAvatar.animate().setInterpolator(new DecelerateInterpolator()).setDuration(150).translationX(TAPUtils.getInstance().dpToPx(48)).withEndAction(
                                            () -> civTheirContactAvatar.animate().setInterpolator(new AccelerateInterpolator()).setDuration(150).translationX(24).start()).start();
                                }).start();
                    }).start();
                }).start();
        });

    }
}
