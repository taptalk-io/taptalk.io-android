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
import io.taptalk.TapTalk.Const.TAPDefaultConstant.GROUP_MEMBER_LIMIT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_ADD_MEMBER
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_OPEN_MEMBER_PROFILE
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPGroupMemberListListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPGroupManager
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
import kotlinx.android.synthetic.main.tap_layout_popup_loading_screen.*

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
                if (groupViewModel?.selectedMembers?.size!! > 1) {
                    TapTalkDialog.Builder(this)
                            .setTitle("${resources.getString(R.string.tap_remove_group_members)}s")
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_multiple_members_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                TAPDataManager.getInstance().removeRoomParticipant(groupViewModel?.groupData?.roomID
                                        ?: "",
                                        groupViewModel?.selectedMembers?.keys?.toList(), removeRoomMembersView)
                            }
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setSecondaryButtonListener {}
                            .show()
                } else {
                    TapTalkDialog.Builder(this)
                            .setTitle(resources.getString(R.string.tap_remove_group_members))
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_member_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                TAPDataManager.getInstance().removeRoomParticipant(groupViewModel?.groupData?.roomID
                                        ?: "",
                                        groupViewModel?.selectedMembers?.keys?.toList(), removeRoomMembersView)
                            }
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setSecondaryButtonListener {}
                            .show()
                }
            }

            R.id.ll_promote_demote_admin -> {
                when (groupViewModel?.adminButtonStatus) {
                    TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE -> {
                        TapTalkDialog.Builder(this)
                                .setTitle(resources.getString(R.string.tap_promote_admin))
                                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                .setMessage(getString(R.string.tap_promote_admin_confirmation))
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .setPrimaryButtonListener {
                                    TAPDataManager.getInstance().promoteGroupAdmins(groupViewModel?.groupData?.roomID
                                            ?: "",
                                            groupViewModel?.getSelectedUserIDs(), appointAdminView)
                                }
                                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                                .setSecondaryButtonListener {}
                                .show()
                    }

                    TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE -> {
                        TapTalkDialog.Builder(this)
                                .setTitle(resources.getString(R.string.tap_demote_admin))
                                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                .setMessage(getString(R.string.tap_demote_admin_confirmation))
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .setPrimaryButtonListener {
                                    TAPDataManager.getInstance().demoteGroupAdmins(groupViewModel?.groupData?.roomID
                                            ?: "",
                                            groupViewModel?.getSelectedUserIDs(), appointAdminView)
                                }
                                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                                .setSecondaryButtonListener {}
                                .show()
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
                    groupViewModel?.groupData?.admins?.contains(contact?.userID) == true
                    && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
                groupViewModel?.addSelectedMember(contact)
                fl_add_members.visibility = View.VISIBLE
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageResource(R.drawable.tap_ic_demote_admins)
                tv_promote_demote_icon.text = resources.getText(R.string.tap_demote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE
                startSelectionMode()
            } else if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
                groupViewModel?.addSelectedMember(contact)
                fl_add_members.visibility = View.VISIBLE
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageResource(R.drawable.tap_ic_appoint_admin)
                tv_promote_demote_icon.text = resources.getText(R.string.tap_promote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE
                startSelectionMode()
            }
        }

        override fun onContactSelected(contact: TAPUserModel?): Boolean {
            if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
                groupViewModel?.addSelectedMember(contact)
                fl_add_members.visibility = View.GONE
                ll_promote_demote_admin.visibility = View.GONE
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.NOT_SHOWED
            }
            return true
        }

        override fun onContactDeselected(contact: TAPUserModel?) {
            if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
                groupViewModel?.removeSelectedMember(contact?.userID ?: "")
            }

            if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.isSelectedMembersEmpty() == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
                cancelSelectionMode(false)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.NOT_SHOWED
            } else if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.selectedMembers?.size == 1 &&
                    groupViewModel?.groupData?.admins?.contains(
                            groupViewModel?.selectedMembers?.entries?.iterator()?.next()?.value?.userID) == true
                    && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
                fl_add_members.visibility = View.VISIBLE
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageResource(R.drawable.tap_ic_demote_admins)
                tv_promote_demote_icon.text = resources.getText(R.string.tap_demote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE
            } else if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.selectedMembers?.size == 1
                    && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
                fl_add_members.visibility = View.VISIBLE
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

        tv_title.text = resources.getString(R.string.tap_group_members)
        if (initViewModel()) initView()
        else stateLoadingMember()
    }

    private fun stateLoadingMember() {
        sv_members.visibility = View.GONE
        ll_member_loading.visibility = View.VISIBLE
        TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_loading_progress)
    }

    private fun initView() {
        tv_title.text = resources.getString(R.string.tap_group_members)
        //groupViewModel?.groupData = intent.getParcelableExtra(ROOM)
        //groupViewModel?.setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))
        sv_members.visibility = View.VISIBLE
        ll_member_loading.visibility = View.GONE
        iv_loading_progress.clearAnimation()

        groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
                ?: mutableListOf()

        adapter = TAPGroupMemberAdapter(TAPGroupMemberAdapter.NORMAL_MODE, groupViewModel?.participantsList
                ?: mutableListOf(), groupViewModel?.groupData?.admins ?: listOf(), groupInterface)
        rv_contact_list.adapter = adapter
        rv_contact_list.layoutManager = LinearLayoutManager(this)
        rv_contact_list.setHasFixedSize(true)

        if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < GROUP_MEMBER_LIMIT) {
            fl_add_members.visibility = View.VISIBLE
        } else {
            fl_add_members.visibility = View.GONE
        }

        //set total member count
        tv_member_count.text = String.format(getString(R.string.tap_group_member_count), groupViewModel?.groupData?.groupParticipants?.size)
        tv_member_count.visibility = View.VISIBLE

        OverScrollDecoratorHelper.setUpOverScroll(sv_members)

        iv_button_back.setOnClickListener(this)
        iv_button_action.setOnClickListener(this)
        ll_add_button.setOnClickListener(this)
        ll_remove_button.setOnClickListener(this)
        ll_promote_demote_admin.setOnClickListener(this)
        fl_loading.setOnClickListener {}

        et_search.addTextChangedListener(searchTextWatcher)
        et_search.setOnEditorActionListener(searchEditorActionListener)
        et_search.hint = resources.getString(R.string.tap_search_for_group_members)
    }

    private fun initViewModel(): Boolean {
        groupViewModel = ViewModelProviders.of(this).get(TAPGroupMemberViewModel::class.java)
        groupViewModel?.setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))

        return null != groupViewModel?.groupData?.groupParticipants
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
            groupViewModel?.isSelectionMode == true -> {
                if (et_search.text.isNotEmpty()) et_search.setText("")
                cancelSelectionMode(true)
            }
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
        if (View.GONE == fl_add_members.visibility) fl_add_members.visibility = View.VISIBLE
        ll_button_admin_action.visibility = View.GONE

        if (View.GONE == ll_add_button.visibility) ll_add_button.visibility = View.VISIBLE
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
            showLoading(getString(R.string.tap_removing))
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            super.onSuccess(response)
            groupViewModel?.groupData = response?.room
            groupViewModel?.groupData?.groupParticipants = response?.participants
            groupViewModel?.groupData?.admins = response?.admins

            if (null != groupViewModel?.groupData) TAPGroupManager.getInstance.addGroupData(groupViewModel?.groupData!!)
            //adapter?.items = groupViewModel?.groupData?.groupParticipants
            adapter?.setMemberItems(groupViewModel?.groupData?.groupParticipants ?: listOf())

            // Set total member count
            tv_member_count.text = String.format(getString(R.string.tap_group_member_count), groupViewModel?.groupData?.groupParticipants?.size)
            tv_member_count.visibility = View.VISIBLE
            groupViewModel?.isUpdateMember = true

            Handler().postDelayed({
                cancelSelectionMode(true)
                this@TAPGroupMemberListActivity.endLoading(getString(R.string.tap_removed_member))
            }, 400L)
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberListActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), error!!.message)
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberListActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general))
        }
    }

    private val appointAdminView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading(getString(R.string.tap_updating))
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            groupViewModel?.groupData = response?.room
            groupViewModel?.groupData?.groupParticipants = response?.participants
            groupViewModel?.groupData?.admins = response?.admins

            if (null != groupViewModel?.groupData) TAPGroupManager.getInstance.addGroupData(groupViewModel?.groupData!!)
            //adapter?.items = groupViewModel?.groupData?.groupParticipants
            adapter?.adminList = groupViewModel?.groupData?.admins ?: mutableListOf()
            adapter?.setMemberItems(groupViewModel?.groupData?.groupParticipants ?: listOf())

            // Set total member count
            tv_member_count.text = String.format(getString(R.string.tap_group_member_count), groupViewModel?.groupData?.groupParticipants?.size)
            tv_member_count.visibility = View.VISIBLE
            groupViewModel?.isUpdateMember = true

            Handler().postDelayed({
                cancelSelectionMode(true)
                this@TAPGroupMemberListActivity.endLoading(getString(R.string.tap_promoted_admin))
            }, 400L)
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberListActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), error!!.message)
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberListActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general))
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

                    if (groupViewModel?.groupData?.groupParticipants?.size ?: 0 >= GROUP_MEMBER_LIMIT) {
                        fl_add_members.visibility = View.GONE
                    }

                    //set total member count
                    tv_member_count.text = String.format(getString(R.string.tap_group_member_count), groupViewModel?.groupData?.groupParticipants?.size)
                    tv_member_count.visibility = View.VISIBLE
                    groupViewModel?.isUpdateMember = true
                }

                GROUP_OPEN_MEMBER_PROFILE -> {
                    if (null != data?.getParcelableExtra(ROOM)) {
                        val roomModel: TAPRoomModel? = data.getParcelableExtra(ROOM)
                        groupViewModel?.groupData = roomModel
                        //adapter?.items = groupViewModel?.groupData?.groupParticipants
                        adapter?.adminList = groupViewModel?.groupData?.admins ?: mutableListOf()
                        adapter?.items = groupViewModel?.groupData?.groupParticipants ?: listOf()

                        // Set total member count
                        tv_member_count.text = String.format(getString(R.string.tap_group_member_count), groupViewModel?.groupData?.groupParticipants?.size)
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

    private fun showLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(getDrawable(R.drawable.tap_ic_loading_progress_circle_white))
            if (null == iv_loading_image.animation)
                TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_loading_image)
            tv_loading_text.text = message
            iv_button_action.setOnClickListener(null)
            fl_loading.visibility = View.VISIBLE
        }
    }

    private fun endLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(getDrawable(R.drawable.tap_ic_checklist_pumpkin))
            iv_loading_image.clearAnimation()
            tv_loading_text.text = message
            Handler().postDelayed({
                hideLoading()
                iv_button_action.setOnClickListener(this)
            }, 1000L)
        }
    }

    private fun hideLoading() {
        fl_loading.visibility = View.GONE
    }

    private fun showErrorDialog(title: String, message: String) {
        TapTalkDialog.Builder(this@TAPGroupMemberListActivity)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener {}
                .show()
    }
}