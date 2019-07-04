package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_ADD_MEMBER
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Interface.TapTalkGroupMemberListInterface
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Adapter.TAPGroupMemberAdapter
import io.taptalk.TapTalk.ViewModel.TAPGroupMemberViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_create_new_group.et_search
import kotlinx.android.synthetic.main.tap_activity_create_new_group.iv_button_action
import kotlinx.android.synthetic.main.tap_activity_create_new_group.iv_button_back
import kotlinx.android.synthetic.main.tap_activity_create_new_group.rv_contact_list
import kotlinx.android.synthetic.main.tap_activity_create_new_group.tv_member_count
import kotlinx.android.synthetic.main.tap_activity_create_new_group.tv_title
import kotlinx.android.synthetic.main.tap_activity_group_members.*

class TAPGroupMemberListActivity : TAPBaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_button_action -> {
                toggleSearchBar()
            }

            R.id.iv_button_back -> {
                onBackPressed()
            }

            R.id.ll_add_button -> {
                val intent = Intent(this, TAPAddMembersActivity::class.java)
                intent.putParcelableArrayListExtra(GROUP_MEMBERS, ArrayList(groupViewModel?.groupData?.groupParticipants))
                intent.putExtra(ROOM_ID, groupViewModel?.groupData?.roomID)
                startActivityForResult(intent, GROUP_ADD_MEMBER)
            }
        }
    }

    var groupViewModel: TAPGroupMemberViewModel? = null
    var adapter: TAPGroupMemberAdapter? = null
    private val groupInterface = object : TapTalkGroupMemberListInterface {
        override fun onContactLongPress(contact: TAPUserModel?) {
            groupViewModel?.addSelectedMember(contact)
            startSelectionMode()
        }

        override fun onContactSelected(contact: TAPUserModel?): Boolean {
            groupViewModel?.addSelectedMember(contact)
            return true
        }

        override fun onContactDeselected(contact: TAPUserModel?) {
            groupViewModel?.removeSelectedMember(contact?.userID ?: "")
            if (groupViewModel?.isSelectedMembersEmpty() == true) {
                cancelSelectionMode(false)
            }
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
                ?: mutableListOf(), groupViewModel?.groupData?.admins ?: listOf(), groupInterface)
        rv_contact_list.adapter = adapter
        rv_contact_list.layoutManager = LinearLayoutManager(this)
        rv_contact_list.setHasFixedSize(true)

        //set total member count
        tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
        tv_member_count.visibility = View.VISIBLE

        iv_button_back.setOnClickListener(this)
        iv_button_action.setOnClickListener(this)
        ll_add_button.setOnClickListener(this)

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

    override fun onBackPressed() {
        when {
            groupViewModel?.isSelectionMode == true -> cancelSelectionMode(true)
            groupViewModel?.isUpdateMember == true -> {
                val intent = Intent()
                intent.putExtra(ROOM, groupViewModel?.groupData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else -> finish()
        }
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

    private fun cancelSelectionMode(isNeedClearAll: Boolean) {
        groupViewModel?.isSelectionMode = false
        ll_button_admin_action.visibility = View.GONE
        ll_add_button.visibility = View.VISIBLE
        adapter?.updateCellMode(TAPGroupMemberAdapter.NORMAL_MODE)

        if (isNeedClearAll) {
            Thread(Runnable {
                adapter?.items?.forEach {
                    it.isSelected = false
                }
            }).start()
        }
    }

    private fun startSelectionMode() {
        groupViewModel?.isSelectionMode = true
        ll_button_admin_action.visibility = View.VISIBLE
        ll_add_button.visibility = View.GONE
        adapter?.updateCellMode(TAPGroupMemberAdapter.SELECT_MODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                GROUP_ADD_MEMBER -> {
                    val updatedGroupParticipant = data?.getParcelableArrayListExtra<TAPUserModel>(GROUP_MEMBERS)
                    groupViewModel?.groupData?.groupParticipants = updatedGroupParticipant?.toMutableList() ?: groupViewModel?.participantsList
                    adapter?.items = groupViewModel?.groupData?.groupParticipants
                    adapter?.notifyDataSetChanged()

                    //set total member count
                    tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
                    tv_member_count.visibility = View.VISIBLE
                    groupViewModel?.isUpdateMember = true
                }
            }
        }
    }
}