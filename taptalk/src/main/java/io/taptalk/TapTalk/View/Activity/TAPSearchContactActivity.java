package io.taptalk.TapTalk.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CONTACT_LIST;

public class TAPSearchContactActivity extends TAPBaseActivity {

    private LinearLayout llAddNewContact;
    private ImageView ivButtonBack, ivButtonCancel;
    private EditText etSearch;
    private RecyclerView rvSearchResults;

    private TAPContactListAdapter adapter;

    private TAPContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_search_contact);

        initViewModel();
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPContactListViewModel.class);
        vm.setContactList(getIntent().getParcelableArrayListExtra(CONTACT_LIST));
        vm.getFilteredContacts().addAll(vm.getContactList());
    }

    private void initView() {
        getWindow().setBackgroundDrawable(null);

        llAddNewContact = findViewById(R.id.ll_add_new_contact);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonCancel = findViewById(R.id.iv_button_cancel);
        etSearch = findViewById(R.id.et_search);
        rvSearchResults = findViewById(R.id.rv_search_results);

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new TAPContactListAdapter(TAPContactListAdapter.CHAT, vm.getFilteredContacts());
        rvSearchResults.setAdapter(adapter);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonCancel.setOnClickListener(v -> clearSearch());
        llAddNewContact.setOnClickListener(v -> openNewContactActivity());

        TAPUtils.getInstance().animateClickButton(llAddNewContact, 0.97f);

        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            TAPUtils.getInstance().dismissKeyboard(TAPSearchContactActivity.this);
            return false;
        });
    }

    private void clearSearch() {
        TAPUtils.getInstance().dismissKeyboard(this);
        etSearch.setText("");
        etSearch.clearFocus();
    }

    private void openNewContactActivity() {
        Intent intent = new Intent(this, TAPNewContactActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            vm.getFilteredContacts().clear();
            String searchKeyword = etSearch.getText().toString().toLowerCase().trim();
            if (searchKeyword.isEmpty()) {
                vm.getFilteredContacts().addAll(vm.getContactList());
            } else {
                for (TAPUserModel user : vm.getContactList()) {
                    if (user.getName().toLowerCase().contains(searchKeyword)) {
                        vm.getFilteredContacts().add(user);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
