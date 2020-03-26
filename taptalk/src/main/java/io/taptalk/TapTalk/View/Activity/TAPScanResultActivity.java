package io.taptalk.TapTalk.View.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.ViewModel.TAPScanResultViewModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ADDED_CONTACT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SCAN_RESULT;

public class TAPScanResultActivity extends TAPBaseActivity {

    private TAPScanResultViewModel vm;

    private CardView cvResult;
    private ConstraintLayout clContainer;
    private FrameLayout flButtonClose;
    private CircleImageView civMyUserAvatar, civTheirContactAvatar;
    private LinearLayout llButton, llTextUsername, llAddSuccess;
    private ImageView ivButtonIcon, ivLoading, ivAddLoading;
    private TextView tvButtonTitle, tvMyAvatarLabel, tvContactAvatarLabel, tvAlreadyContact,
            tvThisIsYou, tvContactUsername, tvContactFullName, tvAddSuccess;

    private RequestManager glide;

    public static void start(
            Context context,
            String instanceKey,
            TAPUserModel contact
    ) {
        Intent intent = new Intent(context, TAPScanResultActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(ADDED_CONTACT, contact);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
        }
    }

    public static void start(
            Context context,
            String instanceKey,
            String textValue
    ) {
        Intent intent = new Intent(context, TAPScanResultActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(SCAN_RESULT, textValue);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_scan_result);
        vm = ViewModelProviders.of(this).get(TAPScanResultViewModel.class);
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
        flButtonClose = findViewById(R.id.fl_button_close);
        civMyUserAvatar = findViewById(R.id.civ_my_avatar);
        civTheirContactAvatar = findViewById(R.id.civ_contact_avatar);
        llButton = findViewById(R.id.ll_button);
        llTextUsername = findViewById(R.id.ll_text_username);
        llAddSuccess = findViewById(R.id.ll_add_success);
        ivButtonIcon = findViewById(R.id.iv_button_icon);
        tvButtonTitle = findViewById(R.id.tv_button_title);
        tvMyAvatarLabel = findViewById(R.id.tv_my_avatar_label);
        tvContactAvatarLabel = findViewById(R.id.tv_contact_avatar_label);
        tvAlreadyContact = findViewById(R.id.tv_already_contact);
        tvThisIsYou = findViewById(R.id.tv_this_is_you);
        tvContactFullName = findViewById(R.id.tv_contact_username);
        tvContactUsername = findViewById(R.id.tv_contact_full_name);
        tvAddSuccess = findViewById(R.id.tv_add_success);

        glide = Glide.with(this);

        TAPUtils.rotateAnimateInfinitely(this, ivLoading);

        flButtonClose.setOnClickListener(v -> onBackPressed());

        vm.setAddedContactUserModel(getIntent().getParcelableExtra(ADDED_CONTACT));
        vm.setScanResult(getIntent().getStringExtra(SCAN_RESULT));
        vm.setMyUserModel(TAPChatManager.getInstance().getActiveUser());

        if (null != vm.getAddedContactUserModel()) {
            setUpFromNewContact();
        } else if (null != vm.getScanResult()) {
            vm.setScanResult(vm.getScanResult().replace("id:", ""));
            setUpFromScanQR();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llButton.setBackground(getDrawable(R.drawable.tap_bg_scan_result_button_ripple));
        }
    }

    private void setUpFromNewContact() {
        ivLoading.clearAnimation();
        ivLoading.setVisibility(View.GONE);
        cvResult.setVisibility(View.VISIBLE);

        loadProfilePicture(civTheirContactAvatar, tvContactAvatarLabel, vm.getAddedContactUserModel());

        tvContactFullName.setText(vm.getAddedContactUserModel().getName());
        tvContactUsername.setText(vm.getAddedContactUserModel().getUsername());

        animateAddSuccess(vm.getAddedContactUserModel());

        llButton.setOnClickListener(v -> {
            TapUIChatActivity.start(TAPScanResultActivity.this,
                    instanceKey,
                    TAPChatManager.getInstance().arrangeRoomId(vm.getMyUserModel().getUserID(), vm.getAddedContactUserModel().getUserID()),
                    vm.getAddedContactUserModel().getName(),
                    vm.getAddedContactUserModel().getAvatarURL(),
                    1,
                    ""); // TODO: 20 May 2019 GET ROOM COLOR
            finish();
        });
    }

    private void setUpFromScanQR() {
        TAPDataManager.getInstance().getUserByIdFromApi(vm.getScanResult(), getUserView);
    }

