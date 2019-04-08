package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Model.TAPCountryListItem;
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem;
import io.taptalk.TapTalk.View.Adapter.TAPCountryListAdapter;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_INITIAL;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_ITEM;

public class TAPCountryListActivity extends AppCompatActivity {
    private TextView tvCloseBtn;
    private EditText etSearch;
    private RecyclerView rvCountryList;
    private List<TAPCountryListItem> countryList;
    private List<TAPCountryRecycleItem> countryItem = new ArrayList<>();
    private TAPCountryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_country_list);
        initPassingIntentData();
        initView();
        initAdapter();
    }

    private void initPassingIntentData() {
        try {
            countryList = getIntent().getParcelableArrayListExtra("CountryList");
        } catch (Exception e) {
            countryList = new ArrayList<>();
        }
    }

    private void initView() {
        tvCloseBtn = findViewById(R.id.tv_close_btn);
        etSearch = findViewById(R.id.et_search);
        rvCountryList = findViewById(R.id.rv_country_list);

        tvCloseBtn.setOnClickListener(v -> onBackPressed());
    }

    private void initAdapter() {
        try {
            countryItem.clear();
            int countryCounter = 0;
            char tempInitial = 'a';
            for (TAPCountryListItem entry : countryList) {
                char countryInitial = entry.getCommonName().charAt(0);
                if (0 == countryCounter || tempInitial != countryInitial) {
                    TAPCountryRecycleItem countryRecycleFirstInitial = new TAPCountryRecycleItem();
                    countryRecycleFirstInitial.setRecyclerItemType(COUNTRY_INITIAL);
                    countryRecycleFirstInitial.setCountryInitial(countryInitial);
                    countryItem.add(countryRecycleFirstInitial);
                }

                TAPCountryRecycleItem countryRecycleItem = new TAPCountryRecycleItem();
                countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM);
                countryRecycleItem.setCountryListItem(entry);
                countryItem.add(countryRecycleItem);

                tempInitial = countryInitial;
                countryCounter++;
            }

            adapter = new TAPCountryListAdapter(countryItem);
            rvCountryList.setAdapter(adapter);
            rvCountryList.setHasFixedSize(true);
            rvCountryList.setLayoutManager(new LinearLayoutManager(TAPCountryListActivity.this, LinearLayoutManager.VERTICAL, false));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("><><><", "initAdapter: ", e);
        }
    }
}
