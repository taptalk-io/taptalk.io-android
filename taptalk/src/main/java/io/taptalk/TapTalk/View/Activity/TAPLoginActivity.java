package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import io.taptalk.TapTalk.View.Fragment.TAPLoginVerificationFragment;
import io.taptalk.TapTalk.View.Fragment.TAPPhoneLoginFragment;
import io.taptalk.Taptalk.R;

public class TAPLoginActivity extends TAPBaseActivity {

    private static final String TAG = TAPLoginActivity.class.getSimpleName();
    private FrameLayout flContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_login);

        initView();
        initFirstPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void showOTPVerification(Long otpID, String otpKey, String phoneNumber, String phoneNumberWithCode, int countryID, String countryCallingID) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.tap_slide_left_fragment, R.animator.tap_fade_out_fragment, R.animator.tap_fade_in_fragment, R.animator.tap_slide_right_fragment)
                .replace(R.id.fl_container, TAPLoginVerificationFragment.Companion.getInstance(otpID, otpKey, phoneNumber, phoneNumberWithCode, countryID, countryCallingID))
                .addToBackStack(null)
                .commit();
    }

}
