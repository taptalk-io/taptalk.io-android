package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.SHORT_ANIMATION_TIME
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TAPUtils
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
import io.taptalk.TapTalk.R
import kotlinx.android.synthetic.main.tap_activity_group_members.*
import kotlinx.android.synthetic.main.tap_layout_popup_loading_screen.*

@Suppress("CAST_NEVER_SUCCEEDS")
class TAPGroupMemberListActivity : TAPBaseActivity(), View.OnClickListener {

    var groupViewModel: TAPGroupMemberViewModel? = null
    var adapter: TAPGroupMemberAdapter? = null

    companion object {
        fun start(
                context: Activity,
                instanceKey: String,
                room: TAPRoomModel
        ) {
            val intent = Intent(context, TAPGroupMemberListActivity::class.java)
            intent.putExtra(INSTANCE_KEY, instanceKey)
            intent.putExtra(ROOM, room)
            context.startActivityForResult(intent, GROUP_UPDATE_DATA)
            context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_group_members)

        tv_title.text = resources.getString(R.string.tap_group_members)
        if (initViewModel()) initView()
        else stateLoadingMember()
    }

    override fun onBackPressed() {
        when {
            groupViewModel?.isSearchActive == true -> {
                showToolbar()
            }
            groupViewModel?.isSelectionMode == true -> {
                if (et_search.text.isNotEmpty()) et_search.setText("")
                cancelSelectionMode(true)
            }
            groupViewModel?.isUpdateMember == true -> {
                val intent = Intent()
                intent.putExtra(ROOM, groupViewModel?.groupData)
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
            }
            else -> {
                finish()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_button_search -> {
                showSearchBar()
            }

            R.id.iv_button_clear_text -> {
                et_search.setText("")
            }

            R.id.iv_button_back -> {
                onBackPressed()
            }

            R.id.ll_add_button -> {
                TAPAddGroupMemberActivity.start(
                        this,
                        instanceKey,
                        groupViewModel?.groupData?.roomID,
                        groupViewModel?.groupData?.groupParticipants?.let { ArrayList(it) })
            }

            R.id.ll_remove_button -> {
                if (groupViewModel?.selectedMembers?.size!! > 1) {
                    TapTalkDialog.Builder(this)
                            .setTitle("${resources.getString(R.string.tap_remove_group_member)}s")
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_multiple_members_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                groupViewModel?.loadingStartText = getString(R.string.tap_removing)
                                groupViewModel?.loadingEndText = getString(R.string.tap_removed_member)
                                TAPDataManager.getInstance(instanceKey).removeRoomParticipant(groupViewModel?.groupData?.roomID
                                        ?: "",
                                        groupViewModel?.selectedMembers?.keys?.toList(), userActionView)
                            }
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setSecondaryButtonListener {}
                            .show()
                } else {
                    TapTalkDialog.Builder(this)
                            .setTitle(resources.getString(R.string.tap_remove_group_member))
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_member_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                groupViewModel?.loadingStartText = getString(R.string.tap_removing)
                                groupViewModel?.loadingEndText = getString(R.string.tap_removed_member)
                                TAPDataManager.getInstance(instanceKey).removeRoomParticipant(groupViewModel?.groupData?.roomID
                                        ?: "",
                                        groupViewModel?.selectedMembers?.keys?.toList(), userActionView)
                            }
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setSecondaryButtonListener {}
                            .show()
                }
            }

            R.id.ll_promote_demote_admin -> {
                when (groupViewModel?.adminButtonStatus) {
                    TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE -> {
                        groupViewModel?.loadingStartText = getString(R.string.tap_updating)
                        groupViewModel?.loadingEndText = getString(R.string.tap_promoted_admin)
                        TAPDataManager.getInstance(instanceKey).promoteGroupAdmins(groupViewModel?.groupData?.roomID
                                ?: "",
                                groupViewModel?.getSelectedUserIDs(), userActionView)
                    }

                    TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE -> {
                        TapTalkDialog.Builder(this)
                                .setTitle(resources.getString(R.string.tap_demote_admin))
                                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                .setMessage(getString(R.string.tap_demote_admin_confirmation))
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .setPrimaryButtonListener {
                                    groupViewModel?.loadingStartText = getString(R.string.tap_updating)
                                    groupViewModel?.loadingEndText = getString(R.string.tap_demoted_admin)
                                    TAPDataManager.getInstance(instanceKey).demoteGroupAdmins(groupViewModel?.groupData?.roomID
                                            ?: "",
                                            groupViewModel?.getSelectedUserIDs(), userActionView)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GROUP_ADD_MEMBER -> {
                    val updatedGroupParticipant = data?.getParcelableArrayListExtra<TAPUserModel>(GROUP_MEMBERS)
                    groupViewModel?.groupData?.groupParticipants = updatedGroupParticipant?.toMutableList()
                            ?: groupViewModel?.participantsList
//                    adapter?.items = groupViewModel?.groupData?.groupParticipants
//                    adapter?.notifyDataSetChanged()
                    searchTextWatcher.onTextChanged(et_search.text, et_search.text.length, et_search.text.length, et_search.text.length)

                    if (groupViewModel?.groupData?.groupParticipants?.size ?: 0 >= TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                        fl_add_members.visibility = View.GONE
                    }
                    groupViewModel?.isUpdateMember = true
                }

                GROUP_OPEN_MEMBER_PROFILE -> {
                    if (null != data?.getParcelableExtra(ROOM)) {
                        groupViewModel?.groupData = data.getParcelableExtra(ROOM)
                        groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
                        adapter?.clearItems()
                        adapter?.adminList = groupViewModel?.groupData?.admins ?: mutableListOf()
                        adapter?.items?.addAll(groupViewModel?.participantsList ?: listOf())

                        // Set total member count
                        if (!adapter?.items?.contains(groupViewModel?.memberCountModel)!!) {
                            adapter?.addItem(groupViewModel?.memberCountModel)
                        }
                        groupViewModel?.isUpdateMember = true
                    }

                    if (data?.getBooleanExtra(CLOSE_ACTIVITY, false) == true) {
                        val intent = Intent()
                        intent.putExtra(CLOSE_ACTIVITY, true)
                        setResult(Activity.RESULT_OK, intent)
                        if (groupViewModel?.isUpdateMember == true) {
                            intent.putExtra(ROOM, groupViewModel?.groupData)
                        }
                        finish()
                        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
                    }
                }
            }
        }
    }

    private fun stateLoadingMember() {
        rv_contact_list.visibility = View.GONE
        ll_member_loading.visibility = View.VISIBLE
        TAPUtils.rotateAnimateInfinitely(this, iv_loading_progress)
    }

    private fun initView() {
        tv_title.text = resources.getString(R.string.tap_group_members)
        //groupViewModel?.groupData = intent.getParcelableExtra(ROOM)
        //groupViewModel?.setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))
        rv_contact_list.visibility = View.VISIBLE
        ll_member_loading.visibility = View.GONE
        iv_loading_progress.clearAnimation()

        groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
                ?: mutableListOf()
        adapter = TAPGroupMemberAdapter(TAPGroupMemberAdapter.NORMAL_MODE, groupViewModel?.participantsList
                ?: mutableListOf(), groupViewModel?.groupData?.admins
                ?: listOf(), groupInterface)
        adapter?.addItem(groupViewModel?.memberCountModel)

        rv_contact_list.adapter = adapter
        rv_contact_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
            fl_add_members.visibility = View.VISIBLE
        } else {
            fl_add_members.visibility = View.GONE
        }

        OverScrollDecoratorHelper.setUpOverScroll(rv_contact_list, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

        iv_button_back.setOnClickListener(this)
        iv_button_search.setOnClickListener(this)
        iv_button_clear_text.setOnClickListener(this)
        ll_add_button.setOnClickListener(this)
        ll_remove_button.setOnClickListener(this)
        ll_promote_demote_admin.setOnClickListener(this)
        fl_loading.setOnClickListener {}

        et_search.addTextChangedListener(searchTextWatcher)
        et_search.setOnEditorActionListener(searchEditorActionListener)
        et_search.hint = resources.getString(R.string.tap_search_for_group_members)

        rv_contact_list.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                TAPUtils.dismissKeyboard(this@TAPGroupMemberListActivity)
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ll_add_button.background = getDrawable(R.drawable.tap_bg_button_active_ripple)
            ll_promote_demote_admin.background = getDrawable(R.drawable.tap_bg_button_active_ripple)
            ll_remove_button.background = getDrawable(R.drawable.tap_bg_button_white_ripple)
        }
    }

    private fun initViewModel(): Boolean {
        groupViewModel = ViewModelProvider(this,
                TAPGroupMemberViewModel.Companion.TAPGroupMemberViewModelFactory(
                        application, instanceKey))
                .get(TAPGroupMemberViewModel::class.java)
        groupViewModel?.setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))

