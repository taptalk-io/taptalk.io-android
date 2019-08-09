package io.taptalk.TapTalk.View.Activity

import android.animation.ValueAnimator
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPContactManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPGroupManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMenuItem
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Adapter.TAPMenuButtonAdapter
import io.taptalk.TapTalk.ViewModel.TAPProfileViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_chat_profile.*
import kotlinx.android.synthetic.main.tap_layout_popup_loading_screen.*
import java.util.*
import kotlin.math.abs

class TAPGroupMemberProfileActivity : TAPBaseActivity() {

    lateinit var glide: RequestManager
    var groupViewModel: TAPProfileViewModel? = null
    var menuAdapter: TAPMenuButtonAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_chat_profile)
        glide = Glide.with(this)
        initViewModel()
        initView()
    }

    fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPProfileViewModel::class.java)
        groupViewModel?.room = intent.getParcelableExtra(ROOM)
        groupViewModel?.groupMemberUser = intent.getParcelableExtra(K_USER)
        groupViewModel?.isAdminGroup = intent.getBooleanExtra(IS_ADMIN, false)
        groupViewModel?.sharedMedias?.clear()
    }

    fun initView() {
        window.setBackgroundDrawable(null)
        updateView()

        // Set gradient for profile picture overlay
        v_gradient.background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack40),
                        ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack18),
                        ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack),
                        ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack40)))

        val menuButtonItem = generateGroupMemberMenu()
        menuAdapter = TAPMenuButtonAdapter(menuButtonItem, profileMenuInterface)
        rv_menu_buttons.adapter = menuAdapter
        rv_menu_buttons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        app_bar_layout.addOnOffsetChangedListener(offsetChangedListener)
        iv_shared_media_loading.visibility = View.GONE

        iv_button_back.setOnClickListener { onBackPressed() }
        fl_loading.setOnClickListener {}

    }

    private fun updateView() {
        if (null != groupViewModel && null != groupViewModel?.groupMemberUser
                && null != groupViewModel?.groupMemberUser?.avatarURL
                && null != groupViewModel?.groupMemberUser?.avatarURL?.fullsize
                && groupViewModel?.groupMemberUser?.avatarURL?.fullsize?.isNotEmpty() == true) {
            glide.load(groupViewModel!!.groupMemberUser.avatarURL!!.fullsize)
                    .apply(RequestOptions().placeholder(R.drawable.tap_bg_grey_e4))
                    .into(iv_profile)
        } else {
            iv_profile.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapGrey9b))
        }

        //update room name
        tv_full_name.text = groupViewModel?.groupMemberUser?.name ?: ""
        tv_collapsed_name.text = groupViewModel?.groupMemberUser?.name ?: ""
    }

    private fun generateGroupMemberMenu(): ArrayList<TAPMenuItem> {
        val menuItems = ArrayList<TAPMenuItem>()
        //Group Member Profile
        if (null == TAPContactManager.getInstance().getUserData(groupViewModel?.groupMemberUser?.userID
                        ?: "0")
                || (null != TAPContactManager.getInstance().getUserData(groupViewModel?.groupMemberUser?.userID
                        ?: "0")
                        && 0 == TAPContactManager.getInstance().getUserData(groupViewModel?.groupMemberUser?.userID
                        ?: "0").isContact)) {
            val menuAddToContact = TAPMenuItem(
                    MENU_ADD_TO_CONTACTS,
                    R.drawable.tap_ic_add_circle_grey,
                    R.color.tapIconGroupMemberProfileMenuAddToContacts,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_add_to_contacts))

            menuItems.add(menuAddToContact)
        }
        val menuSendMessage = TAPMenuItem(
                MENU_SEND_MESSAGE,
                R.drawable.tap_ic_send_message_grey,
                R.color.tapIconGroupMemberProfileMenuSendMessage,
                R.style.tapChatProfileMenuLabelStyle,
                false,
                false,
                getString(R.string.tap_send_message))
        menuItems.add(menuSendMessage)

        if (null != groupViewModel?.groupMemberUser && null != groupViewModel?.groupMemberUser?.userID
                && null != groupViewModel?.room && null != groupViewModel?.room?.admins
                && groupViewModel?.room?.admins?.contains(groupViewModel?.groupMemberUser?.userID) == true &&
                groupViewModel?.room?.admins?.contains(TAPChatManager.getInstance().activeUser.userID) == true) {
            val menuDemoteAdmin = TAPMenuItem(
                    MENU_DEMOTE_ADMIN,
                    R.drawable.tap_ic_remove_circle_grey,
                    R.color.tapIconGroupMemberProfileMenuDemoteAdmin,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_demote_admin))
            menuItems.add(menuDemoteAdmin)
        } else if (null != groupViewModel?.groupMemberUser && null != groupViewModel?.groupMemberUser?.userID
                && null != groupViewModel?.room && null != groupViewModel?.room?.roomID && null != groupViewModel?.room?.admins
                && groupViewModel?.room?.admins?.contains(TAPChatManager.getInstance().activeUser.userID) == true) {
            val menuPromoteAdmin = TAPMenuItem(
                    MENU_PROMOTE_ADMIN,
                    R.drawable.tap_ic_appoint_admin,
                    R.color.tapIconGroupMemberProfileMenuPromoteAdmin,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_promote_admin))
            menuItems.add(menuPromoteAdmin)
        }

