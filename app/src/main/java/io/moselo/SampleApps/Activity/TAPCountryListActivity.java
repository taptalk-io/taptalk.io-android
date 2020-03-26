package io.moselo.SampleApps.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.recyclerview_fastscroll.views.FastScrollRecyclerView;
import io.taptalk.TapTalk.Model.TAPCountryListItem;
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem;
import io.moselo.SampleApps.Adapter.TAPCountryListAdapter;
import io.taptalk.TaptalkSample.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_COUNTRY_PICK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.COUNTRY_PICK;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_INITIAL;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_ITEM;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_ITEM_BOTTOM;

public class TAPCountryListActivity extends AppCompatActivity {
    private TextView tvCloseBtn;
    private EditText etSearch;
    private FastScrollRecyclerView rvCountryList;
    private List<TAPCountryListItem> countryList;
    private TAPCountryListAdapter adapter;
    private int choosenCountryID = 0;

    public static void start(
            Activity context,
            String instanceKey,
            ArrayList<TAPCountryListItem> countryListItems,
            int defaultCountryID) {
        Intent intent = new Intent(context, TAPCountryListActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(COUNTRY_LIST, countryListItems);
        intent.putExtra(COUNTRY_ID, defaultCountryID);
        context.startActivityForResult(intent, COUNTRY_PICK);
        context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
    }

    public interface TAPCountryPickInterface {
        void onPick(TAPCountryListItem country);
    }

    private TAPCountryPickInterface countryPickInterface = country -> {
        Intent intent = new Intent();
        intent.putExtra(K_COUNTRY_PICK, country);
        setResult(RESULT_OK, intent);
        onBackPressed();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_country_list);
        initPassingIntentData();
        initView();
    }

    private void initPassingIntentData() {
        try {
            countryList = getIntent().getParcelableArrayListExtra(COUNTRY_LIST);
            choosenCountryID = getIntent().getIntExtra(COUNTRY_ID, 0);
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
            adapter = new TAPCountryListAdapter(setupDataForRecycler(""), countryPickInterface);
            rvCountryList.setAdapter(adapter);
            rvCountryList.setHasFixedSize(true);
            rvCountryList.setLayoutManager(new LinearLayoutManager(TAPCountryListActivity.this, LinearLayoutManager.VERTICAL, false));
        } catch (Exception e) {
            e.printStackTrace();
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
                if ((countryCounter == countryListSize - 1 || (0 < countryList.get(countryCounter + 1).getCommonName().length() &&
                        (countryList.get(countryCounter + 1).getCommonName().charAt(0) != countryInitial
                                || !countryList.get(countryCounter + 1).getCommonName().toLowerCase().contains(searchKeyword.toLowerCase()))))
                        && choosenCountryID == entry.getCountryID()) {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM_BOTTOM);
                    countryRecycleItem.setCountryListItem(entry);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryRecycleItem.setSelected(true);
                    countryItem.add(countryRecycleItem);
                } else if (countryCounter == countryListSize - 1 || (0 < countryList.get(countryCounter + 1).getCommonName().length() &&
                        (countryList.get(countryCounter + 1).getCommonName().charAt(0) != countryInitial
                                || !countryList.get(countryCounter + 1).getCommonName().toLowerCase().contains(searchKeyword.toLowerCase())))) {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM_BOTTOM);
                    countryRecycleItem.setCountryListItem(entry);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryRecycleItem.setSelected(false);
                    countryItem.add(countryRecycleItem);
                } else if (choosenCountryID == entry.getCountryID()) {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM);
                    countryRecycleItem.setCountryListItem(entry);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryRecycleItem.setSelected(true);
                    countryItem.add(countryRecycleItem);
                } else {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM);
                    countryRecycleItem.setCountryListItem(entry);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryRecycleItem.setSelected(false);
                    countryItem.add(countryRecycleItem);
                }
            }
        }
        return countryItem;
    }

    private void searchCountry(String countryKeyword) {
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
