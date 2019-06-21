package io.taptalk.TapTalk.View.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import io.taptalk.TapTalk.View.Adapter.TAPGroupMemberAdapter
import io.taptalk.TapTalk.ViewModel.TAPGroupViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_create_new_group.*

class TAPGroupMemberListActivity : TAPBaseActivity() {

    var groupViewModel: TAPGroupViewModel? = null
    var adapter : TAPGroupMemberAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_create_new_group)
        initViewModel()
        initView()
    }

    private fun initView() {
        tv_title.text = resources.getString(R.string.tap_group_members)
        ll_group_members.visibility = View.GONE
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPGroupViewModel::class.java)
    }
}