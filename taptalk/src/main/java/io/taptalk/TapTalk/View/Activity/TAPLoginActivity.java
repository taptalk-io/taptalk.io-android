package io.taptalk.TapTalk.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface;
import io.taptalk.TapTalk.Interface.TAPVerifyOTPInterface;
import io.taptalk.Taptalk.R;

public class TAPLoginActivity extends TAPBaseActivity {

    private static final String TAG = TAPLoginActivity.class.getSimpleName();
    private TextInputEditText etPhoneAndOTP;
    private TextView tvSignIn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_login);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        etPhoneAndOTP = findViewById(R.id.et_phone_and_otp);
        tvSignIn = findViewById(R.id.tv_sign_in);
        progressBar = findViewById(R.id.pb_signing_in);
        etPhoneAndOTP.setHint("Phone Number");

        etPhoneAndOTP.setOnEditorActionListener((v, actionId, event) -> {
            attemptLogin();
            return false;
        });

        tvSignIn.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        if (etPhoneAndOTP.getText().toString().equals("")) {
            etPhoneAndOTP.setError("Please fill your Phone Number.");
        } else {
            TAPUtils.getInstance().dismissKeyboard(this);
            progressBar.setVisibility(View.VISIBLE);
            tvSignIn.setVisibility(View.GONE);

            new Thread(() -> {
                try {
                    setDataAndCallAPI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void verifyOTP(long otpID, String otpKey) {
        if (etPhoneAndOTP.getText().toString().equals("")) {
            etPhoneAndOTP.setError("Please fill your OTP.");
        } else {
            TAPUtils.getInstance().dismissKeyboard(this);
            progressBar.setVisibility(View.VISIBLE);
            tvSignIn.setVisibility(View.GONE);
            new Thread(() -> {
                try {
                    TapTalk.verifyOTP(otpID, otpKey, etPhoneAndOTP.getText().toString(), verifyOTPInterface);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void setDataAndCallAPI() {
        String phoneNumber = etPhoneAndOTP.getText().toString();
        if ('0' == phoneNumber.charAt(0))
            phoneNumber = phoneNumber.replaceFirst("0", "");
        else if ("+62".equals(phoneNumber.substring(0, 3)))
            phoneNumber = phoneNumber.substring(3);
        else if ("62".equals(phoneNumber.substring(0, 2)))
            phoneNumber = phoneNumber.substring(2);
        TapTalk.loginWithRequestOTP(1, phoneNumber, requestOTPInterface);
    }

    TAPRequestOTPInterface requestOTPInterface = new TAPRequestOTPInterface() {
        @Override
        public void onRequestSuccess(long otpID, String otpKey, String phone, boolean succeess) {
            etPhoneAndOTP.setHint("Please Enter your OTP Code");
            etPhoneAndOTP.setText("");
            tvSignIn.setText("VERIFY");
            progressBar.setVisibility(View.GONE);
            tvSignIn.setVisibility(View.VISIBLE);

            etPhoneAndOTP.setOnEditorActionListener((v, actionId, event) -> {
                verifyOTP(otpID, otpKey);
                return false;
            });

            tvSignIn.setOnClickListener(v -> verifyOTP(otpID, otpKey));
        }

        @Override
        public void onRequestFailed(String errorMessage, String errorCode) {
            etPhoneAndOTP.setText("");
            progressBar.setVisibility(View.GONE);
            tvSignIn.setVisibility(View.VISIBLE);
            showDialog("ERROR " + errorCode, errorMessage);
        }
    };

    TAPVerifyOTPInterface verifyOTPInterface = new TAPVerifyOTPInterface() {
        @Override
        public void verifyOTPSuccessToLogin() {
            TAPApiManager.getInstance().setLogout(false);
            runOnUiThread(() -> {
                Intent intent = new Intent(TAPLoginActivity.this, TAPRoomListActivity.class);
                startActivity(intent);
                finish();
            });

        }

        @Override
        public void verifyOTPSuccessToRegister() {
            runOnUiThread(() -> Toast.makeText(TAPLoginActivity.this, "Register", Toast.LENGTH_SHORT).show());
        }

        @Override
        public void verifyOTPFailed(String errorCode, String errorMessage) {
            showDialog("ERROR ", errorMessage);
        }
    };

    private void showDialog(String title, String message) {
        new TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener(view -> {
                    progressBar.setVisibility(View.GONE);
                    tvSignIn.setVisibility(View.VISIBLE);
                }).show();
    }

}
