package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Model.TAPUserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.TAPContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpContactListViewModel;

public class TAPBlockedListActivity extends TAPBaseActivity {

    private ImageView ivButtonBack;
    private RecyclerView rvBlockedList;

    private TAPContactListAdapter adapter;

    private HpContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_blocked_list);

        initViewModel();
        initView();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpContactListViewModel.class);

        //Dummy Contacts
        if (vm.getContactList().size() == 0) {
            TAPUserModel u1 = new TAPUserModel("u1", "Dummy Spam 1");
            TAPUserModel u2 = new TAPUserModel("u2", "Dummy Spam 2");
            TAPUserModel u3 = new TAPUserModel("u3", "Dummy Spam 3");
            vm.getFilteredContacts().add(u1);
            vm.getFilteredContacts().add(u2);
            vm.getFilteredContacts().add(u3);
        }
        //End Dummy
    }

    @Override
    protected void initView() {
        getWindow().setBackgroundDrawable(null);

        ivButtonBack = findViewById(R.id.iv_button_back);
        rvBlockedList = findViewById(R.id.rv_blocked_list);

        adapter = new TAPContactListAdapter(TAPContactListAdapter.NONE, vm.getFilteredContacts());
        rvBlockedList.setAdapter(adapter);
        rvBlockedList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvBlockedList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
    }
}
