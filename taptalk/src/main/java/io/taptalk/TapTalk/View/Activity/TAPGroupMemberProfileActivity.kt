package io.taptalk.TapTalk.View.Activity

import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProviders
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
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_ADMIN
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Model.TAPMenuItem
import io.taptalk.TapTalk.View.Adapter.TAPMenuButtonAdapter
import io.taptalk.TapTalk.ViewModel.TAPProfileViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_chat_profile.*
import kotlinx.android.synthetic.main.tap_loading_layout_block_screen.*
import java.util.*

class TAPGroupMemberProfileActivity : TAPBaseActivity() {

    lateinit var glide: RequestManager
    var groupViewModel: TAPProfileViewModel? = null
    var menuAdapter : TAPMenuButtonAdapter? = null

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

        iv_button_back.setOnClickListener{ onBackPressed() }
        fl_loading.setOnClickListener{}

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
        if (null != groupViewModel?.groupMemberUser && groupViewModel?.isAdminGroup == true) run {
            //Group Member Profile
            val menuAddToContact = TAPMenuItem(
                    MENU_ADD_TO_CONTACTS,
                    R.drawable.tap_ic_add_circle_grey,
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_add_to_contacts))
            val menuSendMessage = TAPMenuItem(
                    MENU_SEND_MESSAGE,
                    R.drawable.tap_ic_send_message_grey,
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_send_message))
            val menuDemoteAdmin = TAPMenuItem(
                    MENU_DEMOTE_ADMIN,
                    R.drawable.tap_ic_delete_red,
                    R.color.tapIconChatProfileMenuClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_remove_admin))
            val menuKickMember = TAPMenuItem(
                    MENU_KICK_MEMBER,
                    R.drawable.tap_ic_delete_red,
                    R.color.tapIconChatProfileMenuClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_remove_group_members))
            menuItems.add(menuAddToContact)
            menuItems.add(menuSendMessage)
            menuItems.add(menuDemoteAdmin)
            menuItems.add(menuKickMember)
        } else if (null != groupViewModel?.groupMemberUser) run {
            val menuAddToContact = TAPMenuItem(
                    MENU_ADD_TO_CONTACTS,
                    R.drawable.tap_ic_add_circle_grey,
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_add_to_contacts))
            val menuSendMessage = TAPMenuItem(
                    MENU_SEND_MESSAGE,
                    R.drawable.tap_ic_send_message_grey,
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_send_message))
            val menuPromoteAdmin = TAPMenuItem(
                    MENU_PROMOTE_ADMIN,
                    R.drawable.tap_ic_appoint_admin,
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_appoint_admin))
            val menuKickMember = TAPMenuItem(
                    MENU_KICK_MEMBER,
                    R.drawable.tap_ic_delete_red,
                    R.color.tapIconChatProfileMenuClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle,
                    false,
                    false,
                    getString(R.string.tap_remove_group_members))
            // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
            menuItems.add(menuAddToContact)
            menuItems.add(menuSendMessage)
            menuItems.add(menuPromoteAdmin)
            menuItems.add(menuKickMember)
        }
        return menuItems
    }

    private val profileMenuInterface = TAPChatProfileActivity.ProfileMenuInterface {

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
                collapsing_toolbar_layout.setScrimVisibleHeightTrigger(scrimHeight)
            }

            if (Math.abs(verticalOffset) >= scrollRange && !isShowing) {
                // Show Toolbar
                isShowing = true
                ll_toolbar_collapsed.setVisibility(View.VISIBLE)
                ll_toolbar_collapsed.animate()
                        .alpha(1f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .start()
                tv_collapsed_name.setTranslationY(nameTranslationY.toFloat())
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
            } else if (Math.abs(verticalOffset) < scrollRange && isShowing) {
                // Hide Toolbar
                isShowing = false
                ll_toolbar_collapsed.animate()
                        .alpha(0f)
                        .setDuration(DEFAULT_ANIMATION_TIME.toLong())
                        .withEndAction({ ll_toolbar_collapsed.setVisibility(View.GONE) })
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

    private fun endLoading() {
        runOnUiThread {
            iv_saving.setImageDrawable(getDrawable(R.drawable.tap_ic_checklist_pumpkin))
            iv_saving.clearAnimation()
            tv_loading_text.text = getString(R.string.tap_finished)
            fl_loading.setOnClickListener { hideLoading() }

            Handler().postDelayed({ this.hideLoading() }, 1000L)
        }
    }

    private fun hideLoading() {
        fl_loading.visibility = View.GONE
    }
}