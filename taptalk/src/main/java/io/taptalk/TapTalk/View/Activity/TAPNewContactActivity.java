package io.taptalk.TapTalk.View.Activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.ViewModel.TAPNewContactViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ADDED_CONTACT;

public class TAPNewContactActivity extends TAPBaseActivity {

    private static final String TAG = TAPNewContactActivity.class.getSimpleName();

    private ConstraintLayout clSearchResult, clButtonAction, clConnectionLost;
    private LinearLayout llEmpty;
    private CardView cvSearchResult;
    private ImageView ivButtonBack, ivButtonClearText, ivExpertCover, ivAvatarIcon, ivButtonImage, ivProgressSearch, ivResultButtonLoading;
    private CircleImageView civAvatar;
    private TextView tvSearchUsernameGuide, tvAvatarLabel, tvFullName, tvUsername, tvButtonText;
    private EditText etSearch;

    private TAPNewContactViewModel vm;

    private RequestManager glide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_new_contact);

        initViewModel();
        initView();
        initListener();
        TAPUtils.getInstance().showKeyboard(this, etSearch);
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPNewContactViewModel.class);
    }

    private void initView() {
        getWindow().setBackgroundDrawable(null);

        clSearchResult = findViewById(R.id.cl_search_result);
        clButtonAction = findViewById(R.id.cl_button_action);
        clConnectionLost = findViewById(R.id.cl_connection_lost);
        llEmpty = findViewById(R.id.ll_empty);
        cvSearchResult = findViewById(R.id.cv_search_result);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonClearText = findViewById(R.id.iv_button_clear_text);
        ivExpertCover = findViewById(R.id.iv_expert_cover);
        ivAvatarIcon = findViewById(R.id.iv_avatar_icon);
        ivButtonImage = findViewById(R.id.iv_button_image);
        ivProgressSearch = findViewById(R.id.iv_progress_search);
        ivResultButtonLoading = findViewById(R.id.iv_loading_progress);
        civAvatar = findViewById(R.id.civ_avatar);
        tvSearchUsernameGuide = findViewById(R.id.tv_search_username_guide);
        tvFullName = findViewById(R.id.tv_full_name);
        tvAvatarLabel = findViewById(R.id.tv_avatar_label);
        tvUsername = findViewById(R.id.tv_username);
        tvButtonText = findViewById(R.id.tv_button_text);
        etSearch = findViewById(R.id.et_search);

        glide = Glide.with(this);

        etSearch.addTextChangedListener(contactSearchWatcher);
        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> onSearchEditorClicked());

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonClearText.setOnClickListener(v -> clearSearch());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clButtonAction.setBackground(getDrawable(R.drawable.tap_bg_button_active_ripple));
        }
    }

    private void initListener() {
        TAPConnectionManager.getInstance().addSocketListener(socketListener);
    }

    private boolean onSearchEditorClicked() {
        TAPDataManager.getInstance().cancelUserSearchApiCall();
        TAPDataManager.getInstance().getUserByUsernameFromApi(etSearch.getText().toString(), true, getUserView);
        return true;
    }

    private void clearSearch() {
        //showEmpty();
        TAPUtils.getInstance().dismissKeyboard(this);
        etSearch.setText("");
        etSearch.clearFocus();
    }

    private void showEmpty() {
        cvSearchResult.setVisibility(View.GONE);
        tvSearchUsernameGuide.setVisibility(View.VISIBLE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.GONE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvFullName.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        clButtonAction.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        clConnectionLost.setVisibility(View.GONE);
        ivButtonClearText.setVisibility(View.VISIBLE);
        ivProgressSearch.setVisibility(View.GONE);
        ivProgressSearch.clearAnimation();
    }

    private void showResultNotFound() {
        cvSearchResult.setVisibility(View.GONE);
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.GONE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvFullName.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        clButtonAction.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
        clConnectionLost.setVisibility(View.GONE);
        ivProgressSearch.setVisibility(View.INVISIBLE);
    }

    private void showConnectionLost() {
        cvSearchResult.setVisibility(View.GONE);
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.GONE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvFullName.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        clButtonAction.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        clConnectionLost.setVisibility(View.VISIBLE);
        ivProgressSearch.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("PrivateResource")
    private void showUserView() {
        cvSearchResult.setVisibility(View.VISIBLE);
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.VISIBLE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvFullName.setVisibility(View.VISIBLE);
        tvUsername.setVisibility(View.VISIBLE);
        clButtonAction.setVisibility(View.VISIBLE);
        llEmpty.setVisibility(View.GONE);
        clConnectionLost.setVisibility(View.GONE);

        // Remove bottom constraint from avatar
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(clSearchResult);
        constraintSet.clear(civAvatar.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(clSearchResult);

        // Set avatar
        if (null != vm.getSearchResult().getAvatarURL() && !vm.getSearchResult().getAvatarURL().getThumbnail().isEmpty()) {
            glide.load(vm.getSearchResult().getAvatarURL().getThumbnail()).into(civAvatar);
            ImageViewCompat.setImageTintList(civAvatar, null);
            tvAvatarLabel.setVisibility(View.GONE);
        } else {
//            civAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
            ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getSearchResult().getName())));
            civAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_9b9b9b));
            tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(vm.getSearchResult().getName(), 2));
            tvAvatarLabel.setVisibility(View.VISIBLE);
        }

        tvFullName.setText(vm.getSearchResult().getName());
        tvUsername.setText(vm.getSearchResult().getUsername());

        clButtonAction.setOnClickListener(null);

        if (vm.getSearchResult().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
            tvButtonText.setText(getString(R.string.tap_this_is_you));
            TypedArray typedArray = obtainStyledAttributes(R.style.tapClickableLabelStyle, R.styleable.TextAppearance);
            tvButtonText.setTextColor(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1));
            typedArray.recycle();
            clButtonAction.setBackground(null);
        } else {
            // Check if user is in my contacts
            tvButtonText.setVisibility(View.GONE);
            ivButtonImage.setVisibility(View.GONE);
            TAPUtils.getInstance().rotateAnimateInfinitely(this, ivResultButtonLoading);
            ivResultButtonLoading.setVisibility(View.VISIBLE);
            TAPDataManager.getInstance().checkUserInMyContacts(vm.getSearchResult().getUserID(), dbListener);
        }
    }

    @SuppressLint("PrivateResource")
    private void showExpertView() {
        cvSearchResult.setVisibility(View.VISIBLE);
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.VISIBLE);
        civAvatar.setVisibility(View.VISIBLE);
        tvFullName.setVisibility(View.VISIBLE);
        tvUsername.setVisibility(View.VISIBLE);
        clButtonAction.setVisibility(View.VISIBLE);
        llEmpty.setVisibility(View.GONE);
        clConnectionLost.setVisibility(View.GONE);

        // Set bottom constraint from avatar to cover image
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(clSearchResult);
        constraintSet.connect(civAvatar.getId(), ConstraintSet.BOTTOM, ivExpertCover.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(clSearchResult);

        // Set avatar
        if (null != vm.getSearchResult().getAvatarURL() && !vm.getSearchResult().getAvatarURL().getThumbnail().isEmpty()) {
            glide.load(vm.getSearchResult().getAvatarURL().getThumbnail()).into(civAvatar);
            ImageViewCompat.setImageTintList(civAvatar, null);
            tvAvatarLabel.setVisibility(View.GONE);
        } else {
//            civAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
            ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getSearchResult().getName())));
            civAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_9b9b9b));
            tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(vm.getSearchResult().getName(), 2));
            tvAvatarLabel.setVisibility(View.VISIBLE);
        }

        // Set avatar icon
        if (null != vm.getSearchResult().getUserRole() && !vm.getSearchResult().getUserRole().getRoleIconURL().isEmpty()) {
            glide.load(vm.getSearchResult().getUserRole().getRoleIconURL()).into(ivAvatarIcon);
            ivAvatarIcon.setVisibility(View.VISIBLE);
        } else {
            ivAvatarIcon.setVisibility(View.GONE);
        }

        // Set cover image
        // TODO: 25 October 2018 CHECK AND LOAD COVER IMAGE
        //ivExpertCover.setImageDrawable(getDrawable(R.drawable.moselo_default_cover));
        ivExpertCover.setBackground(null);

        tvFullName.setText(vm.getSearchResult().getName());
        tvUsername.setText(vm.getSearchResult().getUsername());

        clButtonAction.setOnClickListener(null);

        if (vm.getSearchResult().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
            tvButtonText.setText(getString(R.string.tap_this_is_you));
            TypedArray typedArray = obtainStyledAttributes(R.style.tapClickableLabelStyle, R.styleable.TextAppearance);
            tvButtonText.setTextColor(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1));
            typedArray.recycle();
            clButtonAction.setBackground(null);
        } else {
            // Check if user is in my contacts
            tvButtonText.setVisibility(View.GONE);
            ivButtonImage.setVisibility(View.GONE);
            TAPUtils.getInstance().rotateAnimateInfinitely(this, ivResultButtonLoading);
            ivResultButtonLoading.setVisibility(View.VISIBLE);
            TAPDataManager.getInstance().checkUserInMyContacts(vm.getSearchResult().getUserID(), dbListener);
        }
    }

    private void showSearchResult() {
        // TODO: 25 October 2018 CHECK USER ROLE
        if (null != vm.getSearchResult().getUserRole() && vm.getSearchResult().getUserRole().getCode().equals("expert")) {
            showExpertView();
        } else {
            showUserView();
        }
    }

    private void addToContact() {
        TAPDataManager.getInstance().addContactApi(vm.getSearchResult().getUserID(), addContactView);
    }

    private void openChatRoom() {
        // TODO: 25 October 2018 SET ROOM TYPE AND COLOR
        TAPUtils.getInstance().startChatActivity(
                this,
                TAPChatManager.getInstance().arrangeRoomId(TAPChatManager.getInstance().getActiveUser().getUserID(), vm.getSearchResult().getUserID()),
                vm.getSearchResult().getName(),
                vm.getSearchResult().getAvatarURL(),
                1,
                /* TEMPORARY ROOM COLOR */TAPUtils.getInstance().getRandomColor(vm.getSearchResult().getName()) + "");
    }

    private void enableInput() {
        runOnUiThread(() -> {
            etSearch.setEnabled(true);
            etSearch.addTextChangedListener(contactSearchWatcher);
            ivButtonClearText.setOnClickListener(v -> clearSearch());
            showSearchResult();
        });
    }

    private void disableInput() {
        runOnUiThread(() -> {
            tvButtonText.setVisibility(View.GONE);
            TAPUtils.getInstance().rotateAnimateInfinitely(this, ivResultButtonLoading);
            ivResultButtonLoading.setVisibility(View.VISIBLE);
            etSearch.setEnabled(false);
            etSearch.removeTextChangedListener(contactSearchWatcher);
            ivButtonClearText.setOnClickListener(null);
            TAPUtils.getInstance().dismissKeyboard(this);
        });
    }

    private TextWatcher contactSearchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TAPDataManager.getInstance().cancelUserSearchApiCall();
            endLoading();
            searchTimer.cancel();
            if (s.length() > 0) {
                searchTimer.start();
                ivButtonClearText.setVisibility(View.VISIBLE);
            } else {
                showEmpty();
                vm.setPendingSearch("");
                ivButtonClearText.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private CountDownTimer searchTimer = new CountDownTimer(300L, 100L) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            TAPDataManager.getInstance().getUserByUsernameFromApi(etSearch.getText().toString(), true, getUserView);
        }
    };

    TAPDatabaseListener<TAPUserModel> dbListener = new TAPDatabaseListener<TAPUserModel>() {
        @SuppressLint("PrivateResource")
        @Override
        public void onContactCheckFinished(int isContact) {
            // Update action button after contact check finishes
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    clButtonAction.setBackground(getDrawable(R.drawable.tap_bg_button_active_ripple));
                } else {
                    clButtonAction.setBackground(ContextCompat.getDrawable(TAPNewContactActivity.this, R.drawable.tap_bg_button_active));
                }
                TypedArray typedArray = obtainStyledAttributes(R.style.tapButtonLabelStyle, R.styleable.TextAppearance);
                tvButtonText.setTextColor(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1));
                typedArray.recycle();
                if (isContact == 0) {
                    // Searched user is not a contact
                    runOnUiThread(() -> {
                        ivButtonImage.setVisibility(View.GONE);
                        tvButtonText.setVisibility(View.VISIBLE);
                        ivResultButtonLoading.clearAnimation();
                        ivResultButtonLoading.setVisibility(View.GONE);
                        tvButtonText.setText(getString(R.string.tap_add_to_contacts));
                        clButtonAction.setOnClickListener(v -> addToContact());
                    });
                } else {
                    // Searched user is in my contacts
                    runOnUiThread(() -> {
                        ivButtonImage.setVisibility(View.VISIBLE);
                        tvButtonText.setVisibility(View.VISIBLE);
                        ivResultButtonLoading.clearAnimation();
                        ivResultButtonLoading.setVisibility(View.GONE);
                        tvButtonText.setText(getString(R.string.tap_chat_now));
                        clButtonAction.setOnClickListener(v -> openChatRoom());
                    });
                }
            });
        }

        @Override
        public void onInsertFinished() {
            // Re-enable editing and update view after add contact finishes
            enableInput();
        }
    };

    TAPDefaultDataView<TAPGetUserResponse> getUserView = new TAPDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void startLoading() {
            TAPNewContactActivity.this.startLoading();
        }

        @Override
        public void endLoading() {
            TAPNewContactActivity.this.endLoading();
        }

        @Override
        public void onSuccess(TAPGetUserResponse response) {
            TAPUserModel userResponse = response.getUser();
            TAPContactManager.getInstance().updateUserData(userResponse);
            vm.setSearchResult(userResponse);
            showSearchResult();
        }

        @Override
        public void onError(TAPErrorModel error) {
//            if (error.getCode().equals(String.valueOf(API_PARAMETER_VALIDATION_FAILED))) {
            // User not found
            showResultNotFound();
            endLoading();
//            }
        }

        @Override
        public void onError(String errorMessage) {
            if (!TAPNetworkStateManager.getInstance().hasNetworkConnection(TAPNewContactActivity.this)) {
                // No internet connection
                vm.setPendingSearch(etSearch.getText().toString());
                showConnectionLost();
                endLoading();
            }
        }
    };

    TAPDefaultDataView<TAPAddContactResponse> addContactView = new TAPDefaultDataView<TAPAddContactResponse>() {
        @Override
        public void startLoading() {
            // Disable editing when loading
            disableInput();
        }

        @Override
        public void onSuccess(TAPAddContactResponse response) {
            // Add contact to database
            TAPUserModel newContact = response.getUser().setUserAsContact();
            TAPDataManager.getInstance().insertMyContactToDatabase(dbListener, newContact);
            TAPContactManager.getInstance().updateUserData(newContact);

            // Change To Animation Page
            Intent intent = new Intent(TAPNewContactActivity.this, TAPScanResultActivity.class);
            intent.putExtra(ADDED_CONTACT, newContact);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
        }

        @Override
        public void onError(TAPErrorModel error) {
            enableInput();
            new TapTalkDialog.Builder(TAPNewContactActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            enableInput();
            Toast.makeText(TAPNewContactActivity.this, getString(R.string.tap_error_message_general), Toast.LENGTH_SHORT).show();
        }
    };

    private TAPSocketListener socketListener = new TAPSocketListener() {
        @Override
        public void onSocketConnected() {
            // Resume pending search on connect
            if (null == vm.getPendingSearch() || vm.getPendingSearch().isEmpty()) {
                return;
            }
            TAPDataManager.getInstance().getUserByUsernameFromApi(vm.getPendingSearch(), true, getUserView);
            vm.setPendingSearch("");
        }
    };

    private void startLoading() {
        ivButtonClearText.setVisibility(View.GONE);
        ivProgressSearch.setVisibility(View.VISIBLE);
        TAPUtils.getInstance().rotateAnimateInfinitely(TAPNewContactActivity.this, ivProgressSearch);
    }

    private void endLoading() {
        ivProgressSearch.setVisibility(View.GONE);
        ivProgressSearch.clearAnimation();
        ivButtonClearText.setVisibility(View.VISIBLE);
    }
}
