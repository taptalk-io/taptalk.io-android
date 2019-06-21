package io.taptalk.TapTalk.View.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.View.Adapter.TAPGroupMemberAdapter
import io.taptalk.TapTalk.ViewModel.TAPGroupViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_create_new_group.*

class TAPGroupMemberListActivity : TAPBaseActivity() {

    var groupViewModel: TAPGroupViewModel? = null
    var adapter: TAPGroupMemberAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_group_members)
        initViewModel()
        initView()
    }

    private fun initView() {
        tv_title.text = resources.getString(R.string.tap_group_members)
        groupViewModel?.groupData = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)

        adapter = TAPGroupMemberAdapter(groupViewModel?.groupData?.groupParticipants
                ?: mutableListOf())
        rv_contact_list.adapter = adapter
        rv_contact_list.layoutManager = LinearLayoutManager(this)
        rv_contact_list.setHasFixedSize(true)

        //set total member count
        tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPGroupViewModel::class.java)
    }
}