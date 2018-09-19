package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.HorizontalDecoration;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.ContactListListener;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.ContactInitialAdapter;
import com.moselo.HomingPigeon.View.Adapter.ContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.Extras.GROUP_MEMBERS;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.Extras.MY_ID;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.GROUP_MEMBER_LIMIT;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.RequestCode.CREATE_GROUP;

public class CreateNewGroupActivity extends AppCompatActivity {

    LinearLayout llGroupMembers;
    ImageView ivButtonBack, ivButtonAction;
    TextView tvTitle, tvMemberCount;
    EditText etSearch;
    Button btnContinue;
    RecyclerView rvContactList, rvGroupMembers;

    ContactInitialAdapter contactListAdapter;
    ContactListAdapter selectedMembersAdapter;
    ContactListListener listener;
    ContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        initViewModel();
        initListener();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case CREATE_GROUP:
                        finish();
                        break;
                }
        }
    }

    private void initViewModel() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, ""));

        vm = ViewModelProviders.of(this).get(ContactListViewModel.class);
        vm.getSelectedContacts().add(myUser);
    }

    private void initListener() {
        listener = new ContactListListener() {
            @Override
            public boolean onContactSelected(UserModel contact, boolean isSelected) {
                Utils.getInstance().dismissKeyboard(CreateNewGroupActivity.this);
                if (isSelected) {
                    if (vm.getSelectedContacts().size() >= GROUP_MEMBER_LIMIT) {
                        // TODO: 18 September 2018 SHOW DIALOG
                        return false;
                    }
                    vm.getSelectedContacts().add(contact);
                    selectedMembersAdapter.notifyItemInserted(vm.getSelectedContacts().size());
                    updateSelectedMemberDecoration();
                } else {
                    int index = vm.getSelectedContacts().indexOf(contact);
                    vm.getSelectedContacts().remove(contact);
                    selectedMembersAdapter.notifyItemRemoved(index);
                    new Handler().postDelayed(() -> updateSelectedMemberDecoration(), 200L);
                }
                if (vm.getSelectedContacts().size() > 1) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.group_member_count), vm.getSelectedContacts().size(), GROUP_MEMBER_LIMIT));
                return true;
            }

            @Override
            public void onContactRemoved(UserModel contact) {
                Utils.getInstance().dismissKeyboard(CreateNewGroupActivity.this);
                if (vm.getFilteredContacts().contains(contact)) {
                    int index = vm.getFilteredContacts().indexOf(contact);
                    vm.getFilteredContacts().get(index).setSelected(false);
                    contactListAdapter.notifyDataSetChanged();
                }
                if (vm.getSelectedContacts().size() > 1) {
                    llGroupMembers.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> updateSelectedMemberDecoration(), 200L);
                } else {
                    llGroupMembers.setVisibility(View.GONE);
                }
                tvMemberCount.setText(String.format(getString(R.string.group_member_count), vm.getSelectedContacts().size(), GROUP_MEMBER_LIMIT));
            }
        };
    }

    private void initView() {
        //Dummy Contacts
        if (vm.getContactList().size() == 0) {
            UserModel u0 = new UserModel("u0", "Ababa");
            UserModel u1 = new UserModel("u1", "Bambang 1");
            UserModel u2 = new UserModel("u2", "Bambang 2");
            UserModel u3 = new UserModel("u3", "Bambang 3");
            UserModel u4 = new UserModel("u4", "Caca");
            UserModel u5 = new UserModel("u5", "Coco");
            UserModel u6 = new UserModel("u6", "123asd");
            UserModel u7 = new UserModel("u7", "!!!11111!!!");
            UserModel u8 = new UserModel("u8", "!!wkwkwk!!");
            vm.getContactList().add(u0);
            vm.getContactList().add(u1);
            vm.getContactList().add(u2);
            vm.getContactList().add(u3);
            vm.getContactList().add(u4);
            vm.getContactList().add(u5);
            vm.getContactList().add(u6);
            vm.getContactList().add(u7);
            vm.getContactList().add(u8);
            vm.getFilteredContacts().addAll(vm.getContactList());
        }
        //End Dummy

        getWindow().setBackgroundDrawable(null);

        llGroupMembers = findViewById(R.id.ll_group_members);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonAction = findViewById(R.id.iv_button_action);
        tvTitle = findViewById(R.id.tv_title);
        tvMemberCount = findViewById(R.id.tv_member_count);
        tvMemberCount = findViewById(R.id.tv_member_count);
        etSearch = findViewById(R.id.et_search);
        btnContinue = findViewById(R.id.btn_continue);
        rvContactList = findViewById(R.id.rv_contact_list);
        rvGroupMembers = findViewById(R.id.rv_group_members);

        vm.setSeparatedContacts(Utils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));

        contactListAdapter = new ContactInitialAdapter(ContactListAdapter.SELECT, vm.getSeparatedContacts(), listener);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        selectedMembersAdapter = new ContactListAdapter(ContactListAdapter.SELECTED_MEMBER, vm.getSelectedContacts(), listener, vm.getSelectedContacts().get(0).getUserID());
        rvGroupMembers.setAdapter(selectedMembersAdapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        updateSelectedMemberDecoration();

        etSearch.addTextChangedListener(searchTextWatcher);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            updateFilteredContacts(etSearch.getText().toString().toLowerCase());
            return true;
        });

        ivButtonBack.setOnClickListener(v -> onBackPressed());

        ivButtonAction.setOnClickListener(v -> {
            if (vm.isSelecting()) {
                showToolbar();
            } else {
                showSearchBar();
            }
        });

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupSubjectActivity.class);
            intent.putExtra(MY_ID, vm.getSelectedContacts().get(0).getUserID());
            intent.putParcelableArrayListExtra(GROUP_MEMBERS, new ArrayList<>(vm.getSelectedContacts()));
            startActivityForResult(intent, CREATE_GROUP);
        });
    }

    private void showToolbar() {
        vm.setSelecting(false);
        tvTitle.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.GONE);
        etSearch.setText("");
        etSearch.clearFocus();
        ivButtonAction.setImageResource(R.drawable.ic_search_grey);
        Utils.getInstance().dismissKeyboard(this);
    }

    private void showSearchBar() {
        vm.setSelecting(true);
        tvTitle.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        etSearch.requestFocus();
        ivButtonAction.setImageResource(R.drawable.ic_close_grey);
    }

    private void updateSelectedMemberDecoration() {
        if (rvGroupMembers.getItemDecorationCount() > 0) {
            rvGroupMembers.removeItemDecorationAt(0);
        }
        rvGroupMembers.addItemDecoration(new HorizontalDecoration(0, 0,
                0, Utils.getInstance().dpToPx(16), selectedMembersAdapter.getItemCount(),
                0, 0));
    }

    private void updateFilteredContacts(String searchKeyword) {
        vm.getSeparatedContacts().clear();
        vm.getFilteredContacts().clear();
        if (searchKeyword.trim().isEmpty()) {
            vm.getFilteredContacts().addAll(vm.getContactList());
        } else {
            List<UserModel> filteredContacts = new ArrayList<>();
            for (UserModel user : vm.getContactList()) {
                if (user.getName().toLowerCase().contains(searchKeyword)) {
                    filteredContacts.add(user);
                }
            }
            vm.getFilteredContacts().addAll(filteredContacts);
        }
        vm.setSeparatedContacts(Utils.getInstance().separateContactsByInitial(vm.getFilteredContacts()));
        contactListAdapter.setItems(vm.getSeparatedContacts());
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            updateFilteredContacts(s.toString().toLowerCase());
            etSearch.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
