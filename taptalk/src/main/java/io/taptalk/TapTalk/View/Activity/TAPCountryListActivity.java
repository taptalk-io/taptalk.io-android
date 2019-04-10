package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.AlphabeticalFastScrolling.IndexFastScrollRecyclerView;
import io.taptalk.TapTalk.Model.TAPCountryListItem;
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem;
import io.taptalk.TapTalk.View.Adapter.TAPCountryListAdapter;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_INITIAL;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_ITEM;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_ITEM_BOTTOM;

public class TAPCountryListActivity extends AppCompatActivity {
    private TextView tvCloseBtn;
    private EditText etSearch;
    private IndexFastScrollRecyclerView rvCountryList;
    private List<TAPCountryListItem> countryList;
    private TAPCountryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_country_list);
        initPassingIntentData();
        initView();
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
        initAdapter();
    }

    private void initAdapter() {
        try {
            adapter = new TAPCountryListAdapter(setupDataForRecycler());
            rvCountryList.setAdapter(adapter);
            rvCountryList.setHasFixedSize(true);
            rvCountryList.setLayoutManager(new LinearLayoutManager(TAPCountryListActivity.this, LinearLayoutManager.VERTICAL, false));
            rvCountryList.setIndexTextSize(12);
            rvCountryList.setIndexBarColor("#33334c");
            rvCountryList.setIndexBarCornerRadius(0);
            rvCountryList.setIndexBarTransparentValue((float) 0.4);
            rvCountryList.setIndexbarMargin(0);
            rvCountryList.setIndexbarWidth(40);
            rvCountryList.setPreviewPadding(0);
            rvCountryList.setIndexBarTextColor("#FFFFFF");

            rvCountryList.setIndexBarVisibility(true);
            rvCountryList.setIndexbarHighLateTextColor("#33334c");
            rvCountryList.setIndexBarHighLateTextVisibility(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("><><><", "initAdapter: ", e);
        }
    }

    private List<TAPCountryRecycleItem> setupDataForRecycler() {
        List<TAPCountryRecycleItem> countryItem = new ArrayList<>();
        int countryListSize = countryList.size();
        for (int countryCounter = 0; countryCounter < countryListSize; countryCounter++) {
            TAPCountryListItem entry = countryList.get(countryCounter);
            char countryInitial = entry.getCommonName().charAt(0);
            if (0 == countryCounter ||
                    (0 < countryList.get(countryCounter - 1).getCommonName().length() &&
                            countryList.get(countryCounter - 1).getCommonName().charAt(0) != countryInitial)) {
                TAPCountryRecycleItem countryRecycleFirstInitial = new TAPCountryRecycleItem();
                countryRecycleFirstInitial.setRecyclerItemType(COUNTRY_INITIAL);
                countryRecycleFirstInitial.setCountryInitial(countryInitial);
                countryItem.add(countryRecycleFirstInitial);
            }

            TAPCountryRecycleItem countryRecycleItem = new TAPCountryRecycleItem();
            if (countryCounter == countryListSize - 1 || (countryCounter < countryListSize - 1 &&
                    0 < countryList.get(countryCounter + 1).getCommonName().length() &&
                    countryList.get(countryCounter + 1).getCommonName().charAt(0) != countryInitial)) {
                countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM_BOTTOM);
                countryRecycleItem.setCountryListItem(entry);
                countryRecycleItem.setCountryInitial(countryInitial);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
            } else {
                countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM);
                countryRecycleItem.setCountryListItem(entry);
                countryRecycleItem.setCountryInitial(countryInitial);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
                countryItem.add(countryRecycleItem);
            }
        }

        return countryItem;
    }
}
