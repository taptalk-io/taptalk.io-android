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
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPContactManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMenuItem
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Adapter.TAPMenuButtonAdapter
import io.taptalk.TapTalk.ViewModel.TAPProfileViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_chat_profile.*
import kotlinx.android.synthetic.main.tap_loading_layout_block_screen.*
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
                intArrayOf(resources.getColor(R.color.tapTransparentBlack40),
                        resources.getColor(R.color.tapTransparentBlack18),
                        resources.getColor(R.color.tapTransparentBlack),
                        resources.getColor(R.color.tapTransparentBlack40)))

        var menuButtonItem = generateGroupMemberMenu()
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
            iv_profile.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.tapGrey9b));
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
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_add_to_contacts))

            menuItems.add(menuAddToContact)
        }
        val menuSendMessage = TAPMenuItem(
                MENU_SEND_MESSAGE,
                R.drawable.tap_ic_send_message_grey,
                R.color.tapIconGroupProfileMenuViewMembers,
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
                    R.drawable.tap_ic_icon_remove_circle_grey,
                    R.color.tapIconGroupProfileMenuViewMembers,
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
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_promote_admin))
            menuItems.add(menuPromoteAdmin)
        }

        if (null != groupViewModel?.room && null != groupViewModel?.room?.admins
                && groupViewModel?.room?.admins?.contains(TAPChatManager.getInstance().activeUser.userID) == true) {
            val menuKickMember = TAPMenuItem(
                    MENU_REMOVE_MEMBER,
                    R.drawable.tap_ic_delete_red,
                    R.color.tapIconChatProfileMenuClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_remove_group_members))
            menuItems.add(menuKickMember)
        }
        return menuItems
    }

    private val profileMenuInterface = TAPChatProfileActivity.ProfileMenuInterface {
        when {
            it.menuID == MENU_SEND_MESSAGE -> openChatRoom(groupViewModel?.groupMemberUser)
            it.menuID == MENU_ADD_TO_CONTACTS -> TAPDataManager.getInstance().addContactApi(groupViewModel?.groupMemberUser?.userID
                    ?: "0", addContactView)
            it.menuID == MENU_PROMOTE_ADMIN -> {
                TapTalkDialog.Builder(this)
                        .setTitle(resources.getString(R.string.tap_promote_admin))
                        .setMessage("Are you sure you want to promote this member to admin?")
                        .setPrimaryButtonTitle("OK")
                        .setPrimaryButtonListener {
                            TAPDataManager.getInstance().promoteGroupAdmins(groupViewModel?.room?.roomID,
                                    listOf(groupViewModel?.groupMemberUser?.userID), appointAdminView)
                        }
                        .setSecondaryButtonTitle("Cancel")
                        .setSecondaryButtonListener {}
                        .show()
            }
            it.menuID == MENU_DEMOTE_ADMIN -> {
                TapTalkDialog.Builder(this)
                        .setTitle(resources.getString(R.string.tap_demote_admin))
                        .setMessage("Are you sure you want to demote this admin?")
                        .setPrimaryButtonTitle("OK")
                        .setPrimaryButtonListener {
                            TAPDataManager.getInstance().demoteGroupAdmins(groupViewModel?.room?.roomID,
                                    listOf(groupViewModel?.groupMemberUser?.userID), appointAdminView)
                        }
                        .setSecondaryButtonTitle("Cancel")
                        .setSecondaryButtonListener {}
                        .show()
            }

            it.menuID == MENU_REMOVE_MEMBER -> {
                TapTalkDialog.Builder(this)
                        .setTitle(resources.getString(R.string.tap_remove_group_members))
                        .setMessage("Are you sure you want to remove this member?")
                        .setPrimaryButtonTitle("OK")
                        .setPrimaryButtonListener {
                            TAPDataManager.getInstance().removeRoomParticipant(groupViewModel?.room?.roomID,
                                    listOf(groupViewModel?.groupMemberUser?.userID), removeRoomMembersView)
                        }
                        .setSecondaryButtonTitle("Cancel")
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
                scrimHeight = ll_toolbar_collapsed.getLayoutParams().height * 3 / 2
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
                        .withEndAction { ll_toolbar_collapsed.setVisibility(View.GONE) }
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
                        resources.getColor(R.color.tapIconTransparentBackgroundBackButton),
                        resources.getColor(R.color.tapIconNavBarBackButton))
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
                        resources.getColor(R.color.tapIconNavBarBackButton),
                        resources.getColor(R.color.tapIconTransparentBackgroundBackButton))
                transitionToExpand!!.duration = DEFAULT_ANIMATION_TIME.toLong()
                transitionToExpand!!.addUpdateListener { valueAnimator ->
                    iv_button_back.setColorFilter(
                            valueAnimator.animatedValue as Int, PorterDuff.Mode.SRC_IN)
                }
            }
            return transitionToExpand
        }
    }

    private fun showLoading() {
        runOnUiThread {
            iv_saving.setImageDrawable(getDrawable(R.drawable.tap_ic_loading_progress_circle_white))
            if (null == iv_saving.animation) {
                TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_saving)
            }
            tv_loading_text.text = getString(R.string.tap_loading)
            fl_loading.visibility = View.VISIBLE
        }
    }

    private fun endLoading(isDirectFinish: Boolean) {
        runOnUiThread {
            iv_saving.setImageDrawable(getDrawable(R.drawable.tap_ic_checklist_pumpkin))
            iv_saving.clearAnimation()
            tv_loading_text.text = getString(R.string.tap_finished)
            fl_loading.setOnClickListener { hideLoading() }

            Handler().postDelayed({
                this.hideLoading()
                if (isDirectFinish) {
                    val intent = Intent()
                    intent.putExtra(ROOM, groupViewModel?.room)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }, 1000L)
        }
    }

    private fun hideLoading() {
        fl_loading.visibility = View.GONE
    }

    private val addContactView = object : TAPDefaultDataView<TAPCommonResponse>() {
        override fun startLoading() {
            super.startLoading()
            showLoading()
        }

        override fun onSuccess(response: TAPCommonResponse?) {
            super.onSuccess(response)
            val newContact = groupViewModel?.groupMemberUser?.setUserAsContact()
            TAPDataManager.getInstance().insertMyContactToDatabase(dbListener, newContact)
            TAPContactManager.getInstance().updateUserDataMap(newContact)
            this@TAPGroupMemberProfileActivity.endLoading(true)
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            this@TAPGroupMemberProfileActivity.endLoading(false)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            this@TAPGroupMemberProfileActivity.endLoading(false)
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

    private val appointAdminView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading()
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            groupViewModel?.room = response?.room
            groupViewModel?.room?.groupParticipants = response?.participants
            groupViewModel?.room?.admins = response?.admins

            this@TAPGroupMemberProfileActivity.endLoading(true)
        }

        override fun onError(error: TAPErrorModel?) {
            this@TAPGroupMemberProfileActivity.endLoading(false)
        }

        override fun onError(errorMessage: String?) {
            this@TAPGroupMemberProfileActivity.endLoading(false)
        }
    }

    private val removeRoomMembersView = object : TAPDefaultDataView<TAPCreateRoomResponse>() {
        override fun startLoading() {
            showLoading()
        }

        override fun onSuccess(response: TAPCreateRoomResponse?) {
            super.onSuccess(response)
            groupViewModel?.room = response?.room
            groupViewModel?.room?.groupParticipants = response?.participants
            groupViewModel?.room?.admins = response?.admins

            this@TAPGroupMemberProfileActivity.endLoading(true)
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            this@TAPGroupMemberProfileActivity.endLoading(false)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            this@TAPGroupMemberProfileActivity.endLoading(false)
        }
    }
}