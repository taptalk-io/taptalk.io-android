package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.recyclerview_fastscroll.views.FastScrollRecyclerView;
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
    private FastScrollRecyclerView rvCountryList;
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
        etSearch.addTextChangedListener(searchTextWatcher);

        initAdapter();
    }

    private void initAdapter() {
        try {
            adapter = new TAPCountryListAdapter(setupDataForRecycler(""));
            rvCountryList.setAdapter(adapter);
            rvCountryList.setHasFixedSize(true);
            rvCountryList.setLayoutManager(new LinearLayoutManager(TAPCountryListActivity.this, LinearLayoutManager.VERTICAL, false));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("><><><", "initAdapter: ", e);
        }
    }

    private List<TAPCountryRecycleItem> setupDataForRecycler(String searchKeyword) {
        List<TAPCountryRecycleItem> countryItem = new ArrayList<>();
        int countryListSize = countryList.size();
        for (int countryCounter = 0; countryCounter < countryListSize; countryCounter++) {
            TAPCountryListItem entry = countryList.get(countryCounter);

            if (entry.getCommonName().toLowerCase().contains(searchKeyword.toLowerCase())) {
                char countryInitial = entry.getCommonName().charAt(0);
                if (0 == countryItem.size() ||
                        (0 < countryList.get(countryCounter - 1).getCommonName().length() &&
                                countryList.get(countryCounter - 1).getCommonName().charAt(0) != countryInitial)) {
                    TAPCountryRecycleItem countryRecycleFirstInitial = new TAPCountryRecycleItem();
                    countryRecycleFirstInitial.setRecyclerItemType(COUNTRY_INITIAL);
                    countryRecycleFirstInitial.setCountryInitial(countryInitial);
                    countryItem.add(countryRecycleFirstInitial);
                }

                TAPCountryRecycleItem countryRecycleItem = new TAPCountryRecycleItem();
                if (countryCounter == countryListSize - 1 || (0 < countryList.get(countryCounter + 1).getCommonName().length() &&
                        (countryList.get(countryCounter + 1).getCommonName().charAt(0) != countryInitial
                        || !countryList.get(countryCounter + 1).getCommonName().toLowerCase().contains(searchKeyword.toLowerCase())))) {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM_BOTTOM);
                    countryRecycleItem.setCountryListItem(entry);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryItem.add(countryRecycleItem);
                } else {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM);
                    countryRecycleItem.setCountryListItem(entry);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryItem.add(countryRecycleItem);
                }
            }
        }
        return countryItem;
    }

    private void searchCountry(String countryKeyword) {
        List<TAPCountryRecycleItem> recycleItems = adapter.getItems();

        if ("".equals(countryKeyword) || countryKeyword.isEmpty()) {
            adapter.setItems(setupDataForRecycler(""));
        } else {
            adapter.setItems(setupDataForRecycler(countryKeyword));
        }
        adapter.notifyDataSetChanged();
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchCountry(etSearch.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
