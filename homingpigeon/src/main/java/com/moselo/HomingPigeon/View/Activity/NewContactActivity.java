package com.moselo.HomingPigeon.View.Activity;

import android.os.Bundle;

import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.R;

public class NewContactActivity extends BaseActivity {

    CircleImageView civAvatarUser;
    CircleImageView civAvatarExpert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        initView();
        setupDummyData();
    }

    private void initView() {
        civAvatarUser = findViewById(R.id.civ_user_avatar);
        civAvatarExpert = findViewById(R.id.civ_expert_avatar);
    }

    // TODO: 17/09/18 must be deleted if the real data comes
    private void setupDummyData() {
        GlideApp.with(this).load("https://images.pexels.com/photos/1115128/pexels-photo-1115128.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                .centerCrop().override(Utils.getInstance().getScreenWidth(), Utils.getInstance().getScreeHeight()).into(civAvatarUser);

        GlideApp.with(this).load("https://images.pexels.com/photos/1115128/pexels-photo-1115128.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                .centerCrop().override(Utils.getInstance().getScreenWidth(), Utils.getInstance().getScreeHeight()).into(civAvatarExpert);
    }
}
