package io.taptalk.TapTalk.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
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
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.ViewModel.TAPNewContactViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ADDED_CONTACT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.API_PARAMETER_VALIDATION_FAILED;

public class TAPNewContactActivity extends TAPBaseActivity {

    private static final String TAG = TAPNewContactActivity.class.getSimpleName();

    private ConstraintLayout clSearchResult, clButtonAction, clConnectionLost;
    private LinearLayout llEmpty;
    private ImageView ivButtonBack, ivButtonCancel, ivExpertCover, ivAvatarIcon, ivButtonImage;
    private CircleImageView civAvatar;
    private TextView tvSearchUsernameGuide, tvUserName, tvCategory, tvButtonText;
    private EditText etSearch;
    private ProgressBar pbSearch, pbButton;

    private TAPNewContactViewModel vm;

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPNewContactViewModel.class);
    }

    private void initView() {
        clSearchResult = findViewById(R.id.cl_search_result);
        clButtonAction = findViewById(R.id.cl_button_action);
        clConnectionLost = findViewById(R.id.cl_connection_lost);
        llEmpty = findViewById(R.id.ll_empty);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonCancel = findViewById(R.id.iv_button_cancel);
        ivExpertCover = findViewById(R.id.iv_expert_cover);
        ivAvatarIcon = findViewById(R.id.iv_avatar_icon);
        ivButtonImage = findViewById(R.id.iv_button_image);
        civAvatar = findViewById(R.id.civ_avatar);
        tvSearchUsernameGuide = findViewById(R.id.tv_search_username_guide);
        tvUserName = findViewById(R.id.tv_user_name);
        tvCategory = findViewById(R.id.tv_category);
        tvButtonText = findViewById(R.id.tv_button_text);
        etSearch = findViewById(R.id.et_search);
        pbSearch = findViewById(R.id.pb_search);
        pbButton = findViewById(R.id.pb_button);

        etSearch.addTextChangedListener(contactSearchWatcher);
        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> onSearchEditorClicked());

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonCancel.setOnClickListener(v -> clearSearch());
    }

    private void initListener() {
        TAPConnectionManager.getInstance().addSocketListener(socketListener);
    }

    private boolean onSearchEditorClicked() {
        TAPDataManager.getInstance().cancelUserSearchApiCall();
        TAPDataManager.getInstance().getUserByUsernameFromApi(etSearch.getText().toString(), getUserView);
        return true;
    }

    private void clearSearch() {
        //showEmpty();
        TAPUtils.getInstance().dismissKeyboard(this);
        etSearch.setText("");
        etSearch.clearFocus();
    }

    private void showEmpty() {
        tvSearchUsernameGuide.setVisibility(View.VISIBLE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.GONE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvUserName.setVisibility(View.GONE);
        tvCategory.setVisibility(View.GONE);
        clButtonAction.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        clConnectionLost.setVisibility(View.GONE);
        ivButtonCancel.setVisibility(View.VISIBLE);
        pbSearch.setVisibility(View.INVISIBLE);
    }

    private void showResultNotFound() {
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.GONE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvUserName.setVisibility(View.GONE);
        tvCategory.setVisibility(View.GONE);
        clButtonAction.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
        clConnectionLost.setVisibility(View.GONE);
    }

    private void showConnectionLost() {
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.GONE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvUserName.setVisibility(View.GONE);
        tvCategory.setVisibility(View.GONE);
        clButtonAction.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        clConnectionLost.setVisibility(View.VISIBLE);
    }

    private void showUserView() {
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.GONE);
        civAvatar.setVisibility(View.VISIBLE);
        ivAvatarIcon.setVisibility(View.GONE);
        tvUserName.setVisibility(View.VISIBLE);
        tvCategory.setVisibility(View.GONE);
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
            Glide.with(this).load(vm.getSearchResult().getAvatarURL().getThumbnail()).into(civAvatar);
            civAvatar.setBackground(null);
        } else {
            civAvatar.setImageDrawable(null);
            civAvatar.setBackground(getDrawable(R.drawable.tap_bg_circle_9b9b9b));
            civAvatar.setBackgroundTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getSearchResult().getName())));
        }

        tvUserName.setText(vm.getSearchResult().getName());

        // Check if user is in my contacts
        tvButtonText.setVisibility(View.GONE);
        ivButtonImage.setVisibility(View.GONE);
        pbButton.setVisibility(View.VISIBLE);
        TAPDataManager.getInstance().checkUserInMyContacts(vm.getSearchResult().getUserID(), dbListener);
    }

    private void showExpertView() {
        tvSearchUsernameGuide.setVisibility(View.GONE);
        ivExpertCover.setVisibility(View.VISIBLE);
        civAvatar.setVisibility(View.VISIBLE);
        ivAvatarIcon.setVisibility(View.VISIBLE);
        tvUserName.setVisibility(View.VISIBLE);
        tvCategory.setVisibility(View.VISIBLE);
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
            Glide.with(this).load(vm.getSearchResult().getAvatarURL().getThumbnail()).into(civAvatar);
            civAvatar.setBackground(null);
        } else {
            civAvatar.setImageDrawable(null);
            civAvatar.setBackground(getDrawable(R.drawable.tap_bg_circle_9b9b9b));
            civAvatar.setBackgroundTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getSearchResult().getName())));
        }

        // Set cover image
        // TODO: 25 October 2018 CHECK AND LOAD COVER IMAGE
        if (null != vm.getSearchResult().getAvatarURL() && !vm.getSearchResult().getAvatarURL().getFullsize().isEmpty()) {
            Glide.with(this).load(vm.getSearchResult().getAvatarURL().getFullsize()).into(ivExpertCover);
            ivExpertCover.setBackground(null);
        } else {
            ivExpertCover.setImageDrawable(null);
            ivExpertCover.setBackgroundColor(getResources().getColor(R.color.mediumPurple));
        }

        tvUserName.setText(vm.getSearchResult().getName());

        // TODO: 25 October 2018 SET CATEGORY
        if (null != vm.getSearchResult().getUserRole()) tvCategory.setText(vm.getSearchResult().getUserRole().getRoleName());
        // TODO: 25 October 2018 TESTING
        tvCategory.setText("Category");

        // Check if user is in my contacts
        tvButtonText.setVisibility(View.GONE);
        ivButtonImage.setVisibility(View.GONE);
        pbButton.setVisibility(View.VISIBLE);
        TAPDataManager.getInstance().checkUserInMyContacts(vm.getSearchResult().getUserID(), dbListener);
    }

    private void showSearchResult() {
        // TODO: 25 October 2018 CHECK USER ROLE
        if (null != vm.getSearchResult().getUserRole() && vm.getSearchResult().getUserRole().getCode().equals("1")) {
            showUserView();
        } else {
            showExpertView();
        }
    }

    private void addToContact() {
        TAPDataManager.getInstance().addContactApi(vm.getSearchResult().getUserID(), addContactView);
    }

    private void openChatRoom() {
        // TODO: 25 October 2018 SET ROOM TYPE AND COLOR
        TAPUtils.getInstance().startChatActivity(
                this,
                TAPChatManager.getInstance().arrangeRoomId(TAPDataManager.getInstance().getActiveUser().getUserID(), vm.getSearchResult().getUserID()),
                vm.getSearchResult().getName(),
                vm.getSearchResult().getAvatarURL(),
                1,
                /* TEMPORARY ROOM COLOR */TAPUtils.getInstance().getRandomColor(vm.getSearchResult().getName()) + "");
    }

    private void enableInput() {
        runOnUiThread(() -> {
            etSearch.setEnabled(true);
            etSearch.addTextChangedListener(contactSearchWatcher);
            ivButtonCancel.setOnClickListener(v -> clearSearch());
            showSearchResult();
        });
    }

    private void disableInput() {
        runOnUiThread(() -> {
            tvButtonText.setVisibility(View.GONE);
            pbButton.setVisibility(View.VISIBLE);
            etSearch.setEnabled(false);
            etSearch.removeTextChangedListener(contactSearchWatcher);
            ivButtonCancel.setOnClickListener(null);
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
            searchTimer.cancel();
            if (s.length() > 0) {
                searchTimer.start();
            } else {
                showEmpty();
                vm.setPendingSearch("");
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
            TAPDataManager.getInstance().getUserByUsernameFromApi(etSearch.getText().toString(), getUserView);
        }
    };

    TAPDatabaseListener<TAPUserModel> dbListener = new TAPDatabaseListener<TAPUserModel>() {
        @Override
        public void onContactCheckFinished(int isContact) {
            // Update action button after contact check finishes
            if (isContact == 0) {
                // Searched user is not a contact
                runOnUiThread(() -> {
                    ivButtonImage.setVisibility(View.GONE);
                    tvButtonText.setVisibility(View.VISIBLE);
                    pbButton.setVisibility(View.GONE);
                    tvButtonText.setText(getString(R.string.add_to_contacts));
                    clButtonAction.setOnClickListener(v -> addToContact());
                });
            } else {
                // Searched user is in my contacts
                runOnUiThread(() -> {
                    ivButtonImage.setVisibility(View.VISIBLE);
                    tvButtonText.setVisibility(View.VISIBLE);
                    pbButton.setVisibility(View.GONE);
                    tvButtonText.setText(getString(R.string.chat_now));
                    clButtonAction.setOnClickListener(v -> openChatRoom());
                });
            }
        }

        @Override
        public void onInsertFinished() {
            // Re-enable editing and update view after add contact finishes
            enableInput();
        }
    };

    TapDefaultDataView<TAPGetUserResponse> getUserView = new TapDefaultDataView<TAPGetUserResponse>() {
        @Override
        public void startLoading() {
            ivButtonCancel.setVisibility(View.INVISIBLE);
            pbSearch.setVisibility(View.VISIBLE);
        }

        @Override
        public void endLoading() {
            ivButtonCancel.setVisibility(View.VISIBLE);
            pbSearch.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onSuccess(TAPGetUserResponse response) {
            vm.setSearchResult(response.getUser());
            showSearchResult();
        }

        @Override
        public void onError(TAPErrorModel error) {
            if (error.getCode().equals(String.valueOf(API_PARAMETER_VALIDATION_FAILED))) {
                // User not found
                showResultNotFound();
                endLoading();
            } else {
                // Other errors
                new TapTalkDialog.Builder(TAPNewContactActivity.this)
                        .setTitle(getString(R.string.error))
                        .setMessage(error.getMessage())
                        .setPrimaryButtonTitle(getString(R.string.ok))
                        .setPrimaryButtonListener(true, v -> endLoading())
                        .show();
            }
        }

        @Override
        public void onError(String errorMessage) {
            if (!TAPNetworkStateManager.getInstance().hasNetworkConnection(TAPNewContactActivity.this)) {
                // No internet connection
                vm.setPendingSearch(etSearch.getText().toString());
                showConnectionLost();
            }
        }
    };

    TapDefaultDataView<TAPCommonResponse> addContactView = new TapDefaultDataView<TAPCommonResponse>() {
        @Override
        public void startLoading() {
            // Disable editing when loading
            disableInput();
        }

        @Override
        public void onSuccess(TAPCommonResponse response) {
            // Add contact to database
            TAPUserModel newContact = vm.getSearchResult().setUserAsContact();
            TAPDataManager.getInstance().insertMyContactToDatabase(dbListener, newContact);
            TAPContactManager.getInstance().updateUserDataMap(newContact);

            // Change To Animation Page
            Intent intent = new Intent(TAPNewContactActivity.this, TAPScanResultActivity.class);
            intent.putExtra(ADDED_CONTACT, vm.getSearchResult());
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
        }

        @Override
        public void onError(TAPErrorModel error) {
            enableInput();
            new TapTalkDialog.Builder(TAPNewContactActivity.this)
                    .setTitle(getString(R.string.error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.ok))
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            enableInput();
            Toast.makeText(TAPNewContactActivity.this, getString(R.string.error_message_general), Toast.LENGTH_SHORT).show();
        }
    };

    private TAPSocketListener socketListener = new TAPSocketListener() {
        @Override
        public void onSocketConnected() {
            // Resume pending search on connect
            if (null == vm.getPendingSearch() || vm.getPendingSearch().isEmpty()) {
                return;
            }
            TAPDataManager.getInstance().getUserByUsernameFromApi(vm.getPendingSearch(), getUserView);
            vm.setPendingSearch("");
        }
    };
}
