package com.moselo.HomingPigeon.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Helper.Const;

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

    private void initView() {
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

        vOverlay.setOnClickListener(v -> {});
    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private  boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void attemptLogin() {
        if (etUsername.getText().toString().equals("")) {
            Toast.makeText(this, "Please fill your username.", Toast.LENGTH_SHORT).show();
        }
        else if (etPassword.getText().toString().equals("")) {
            Toast.makeText(this, "Please fill your password.", Toast.LENGTH_SHORT).show();
        }
        else {
            Utils.getInstance().dismissKeyboard(this);
            progressBar.setVisibility(View.VISIBLE);
            vOverlay.setVisibility(View.VISIBLE);

            Intent intent = new Intent(this, RoomListActivity.class);
            intent.putExtra(Const.K_MY_USERNAME, etUsername.getText().toString());
            startActivity(intent);
            ConnectionManager.getInstance().connect();
            getUserID(Utils.getInstance().generateRandomNumber(1000000)+"", etUsername.getText().toString());
            finish();
        }
    }

    private void getUserID(String userID, String username){
        UserModel userModel = UserModel.Builder(userID, username);
        DataManager.getInstance().saveActiveUser(this, userModel);
    }
}
