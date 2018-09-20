package com.moselo.HomingPigeon.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_MY_USERNAME;

public class LoginActivity extends BaseActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextView tvSignIn;
    private ProgressBar progressBar;
    private View vOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvSignIn = findViewById(R.id.tv_sign_in);
        progressBar = findViewById(R.id.pb_signing_in);
        vOverlay = findViewById(R.id.v_signing_in);

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            attemptLogin();
            return false;
        });

        tvSignIn.setOnClickListener(v -> attemptLogin());

        vOverlay.setOnClickListener(v -> {
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void attemptLogin() {
        if (etUsername.getText().toString().equals("")) {
            etUsername.setError("Please fill your username.");
        } else if (etPassword.getText().toString().equals("")) {
            etPassword.setError("Please fill your password.");
        } else if (!checkValidUsername(etUsername.getText().toString())) {
            etUsername.setError("Please enter valid username.");
        } else {
            Utils.getInstance().dismissKeyboard(this);
            progressBar.setVisibility(View.VISIBLE);
            vOverlay.setVisibility(View.VISIBLE);

            Intent intent = new Intent(this, RoomListActivity.class);
            intent.putExtra(K_MY_USERNAME, etUsername.getText().toString());
            startActivity(intent);
            getUserID(getDummyUserID(etUsername.getText().toString())+ "", etUsername.getText().toString());
            ConnectionManager.getInstance().connect();
            finish();
        }
    }

    private void getUserID(String userID, String username) {
        UserModel userModel = UserModel.Builder(userID, username);
        DataManager.getInstance().saveActiveUser(this, userModel);
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

            default:
                return 0;
        }
    }
}
