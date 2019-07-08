package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_ADD_MEMBER
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_OPEN_MEMBER_PROFILE
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TAPGroupMemberListListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPRoomModel
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
import kotlinx.android.synthetic.main.tap_loading_layout_block_screen.*

@Suppress("CAST_NEVER_SUCCEEDS")
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

            R.id.ll_remove_button -> {
                TAPDataManager.getInstance().removeRoomParticipant(groupViewModel?.groupData?.roomID
                        ?: "",
                        groupViewModel?.selectedMembers?.keys?.toList(), removeRoomMembersView)
            }

            R.id.ll_promote_demote_admin -> {
                when (groupViewModel?.adminButtonStatus) {
                    TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE -> {
                        TAPDataManager.getInstance().promoteGroupAdmins(groupViewModel?.groupData?.roomID
                                ?: "",
                                groupViewModel?.getSelectedUserIDs(), appointAdminView)
                    }

                    TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE -> {
                        TAPDataManager.getInstance().demoteGroupAdmins(groupViewModel?.groupData?.roomID
                                ?: "",
                                groupViewModel?.getSelectedUserIDs(), appointAdminView)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    var groupViewModel: TAPGroupMemberViewModel? = null
    var adapter: TAPGroupMemberAdapter? = null
    private val groupInterface = object : TAPGroupMemberListListener() {
        override fun onContactLongPress(contact: TAPUserModel?) {
            if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.admins?.contains(contact?.userID) == true) {
                groupViewModel?.addSelectedMember(contact)
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageResource(R.drawable.tap_ic_demote_admins)
                tv_promote_demote_icon.text = resources.getText(R.string.tap_demote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE
                startSelectionMode()
            } else if (groupViewModel?.isActiveUserIsAdmin == true) {
                groupViewModel?.addSelectedMember(contact)
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageResource(R.drawable.tap_ic_appoint_admin)
                tv_promote_demote_icon.text = resources.getText(R.string.tap_promote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE
                startSelectionMode()
            }
        }

        override fun onContactSelected(contact: TAPUserModel?): Boolean {
            if (groupViewModel?.isActiveUserIsAdmin == true) {
                groupViewModel?.addSelectedMember(contact)
                ll_promote_demote_admin.visibility = View.GONE
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.NOT_SHOWED
            }
            return true
        }

        override fun onContactDeselected(contact: TAPUserModel?) {
            if (groupViewModel?.isActiveUserIsAdmin == true) {
                groupViewModel?.removeSelectedMember(contact?.userID ?: "")
            }

            if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.isSelectedMembersEmpty() == true) {
                cancelSelectionMode(false)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.NOT_SHOWED
            } else if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.selectedMembers?.size == 1 &&
                    groupViewModel?.groupData?.admins?.contains(
                            groupViewModel?.selectedMembers?.entries?.iterator()?.next()?.value?.userID) == true) {
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageResource(R.drawable.tap_ic_demote_admins)
                tv_promote_demote_icon.text = resources.getText(R.string.tap_demote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE
            } else if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.selectedMembers?.size == 1) {
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageResource(R.drawable.tap_ic_appoint_admin)
                tv_promote_demote_icon.text = resources.getText(R.string.tap_promote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE
            }
        }

        override fun onGroupMemberClicked(member: TAPUserModel?, isAdmin: Boolean) {
            if (member?.userID ?: "0" != TAPChatManager.getInstance().activeUser.userID) {
                val intent = Intent(this@TAPGroupMemberListActivity, TAPGroupMemberProfileActivity::class.java)
                intent.putExtra(ROOM, groupViewModel?.groupData)
                intent.putExtra(TAPDefaultConstant.K_USER, member)
                intent.putExtra(IS_ADMIN, isAdmin)
                startActivityForResult(intent, GROUP_OPEN_MEMBER_PROFILE)
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
        //groupViewModel?.groupData = intent.getParcelableExtra(ROOM)
        groupViewModel?.setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))
        groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
                ?: mutableListOf()

        adapter = TAPGroupMemberAdapter(TAPGroupMemberAdapter.NORMAL_MODE, groupViewModel?.participantsList
                ?: mutableListOf(), groupViewModel?.groupData?.admins ?: listOf(), groupInterface)
        rv_contact_list.adapter = adapter
        rv_contact_list.layoutManager = LinearLayoutManager(this)
        rv_contact_list.setHasFixedSize(true)

        if (groupViewModel?.isActiveUserIsAdmin == true) {
            fl_add_members.visibility = View.VISIBLE
        } else {
            fl_add_members.visibility = View.GONE
        }

        //set total member count
        tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
        tv_member_count.visibility = View.VISIBLE

        iv_button_back.setOnClickListener(this)
        iv_button_action.setOnClickListener(this)
        ll_add_button.setOnClickListener(this)
        ll_remove_button.setOnClickListener(this)
        ll_promote_demote_admin.setOnClickListener(this)
        fl_loading.setOnClickListener {}

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
                iv_button_action.setImageResource(R.drawable.tap_ic_search_orange)
                iv_button_action.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconNavBarMagnifier))
                TAPUtils.getInstance().dismissKeyboard(this@TAPGroupMemberListActivity, et_search)
            }

            else -> {
                //Show Search
                groupViewModel?.isSearchActive = true
                tv_title.visibility = View.GONE
                et_search.visibility = View.VISIBLE
                et_search.requestFocus()
                iv_button_action.setImageResource(R.drawable.tap_ic_close_grey)
                iv_button_action.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconClearTextButton))
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
        groupViewModel?.selectedMembers?.clear()
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
        ll_promote_demote_admin.visibility = View.VISIBLE
        ll_add_button.visibility = View.GONE
        adapter?.updateCellMode(TAPGroupMemberAdapter.SELECT_MODE)
    }

    private val removeRoomMembersView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading()
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            super.onSuccess(response)
            groupViewModel?.groupData = response?.room
            groupViewModel?.groupData?.groupParticipants = response?.participants
            groupViewModel?.groupData?.admins = response?.admins
            //adapter?.items = groupViewModel?.groupData?.groupParticipants
            adapter?.setMemberItems(groupViewModel?.groupData?.groupParticipants ?: listOf())

            //set total member count
            tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
            tv_member_count.visibility = View.VISIBLE
            groupViewModel?.isUpdateMember = true

            Handler().postDelayed({
                cancelSelectionMode(true)
                this@TAPGroupMemberListActivity.endLoading()
            }, 400L)
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            this@TAPGroupMemberListActivity.endLoading()
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            this@TAPGroupMemberListActivity.endLoading()
        }
    }

    private val appointAdminView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading()
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            groupViewModel?.groupData = response?.room
            groupViewModel?.groupData?.groupParticipants = response?.participants
            groupViewModel?.groupData?.admins = response?.admins
            //adapter?.items = groupViewModel?.groupData?.groupParticipants
            adapter?.adminList = groupViewModel?.groupData?.admins ?: mutableListOf()
            adapter?.setMemberItems(groupViewModel?.groupData?.groupParticipants ?: listOf())

            //set total member count
            tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
            tv_member_count.visibility = View.VISIBLE
            groupViewModel?.isUpdateMember = true

            Handler().postDelayed({
                cancelSelectionMode(true)
                this@TAPGroupMemberListActivity.endLoading()
            }, 400L)
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberListActivity.endLoading()
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberListActivity.endLoading()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GROUP_ADD_MEMBER -> {
                    val updatedGroupParticipant = data?.getParcelableArrayListExtra<TAPUserModel>(GROUP_MEMBERS)
                    groupViewModel?.groupData?.groupParticipants = updatedGroupParticipant?.toMutableList()
                            ?: groupViewModel?.participantsList
                    adapter?.items = groupViewModel?.groupData?.groupParticipants
                    adapter?.notifyDataSetChanged()

                    //set total member count
                    tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
                    tv_member_count.visibility = View.VISIBLE
                    groupViewModel?.isUpdateMember = true
                }

                GROUP_OPEN_MEMBER_PROFILE -> {
                    if (null != data?.getParcelableExtra(ROOM)) {
                        val roomModel: TAPRoomModel? = data?.getParcelableExtra(ROOM)
                        groupViewModel?.groupData = roomModel
                        //adapter?.items = groupViewModel?.groupData?.groupParticipants
                        adapter?.adminList = groupViewModel?.groupData?.admins ?: mutableListOf()
                        adapter?.items = groupViewModel?.groupData?.groupParticipants ?: listOf()

                        //set total member count
                        tv_member_count.text = "${groupViewModel?.groupData?.groupParticipants?.size} Members"
                        tv_member_count.visibility = View.VISIBLE
                        groupViewModel?.isUpdateMember = true
                    }

                    if (data?.getBooleanExtra(IS_NEED_TO_CLOSE_ACTIVITY_BEFORE, false) == true) {
                        val intent = Intent()
                        intent.putExtra(IS_NEED_TO_CLOSE_ACTIVITY_BEFORE, true)
                        setResult(Activity.RESULT_OK, intent)
                        onBackPressed()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        runOnUiThread {
            iv_saving.setImageDrawable(getDrawable(R.drawable.tap_ic_loading_progress_circle_white))
            if (null == iv_saving.animation)
                TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_saving)
            tv_loading_text.text = getString(R.string.tap_loading)
            iv_button_action.setOnClickListener(null)
            fl_loading.visibility = View.VISIBLE
        }
    }

    private fun endLoading() {
        runOnUiThread {
            iv_saving.setImageDrawable(getDrawable(R.drawable.tap_ic_checklist_pumpkin))
            iv_saving.clearAnimation()
            tv_loading_text.text = getString(R.string.tap_finished)
            Handler().postDelayed({
                hideLoading()
                iv_button_action.setOnClickListener(this)
            }, 1000L)
        }
    }

    private fun hideLoading() {
        fl_loading.visibility = View.GONE
    }
}