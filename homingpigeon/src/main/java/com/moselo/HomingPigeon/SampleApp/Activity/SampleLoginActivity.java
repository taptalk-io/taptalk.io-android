package com.moselo.HomingPigeon.SampleApp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.SampleApp.Helper.Const;

public class SampleLoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextView tvSignIn;
    private ProgressBar progressBar;
    private View vOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_login);

        initView();
        ConnectionManager.getInstance().connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionManager.getInstance().close();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvSignIn = findViewById(R.id.tv_sign_in);
        progressBar = findViewById(R.id.pb_signing_in);
        vOverlay = findViewById(R.id.v_signing_in);

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                attemptLogin();
                return false;
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        vOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
            dismissKeyboard(this);
            progressBar.setVisibility(View.VISIBLE);
            vOverlay.setVisibility(View.VISIBLE);

            Intent intent = new Intent(this, SampleRoomListActivity.class);
            intent.putExtra(Const.K_MY_USERNAME, etUsername.getText().toString());
            startActivity(intent);
            ConnectionManager.getInstance().connect();
            getUserID();
            finish();
        }
    }

    private void getUserID(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(DefaultConstant.K_USER_ID, Utils.getInstance().generateRandomNumber(1000000)+"")
                .apply();
        Log.e(SampleLoginActivity.class.getSimpleName(), "getUserID: "+ prefs.getString(DefaultConstant.K_USER_ID,"0"));
    }

    private void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
}
