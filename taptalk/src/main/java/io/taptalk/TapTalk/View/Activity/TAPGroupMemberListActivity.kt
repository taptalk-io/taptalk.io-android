package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.CLOSE_ACTIVITY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_ADD_MEMBER
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_OPEN_MEMBER_PROFILE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.GROUP_UPDATE_DATA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.SHORT_ANIMATION_TIME
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Helper.TapTalkDialog.DialogType.ERROR_DIALOG
import io.taptalk.TapTalk.Listener.TAPGroupMemberListListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPGroupManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPGroupMemberAdapter
import io.taptalk.TapTalk.ViewModel.TAPGroupMemberViewModel
import io.taptalk.TapTalk.ViewModel.TAPGroupMemberViewModel.AdminButtonShowed.DEMOTE
import io.taptalk.TapTalk.ViewModel.TAPGroupMemberViewModel.AdminButtonShowed.NOT_SHOWED
import io.taptalk.TapTalk.ViewModel.TAPGroupMemberViewModel.AdminButtonShowed.PROMOTE
import io.taptalk.TapTalk.databinding.TapActivityGroupMembersBinding

@Suppress("CAST_NEVER_SUCCEEDS")
class TAPGroupMemberListActivity : TAPBaseActivity(), View.OnClickListener {

    private lateinit var vb: TapActivityGroupMembersBinding

    private val vm: TAPGroupMemberViewModel by lazy {
        ViewModelProvider(this)[TAPGroupMemberViewModel::class.java]
    }

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
        vb = TapActivityGroupMembersBinding.inflate(layoutInflater)
        setContentView(vb.root)

