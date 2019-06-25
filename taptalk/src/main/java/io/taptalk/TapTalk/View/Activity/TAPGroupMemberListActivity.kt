package io.taptalk.TapTalk.View.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Helper.TAPUtils
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

        //Action button click Listener
        iv_button_action.setOnClickListener { toggleSearchBar() }

        et_search.setOnEditorActionListener(searchEditorActionListener)
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPGroupViewModel::class.java)
    }

    private fun toggleSearchBar() {
        when (groupViewModel?.isSearchActive) {
            true -> {
                //Show Toolbar
                groupViewModel?.isSearchActive = false
                tv_title.visibility = View.VISIBLE
                et_search.visibility = View.GONE
                et_search.setText("")
                iv_button_action.setImageResource(R.drawable.tap_ic_search_grey)
                TAPUtils.getInstance().dismissKeyboard(this@TAPGroupMemberListActivity, et_search)
            }

            else -> {
                //Show Search
                groupViewModel?.isSearchActive = true
                tv_title.visibility = View.GONE
                et_search.visibility = View.VISIBLE
                et_search.requestFocus()
                iv_button_action.setImageResource(R.drawable.tap_ic_close_grey)
                TAPUtils.getInstance().showKeyboard(this, et_search)
            }
        }
    }

    private val searchEditorActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            TAPUtils.getInstance().dismissKeyboard(this@TAPGroupMemberListActivity, et_search)
            return@OnEditorActionListener true
        }

        return@OnEditorActionListener false
    }
}