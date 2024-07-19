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
import io.taptalk.TapTalk.databinding.TapActivityGroupMembersBinding

@Suppress("CAST_NEVER_SUCCEEDS")
class TAPGroupMemberListActivity : TAPBaseActivity(), View.OnClickListener {

    private lateinit var vb: TapActivityGroupMembersBinding
    var adapter: TAPGroupMemberAdapter? = null
    var instanceKey: String = ""
    var isSearchActive: Boolean = false
    var isSelectionMode: Boolean = false
    var isUpdateMember: Boolean = false
    var isActiveUserIsAdmin: Boolean = false
    var participantsList: MutableList<TAPUserModel>? = mutableListOf()
    var groupData: TAPRoomModel? = null
    var selectedMembers: LinkedHashMap<String?, TAPUserModel?> = linkedMapOf()
    var adminButtonStatus: AdminButtonShowed = AdminButtonShowed.NOT_SHOWED
    var memberCountModel: TAPUserModel? = TAPUserModel("", "")
    var loadingStartText: String = ""
    var loadingEndText: String = ""

    enum class AdminButtonShowed {
        PROMOTE, DEMOTE, NOT_SHOWED
    }

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
        when {
            isSearchActive -> {
                showToolbar()
            }
            isSelectionMode -> {
                if (vb.etSearch.text.isNotEmpty()) vb.etSearch.setText("")
                cancelSelectionMode(true)
            }
            isUpdateMember -> {
                val intent = Intent()
                intent.putExtra(ROOM, groupData)
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
                        groupData?.roomID,
                        groupData?.participants?.let { ArrayList(it) })
            }

