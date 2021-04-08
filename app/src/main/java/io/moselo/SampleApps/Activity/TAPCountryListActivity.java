package io.moselo.SampleApps.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.recyclerview_fastscroll.views.FastScrollRecyclerView;
import io.taptalk.TapTalk.Model.TAPCountryListItem;
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem;
import io.moselo.SampleApps.Adapter.TAPCountryListAdapter;
import io.taptalk.TapTalkSample.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_COUNTRY_PICK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.COUNTRY_PICK;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_INITIAL;
import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_ITEM;

public class TAPCountryListActivity extends AppCompatActivity {
    private ImageView ivCloseBtn, ivSearchIcon, ivSearchClose;
    private TextView tvToolbarTitle;
    private EditText etSearch;
    private ConstraintLayout clEmptyState;
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
        TAPUtils.dismissKeyboard(this);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_country_list);
        initPassingIntentData();
        initView();
    }

    @Override
    public void onBackPressed() {
        if (etSearch.getVisibility() == View.VISIBLE) {
            hideSearchBar();
        } else {
            TAPUtils.dismissKeyboard(this);
            super.onBackPressed();
        }
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
        ivCloseBtn = findViewById(R.id.iv_close_btn);
        etSearch = findViewById(R.id.et_search);
        rvCountryList = findViewById(R.id.rv_country_list);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivSearchClose = findViewById(R.id.iv_search_close);
        ivSearchIcon = findViewById(R.id.iv_search_icon);
        clEmptyState = findViewById(R.id.cl_empty_state);


        ivCloseBtn.setOnClickListener(v -> onBackPressed());
        etSearch.addTextChangedListener(searchTextWatcher);

        ivSearchIcon.setOnClickListener(view -> {
            showSearchBar();
        });

        ivSearchClose.setOnClickListener(view -> {
            etSearch.setText("");
        });

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
        List<TAPCountryRecycleItem> filteredCountries = new ArrayList<>();
        int countryListSize = countryList.size();

        for (int counter = 0; counter < countryListSize; counter++) {
            TAPCountryListItem country = countryList.get(counter);

            if (country.getCommonName().toLowerCase().contains(searchKeyword.toLowerCase())) {
                char countryInitial = country.getCommonName().charAt(0);
                if (searchKeyword.isEmpty() &&
                    (counter == 0 ||
                    (0 < countryList.get(counter - 1).getCommonName().length() &&
                    countryList.get(counter - 1).getCommonName().charAt(0) != countryInitial))
                ) {
                    TAPCountryRecycleItem countryRecycleFirstInitial = new TAPCountryRecycleItem();
                    countryRecycleFirstInitial.setRecyclerItemType(COUNTRY_INITIAL);
                    countryRecycleFirstInitial.setCountryInitial(countryInitial);
                    filteredCountries.add(countryRecycleFirstInitial);
                }

                TAPCountryRecycleItem countryRecycleItem = new TAPCountryRecycleItem();
                if (choosenCountryID == country.getCountryID()) {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM);
                    countryRecycleItem.setCountryListItem(country);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryRecycleItem.setSelected(true);
                    filteredCountries.add(countryRecycleItem);
                } else {
                    countryRecycleItem.setRecyclerItemType(COUNTRY_ITEM);
                    countryRecycleItem.setCountryListItem(country);
                    countryRecycleItem.setCountryInitial(countryInitial);
                    countryRecycleItem.setSelected(false);
                    filteredCountries.add(countryRecycleItem);
                }
            }
        }

        if (!searchKeyword.isEmpty()) {
            List<TAPCountryRecycleItem> filteredCountriesCopy = new ArrayList<>(filteredCountries);
            int initialCount = 0;
            for (int counter = 0; counter < filteredCountriesCopy.size(); counter++) {
                TAPCountryRecycleItem country = filteredCountriesCopy.get(counter);
                char countryInitial = country.getCountryInitial();
                if (counter == 0 ||
                    (0 < filteredCountriesCopy.get(counter - 1).getCountryListItem().getCommonName().length() &&
                    filteredCountriesCopy.get(counter - 1).getCountryListItem().getCommonName().charAt(0) != countryInitial)
                ) {
                    TAPCountryRecycleItem countryRecycleFirstInitial = new TAPCountryRecycleItem();
                    countryRecycleFirstInitial.setRecyclerItemType(COUNTRY_INITIAL);
                    countryRecycleFirstInitial.setCountryInitial(countryInitial);
                    filteredCountries.add(counter + initialCount, countryRecycleFirstInitial);
                    initialCount++;
                }
            }
        }

        return filteredCountries;
    }

    private void searchCountry(String countryKeyword) {
        if ("".equals(countryKeyword) || countryKeyword.isEmpty()) {
            adapter.setItems(setupDataForRecycler(""));
        } else {
            adapter.setItems(setupDataForRecycler(countryKeyword));
        }
        adapter.notifyDataSetChanged();

        if (adapter.getItems().size() == 0) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }

    private void showSearchBar() {
        ivSearchIcon.setVisibility(View.INVISIBLE);
        ivSearchIcon.setEnabled(false);
        etSearch.setVisibility(View.VISIBLE);
        TAPUtils.showKeyboard(this, etSearch);
        tvToolbarTitle.setVisibility(View.GONE);
    }

    private void hideSearchBar() {
        etSearch.setText("");
        etSearch.setVisibility(View.GONE);
        ivSearchIcon.setVisibility(View.VISIBLE);
        ivSearchIcon.setEnabled(true);
        TAPUtils.dismissKeyboard(this, etSearch);
        tvToolbarTitle.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        clEmptyState.setVisibility(View.VISIBLE);
        rvCountryList.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        clEmptyState.setVisibility(View.GONE);
        rvCountryList.setVisibility(View.VISIBLE);
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etSearch.getText().toString().isEmpty()) {
                ivSearchClose.setVisibility(View.GONE);
            }else {
                ivSearchClose.setVisibility(View.VISIBLE);
            }
            searchCountry(etSearch.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
