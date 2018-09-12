package com.moselo.HomingPigeon.View.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moselo.HomingPigeon.R;

public class SampleNewChatActivity extends AppCompatActivity {

    LinearLayout llButtonNewContact, llButtonScanQR, llButtonNewGroup;
    ImageView ivButtonBack, ivButtonSearch;
    TextView tvTitle;
    RecyclerView rvContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_new_chat);
    }

    private void initView() {
        llButtonNewContact = findViewById(R.id.ll_button_new_contact);
        llButtonScanQR = findViewById(R.id.ll_button_scan_qr);
        llButtonNewGroup = findViewById(R.id.ll_button_new_group);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        tvTitle = findViewById(R.id.tv_title);
        rvContactList = findViewById(R.id.rv_contact_list);
    }
}
