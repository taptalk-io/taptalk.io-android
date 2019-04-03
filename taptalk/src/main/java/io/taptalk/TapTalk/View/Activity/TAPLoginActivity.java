package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;

import io.taptalk.TapTalk.View.Fragment.TAPLoginVerificationFragment;
import io.taptalk.TapTalk.View.Fragment.TAPPhoneLoginFragment;
import io.taptalk.Taptalk.R;

public class TAPLoginActivity extends TAPBaseActivity {

    private static final String TAG = TAPLoginActivity.class.getSimpleName();
    private TAPPhoneLoginFragment fPhoneLogin;
    private TAPLoginVerificationFragment fOTPVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_login);

        initView();
        showPhoneLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        fPhoneLogin = (TAPPhoneLoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_enter_phone);
        fOTPVerification = (TAPLoginVerificationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_verify_otp);
    }

    public void showPhoneLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .show(fPhoneLogin)
                .hide(fOTPVerification)
                .commit();
    }

    public void showOTPVerification() {
        getSupportFragmentManager()
                .beginTransaction()
                .show(fOTPVerification)
                .hide(fPhoneLogin)
                .commit();
    }


//    private void verifyOTP(long otpID, String otpKey) {
//        if (etPhoneAndOTP.getText().toString().equals("")) {
//            etPhoneAndOTP.setError("Please fill your OTP.");
//        } else {
//            TAPUtils.getInstance().dismissKeyboard(this);
//            progressBar.setVisibility(View.VISIBLE);
//            tvSignIn.setVisibility(View.GONE);
//            new Thread(() -> {
//                try {
//                    TapTalk.verifyOTP(otpID, otpKey, etPhoneAndOTP.getText().toString(), verifyOTPInterface);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//    }
//

//
//    TAPRequestOTPInterface requestOTPInterface = new TAPRequestOTPInterface() {
//        @Override
//        public void onRequestSuccess(long otpID, String otpKey, String phone, boolean succeess) {
//            etPhoneAndOTP.setHint("Please Enter your OTP Code");
//            etPhoneAndOTP.setText("");
//            tvSignIn.setText("VERIFY");
//            progressBar.setVisibility(View.GONE);
//            tvSignIn.setVisibility(View.VISIBLE);
//
//            etPhoneAndOTP.setOnEditorActionListener((v, actionId, event) -> {
//                verifyOTP(otpID, otpKey);
//                return false;
//            });
//
//            tvSignIn.setOnClickListener(v -> verifyOTP(otpID, otpKey));
//        }
//
//        @Override
//        public void onRequestFailed(String errorMessage, String errorCode) {
//            etPhoneAndOTP.setText("");
//            progressBar.setVisibility(View.GONE);
//            tvSignIn.setVisibility(View.VISIBLE);
//            showDialog("ERROR " + errorCode, errorMessage);
//        }
//    };
//
//    TAPVerifyOTPInterface verifyOTPInterface = new TAPVerifyOTPInterface() {
//        @Override
//        public void verifyOTPSuccessToLogin() {
//            TAPApiManager.getInstance().setLogout(false);
//            runOnUiThread(() -> {
//                Intent intent = new Intent(TAPLoginActivity.this, TAPRoomListActivity.class);
//                startActivity(intent);
//                finish();
//            });
//
//        }
//
//        @Override
//        public void verifyOTPSuccessToRegister() {
//            runOnUiThread(() -> Toast.makeText(TAPLoginActivity.this, "Register", Toast.LENGTH_SHORT).show());
//        }
//
//        @Override
//        public void verifyOTPFailed(String errorCode, String errorMessage) {
//            showDialog("ERROR ", errorMessage);
//        }
//    };

}
