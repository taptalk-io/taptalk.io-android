package io.moselo.SampleApps.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import io.moselo.SampleApps.Fragment.TAPLoginVerificationFragment;
import io.moselo.SampleApps.Fragment.TAPPhoneLoginFragment;
import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalk.ViewModel.TAPLoginViewModel;
import io.taptalk.TapTalkSample.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.REGISTER;

public class TAPLoginActivity extends TAPBaseActivity {

    private static final String TAG = TAPLoginActivity.class.getSimpleName();
    private FrameLayout flContainer;
    private TAPLoginViewModel vm;

    public static void start(
            Context context,
            String instanceKey) {
        Intent intent = new Intent(context, TAPLoginActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_login);
        initViewModel();
        initView();
        initFirstPage();
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
                    TAPApiManager.getInstance().setLoggedOut(false);
                    TapUIRoomListActivity.start(TAPLoginActivity.this, instanceKey);
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

    public void showOTPVerification(Long otpID, String otpKey, String phoneNumber, String phoneNumberWithCode, int countryID, String countryCallingID, String countryFlagUrl) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.tap_slide_left_fragment, R.animator.tap_fade_out_fragment, R.animator.tap_fade_in_fragment, R.animator.tap_slide_right_fragment)
                .replace(R.id.fl_container, TAPLoginVerificationFragment.Companion.getInstance(otpID, otpKey, phoneNumber, phoneNumberWithCode, countryID, countryCallingID, countryFlagUrl))
                .addToBackStack(null)
                .commit();
    }

    public void setLastLoginData(Long otpID, String otpKey, String phoneNumber, String phoneNumberWithCode, int countryID, String countryCallingID) {
        vm.setLastLoginData(otpID, otpKey, phoneNumber, phoneNumberWithCode, countryID, countryCallingID);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPLoginViewModel.class);
    }

    public TAPLoginViewModel getVm() {
        return null == vm ? vm = ViewModelProviders.of(this).get(TAPLoginViewModel.class) : vm;
    }
}
