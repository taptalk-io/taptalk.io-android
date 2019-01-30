package io.taptalk.TapTalk.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TAPLoginInterface;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAuthTicketResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

public class TAPLoginActivity extends TAPBaseActivity {

    private static final String TAG = TAPLoginActivity.class.getSimpleName();
    private TextInputEditText etUsername;
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
        TAPDataManager.getInstance().getAuthTicket(ipAddress, userAgent, userPlatform, deviceID, xcUserID,
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
            case "veronica":
            case "poppy":
            case "axel":
            case "ovita":
            case "amalia":
            case "ronal":
            case "ardanti":
            case "anita":
            case "kevinfianto":
            case "dessy":
            case "neni":
            case "bernama":
            case "william":
            case "sarah":
            case "retyan":
            case "sekar":
            case "putri":
            case "mei":
            case "yuendry":
            case "ervin":
            case "fauzi":
            case "lucas":
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
            case "veronica":
                return 17;
            case "poppy":
                return 18;
            case "axel":
                return 19;
            case "ovita":
                return 20;
            case "putri":
                return 21;
            case "amalia":
                return 22;
            case "ronal":
                return 23;
            case "ardanti":
                return 24;
            case "anita":
                return 25;
            case "kevinfianto":
                return 26;
            case "dessy":
                return 27;
            case "neni":
                return 28;
            case "bernama":
                return 29;
            case "william":
                return 30;
            case "sarah":
                return 31;
            case "retyan":
                return 32;
            case "sekar":
                return 33;
            case "mei":
                return 34;
            case "yuendry":
                return 35;
            case "ervin":
                return 36;
            case "fauzi":
                return 37;
            case "lucas":
                return 38;
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
            case "17":
                return "Veronica Dian";
            case "18":
                return "Poppy Sibarani";
            case "19":
                return "Axel Soedarsono";
            case "20":
                return "Ovita";
            case "21":
                return "Putri Prima";
            case "22":
                return "Amalia Nanda";
            case "23":
                return "Ronal Gorba";
            case "24":
                return "Ardanti Wulandari";
            case "25":
                return "Anita";
            case "26":
                return "Kevin Fianto";
            case "27":
                return "Dessy Silitonga";
            case "28":
                return "Neni Nurhasanah";
            case "29":
                return "Bernama Sabur";
            case "30":
                return "William Raymond";
            case "31":
                return "Sarah Febrina";
            case "32":
                return "Retyan Arthasani";
            case "33":
                return "Sekar Sari";
            case "34":
                return "Meilika";
            case "35":
                return "Yuendry";
            case "36":
                return "Ervin";
            case "37":
                return "Fauzi";
            case "38":
                return "Lucas";
            default:
                return "";
        }
    }

    TapDefaultDataView<TAPAuthTicketResponse> authView = new TapDefaultDataView<TAPAuthTicketResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
        }

        @Override
        public void endLoading() {
            super.endLoading();
        }

        @Override
        public void onSuccess(TAPAuthTicketResponse response) {
            super.onSuccess(response);
            TAPApiManager.getInstance().setLogout(false);
            TapTalk.saveAuthTicketAndGetAccessToken(response.getTicket()
                    , loginInterface);
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            showDialog("ERROR " + error.getCode(), error.getMessage());
        }
    };

    TAPLoginInterface loginInterface = new TAPLoginInterface() {
        @Override
        public void onLoginSuccess() {
            runOnUiThread(() -> {
                Intent intent = new Intent(TAPLoginActivity.this, TAPRoomListActivity.class);
                startActivity(intent);
                finish();
            });
        }

        @Override
        public void onLoginFailed(TAPErrorModel error) {
            showDialog("ERROR ", error.getMessage());
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

}