//        if (null != groupViewModel?.room && null != groupViewModel?.room?.admins
//                && groupViewModel?.room?.admins?.contains(TAPChatManager.getInstance().activeUser.userID) == true) {
//            val menuKickMember = TAPMenuItem(
//                    MENU_REMOVE_MEMBER,
//                    R.drawable.tap_ic_delete_red,
//                    R.color.tapIconGroupMemberProfileMenuRemoveMember,
//                    R.style.tapChatProfileMenuDestructiveLabelStyle,
//                    false,
//                    false,
//                    getString(R.string.tap_remove_group_members))
//            menuItems.add(menuKickMember)
//        }
        return menuItems
    }

    private val profileMenuInterface = TAPChatProfileActivity.ProfileMenuInterface {
        when {
            it.menuID == MENU_SEND_MESSAGE -> openChatRoom(groupViewModel?.groupMemberUser)
            it.menuID == MENU_ADD_TO_CONTACTS -> TAPDataManager.getInstance().addContactApi(groupViewModel?.groupMemberUser?.userID
                    ?: "0", addContactView)
            it.menuID == MENU_PROMOTE_ADMIN -> {
                TAPDataManager.getInstance().promoteGroupAdmins(groupViewModel?.room?.roomID,
                        listOf(groupViewModel?.groupMemberUser?.userID), promoteAdminView)
            }
            it.menuID == MENU_DEMOTE_ADMIN -> {
                val show = TapTalkDialog.Builder(this)
                        .setTitle(resources.getString(R.string.tap_demote_admin))
                        .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                        .setMessage(getString(R.string.tap_demote_admin_confirmation))
                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                        .setPrimaryButtonListener {
                            TAPDataManager.getInstance().demoteGroupAdmins(groupViewModel?.room?.roomID,
                                    listOf(groupViewModel?.groupMemberUser?.userID), demoteAdminView)
                        }
                        .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                        .setSecondaryButtonListener {}
                        .show()
            }

            it.menuID == MENU_REMOVE_MEMBER -> {
                TapTalkDialog.Builder(this)
                        .setTitle(resources.getString(R.string.tap_remove_group_members))
                        .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                        .setMessage(getString(R.string.tap_remove_member_confirmation))
                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                        .setPrimaryButtonListener {
                            TAPDataManager.getInstance().removeRoomParticipant(groupViewModel?.room?.roomID,
                                    listOf(groupViewModel?.groupMemberUser?.userID), removeRoomMembersView)
                        }
                        .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                        .setSecondaryButtonListener {}
                        .show()
            }
        }
    }

    private val offsetChangedListener = object : AppBarLayout.OnOffsetChangedListener {

        private var isShowing: Boolean = false
        private var scrollRange = -1
        private val nameTranslationY = TAPUtils.getInstance().dpToPx(8)
        private var scrimHeight: Int = 0

        private var transitionToCollapse: ValueAnimator? = null
        private var transitionToExpand: ValueAnimator? = null

        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            if (scrollRange == -1) {
                // Initialize
                scrimHeight = ll_toolbar_collapsed.layoutParams.height * 3 / 2
                scrollRange = appBarLayout.totalScrollRange - scrimHeight
                collapsing_toolbar_layout.scrimVisibleHeightTrigger = scrimHeight
            }

            if (abs(verticalOffset) >= scrollRange && !isShowing) {
                // Show Toolbar
                isShowing = true
                ll_toolbar_collapsed.visibility = View.VISIBLE
                ll_toolbar_collapsed.animate()
                        .alpha(1f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .start()
                tv_collapsed_name.translationY = nameTranslationY.toFloat()
                tv_collapsed_name.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .start()
                v_profile_separator.animate()
                        .alpha(1f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .start()
                getTransitionToExpand()!!.cancel()
                getTransitionToCollapse()!!.start()
            } else if (abs(verticalOffset) < scrollRange && isShowing) {
                // Hide Toolbar
                isShowing = false
                ll_toolbar_collapsed.animate()
                        .alpha(0f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .withEndAction { ll_toolbar_collapsed.visibility = View.GONE }
                        .start()
                tv_collapsed_name.animate()
                        .translationY(nameTranslationY.toFloat())
                        .alpha(0f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .start()
                v_profile_separator.animate()
                        .alpha(0f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .start()
                getTransitionToCollapse()!!.cancel()
                getTransitionToExpand()!!.start()
            }
        }

        private fun getTransitionToCollapse(): ValueAnimator? {
            if (null == transitionToCollapse) {
                transitionToCollapse = ValueAnimator.ofArgb(
                        ContextCompat.getColor(TapTalk.appContext, R.color.tapIconTransparentBackgroundBackButton),
                        ContextCompat.getColor(TapTalk.appContext, R.color.tapIconNavBarBackButton))
                transitionToCollapse!!.duration = DEFAULT_ANIMATION_TIME.toLong()
                transitionToCollapse!!.addUpdateListener { valueAnimator ->
                    iv_button_back.setColorFilter(
                            valueAnimator.animatedValue as Int, PorterDuff.Mode.SRC_IN)
                }
            }
            return transitionToCollapse
        }

        private fun getTransitionToExpand(): ValueAnimator? {
            if (null == transitionToExpand) {
                transitionToExpand = ValueAnimator.ofArgb(
                        ContextCompat.getColor(TapTalk.appContext, R.color.tapIconNavBarBackButton),
                        ContextCompat.getColor(TapTalk.appContext, R.color.tapIconTransparentBackgroundBackButton))
                transitionToExpand!!.duration = DEFAULT_ANIMATION_TIME.toLong()
                transitionToExpand!!.addUpdateListener { valueAnimator ->
                    iv_button_back.setColorFilter(
                            valueAnimator.animatedValue as Int, PorterDuff.Mode.SRC_IN)
                }
            }
            return transitionToExpand
        }
    }

    private fun showLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(getDrawable(R.drawable.tap_ic_loading_progress_circle_white))
            if (null == iv_loading_image.animation)
                TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_loading_image)
            tv_loading_text.text = message
            fl_loading.visibility = View.VISIBLE
        }
    }

    private fun endLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(getDrawable(R.drawable.tap_ic_checklist_pumpkin))
            iv_loading_image.clearAnimation()
            tv_loading_text.text = message
            fl_loading.setOnClickListener { hideLoading() }

            Handler().postDelayed({
                this.hideLoading()
                val intent = Intent()
                intent.putExtra(ROOM, groupViewModel?.room)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }, 1000L)
        }
    }

    private fun hideLoading() {
        fl_loading.visibility = View.GONE
    }

    private fun showErrorDialog(title: String, message: String) {
        TapTalkDialog.Builder(this@TAPGroupMemberProfileActivity)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener {}
                .show()
    }

    private val addContactView = object : TAPDefaultDataView<TAPAddContactResponse>() {
        override fun startLoading() {
            super.startLoading()
            showLoading(getString(R.string.tap_adding))
        }

        override fun onSuccess(response: TAPAddContactResponse?) {
            super.onSuccess(response)
            val newContact = response?.user?.setUserAsContact()
            TAPDataManager.getInstance().insertMyContactToDatabase(dbListener, newContact)
            TAPContactManager.getInstance().updateUserData(newContact)
            this@TAPGroupMemberProfileActivity.endLoading(getString(R.string.tap_added_contact))
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), error!!.message)
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general))
        }
    }

    private val dbListener = object : TAPDatabaseListener<TAPUserModel>() {
        override fun onInsertFinished() {}
    }

    private fun openChatRoom(userModel: TAPUserModel?) {
        TAPUtils.getInstance().startChatActivity(this,
                TAPChatManager.getInstance().arrangeRoomId(TAPChatManager.getInstance().activeUser.userID,
                        userModel?.userID ?: "0"), userModel?.name ?: "",
                userModel?.avatarURL, TYPE_PERSONAL, "FFFFFF")
        val intent = Intent()
        intent.putExtra(IS_NEED_TO_CLOSE_ACTIVITY_BEFORE, true)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    private val promoteAdminView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading(getString(R.string.tap_updating))
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            groupViewModel?.room = response?.room
            groupViewModel?.room?.groupParticipants = response?.participants
            groupViewModel?.room?.admins = response?.admins

            if (null != groupViewModel?.room) TAPGroupManager.getInstance.addGroupData(groupViewModel?.room!!)

            this@TAPGroupMemberProfileActivity.endLoading(getString(R.string.tap_promoted_admin))
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), error!!.message)
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general))
        }
    }

    private val demoteAdminView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading(getString(R.string.tap_updating))
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            groupViewModel?.room = response?.room
            groupViewModel?.room?.groupParticipants = response?.participants
            groupViewModel?.room?.admins = response?.admins

            if (null != groupViewModel?.room) TAPGroupManager.getInstance.addGroupData(groupViewModel?.room!!)

            this@TAPGroupMemberProfileActivity.endLoading(getString(R.string.tap_demoted_admin))
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), error!!.message)
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general))
        }
    }

    private val removeRoomMembersView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading(getString(R.string.tap_removing))
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            super.onSuccess(response)
            groupViewModel?.room = response?.room
            groupViewModel?.room?.groupParticipants = response?.participants
            groupViewModel?.room?.admins = response?.admins

            if (null != groupViewModel?.room) TAPGroupManager.getInstance.addGroupData(groupViewModel?.room!!)

            this@TAPGroupMemberProfileActivity.endLoading(getString(R.string.tap_removed_member))
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), error!!.message)
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberProfileActivity.hideLoading()
            showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general))
        }
    }
}