            R.id.ll_remove_button -> {
                if (selectedMembers.size > 1) {
                    TapTalkDialog.Builder(this)
                            .setTitle("${resources.getString(R.string.tap_remove_group_member)}s")
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_multiple_members_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                loadingStartText = getString(R.string.tap_removing)
                                loadingEndText = getString(R.string.tap_removed_member)
                                TAPDataManager.getInstance(instanceKey).removeRoomParticipant(
                                    groupData?.roomID ?: "",
                                    selectedMembers.keys.toList(),
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
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setMessage(getString(R.string.tap_remove_member_confirmation))
                            .setPrimaryButtonTitle(getString(R.string.tap_ok))
                            .setPrimaryButtonListener {
                                loadingStartText = getString(R.string.tap_removing)
                                loadingEndText = getString(R.string.tap_removed_member)
                                TAPDataManager.getInstance(instanceKey).removeRoomParticipant(
                                    groupData?.roomID ?: "",
                                    selectedMembers.keys.toList(),
                                    userActionView
                                )
                            }
                            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                            .setSecondaryButtonListener {}
                            .show()
                }
            }

            R.id.ll_promote_demote_admin -> {
                when (adminButtonStatus) {
                    AdminButtonShowed.PROMOTE -> {
                        loadingStartText = getString(R.string.tap_updating)
                        loadingEndText = getString(R.string.tap_promoted_admin)
                        TAPDataManager.getInstance(instanceKey).promoteGroupAdmins(
                            groupData?.roomID ?: "",
                            getSelectedUserIDs(),
                            userActionView
                        )
                    }
                    AdminButtonShowed.DEMOTE -> {
                        TapTalkDialog.Builder(this)
                                .setTitle(resources.getString(R.string.tap_demote_admin))
                                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                .setMessage(getString(R.string.tap_demote_admin_confirmation))
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .setPrimaryButtonListener {
                                    loadingStartText = getString(R.string.tap_updating)
                                    loadingEndText = getString(R.string.tap_demoted_admin)
                                    TAPDataManager.getInstance(instanceKey).demoteGroupAdmins(
                                        groupData?.roomID ?: "",
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
                    groupData?.participants = updatedGroupParticipant?.toMutableList() ?: participantsList
//                    adapter?.items = groupData?.groupParticipants
//                    adapter?.notifyDataSetChanged()
                    searchTextWatcher.onTextChanged(vb.etSearch.text, vb.etSearch.text.length, vb.etSearch.text.length, vb.etSearch.text.length)

                    if ((groupData?.participants?.size ?: 0) >= TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                        vb.flAddMembers.visibility = View.GONE
                    }
                    else {
                        vb.flAddMembers.visibility = View.VISIBLE
                    }
                    isUpdateMember = true
                }

                GROUP_OPEN_MEMBER_PROFILE -> {
                    if (null != data?.getParcelableExtra(ROOM)) {
                        groupData = data.getParcelableExtra(ROOM)
                        participantsList = groupData?.participants?.toMutableList()
                        adapter?.clearItems()
                        adapter?.adminList = groupData?.admins ?: mutableListOf()
                        adapter?.items?.addAll(participantsList ?: listOf())

                        // Set total member count
                        if (!adapter?.items?.contains(memberCountModel)!!) {
                            adapter?.addItem(memberCountModel)
                        }
                        if ((groupData?.participants?.size ?: 0) >= TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
                            vb.flAddMembers.visibility = View.GONE
                        }
                        else {
                            vb.flAddMembers.visibility = View.VISIBLE
                        }
                        isUpdateMember = true
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
                        if (isUpdateMember) {
                            intent.putExtra(ROOM, groupData)
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
        //groupData = intent.getParcelableExtra(ROOM)
        //setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))
        vb.rvContactList.visibility = View.VISIBLE
        vb.llMemberLoading.visibility = View.GONE
        vb.ivLoadingProgress.clearAnimation()

        participantsList = groupData?.participants?.toMutableList() ?: mutableListOf()
        adapter = TAPGroupMemberAdapter(
            TAPGroupMemberAdapter.NORMAL_MODE,
            participantsList ?: mutableListOf(),
            groupData?.admins ?: listOf(),
            groupInterface
        )
        adapter?.addItem(memberCountModel)

        vb.rvContactList.adapter = adapter
        vb.rvContactList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        if (isActiveUserIsAdmin && (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()) {
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
    }

    private fun initViewModel(): Boolean {
//        groupViewModel = ViewModelProvider(this,
//                TAPGroupMemberViewModel.Companion.TAPGroupMemberViewModelFactory(
//                        application, instanceKey))
//                .get(TAPGroupMemberViewModel::class.java)
        setGroupDataAndCheckAdmin(intent.getParcelableExtra(ROOM))

        return null != groupData?.participants
    }

    private fun setGroupDataAndCheckAdmin(groupData: TAPRoomModel?) {
        this.groupData = groupData
        if (groupData?.admins?.contains(TAPChatManager.getInstance(instanceKey).activeUser?.userID) == true) {
            isActiveUserIsAdmin = true
        }
    }

    private fun addSelectedMember(member: TAPUserModel?) {
        selectedMembers[member?.userID] = member
    }

    private fun removeSelectedMember(memberID: String) {
        selectedMembers.remove(memberID)
    }

    private fun isSelectedMembersEmpty(): Boolean {
        return selectedMembers.size == 0
    }

    private fun getSelectedUserIDs(): List<String?> {
        return selectedMembers.keys.toList()
    }

    private fun showToolbar() {
        isSearchActive = false
        TAPUtils.dismissKeyboard(this)
        vb.tvTitle.visibility = View.VISIBLE
        vb.etSearch.visibility = View.GONE
        vb.etSearch.setText("")
        vb.ivButtonSearch.visibility = View.VISIBLE
        (vb.clActionBar.background as TransitionDrawable).reverseTransition(SHORT_ANIMATION_TIME)
    }

    private fun showSearchBar() {
        isSearchActive = true
        vb.tvTitle.visibility = View.GONE
        vb.etSearch.visibility = View.VISIBLE
        vb.ivButtonSearch.visibility = View.GONE
        TAPUtils.showKeyboard(this, vb.etSearch)
        (vb.clActionBar.background as TransitionDrawable).startTransition(SHORT_ANIMATION_TIME)
    }

    private fun updateSearchedMember(keyword: String) {
        participantsList?.clear()
        if (keyword.isEmpty()) {
            participantsList = groupData?.participants?.toMutableList()
                    ?: mutableListOf()
            adapter?.items = participantsList
            if (!adapter?.items?.contains(memberCountModel)!!) {
                adapter?.addItem(memberCountModel)
            }
        } else {
            groupData?.participants?.forEach {
                if (it.fullname.contains(keyword, true)) {
                    participantsList?.add(it)
                }
            }
            adapter?.items = participantsList
        }
    }

    private fun cancelSelectionMode(isNeedClearAll: Boolean) {
        isSelectionMode = false
        selectedMembers.clear()
        vb.llButtonAdminAction.visibility = View.GONE

        if (View.GONE == vb.llAddButton.visibility) {
            vb.llAddButton.visibility = View.VISIBLE
        }
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
        isSelectionMode = true
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
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setTitle(title)
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setPrimaryButtonListener {}
            .show()
    }

    private val groupInterface = object : TAPGroupMemberListListener() {
        override fun onContactLongPress(contact: TAPUserModel?) {
            if (isActiveUserIsAdmin &&
                groupData?.admins?.contains(contact?.userID) == true &&
                (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                addSelectedMember(contact)
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_demote_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_demote_admin)
                adminButtonStatus = AdminButtonShowed.DEMOTE
                startSelectionMode()
            }
            else if (
                isActiveUserIsAdmin &&
                (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                addSelectedMember(contact)
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_appoint_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_promote_admin)
                adminButtonStatus = AdminButtonShowed.PROMOTE
                startSelectionMode()
            }
        }

        override fun onContactSelected(contact: TAPUserModel?): Boolean {
            if (isActiveUserIsAdmin &&
                (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                addSelectedMember(contact)
                vb.llPromoteDemoteAdmin.visibility = View.GONE
                adminButtonStatus = AdminButtonShowed.NOT_SHOWED
            }
            return true
        }

        override fun onContactDeselected(contact: TAPUserModel?) {
            if (isActiveUserIsAdmin &&
                (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                removeSelectedMember(contact?.userID ?: "")
            }

            if (isActiveUserIsAdmin && isSelectedMembersEmpty() == true &&
                (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                cancelSelectionMode(false)
                adminButtonStatus = AdminButtonShowed.NOT_SHOWED
            }
            else if (
                isActiveUserIsAdmin &&
                selectedMembers.size == 1 &&
                groupData?.admins?.contains(selectedMembers.entries.iterator().next().value?.userID) == true &&
                (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_demote_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_demote_admin)
                adminButtonStatus = AdminButtonShowed.DEMOTE
            }
            else if (
                isActiveUserIsAdmin &&
                selectedMembers.size == 1 &&
                (groupData?.participants?.size ?: 0) < TAPGroupManager.getInstance(instanceKey).getGroupMaxParticipants()
            ) {
                vb.llPromoteDemoteAdmin.visibility = View.VISIBLE
                vb.ivPromoteDemoteIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPGroupMemberListActivity, R.drawable.tap_ic_appoint_admin))
                vb.tvPromoteDemoteIcon.text = resources.getText(R.string.tap_promote_admin)
                adminButtonStatus = AdminButtonShowed.PROMOTE
            }
        }

        override fun onGroupMemberClicked(member: TAPUserModel?, isAdmin: Boolean) {
            if ((member?.userID ?: "0") != TAPChatManager.getInstance(instanceKey).activeUser.userID) {
                TAPChatProfileActivity.start(
                    this@TAPGroupMemberListActivity,
                    instanceKey,
                    groupData,
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
            showLoading(loadingStartText)
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            groupData = response?.room
            groupData?.participants = response?.participants
            groupData?.admins = response?.admins
            participantsList = groupData?.participants?.toMutableList()
            adapter?.clearItems()
            adapter?.adminList = groupData?.admins ?: mutableListOf()

            if (null != response) {
                TAPGroupManager.getInstance(instanceKey).updateGroupDataFromResponse(response)
            }

            //adapter?.items = groupData?.groupParticipants
            if (vb.etSearch.text.isNotEmpty()) {
                searchTextWatcher.onTextChanged(
                    vb.etSearch.text,
                    vb.etSearch.text.length,
                    vb.etSearch.text.length,
                    vb.etSearch.text.length
                )
            }
            else {
                adapter?.items?.addAll(participantsList ?: listOf())
                if (!adapter?.items?.contains(memberCountModel)!!) {
                    adapter?.addItem(memberCountModel)
                }
            }
            isUpdateMember = true

            Handler(Looper.getMainLooper()).postDelayed({
                cancelSelectionMode(true)
                endLoading(loadingEndText)
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