    private void validateScanResult(TAPUserModel userModel) {
        TAPContactManager.getInstance().updateUserData(userModel);
        cvResult.setVisibility(View.VISIBLE);
        ivLoading.clearAnimation();
        ivLoading.setVisibility(View.GONE);
        vm.setContactModel(userModel);

        loadProfilePicture(civTheirContactAvatar, tvContactAvatarLabel, userModel);

        tvContactFullName.setText(userModel.getName());
        tvContactUsername.setText(userModel.getUsername());

        if (vm.getScanResult().equals(vm.getMyUserModel().getUserID())) {
            viewThisIsYou();
        } else {
            checkIsInContactAndSetUpAnimation();
        }
    }

    private void checkIsInContactAndSetUpAnimation() {
        TAPDataManager.getInstance().checkUserInMyContacts(vm.getContactModel().getUserID(), new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onContactCheckFinished(int isContact) {
                if (isContact != 0) animateAlreadyContact();
                else handleWhenUserNotInContact();
            }
        });
    }

    private void handleWhenUserNotInContact() {
        runOnUiThread(() -> llButton.setOnClickListener(v -> TAPDataManager.getInstance().addContactApi(vm.getContactModel().getUserID(), addContactView)));
    }

    TAPDefaultDataView<TAPAddContactResponse> addContactView = new TAPDefaultDataView<TAPAddContactResponse>() {
        @Override
        public void startLoading() {
            tvButtonTitle.setVisibility(View.GONE);
            ivButtonIcon.setVisibility(View.GONE);
            ivAddLoading.setVisibility(View.VISIBLE);
            TAPUtils.rotateAnimateInfinitely(TAPScanResultActivity.this, ivAddLoading);
        }

        @Override
        public void onSuccess(TAPAddContactResponse response) {
            TAPUserModel newContact = response.getUser().setUserAsContact();
            TAPDataManager.getInstance().insertMyContactToDatabase(newContact);
            TAPContactManager.getInstance().updateUserData(newContact);
            tvButtonTitle.setVisibility(View.VISIBLE);
            ivButtonIcon.setVisibility(View.VISIBLE);
            ivAddLoading.clearAnimation();
            ivAddLoading.setVisibility(View.GONE);
            animateAddSuccess(newContact);
            llButton.setOnClickListener(v -> {
                TapUIChatActivity.start(
                        TAPScanResultActivity.this,
                        instanceKey,
                        TAPChatManager.getInstance().arrangeRoomId(vm.getMyUserModel().getUserID(), vm.getContactModel().getUserID()),
                        vm.getContactModel().getName(),
                        vm.getContactModel().getAvatarURL(),
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

    TAPDefaultDataView<TAPGetUserResponse> getUserView = new TAPDefaultDataView<TAPGetUserResponse>() {
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
            new TapTalkDialog.Builder(TAPScanResultActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_api_call_return_error))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(v -> onBackPressed()).show();
        }
    };

    private void loadProfilePicture(CircleImageView imageView, TextView avatarLabel, TAPUserModel userModel) {
        if (null != userModel.getAvatarURL() && !userModel.getAvatarURL().getThumbnail().isEmpty()) {
            glide.load(userModel.getAvatarURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    // Show initial
                    runOnUiThread(() -> {
                        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getRandomColor(TAPScanResultActivity.this, userModel.getName())));
                        imageView.setImageDrawable(ContextCompat.getDrawable(TAPScanResultActivity.this, R.drawable.tap_bg_circle_9b9b9b));
                        avatarLabel.setText(TAPUtils.getInitials(userModel.getName(), 2));
                        avatarLabel.setVisibility(View.VISIBLE);
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(imageView);
            ImageViewCompat.setImageTintList(imageView, null);
            avatarLabel.setVisibility(View.GONE);
        } else {
            // Show initial
            glide.clear(imageView);
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getRandomColor(this, userModel.getName())));
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_9b9b9b));
            avatarLabel.setText(TAPUtils.getInitials(userModel.getName(), 2));
            avatarLabel.setVisibility(View.VISIBLE);
        }
    }

    public void viewThisIsYou() {
        runOnUiThread(() -> {
            llButton.setVisibility(View.GONE);
            tvThisIsYou.setVisibility(View.VISIBLE);
        });
    }

    public void animateAlreadyContact() {
        runOnUiThread(() -> {
            llButton.setAlpha(0f);
            loadProfilePicture(civMyUserAvatar, tvMyAvatarLabel, vm.getMyUserModel());
            civMyUserAvatar.setTranslationX(TAPUtils.dpToPx(-291));
            tvMyAvatarLabel.setTranslationX(TAPUtils.dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
            tvContactAvatarLabel.setTranslationX(0);
            llButton.setOnClickListener(v -> {
                TapUIChatActivity.start(
                        TAPScanResultActivity.this,
                        instanceKey,
                        TAPChatManager.getInstance().arrangeRoomId(vm.getMyUserModel().getUserID(), vm.getContactModel().getUserID()),
                        vm.getContactModel().getName(),
                        vm.getContactModel().getAvatarURL(),
                        1,
                        ""); // TODO: 20 May 2019 GET ROOM COLOR
                finish();
            });
            tvAlreadyContact.setText(Html.fromHtml("<b>" + vm.getContactModel().getName() + "</b> "
                    + getResources().getString(R.string.tap_is_already_in_your_contacts)));
            cvResult.animate().alpha(1f).withEndAction(() -> {
                //llButton.animate().alpha(0f).start();
                llTextUsername.animate().alpha(0f).withEndAction(() -> {
                    llTextUsername.setVisibility(View.GONE);
                    tvAlreadyContact.setVisibility(View.VISIBLE);
                    llButton.setVisibility(View.VISIBLE);
                    ivButtonIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_send_message_grey));
                    tvButtonTitle.setText(getString(R.string.tap_chat_now));
                    llButton.animate().alpha(1f).start();
                    civMyUserAvatar.animate()
                            .setInterpolator(new AccelerateInterpolator())
                            .translationX(TAPUtils.dpToPx(-54))
                            .withEndAction(() -> {
                                civMyUserAvatar.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(100)
                                        .translationX(TAPUtils.dpToPx(-24))
                                        .start();
                                tvMyAvatarLabel.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(100)
                                        .translationX(TAPUtils.dpToPx(-24))
                                        .start();

                                civTheirContactAvatar.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(150)
                                        .translationX(TAPUtils.dpToPx(48))
                                        .withEndAction(() -> {
                                            civTheirContactAvatar.animate()
                                                    .setInterpolator(new AccelerateInterpolator())
                                                    .setDuration(150)
                                                    .translationX(TAPUtils.dpToPx(24))
                                                    .start();
                                            tvContactAvatarLabel.animate()
                                                    .setInterpolator(new AccelerateInterpolator())
                                                    .setDuration(150)
                                                    .translationX(TAPUtils.dpToPx(24))
                                                    .start();
                                        }).start();
                                tvContactAvatarLabel.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(150)
                                        .translationX(TAPUtils.dpToPx(48))
                                        .start();
                            }).start();
                    tvMyAvatarLabel.animate()
                            .setInterpolator(new AccelerateInterpolator())
                            .translationX(TAPUtils.dpToPx(-54))
                            .start();
                }).start();
            }).start();
        });
    }

    public void animateAddSuccess(TAPUserModel contactModel) {
        runOnUiThread(() -> {
            tvAddSuccess.setText(Html.fromHtml(String.format(getString(R.string.tap_format_s_you_have_added_to_your_contacts), contactModel.getName())));
            loadProfilePicture(civMyUserAvatar, tvMyAvatarLabel, vm.getMyUserModel());
            civMyUserAvatar.setTranslationX(TAPUtils.dpToPx(-291));
            civTheirContactAvatar.setTranslationX(0);
            cvResult.animate().alpha(1f).withEndAction(() -> {
                llButton.animate().alpha(0f).start();
                llTextUsername.animate().alpha(0f).withEndAction(() -> {
                    llTextUsername.setVisibility(View.GONE);
                    llAddSuccess.setVisibility(View.VISIBLE);
                    llButton.setVisibility(View.VISIBLE);
                    ivButtonIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_send_message_grey));
                    tvButtonTitle.setText(getString(R.string.tap_chat_now));
                    llButton.animate().alpha(1f).start();
                    civMyUserAvatar.animate()
                            .setInterpolator(new AccelerateInterpolator())
                            .translationX(TAPUtils.dpToPx(-54))
                            .withEndAction(() -> {
                                civMyUserAvatar.animate().
                                        setInterpolator(new DecelerateInterpolator())
                                        .setDuration(100)
                                        .translationX(TAPUtils.dpToPx(-24))
                                        .start();
                                tvMyAvatarLabel.animate().
                                        setInterpolator(new DecelerateInterpolator())
                                        .setDuration(100)
                                        .translationX(TAPUtils.dpToPx(-24))
                                        .start();

                                civTheirContactAvatar.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(150)
                                        .translationX(TAPUtils.dpToPx(48))
                                        .withEndAction(() -> {
                                            civTheirContactAvatar.animate()
                                                    .setInterpolator(new AccelerateInterpolator())
                                                    .setDuration(150)
                                                    .translationX(TAPUtils.dpToPx(24))
                                                    .start();
                                            tvContactAvatarLabel.animate()
                                                    .setInterpolator(new AccelerateInterpolator())
                                                    .setDuration(150)
                                                    .translationX(TAPUtils.dpToPx(24))
                                                    .start();
                                        }).start();
                                tvContactAvatarLabel.animate()
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(150)
                                        .translationX(TAPUtils.dpToPx(48))
                                        .start();
                            }).start();
                    tvMyAvatarLabel.animate()
                            .setInterpolator(new AccelerateInterpolator())
                            .translationX(TAPUtils.dpToPx(-54))
                            .start();
                }).start();
            }).start();
        });
    }
}
