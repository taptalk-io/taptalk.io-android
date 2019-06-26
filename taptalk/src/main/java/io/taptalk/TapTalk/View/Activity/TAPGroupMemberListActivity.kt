package io.taptalk.TapTalk.View.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Interface.TapTalkGroupMemberListInterface
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Adapter.TAPGroupMemberAdapter
import io.taptalk.TapTalk.ViewModel.TAPGroupMemberViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_create_new_group.*

class TAPGroupMemberListActivity : TAPBaseActivity() {

    var groupViewModel: TAPGroupMemberViewModel? = null
    var adapter: TAPGroupMemberAdapter? = null
    private val groupInterface = object : TapTalkGroupMemberListInterface {
        override fun onContactLongPress(contact: TAPUserModel?) {
            adapter?.updateCellMode(TAPGroupMemberAdapter.SELECT_MODE)
        }

        override fun onContactSelected(contact: TAPUserModel?): Boolean {
            return super.onContactSelected(contact)
        }

        override fun onContactDeselected(contact: TAPUserModel?) {
            super.onContactDeselected(contact)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_group_members)
        initViewModel()
        initView()
    }

    private fun initView() {
        tv_title.text = resources.getString(R.string.tap_group_members)
        groupViewModel?.groupData = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
                ?: mutableListOf()

        adapter = TAPGroupMemberAdapter(TAPGroupMemberAdapter.NORMAL_MODE, groupViewModel?.participantsList
                ?: mutableListOf(), groupInterface)
        rv_contact_list.adapter = adapter
        rv_contact_list.layoutManager = LinearLayoutManager(this)
        rv_contact_list.setHasFixedSize(true)

        //set total member count
        tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
        tv_member_count.visibility = View.VISIBLE

        //Action button click Listener
        iv_button_action.setOnClickListener { toggleSearchBar() }

        et_search.addTextChangedListener(searchTextWatcher)
        et_search.setOnEditorActionListener(searchEditorActionListener)
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPGroupMemberViewModel::class.java)
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

    private fun updateSearchedMember(keyword: String) {
        groupViewModel?.participantsList?.clear()
        if (keyword.isEmpty()) {
            groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
                    ?: mutableListOf()
            tv_member_count.visibility = View.VISIBLE
        } else {
            groupViewModel?.groupData?.groupParticipants?.forEach {
                if (it.name.toLowerCase().contains(keyword.toLowerCase())) {
                    groupViewModel?.participantsList?.add(it)
                }
            }
            tv_member_count.visibility = View.GONE
        }
        adapter?.items = groupViewModel?.participantsList
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            et_search.removeTextChangedListener(this)
            updateSearchedMember(s?.toString() ?: "")
            et_search.addTextChangedListener(this)
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