        return null != groupViewModel?.groupData?.groupParticipants
    }

    private fun showToolbar() {
        groupViewModel?.isSearchActive = false
        TAPUtils.dismissKeyboard(this)
        tv_title.visibility = View.VISIBLE
        et_search.visibility = View.GONE
        et_search.setText("")
        iv_button_search.visibility = View.VISIBLE
        (cl_action_bar.background as TransitionDrawable).reverseTransition(SHORT_ANIMATION_TIME)
    }

    private fun showSearchBar() {
        groupViewModel?.isSearchActive = true
        tv_title.visibility = View.GONE
        et_search.visibility = View.VISIBLE
        iv_button_search.visibility = View.GONE
        TAPUtils.showKeyboard(this, et_search)
        (cl_action_bar.background as TransitionDrawable).startTransition(SHORT_ANIMATION_TIME)
    }

    private fun updateSearchedMember(keyword: String) {
        groupViewModel?.participantsList?.clear()
        if (keyword.isEmpty()) {
            groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
                    ?: mutableListOf()
            adapter?.items = groupViewModel?.participantsList
            if (!adapter?.items?.contains(groupViewModel?.memberCountModel)!!) {
                adapter?.addItem(groupViewModel?.memberCountModel)
            }
        } else {
            groupViewModel?.groupData?.groupParticipants?.forEach {
                if (it.name.toLowerCase().contains(keyword.toLowerCase())) {
                    groupViewModel?.participantsList?.add(it)
                }
            }
            adapter?.items = groupViewModel?.participantsList
        }
    }

    private fun cancelSelectionMode(isNeedClearAll: Boolean) {
        groupViewModel?.isSelectionMode = false
        groupViewModel?.selectedMembers?.clear()
        //if (View.GONE == fl_add_members.visibility) fl_add_members.visibility = View.VISIBLE
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

    private fun showLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_loading_progress_circle_white))
            if (null == iv_loading_image.animation)
                TAPUtils.rotateAnimateInfinitely(this, iv_loading_image)
            tv_loading_text.text = message
            iv_button_search.setOnClickListener(null)
            iv_button_clear_text.setOnClickListener(null)
            fl_loading.visibility = View.VISIBLE
        }
    }

    private fun endLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_checklist_pumpkin))
            iv_loading_image.clearAnimation()
            tv_loading_text.text = message
            Handler().postDelayed({
                hideLoading()
                iv_button_search.setOnClickListener(this)
                iv_button_clear_text.setOnClickListener(this)
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

    private val groupInterface = object : TAPGroupMemberListListener() {
        override fun onContactLongPress(contact: TAPUserModel?) {
            if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.admins?.contains(contact?.userID) == true
                    && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                groupViewModel?.addSelectedMember(contact)
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_demote_admins))
                tv_promote_demote_icon.text = resources.getText(R.string.tap_demote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE
                startSelectionMode()
            } else if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                groupViewModel?.addSelectedMember(contact)
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_appoint_admin))
                tv_promote_demote_icon.text = resources.getText(R.string.tap_promote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE
                startSelectionMode()
            }
        }

        override fun onContactSelected(contact: TAPUserModel?): Boolean {
            if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                groupViewModel?.addSelectedMember(contact)
                ll_promote_demote_admin.visibility = View.GONE
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.NOT_SHOWED
            }
            return true
        }

        override fun onContactDeselected(contact: TAPUserModel?) {
            if (groupViewModel?.isActiveUserIsAdmin == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                groupViewModel?.removeSelectedMember(contact?.userID ?: "")
            }

            if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.isSelectedMembersEmpty() == true &&
                    groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                cancelSelectionMode(false)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.NOT_SHOWED
            } else if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.selectedMembers?.size == 1 &&
                    groupViewModel?.groupData?.admins?.contains(
                            groupViewModel?.selectedMembers?.entries?.iterator()?.next()?.value?.userID) == true
                    && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_demote_admins))
                tv_promote_demote_icon.text = resources.getText(R.string.tap_demote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE
            } else if (groupViewModel?.isActiveUserIsAdmin == true && groupViewModel?.selectedMembers?.size == 1
                    && groupViewModel?.groupData?.groupParticipants?.size ?: 0 < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                ll_promote_demote_admin.visibility = View.VISIBLE
                iv_promote_demote_icon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_appoint_admin))
                tv_promote_demote_icon.text = resources.getText(R.string.tap_promote_admin)
                groupViewModel?.adminButtonStatus = TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE
            }
        }

        override fun onGroupMemberClicked(member: TAPUserModel?, isAdmin: Boolean) {
            if (member?.userID ?: "0" != TAPChatManager.getInstance(instanceKey).activeUser.userID) {
                TAPChatProfileActivity.start(
                        this@TAPGroupMemberListActivity,
                        instanceKey,
                        groupViewModel?.groupData,
                        member,
                        isAdmin
                )
            }
        }
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            et_search.removeTextChangedListener(this)
            if (null != s && s.isEmpty()) {
                iv_button_clear_text.visibility = View.GONE
            } else {
                iv_button_clear_text.visibility = View.VISIBLE
            }
            updateSearchedMember(s?.toString() ?: "")
            et_search.addTextChangedListener(this)
        }
    }

    private val searchEditorActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            TAPUtils.dismissKeyboard(this@TAPGroupMemberListActivity, et_search)
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    private val userActionView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading(groupViewModel!!.loadingStartText)
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            groupViewModel?.groupData = response?.room
            groupViewModel?.groupData?.groupParticipants = response?.participants
            groupViewModel?.groupData?.admins = response?.admins
            groupViewModel?.participantsList = groupViewModel?.groupData?.groupParticipants?.toMutableList()
            adapter?.clearItems()
            adapter?.adminList = groupViewModel?.groupData?.admins ?: mutableListOf()

            if (null != response) {
                TAPGroupManager.getInstance(instanceKey).updateGroupDataFromResponse(response)
            }

            //adapter?.items = groupViewModel?.groupData?.groupParticipants
            if (et_search.text.isNotEmpty()) {
                searchTextWatcher.onTextChanged(et_search.text, et_search.text.length, et_search.text.length, et_search.text.length)
            } else {
                adapter?.items?.addAll(groupViewModel?.participantsList ?: listOf())
                if (!adapter?.items?.contains(groupViewModel?.memberCountModel)!!) {
                    adapter?.addItem(groupViewModel?.memberCountModel)
                }
            }
            groupViewModel?.isUpdateMember = true

            Handler().postDelayed({
                cancelSelectionMode(true)
                this@TAPGroupMemberListActivity.endLoading(groupViewModel!!.loadingEndText)
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
}