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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
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
    private ConstraintLayout clContainer;
    private CircleImageView civMyUserAvatar, civTheirContactAvatar;
    private LinearLayout llButton, llTextUsername, llAddSuccess;
    private ImageView ivButtonIcon, ivButtonClose, ivLoading, ivAddLoading;
    private TextView tvButtonTitle, tvAlreadyContact, tvThisIsYou, tvContactUsername, tvContactFullName, tvAddSuccess;

    private TAPUserModel addedContactUserModel;
    private String scanResult;
    private TAPUserModel myUserModel;
    private TAPUserModel contactModel;

    private RequestManager glide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_scan_result_activity);
        mViewModel = ViewModelProviders.of(this).get(TAPScanResultViewModel.class);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_fade_out);
    }

    private void initView() {
        cvResult = findViewById(R.id.cv_result);
        ivLoading = findViewById(R.id.iv_loading);
        ivAddLoading = findViewById(R.id.iv_add_loading);
        clContainer = findViewById(R.id.cl_container);
        civMyUserAvatar = findViewById(R.id.civ_my_avatar);
        civTheirContactAvatar = findViewById(R.id.civ_contact_avatar);
        llButton = findViewById(R.id.ll_button);
        llTextUsername = findViewById(R.id.ll_text_username);
        llAddSuccess = findViewById(R.id.ll_add_success);
        ivButtonIcon = findViewById(R.id.iv_button_icon);
        ivButtonClose = findViewById(R.id.iv_button_close);
        tvButtonTitle = findViewById(R.id.tv_button_title);
        tvAlreadyContact = findViewById(R.id.tv_already_contact);
        tvThisIsYou = findViewById(R.id.tv_this_is_you);
        tvContactFullName = findViewById(R.id.tv_contact_username);
        tvContactUsername = findViewById(R.id.tv_contact_full_name);
        tvAddSuccess = findViewById(R.id.tv_add_success);

        glide = Glide.with(this);

        TAPUtils.getInstance().rotateAnimateInfinitely(this, ivLoading);

        ivButtonClose.setOnClickListener(v -> onBackPressed());

        addedContactUserModel = getIntent().getParcelableExtra(ADDED_CONTACT);
        scanResult = getIntent().getStringExtra(SCAN_RESULT);
        myUserModel = TAPChatManager.getInstance().getActiveUser();

        if (null != addedContactUserModel) {
            setUpFromNewContact();
        } else if (null != scanResult) {
            setUpFromScanQR();
        }
    }

    private void setUpFromNewContact() {
        ivLoading.clearAnimation();
        ivLoading.setVisibility(View.GONE);
        cvResult.setVisibility(View.VISIBLE);
        if (null != addedContactUserModel.getAvatarURL() && !addedContactUserModel.getAvatarURL().getThumbnail().isEmpty()) {
            glide.load(addedContactUserModel.getAvatarURL().getThumbnail()).into(civTheirContactAvatar);
        } else {
            civTheirContactAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
        }
        tvContactFullName.setText(addedContactUserModel.getName());
        tvContactUsername.setText(addedContactUserModel.getUsername());
        animateAddSuccess(addedContactUserModel);
        llButton.setOnClickListener(v -> {
            TAPUtils.getInstance().startChatActivity(TAPScanResultActivity.this,
                    TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), addedContactUserModel.getUserID()),
                    addedContactUserModel.getName(),
                    addedContactUserModel.getAvatarURL(),
                    1,
                    ""); // TODO: 20 May 2019 GET ROOM COLOR
            finish();
        });
    }

    private void setUpFromScanQR() {
        TAPDataManager.getInstance().getUserByIdFromApi(scanResult.replace("id:",""), getUserView);
    }

    private void validateScanResult(TAPUserModel userModel) {
        TAPContactManager.getInstance().updateUserDataMap(userModel);
        cvResult.setVisibility(View.VISIBLE);
        ivLoading.clearAnimation();
        ivLoading.setVisibility(View.GONE);
        contactModel = userModel;
        if (null != userModel.getAvatarURL() && !userModel.getAvatarURL().getThumbnail().isEmpty()) {
            glide.load(userModel.getAvatarURL().getThumbnail()).into(civTheirContactAvatar);
        } else {
            civTheirContactAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
        }
        tvContactFullName.setText(userModel.getName());
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
        runOnUiThread(() -> llButton.setOnClickListener(v -> TAPDataManager.getInstance().addContactApi(contactModel.getUserID(), addContactView)));
    }

    TapDefaultDataView<TAPCommonResponse> addContactView = new TapDefaultDataView<TAPCommonResponse>() {
        @Override
        public void startLoading() {
            tvButtonTitle.setVisibility(View.GONE);
            ivButtonIcon.setVisibility(View.GONE);
            ivAddLoading.setVisibility(View.VISIBLE);
            TAPUtils.getInstance().rotateAnimateInfinitely(TAPScanResultActivity.this, ivAddLoading);
        }

        @Override
        public void onSuccess(TAPCommonResponse response) {
            TAPUserModel newContact = contactModel.setUserAsContact();
            TAPDataManager.getInstance().insertMyContactToDatabase(newContact);
            TAPContactManager.getInstance().updateUserDataMap(newContact);
            tvButtonTitle.setVisibility(View.VISIBLE);
            ivButtonIcon.setVisibility(View.VISIBLE);
            ivAddLoading.clearAnimation();
            ivAddLoading.setVisibility(View.GONE);
            animateAddSuccess(contactModel);
            llButton.setOnClickListener(v -> {
                TAPUtils.getInstance().startChatActivity(
                        TAPScanResultActivity.this,
                        TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), contactModel.getUserID()),
                        contactModel.getName(),
                        contactModel.getAvatarURL(),
                        1,
                        ""); // TODO: 20 May 2019 GET ROOM COLOR
                finish();
            });
        }

        @Override
        public void onError(TAPErrorModel error) {
            tvButtonTitle.setVisibility(View.VISIBLE);
            ivButtonIcon.setVisibility(View.VISIBLE);
            ivAddLoading.clearAnimation();
            ivAddLoading.setVisibility(View.GONE);
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(v -> {
                    }).show();
        }

        @Override
        public void onError(Throwable throwable) {
            onError(throwable.getMessage());
        }
    };

    TapDefaultDataView<TAPGetUserResponse> getUserView = new TapDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void onSuccess(TAPGetUserResponse response) {
            validateScanResult(response.getUser());
        }

        @Override
        public void onError(TAPErrorModel error) {
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(v -> onBackPressed()).show();
        }

        @Override
        public void onError(Throwable throwable) {
            super.onError(throwable);
            // TODO: 31/10/18 ini textnya masih dummy
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_api_call_return_error))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(v -> onBackPressed()).show();
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
            llButton.setAlpha(0f);
            if (null != myUserModel.getAvatarURL() && !myUserModel.getAvatarURL().getThumbnail().isEmpty()) {
                glide.load(myUserModel.getAvatarURL().getThumbnail()).into(civMyUserAvatar);
            } else {
                civMyUserAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
            }
            civMyUserAvatar.setTranslationX(TAPUtils.getInstance().dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
            llButton.setOnClickListener(v -> {
                TAPUtils.getInstance().startChatActivity(
                        TAPScanResultActivity.this,
                        TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), contactModel.getUserID()),
                        contactModel.getName(),
                        contactModel.getAvatarURL(),
                        1,
                        ""); // TODO: 20 May 2019 GET ROOM COLOR
                finish();
            });
            tvAlreadyContact.setText(Html.fromHtml("<b>" + contactModel.getName() + "</b> "
                    + getResources().getString(R.string.tap_is_already_in_your_contacts)));
            cvResult.animate().alpha(1f).withEndAction(() -> {
                //llButton.animate().alpha(0f).start();
                llTextUsername.animate().alpha(0f).withEndAction(() -> {
                    llTextUsername.setVisibility(View.GONE);
                    tvAlreadyContact.setVisibility(View.VISIBLE);
                    llButton.setVisibility(View.VISIBLE);
                    ivButtonIcon.setImageResource(R.drawable.tap_ic_chat_white);
                    tvButtonTitle.setText(getString(R.string.tap_chat_now));
                    llButton.animate().alpha(1f).start();
                    civMyUserAvatar.animate()
                            .setInterpolator(new AccelerateInterpolator())
                            .translationX(TAPUtils.getInstance().dpToPx(-54))
                            .withEndAction(() -> {
                                civMyUserAvatar.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(100)
                                        .translationX(TAPUtils.getInstance().dpToPx(-24))
                                        .start();
                                civTheirContactAvatar.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(150)
                                        .translationX(TAPUtils.getInstance().dpToPx(48))
                                        .withEndAction(() ->
                                                civTheirContactAvatar.animate()
                                                        .setInterpolator(new AccelerateInterpolator())
                                                        .setDuration(150)
                                                        .translationX(TAPUtils.getInstance().dpToPx(24))
                                                        .start())
                                        .start();
                            }).start();
                }).start();
            }).start();
        });
    }

    public void animateAddSuccess(TAPUserModel contactModel) {
        runOnUiThread(() -> {
            tvAddSuccess.setText(Html.fromHtml(String.format(getString(R.string.you_have_added_to_your_contacts), contactModel.getName())));
            if (null != myUserModel.getAvatarURL() && !myUserModel.getAvatarURL().getThumbnail().isEmpty()) {
                glide.load(myUserModel.getAvatarURL().getThumbnail()).into(civMyUserAvatar);
            } else {
                civMyUserAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
            }
            civMyUserAvatar.setTranslationX(TAPUtils.getInstance().dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
            cvResult.animate().alpha(1f).withEndAction(() -> {
                llButton.animate().alpha(0f).start();
                llTextUsername.animate().alpha(0f).withEndAction(() -> {
                    llTextUsername.setVisibility(View.GONE);
                    llAddSuccess.setVisibility(View.VISIBLE);
                    llButton.setVisibility(View.VISIBLE);
                    ivButtonIcon.setImageResource(R.drawable.tap_ic_chat_white);
                    tvButtonTitle.setText(getString(R.string.tap_chat_now));
                    llButton.animate().alpha(1f).start();
                    civMyUserAvatar.animate()
                            .setInterpolator(new AccelerateInterpolator())
                            .translationX(TAPUtils.getInstance().dpToPx(-54))
                            .withEndAction(() -> {
                                civMyUserAvatar.animate().
                                        setInterpolator(new DecelerateInterpolator())
                                        .setDuration(100)
                                        .translationX(TAPUtils.getInstance().dpToPx(-24))
                                        .start();
                                civTheirContactAvatar.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(150)
                                        .translationX(TAPUtils.getInstance().dpToPx(48))
                                        .withEndAction(() ->
                                                civTheirContactAvatar.animate()
                                                        .setInterpolator(new AccelerateInterpolator())
                                                        .setDuration(150)
                                                        .translationX(TAPUtils.getInstance().dpToPx(24))
                                                        .start())
                                        .start();
                            }).start();
                }).start();
            }).start();
        });
    }
}
