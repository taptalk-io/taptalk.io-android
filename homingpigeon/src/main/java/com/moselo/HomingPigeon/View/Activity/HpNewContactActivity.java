package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HomingPigeonDialog;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetUserResponse;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ViewModel.HpNewContactViewModel;

public class HpNewContactActivity extends HpBaseActivity {

    private static final String TAG = HpNewContactActivity.class.getSimpleName();

    private ConstraintLayout clSearchResult, clButtonAction;
    private LinearLayout llEmpty;
    private ImageView ivButtonBack, ivButtonCancel, ivExpertCover, ivAvatarIcon, ivButtonImage;
    private CircleImageView civAvatar;
    private TextView tvSearchUsernameGuide, tvUserName, tvCategory, tvButtonText;
    private EditText etSearch;
    private ProgressBar pbSearch, pbButton;

    private HpNewContactViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_new_contact);

        initViewModel();
        initView();
        HpUtils.getInstance().showKeyboard(this, etSearch);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpNewContactViewModel.class);
    }

    @Override
    protected void initView() {
        clSearchResult = findViewById(R.id.cl_search_result);
        clButtonAction = findViewById(R.id.cl_button_action);
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

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonCancel.setOnClickListener(v -> clearSearch());
    }

    private void clearSearch() {
        etSearch.setText("");
        HpUtils.getInstance().dismissKeyboard(this);
        showEmpty();
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

        // Remove bottom constraint from avatar
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(clSearchResult);
        constraintSet.clear(civAvatar.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(clSearchResult);

        if (null != vm.getSearchResult().getAvatarURL() && !vm.getSearchResult().getAvatarURL().getThumbnail().isEmpty()) {
            GlideApp.with(this).load(vm.getSearchResult().getAvatarURL().getThumbnail()).into(civAvatar);
        }
        // TODO: 25 October 2018 TESTING
        civAvatar.setImageResource(R.drawable.hp_bg_circle_vibrantgreen);

        tvUserName.setText(vm.getSearchResult().getName());

        // Check if user is in my contacts
        tvButtonText.setVisibility(View.GONE);
        ivButtonImage.setVisibility(View.GONE);
        pbButton.setVisibility(View.VISIBLE);
        HpDataManager.getInstance().checkUserInMyContacts(vm.getSearchResult().getUserID(), contactCheckListener);
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

        // Set bottom constraint from avatar to cover image
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(clSearchResult);
        constraintSet.connect(civAvatar.getId(), ConstraintSet.BOTTOM, ivExpertCover.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(clSearchResult);

        if (null != vm.getSearchResult().getAvatarURL() && !vm.getSearchResult().getAvatarURL().getThumbnail().isEmpty()) {
            GlideApp.with(this).load(vm.getSearchResult().getAvatarURL().getThumbnail()).into(civAvatar);
            // TODO: 25 October 2018 LOAD COVER IMAGE
            GlideApp.with(this).load(vm.getSearchResult().getAvatarURL().getFullsize()).into(ivExpertCover);
        }
        // TODO: 25 October 2018 TESTING
        civAvatar.setImageResource(R.drawable.hp_bg_circle_vibrantgreen);
        ivExpertCover.setImageResource(R.drawable.hp_bg_amethyst_mediumpurple_270_rounded_10dp);

        tvUserName.setText(vm.getSearchResult().getName());

        // TODO: 25 October 2018 SET CATEGORY
        if (null != vm.getSearchResult().getUserRole()) tvCategory.setText(vm.getSearchResult().getUserRole().getRoleName());
        // TODO: 25 October 2018 TESTING
        tvCategory.setText("Category");

        // Check if user is in my contacts
        tvButtonText.setVisibility(View.GONE);
        ivButtonImage.setVisibility(View.GONE);
        pbButton.setVisibility(View.VISIBLE);
        HpDataManager.getInstance().checkUserInMyContacts(vm.getSearchResult().getUserID(), contactCheckListener);
    }

    private void addToContact() {
        // TODO: 25 October 2018
    }

    private void openChatRoom() {
        // TODO: 25 October 2018 SET ROOM TYPE AND COLOR
        HpUtils.getInstance().startChatActivity(
                this,
                HpChatManager.getInstance().arrangeRoomId(HpDataManager.getInstance().getActiveUser().getUserID(), vm.getSearchResult().getUserID()),
                vm.getSearchResult().getName(),
                vm.getSearchResult().getAvatarURL(),
                1,
                /* TEMPORARY ROOM COLOR */HpUtils.getInstance().getRandomColor(vm.getSearchResult().getName()) + "");
    }

    private TextWatcher contactSearchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO: 25 October 2018 CANCEL API CALL IF RUNNING
            searchTimer.cancel();
            if (s.length() > 0) {
                Log.e(TAG, "onTextChanged: ");
                searchTimer.start();
            } else {
                showEmpty();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private CountDownTimer searchTimer = new CountDownTimer(300L, 10L) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Log.e(TAG, "onFinish: ");
            ivButtonCancel.setVisibility(View.INVISIBLE);
            pbSearch.setVisibility(View.VISIBLE);
            HpDataManager.getInstance().getUserByUsernameFromApi(etSearch.getText().toString(), getContactView);
        }
    };

    HpDatabaseListener<HpUserModel> contactCheckListener = new HpDatabaseListener<HpUserModel>() {
        @Override
        public void onContactCheckFinished(int isContact) {
            Log.e(TAG, "onContactCheckFinished: " + isContact);
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
    };

    HpDefaultDataView<HpGetUserResponse> getContactView = new HpDefaultDataView<HpGetUserResponse>() {

        // TODO: 25 October 2018 TESTING
        int tempCount;

        @Override
        public void onSuccess(HpGetUserResponse response) {
            vm.setSearchResult(response.getUser());

            // TODO: 25 October 2018 CHECK USER ROLE
            if (tempCount % 2 == 0) {
                showExpertView();
            } else {
                showUserView();
            }
            tempCount++;
        }

        @Override
        public void endLoading() {
            ivButtonCancel.setVisibility(View.VISIBLE);
            pbSearch.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError(HpErrorModel error) {
            // TODO: 25 October 2018 CHECK ERROR CODE FOR USER NOT FOUND
            if (error.getCode().charAt(0) == '4') {
                showResultNotFound();
                endLoading();
            } else {
                if (BuildConfig.DEBUG) {
                    new HomingPigeonDialog.Builder(HpNewContactActivity.this)
                            .setTitle(getString(R.string.error))
                            .setMessage(error.getMessage())
                            .setPrimaryButtonListener(v -> endLoading())
                            .show();
                }
            }
        }

        @Override
        public void onError(String errorMessage) {
            if (BuildConfig.DEBUG) {
                new HomingPigeonDialog.Builder(HpNewContactActivity.this)
                        .setTitle(getString(R.string.error))
                        .setMessage(errorMessage)
                        .setPrimaryButtonListener(v -> endLoading())
                        .show();
            }
        }
    };
}
