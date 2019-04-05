package io.taptalk.TapTalk.View.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.taptalk.Taptalk.R;

public class TAPCountryList extends AppCompatActivity {
    private TextView tvCloseBtn;
    private EditText etSearch;
    private RecyclerView rvCountryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_country_list);
        initView();
    }

    private void initView() {
        tvCloseBtn = findViewById(R.id.tv_close_btn);
        etSearch = findViewById(R.id.et_search);
        rvCountryList = findViewById(R.id.rv_country_list);

        tvCloseBtn.setOnClickListener(v -> onBackPressed());
    }
}