        vb.tvTitle.text = resources.getString(R.string.tap_group_members)
        if (initViewModel()) initView()
        else stateLoadingMember()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when {
            vm.isSearchActive -> {
                showToolbar()
            }
            vm.isSelectionMode -> {
                if (vm.searchKeyword.isNotEmpty()) vb.etSearch.setText("")
                cancelSelectionMode(true)
            }
            vm.isUpdateMember -> {
                val intent = Intent()
                intent.putExtra(ROOM, vm.groupData)
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
                vb.etSearch.setText("")
            }

            R.id.iv_button_back -> {
                onBackPressed()
            }

            R.id.ll_add_button -> {
                TAPAddGroupMemberActivity.start(
                        this,
                        instanceKey,
                        vm.groupData?.roomID,
                        vm.groupData?.participants?.let { ArrayList(it) })
            }

            R.id.ll_remove_button -> {
                if (vm.selectedMembers.size > 1) {
                    TapTalkDialog.Builder(this)
                            .setTitle("${resources.getString(R.string.tap_remove_group_member)}s")
                            .setDialogType(ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_multiple_members_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                vm.loadingStartText = getString(R.string.tap_removing)
                                vm.loadingEndText = getString(R.string.tap_removed_member)
                                TAPDataManager.getInstance(instanceKey).removeRoomParticipant(
                                    vm.groupData?.roomID ?: "",
                                    vm.selectedMembers.keys.toList(),
                                    userActionView
                                )
                            }
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setSecondaryButtonListener {}
                            .show()
                }
                else {
                    TapTalkDialog.Builder(this)
                            .setTitle(resources.getString(R.string.tap_remove_group_member))
                            .setDialogType(ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_member_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                vm.loadingStartText = getString(R.string.tap_removing)
                                vm.loadingEndText = getString(R.string.tap_removed_member)
                                TAPDataManager.getInstance(instanceKey).removeRoomParticipant(
                                    vm.groupData?.roomID ?: "",
                                    vm.selectedMembers.keys.toList(),
                                    userActionView
                                )
                            }
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setSecondaryButtonListener {}
                            .show()
                }
            }

            R.id.ll_promote_demote_admin -> {
                when (vm.adminButtonStatus) {
                    PROMOTE -> {
                        vm.loadingStartText = getString(R.string.tap_updating)
                        vm.loadingEndText = getString(R.string.tap_promoted_admin)
                        TAPDataManager.getInstance(instanceKey).promoteGroupAdmins(
                            vm.groupData?.roomID ?: "",
                            getSelectedUserIDs(),
                            userActionView
                        )
                    }
                    DEMOTE -> {
                        TapTalkDialog.Builder(this)
                                .setTitle(resources.getString(R.string.tap_demote_admin))
                                .setDialogType(ERROR_DIALOG)
                                .setMessage(getString(R.string.tap_demote_admin_confirmation))
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .setPrimaryButtonListener {
                                    vm.loadingStartText = getString(R.string.tap_updating)
                                    vm.loadingEndText = getString(R.string.tap_demoted_admin)
                                    TAPDataManager.getInstance(instanceKey).demoteGroupAdmins(
                                        vm.groupData?.roomID ?: "",
                                        getSelectedUserIDs(),
                                        userActionView
                                    )
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
                    vm.groupData?.participants = updatedGroupParticipant?.toMutableList() ?: vm.participantsList
                    searchTextWatcher.onTextChanged(vm.searchKeyword, vm.searchKeyword.length, vm.searchKeyword.length, vm.searchKeyword.length)

                    if ((vm.groupData?.participants?.size ?: 0) >= TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                        vb.flAddMembers.visibility = View.GONE
                    }
                    else {
                        vb.flAddMembers.visibility = View.VISIBLE
                    }
                    vm.isUpdateMember = true
                }

                GROUP_OPEN_MEMBER_PROFILE -> {
                    if (null != data?.getParcelableExtra(ROOM)) {
                        vm.groupData = data.getParcelableExtra(ROOM)
                        vm.participantsList = vm.groupData?.participants?.toMutableList()
                        adapter?.clearItems()
                        adapter?.adminList = vm.groupData?.admins ?: mutableListOf()
                        adapter?.items?.addAll(vm.participantsList ?: listOf())

                        // Set total member count
                        if (!adapter?.items?.contains(vm.memberCountModel)!!) {
                            adapter?.addItem(vm.memberCountModel)
                        }
                        if ((vm.groupData?.participants?.size ?: 0) >= TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                            vb.flAddMembers.visibility = View.GONE
                        }
                        else {
                            vb.flAddMembers.visibility = View.VISIBLE
                        }
                        vm.isUpdateMember = true
                    }

                    val message = data?.getParcelableExtra<TAPMessageModel>(MESSAGE)
                    if (message != null) {
                        val intent = Intent()
                        intent.putExtra(MESSAGE, message)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    if (data?.getBooleanExtra(CLOSE_ACTIVITY, false) == true) {
                        val intent = Intent()
                        intent.putExtra(CLOSE_ACTIVITY, true)
                        setResult(Activity.RESULT_OK, intent)
                        if (vm.isUpdateMember) {
                            intent.putExtra(ROOM, vm.groupData)
                        }
                        finish()
                        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
                    }
                }
            }
        }
    }

    private fun stateLoadingMember() {
        vb.rvContactList.visibility = View.GONE
        vb.llMemberLoading.visibility = View.VISIBLE
        TAPUtils.rotateAnimateInfinitely(this, vb.ivLoadingProgress)
    }

    private fun initView() {
        vb.tvTitle.text = resources.getString(R.string.tap_group_members)
        vb.rvContactList.visibility = View.VISIBLE
        vb.llMemberLoading.visibility = View.GONE
        vb.ivLoadingProgress.clearAnimation()

        vm.participantsList = vm.groupData?.participants?.toMutableList() ?: mutableListOf()
        adapter = TAPGroupMemberAdapter(
            TAPGroupMemberAdapter.NORMAL_MODE,
            vm.participantsList ?: mutableListOf(),
            vm.groupData?.admins ?: listOf(),
            groupInterface
        )
        adapter?.addItem(vm.memberCountModel)

        vb.rvContactList.adapter = adapter
        vb.rvContactList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        if (vm.isActiveUserIsAdmin && (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
            vb.flAddMembers.visibility = View.VISIBLE
        }
        else {
            vb.flAddMembers.visibility = View.GONE
        }

        OverScrollDecoratorHelper.setUpOverScroll(vb.rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

        vb.ivButtonBack.setOnClickListener(this)
        vb.ivButtonSearch.setOnClickListener(this)
        vb.ivButtonClearText.setOnClickListener(this)
        vb.llAddButton.setOnClickListener(this)
        vb.llRemoveButton.setOnClickListener(this)
        vb.llPromoteDemoteAdmin.setOnClickListener(this)
        vb.layoutPopupLoadingScreen.flLoading.setOnClickListener {}

        vb.etSearch.addTextChangedListener(searchTextWatcher)
        vb.etSearch.setOnEditorActionListener(searchEditorActionListener)
        vb.etSearch.hint = resources.getString(R.string.tap_search_for_group_members)

        vb.rvContactList.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                TAPUtils.dismissKeyboard(this@TAPGroupMemberListActivity)
            }
        })

        vb.llAddButton.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active_ripple)
        vb.llPromoteDemoteAdmin.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active_ripple)
        vb.llRemoveButton.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_white_ripple)

        if (vm.searchKeyword.isNotEmpty()) {
            showSearchBar()
        }
        else {
            showToolbar()
        }
    }

    private fun initViewModel(): Boolean {
        setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))

        return null != vm.groupData?.participants
    }

    private fun setGroupDataAndCheckAdmin(groupData: TAPRoomModel?) {
        if (groupData == null) {
            return
        }
        vm.groupData = groupData
        if (groupData.admins?.contains(TAPChatManager.getInstance(instanceKey).activeUser?.userID) == true) {
            vm.isActiveUserIsAdmin = true
        }
    }

    private fun addSelectedMember(member: TAPUserModel?) {
        vm.selectedMembers[member?.userID] = member
    }

    private fun removeSelectedMember(memberID: String) {
        vm.selectedMembers.remove(memberID)
    }

    private fun isSelectedMembersEmpty(): Boolean {
        return vm.selectedMembers.size == 0
    }

    private fun getSelectedUserIDs(): List<String?> {
        return vm.selectedMembers.keys.toList()
    }

    private fun showToolbar() {
        vm.isSearchActive = false
        TAPUtils.dismissKeyboard(this)
        vb.tvTitle.visibility = View.VISIBLE
        vb.etSearch.visibility = View.GONE
        vb.etSearch.setText("")
        vb.ivButtonSearch.visibility = View.VISIBLE
        vb.ivButtonClearText.visibility = View.GONE
        (vb.clActionBar.background as TransitionDrawable).reverseTransition(SHORT_ANIMATION_TIME)
    }

    private fun showSearchBar() {
        vm.isSearchActive = true
        vb.tvTitle.visibility = View.GONE
        vb.etSearch.visibility = View.VISIBLE
        vb.ivButtonSearch.visibility = View.GONE
        if (vm.searchKeyword.isEmpty()) {
            vb.ivButtonClearText.visibility = View.GONE
        }
        else {
            vb.ivButtonClearText.visibility = View.VISIBLE
        }
        TAPUtils.showKeyboard(this, vb.etSearch)
        (vb.clActionBar.background as TransitionDrawable).startTransition(SHORT_ANIMATION_TIME)
    }

    private fun updateSearchedMember(keyword: String) {
        vm.participantsList?.clear()
        if (keyword.isEmpty()) {
            vm.participantsList = vm.groupData?.participants?.toMutableList()
                    ?: mutableListOf()
            adapter?.items = vm.participantsList
            if (!adapter?.items?.contains(vm.memberCountModel)!!) {
                adapter?.addItem(vm.memberCountModel)
            }
        } else {
            vm.groupData?.participants?.forEach {
                if (it.fullname.contains(keyword, true)) {
                    vm.participantsList?.add(it)
                }
            }
            adapter?.items = vm.participantsList
        }
    }

    private fun cancelSelectionMode(isNeedClearAll: Boolean) {
        vm.isSelectionMode = false
        vm.selectedMembers.clear()
        vb.llButtonAdminAction.visibility = View.GONE

        if (View.GONE == vb.llAddButton.visibility) {
            vb.llAddButton.visibility = View.VISIBLE
        }
        adapter?.updateCellMode(TAPGroupMemberAdapter.NORMAL_MODE)

        if (isNeedClearAll) {
            Thread {
                adapter?.items?.forEach {
                    it.isSelected = false
                }
            }.start()
        }
    }

    private fun startSelectionMode() {
        vm.isSelectionMode = true
        vb.llButtonAdminAction.visibility = View.VISIBLE
        vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
        vb.llAddButton.visibility = View.GONE
        adapter?.updateCellMode(TAPGroupMemberAdapter.SELECT_MODE)
    }

    private fun showLoading(message: String) {
        runOnUiThread {
            vb.layoutPopupLoadingScreen.ivLoadingImage.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_loading_progress_circle_white))
            if (null == vb.layoutPopupLoadingScreen.ivLoadingImage.animation) {
                TAPUtils.rotateAnimateInfinitely(this, vb.layoutPopupLoadingScreen.ivLoadingImage)
            }
            vb.layoutPopupLoadingScreen.tvLoadingText.text = message
            vb.ivButtonSearch.setOnClickListener(null)
            vb.ivButtonClearText.setOnClickListener(null)
            vb.layoutPopupLoadingScreen.flLoading.visibility = View.VISIBLE
        }
    }

    private fun endLoading(message: String) {
        runOnUiThread {
            vb.layoutPopupLoadingScreen.ivLoadingImage.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_checklist_pumpkin))
            vb.layoutPopupLoadingScreen.ivLoadingImage.clearAnimation()
            vb.layoutPopupLoadingScreen.tvLoadingText.text = message
            Handler().postDelayed({
                hideLoading()
                vb.ivButtonSearch.setOnClickListener(this)
                vb.ivButtonClearText.setOnClickListener(this)
            }, 1000L)
        }
    }

    private fun hideLoading() {
        vb.layoutPopupLoadingScreen.flLoading.visibility = View.GONE
    }

    private fun showErrorDialog(title: String, message: String) {
        TapTalkDialog.Builder(this@TAPGroupMemberListActivity)
            .setDialogType(ERROR_DIALOG)
            .setTitle(title)
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setPrimaryButtonListener {}
            .show()
    }

    private val groupInterface = object : TAPGroupMemberListListener() {
        override fun onContactLongPress(contact: TAPUserModel?) {
            if (vm.isActiveUserIsAdmin &&
                vm.groupData?.admins?.contains(contact?.userID) == true &&
                (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                addSelectedMember(contact)
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_demote_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_demote_admin)
                vm.adminButtonStatus = DEMOTE
                startSelectionMode()
            }
            else if (
                vm.isActiveUserIsAdmin &&
                (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                addSelectedMember(contact)
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_appoint_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_promote_admin)
                vm.adminButtonStatus = PROMOTE
                startSelectionMode()
            }
        }

        override fun onContactSelected(contact: TAPUserModel?): Boolean {
            if (vm.isActiveUserIsAdmin &&
                (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                addSelectedMember(contact)
                vb.llPromoteDemoteAdmin.visibility = View.GONE
                vm.adminButtonStatus = NOT_SHOWED
            }
            return true
        }

        override fun onContactDeselected(contact: TAPUserModel?) {
            if (vm.isActiveUserIsAdmin &&
                (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                removeSelectedMember(contact?.userID ?: "")
            }

            if (vm.isActiveUserIsAdmin &&
                isSelectedMembersEmpty() &&
                (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                cancelSelectionMode(false)
                vm.adminButtonStatus = NOT_SHOWED
            }
            else if (
                vm.isActiveUserIsAdmin &&
                vm.selectedMembers.size == 1 &&
                vm.groupData?.admins?.contains(vm.selectedMembers.entries.iterator().next().value?.userID) == true &&
                (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_demote_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_demote_admin)
                vm.adminButtonStatus = DEMOTE
            }
            else if (
                vm.isActiveUserIsAdmin &&
                vm.selectedMembers.size == 1 &&
                (vm.groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_appoint_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_promote_admin)
                vm.adminButtonStatus = PROMOTE
            }
        }

        override fun onGroupMemberClicked(member: TAPUserModel?, isAdmin: Boolean) {
            if ((member?.userID ?: "0") != TAPChatManager.getInstance(instanceKey).activeUser.userID) {
                TAPChatProfileActivity.start(
                    this@TAPGroupMemberListActivity,
                    instanceKey,
                    vm.groupData,
                    member,
                    isAdmin,
                    false
                )
            }
        }
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            vb.etSearch.removeTextChangedListener(this)
            if (null != s && s.isEmpty()) {
                vb.ivButtonClearText.visibility = View.GONE
            }
            else {
                vb.ivButtonClearText.visibility = View.VISIBLE
            }
            updateSearchedMember(s?.toString() ?: "")
            vm.searchKeyword = s.toString()
            vb.etSearch.addTextChangedListener(this)
        }
    }

    private val searchEditorActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            TAPUtils.dismissKeyboard(this@TAPGroupMemberListActivity, vb.etSearch)
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    private val userActionView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading(vm.loadingStartText)
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            vm.groupData = response?.room
            vm.groupData?.participants = response?.participants
            vm.groupData?.admins = response?.admins
            vm.participantsList = vm.groupData?.participants?.toMutableList()
            adapter?.clearItems()
            adapter?.adminList = vm.groupData?.admins ?: mutableListOf()

            if (null != response) {
                TAPGroupManager.getInstance(instanceKey).updateGroupDataFromResponse(response)
            }

            //adapter?.items = vm.groupData?.groupParticipants
            if (vm.searchKeyword.isNotEmpty()) {
                searchTextWatcher.onTextChanged(
                    vm.searchKeyword,
                    vm.searchKeyword.length,
                    vm.searchKeyword.length,
                    vm.searchKeyword.length
                )
            }
            else {
                adapter?.items?.addAll(vm.participantsList ?: listOf())
                if (!adapter?.items?.contains(vm.memberCountModel)!!) {
                    adapter?.addItem(vm.memberCountModel)
                }
            }
            vm.isUpdateMember = true

            Handler(Looper.getMainLooper()).postDelayed({
                cancelSelectionMode(true)
                endLoading(vm.loadingEndText)
            }, 400L)
        }

        override fun onError(error: TAPErrorModel?) {
            hideLoading()
            showErrorDialog(getString(R.string.tap_error), error!!.message)
        }

        override fun onError(errorMessage: String?) {
            hideLoading()
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general))
        }
    }
}
