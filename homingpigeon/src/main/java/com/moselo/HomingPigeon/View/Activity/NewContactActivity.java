package com.moselo.HomingPigeon.View.Activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.R;

public class NewContactActivity extends BaseActivity {

    EditText etSearch;
    ImageView ivButtonBack;
    ImageView ivButtonCancel;
    TextView tvSearchUsernameMessage;
    CardView cvExpertCard;
    ImageView ivExpertCover;
    CircleImageView civAvatarExpert;
    TextView tvBtnAddContactExpert;
    LinearLayout llBtnChatNowExpert;
    TextView tvExpertName;
    TextView tvExpertCategory;
    CardView cvUserCard;
    CircleImageView civAvatarUser;
    TextView tvBtnAddContactUser;
    LinearLayout llBtnChatNowUser;
    TextView tvUserName;
    LinearLayout llEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        initView();
        setupDummyData();
    }

    private void initView() {
        etSearch = findViewById(R.id.et_search);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonCancel = findViewById(R.id.iv_button_cancel);
        tvSearchUsernameMessage = findViewById(R.id.tv_search_username_message);
        cvExpertCard = findViewById(R.id.cv_expert_card);
        ivExpertCover = findViewById(R.id.iv_expert_cover);
        civAvatarExpert = findViewById(R.id.civ_expert_avatar);
        tvBtnAddContactExpert = findViewById(R.id.tv_btn_add_contact_expert);
        llBtnChatNowExpert = findViewById(R.id.ll_btn_chat_now_expert);
        tvExpertName = findViewById(R.id.tv_expert_name);
        tvExpertCategory = findViewById(R.id.tv_expert_category);
        cvUserCard = findViewById(R.id.cv_user_card);
        civAvatarUser = findViewById(R.id.civ_user_avatar);
        tvBtnAddContactUser = findViewById(R.id.tv_btn_add_contact_user);
        llBtnChatNowUser = findViewById(R.id.ll_btn_chat_now_user);
        tvUserName = findViewById(R.id.tv_user_name);
        llEmpty = findViewById(R.id.ll_empty);

        // TODO: 18/09/18 fix these (only dummy)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (0 == s.toString().length()){
                    tvSearchUsernameMessage.setTextColor(View.VISIBLE);
                    cvExpertCard.setVisibility(View.GONE);
                    cvUserCard.setVisibility(View.GONE);
                    llEmpty.setVisibility(View.GONE);
                } else if ("empty".equals(s.toString())) {
                    tvSearchUsernameMessage.setTextColor(View.GONE);
                    cvExpertCard.setVisibility(View.GONE);
                    cvUserCard.setVisibility(View.GONE);
                    llEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvSearchUsernameMessage.setTextColor(View.GONE);
                    cvExpertCard.setVisibility(View.VISIBLE);
                    cvUserCard.setVisibility(View.GONE);
                    llEmpty.setVisibility(View.GONE);
                }
            }
        });

        tvBtnAddContactExpert.setOnClickListener(v -> {
            tvBtnAddContactExpert.setVisibility(View.GONE);
            llBtnChatNowExpert.setVisibility(View.VISIBLE);
        });

        llBtnChatNowExpert.setOnClickListener(v -> {
            tvSearchUsernameMessage.setTextColor(View.GONE);
            cvExpertCard.setVisibility(View.GONE);
            cvUserCard.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        });

        tvBtnAddContactUser.setOnClickListener(v -> {
            tvBtnAddContactUser.setVisibility(View.GONE);
            llBtnChatNowUser.setVisibility(View.VISIBLE);
        });

        llBtnChatNowUser.setOnClickListener(v -> {
            tvSearchUsernameMessage.setTextColor(View.GONE);
            cvExpertCard.setVisibility(View.VISIBLE);
            cvUserCard.setVisibility(View.GONE);
            llEmpty.setVisibility(View.GONE);
        });

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        ivButtonCancel.setOnClickListener(v -> {
            etSearch.setText("");
            tvSearchUsernameMessage.setTextColor(View.VISIBLE);
            cvExpertCard.setVisibility(View.GONE);
            cvUserCard.setVisibility(View.GONE);
            llEmpty.setVisibility(View.GONE);
        });
    }

    // TODO: 17/09/18 must be deleted if the real data comes
    private void setupDummyData() {
        GlideApp.with(this).load("https://images.pexels.com/photos/1115128/pexels-photo-1115128.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                .centerCrop().override(Utils.getInstance().getScreenWidth(), Utils.getInstance().getScreeHeight()).into(civAvatarUser);

        GlideApp.with(this).load("https://images.pexels.com/photos/1115128/pexels-photo-1115128.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                .centerCrop().override(Utils.getInstance().getScreenWidth(), Utils.getInstance().getScreeHeight()).into(civAvatarExpert);
    }
}
