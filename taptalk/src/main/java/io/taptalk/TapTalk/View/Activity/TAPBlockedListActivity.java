package io.taptalk.TapTalk.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TapContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_DEFAULT_CONTACT_LIST;

public class TAPBlockedListActivity extends TAPBaseActivity {

    private ImageView ivButtonBack;
    private RecyclerView rvBlockedList;

    private TapContactListAdapter adapter;

    private TAPContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_blocked_list);

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

        //Dummy Contacts
        if (vm.getContactList().size() == 0) {
            TAPUserModel u1 = new TAPUserModel("u1", "Dummy Spam 1");
            TAPUserModel u2 = new TAPUserModel("u2", "Dummy Spam 2");
            TAPUserModel u3 = new TAPUserModel("u3", "Tummy Spam 3");
            vm.getFilteredContacts().add(u1);
            vm.getFilteredContacts().add(u2);
            vm.getFilteredContacts().add(u3);
        }
        //End Dummy
    }

    private void initView() {
        getWindow().setBackgroundDrawable(null);

        ivButtonBack = findViewById(R.id.iv_button_back);
        rvBlockedList = findViewById(R.id.rv_blocked_list);

        adapter = new TapContactListAdapter(TAPUtils.generateContactListForRecycler(vm.getFilteredContacts(), TYPE_DEFAULT_CONTACT_LIST));
        rvBlockedList.setAdapter(adapter);
        rvBlockedList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvBlockedList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
    }
}
