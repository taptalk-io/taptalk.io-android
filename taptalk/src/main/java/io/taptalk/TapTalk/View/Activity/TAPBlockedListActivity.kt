package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapContactListAdapter
import io.taptalk.TapTalk.ViewModel.TapBlockedListViewModel
import kotlinx.android.synthetic.main.tap_activity_blocked_list.*

class TAPBlockedListActivity : TAPBaseActivity() {
    private var adapter: TapContactListAdapter? = null
    val vm: TapBlockedListViewModel by lazy {
        ViewModelProvider(this)[TapBlockedListViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_blocked_list)
        initViewModel()
        initView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
    }

    private fun initViewModel() {
        // TODO: for testing purposes only MU
        //Dummy Contacts
        if (vm.blockedList.size == 0) {
            val u1 = TAPUserModel("u1", "Dummy Spam 1")
            val u2 = TAPUserModel("u2", "Dummy Spam 2")
            val u3 = TAPUserModel("u3", "Tummy Spam 3")
            vm.blockedList.add(u1)
            vm.blockedList.add(u2)
            vm.blockedList.add(u3)
        }
        //End Dummy
    }

    private fun initView() {
        window.setBackgroundDrawable(null)
        adapter = TapContactListAdapter(
            instanceKey,
            TAPUtils.generateContactListForRecycler(
                vm.blockedList,
                TapContactListModel.TYPE_DEFAULT_CONTACT_LIST
            )
        )
        rv_blocked_list.adapter = adapter
        rv_blocked_list.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        OverScrollDecoratorHelper.setUpOverScroll(
            rv_blocked_list,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        iv_button_back.setOnClickListener { onBackPressed() }
    }

    companion object {
        fun start(
            context: Context,
            instanceKey: String?
        ) {
            val intent = Intent(context, TAPBlockedListActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
            }
        }
    }
}