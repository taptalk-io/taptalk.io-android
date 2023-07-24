package io.moselo.SampleApps.Activity;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.REGISTER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import io.moselo.SampleApps.Fragment.TAPLoginVerificationFragment;
import io.moselo.SampleApps.Fragment.TAPPhoneLoginFragment;
import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalk.ViewModel.TAPLoginViewModelOld;
import io.taptalk.TapTalkSample.BuildConfig;
import io.taptalk.TapTalkSample.R;

public class TAPLoginActivityOld extends TAPBaseActivity {

    private static final String TAG = TAPLoginActivityOld.class.getSimpleName();
    private FrameLayout flContainer;
    private TAPLoginViewModelOld vm;

    public static void start(
            Context context,
            String instanceKey) {
        start(context, instanceKey, true);
    }

    public static void start(
            Context context,
            String instanceKey,
            boolean newTask) {
        Intent intent = new Intent(context, TAPLoginActivityOld.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_login_old);
        initViewModel();
        initView();
        initFirstPage();
        TAPUtils.checkAndRequestNotificationPermission(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REGISTER:
                    TAPApiManager.getInstance(instanceKey).setLoggedOut(false);
                    if (BuildConfig.DEBUG) {
                        TapDevLandingActivity.Companion.start(TAPLoginActivityOld.this, instanceKey);
                    } else {
                        TapUIRoomListActivity.start(TAPLoginActivityOld.this, instanceKey);
                    }
                    finish();
                    break;
            }
        }
    }

    private void initView() {
        flContainer = findViewById(R.id.fl_container);
    }

    public void initFirstPage() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_container, TAPPhoneLoginFragment.Companion.getInstance())
                .commit();
    }

    public void showPhoneLogin() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.tap_slide_left_fragment, R.animator.tap_fade_out_fragment, R.animator.tap_fade_in_fragment, R.animator.tap_slide_right_fragment)
                .replace(R.id.fl_container, TAPPhoneLoginFragment.Companion.getInstance())
                .addToBackStack(null)
                .commit();
    }

    public void showOTPVerification(Long otpID, String otpKey, String phoneNumber, String phoneNumberWithCode, int countryID, String countryCallingID, String countryFlagUrl, String channel, int nextRequestSeconds) {
        if (BuildConfig.BUILD_TYPE.equals("dev")) {
            String otpCode = phoneNumber.substring(phoneNumber.length() - 6);
            TAPDataManager.getInstance(instanceKey).verifyOTPLogin(otpID, otpKey, otpCode, new TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
                @Override
                public void onSuccess(TAPLoginOTPVerifyResponse response) {
                    if (response.isRegistered()) {
                        TapTalk.authenticateWithAuthTicket(instanceKey, response.getTicket(), true, new TapCommonListener() {
                            @Override
                            public void onSuccess(String successMessage) {
                                TapDevLandingActivity.Companion.start(TAPLoginActivityOld.this, instanceKey);
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                new TapTalkDialog.Builder(TAPLoginActivityOld.this)
                                        .setTitle("Error Verifying OTP")
                                        .setMessage(errorMessage)
                                        .setPrimaryButtonTitle("OK")
                                        .show();
                            }
                        });
                    }
                    else {
                        TAPRegisterActivity.Companion.start(
                                TAPLoginActivityOld.this,
                                instanceKey,
                                countryID,
                                countryCallingID,
                                countryFlagUrl,
                                phoneNumber
                        );
                        vm.setPhoneNumber("0");
                        vm.setCountryID(0);
                    }
                }

                @Override
                public void onError(TAPErrorModel error) {
                    onError(error.getMessage());
                }

                @Override
                public void onError(String errorMessage) {
                    new TapTalkDialog.Builder(TAPLoginActivityOld.this)
                            .setTitle("Error Verifying OTP")
                            .setMessage(errorMessage)
                            .setPrimaryButtonTitle("OK")
                            .show();
                }
            });
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.tap_slide_left_fragment, R.animator.tap_fade_out_fragment, R.animator.tap_fade_in_fragment, R.animator.tap_slide_right_fragment)
                    .replace(R.id.fl_container, TAPLoginVerificationFragment.Companion.getInstance(otpID, otpKey, phoneNumber, phoneNumberWithCode, countryID, countryCallingID, countryFlagUrl, channel, nextRequestSeconds))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void setLastLoginData(Long otpID, String otpKey, String phoneNumber, String phoneNumberWithCode, int countryID, String countryCallingID, String channel) {
        vm.setLastLoginData(otpID, otpKey, phoneNumber, phoneNumberWithCode, countryID, countryCallingID, channel);
    }

    private void initViewModel() {
        vm = new ViewModelProvider(this).get(TAPLoginViewModelOld.class);
    }

    public TAPLoginViewModelOld getVm() {
        return null == vm ? vm = new ViewModelProvider(this).get(TAPLoginViewModelOld.class) : vm;
    }
}
