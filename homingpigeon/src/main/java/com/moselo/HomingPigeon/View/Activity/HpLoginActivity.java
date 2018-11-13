package com.moselo.HomingPigeon.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moselo.HomingPigeon.API.Api.TAPApiManager;
import com.moselo.HomingPigeon.API.View.TapDefaultDataView;
import com.moselo.HomingPigeon.Helper.TapTalk;
import com.moselo.HomingPigeon.Helper.TapTalkDialog;
import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Manager.HpConnectionManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpCommonResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.R;

import java.net.URL;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_MY_USERNAME;

public class HpLoginActivity extends HpBaseActivity {

    private static final String TAG = HpLoginActivity.class.getSimpleName();
    private TextInputEditText etUsername;
    private TextView tvSignIn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_login);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView() {
        etUsername = findViewById(R.id.et_username);
        tvSignIn = findViewById(R.id.tv_sign_in);
        progressBar = findViewById(R.id.pb_signing_in);

        etUsername.setOnEditorActionListener((v, actionId, event) -> {
            attemptLogin();
            return false;
        });

        tvSignIn.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        if (etUsername.getText().toString().equals("")) {
            etUsername.setError("Please fill your username.");
        } else if (!checkValidUsername(etUsername.getText().toString().toLowerCase())) {
            etUsername.setError("Please enter valid username.");
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

    private void setDataAndCallAPI() throws Exception {
        String ipAddress = TAPUtils.getInstance().getStringFromURL(new URL("https://api.ipify.org/"));
        String userAgent = "android";
        String userPlatform = "android";
        String xcUserID = getDummyUserID(etUsername.getText().toString()) + "";
        String fullname = getDummyUserFullName(xcUserID);
        String email = etUsername.getText().toString() + "@moselo.com";
        String phone = "08979809026";
        String username = etUsername.getText().toString();
        String deviceID = Settings.Secure.getString(TapTalk.appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        HpDataManager.getInstance().getAuthTicket(ipAddress, userAgent, userPlatform, deviceID, xcUserID,
                fullname, email, phone, username, authView);
    }

    // TODO: 14/09/18 nanti ini harus dihilangin (Wajib)
    private boolean checkValidUsername(String username) {
        switch (username) {
            case "ritchie":
            case "dominic":
            case "rionaldo":
            case "kevin":
            case "welly":
            case "jony":
            case "michael":
            case "richard":
            case "erwin":
            case "jefry":
            case "cundy":
            case "rizka":
            case "test1":
            case "test2":
            case "test3":
            case "santo":
                return true;

            default:
                return false;
        }
    }

    // TODO: 14/09/18 nanti ini harus dihilangin (Wajib)
    private int getDummyUserID(String username) {
        switch (username) {
            case "ritchie":
                return 1;
            case "dominic":
                return 2;
            case "rionaldo":
                return 3;
            case "kevin":
                return 4;
            case "welly":
                return 5;
            case "jony":
                return 6;
            case "michael":
                return 7;
            case "richard":
                return 8;
            case "erwin":
                return 9;
            case "jefry":
                return 10;
            case "cundy":
                return 11;
            case "rizka":
                return 12;
            case "test1":
                return 13;
            case "test2":
                return 14;
            case "test3":
                return 15;
            case "santo":
                return 16;
            default:
                return 0;
        }
    }

    // TODO: 04/10/18 DUMMY
    private String getDummyUserFullName(String userID) {
        switch (userID) {
            case "1":
                return "Ritchie Nathaniel";
            case "2":
                return "Dominic Vedericho";
            case "3":
                return "Rionaldo Linggautama";
            case "4":
                return "Kevin Reynaldo";
            case "5":
                return "Welly Kencana";
            case "6":
                return "Jony Lim";
            case "7":
                return "Michael Tansy";
            case "8":
                return "Richard Fang";
            case "9":
                return "Erwin Andreas";
            case "10":
                return "Jefry Lorentono";
            case "11":
                return "Cundy Sunardy";
            case "12":
                return "Rizka Fatmawati";
            case "13":
                return "Test 1";
            case "14":
                return "Test 2";
            case "15":
                return "Test 3";
            case "16":
                return "Santo";
            default:
                return "User Ga Tau Dari Mana ini";
        }
    }

    TapDefaultDataView<HpAuthTicketResponse> authView = new TapDefaultDataView<HpAuthTicketResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
        }

        @Override
        public void endLoading() {
            super.endLoading();
        }

        @Override
        public void onSuccess(HpAuthTicketResponse response) {
            super.onSuccess(response);
            TAPApiManager.getInstance().setLogout(false);
            TapTalk.saveAuthTicketAndGetAccessToken(response.getTicket()
                            , accessTokenView);
        }

        @Override
        public void onError(HpErrorModel error) {
            super.onError(error);
            showDialog("ERROR "+error.getCode(), error.getMessage());
        }
    };

    TapDefaultDataView<HpGetAccessTokenResponse> accessTokenView = new TapDefaultDataView<HpGetAccessTokenResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
        }

        @Override
        public void endLoading() {
            super.endLoading();
        }

        @Override
        public void onSuccess(HpGetAccessTokenResponse response) {
            super.onSuccess(response);
            HpDataManager.getInstance().deleteAuthTicket();

            HpDataManager.getInstance().saveAccessToken(response.getAccessToken());
            HpDataManager.getInstance().saveRefreshToken(response.getRefreshToken());
            HpDataManager.getInstance().saveRefreshTokenExpiry(response.getRefreshTokenExpiry());
            HpDataManager.getInstance().saveAccessTokenExpiry(response.getAccessTokenExpiry());
            registerFcmToken();

            HpDataManager.getInstance().saveActiveUser(response.getUser());
            runOnUiThread(() -> {
                Intent intent = new Intent(HpLoginActivity.this, HpRoomListActivity.class);
                intent.putExtra(K_MY_USERNAME, etUsername.getText().toString());
                startActivity(intent);
                HpConnectionManager.getInstance().connect();
                finish();
            });
        }

        @Override
        public void onError(HpErrorModel error) {
            super.onError(error);
            showDialog("ERROR "+error.getCode(), error.getMessage());
        }
    };

    private void showDialog(String title, String message) {
        new TapTalkDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle("OK")
                .setPrimaryButtonListener(view -> {
                    progressBar.setVisibility(View.GONE);
                    tvSignIn.setVisibility(View.VISIBLE);
                }).show();
    }

    private void registerFcmToken(){
        new Thread(() -> HpDataManager.getInstance().registerFcmTokenToServer(HpDataManager.getInstance().getFirebaseToken(), new TapDefaultDataView<HpCommonResponse>() {})).start();
    }
}
