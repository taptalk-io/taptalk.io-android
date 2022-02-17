package io.taptalk.TapTalk.View.Activity

import android.Manifest
import io.taptalk.TapTalk.Manager.TAPGroupManager.Companion.getInstance
import io.taptalk.TapTalk.Manager.TAPGroupManager.getGroupData
import io.taptalk.TapTalk.View.Activity.TAPGroupMemberListActivity.Companion.start
import io.taptalk.TapTalk.Manager.TAPGroupManager.addGroupData
import io.taptalk.TapTalk.Manager.TAPGroupManager.removeGroupData
import io.taptalk.TapTalk.Manager.TAPGroupManager.refreshRoomList
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.View.Adapter.TapChatProfileAdapter
import androidx.recyclerview.widget.GridLayoutManager
import android.view.ViewTreeObserver.OnScrollChangedListener
import io.taptalk.TapTalk.ViewModel.TAPProfileViewModel
import com.bumptech.glide.RequestManager
import android.os.Bundle
import io.taptalk.TapTalk.R
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent
import android.content.pm.PackageManager
import android.content.Intent
import android.app.Activity
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode
import io.taptalk.TapTalk.Manager.TAPGroupManager
import android.os.Parcelable
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Manager.TAPContactManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel
import io.taptalk.TapTalk.Manager.TAPDataManager
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.SimpleItemAnimator
import io.taptalk.TapTalk.Model.TAPImageURL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity
import io.taptalk.TapTalk.View.Activity.TAPEditGroupSubjectActivity
import io.taptalk.TapTalk.View.Activity.TAPGroupMemberListActivity
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Helper.TAPUtils
import androidx.core.app.ActivityCompat
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity.ChatProfileInterface
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData
import io.taptalk.TapTalk.Manager.TAPCacheManager
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse
import android.os.Build
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Manager.TAPOldDataManager
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import io.taptalk.TapTalk.Model.TAPRoomModel
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.jvm.JvmOverloads

class TAPChatProfileActivity : TAPBaseActivity() {
    private var flLoading: FrameLayout? = null
    private var ivButtonBack: ImageView? = null
    private var ivSaving: ImageView? = null
    private var tvTitle: TextView? = null
    private var tvLoadingText: TextView? = null
    private var rvChatProfile: RecyclerView? = null
    private var adapter: TapChatProfileAdapter? = null
    private var glm: GridLayoutManager? = null
    private var sharedMediaPagingScrollListener: OnScrollChangedListener? = null
    private var vm: TAPProfileViewModel? = null
    private var glide: RequestManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_chat_profile)
        glide = Glide.with(this)
        initViewModel()
        initView()
        TAPBroadcastManager.register(
            this,
            downloadProgressReceiver,
            DownloadBroadcastEvent.DownloadProgressLoading,
            DownloadBroadcastEvent.DownloadFinish,
            DownloadBroadcastEvent.DownloadFailed
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(this, downloadProgressReceiver)
    }

    override fun onBackPressed() {
        if (vm!!.isApiCallOnProgress) {
            return
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE -> startVideoDownload(
                    vm!!.pendingDownloadMessage
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK == resultCode) {
            when (requestCode) {
                RequestCode.GROUP_UPDATE_DATA -> {
                    vm!!.groupDataFromManager = getInstance(instanceKey).getGroupData(
                        vm!!.room.roomID
                    )
                    if (null == data) {
                        return
                    }
                    if (null != data.getParcelableExtra(Extras.ROOM)) {
                        vm!!.room = data.getParcelableExtra(Extras.ROOM)
                        updateView()
                    }
                    if (data.getBooleanExtra(Extras.CLOSE_ACTIVITY, false)) {
                        val intent = Intent()
                        intent.putExtra(Extras.CLOSE_ACTIVITY, true)
                        setResult(RESULT_OK)
                        onBackPressed()
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        vm = ViewModelProvider(this).get(TAPProfileViewModel::class.java)
        vm!!.room = intent.getParcelableExtra(Extras.ROOM)
        if (null == vm!!.room) {
            finish()
        }
        vm!!.groupMemberUser = intent.getParcelableExtra(TAPDefaultConstant.K_USER)
        if (null != vm!!.groupMemberUser) {
            vm!!.isGroupMemberProfile = true
            vm!!.isGroupAdmin = intent.getBooleanExtra(Extras.IS_ADMIN, false)
            vm!!.userDataFromManager =
                TAPContactManager.getInstance(instanceKey).getUserData(vm!!.groupMemberUser.userID)
        } else if (vm!!.room.type == RoomType.TYPE_PERSONAL) {
            vm!!.userDataFromManager = TAPContactManager.getInstance(instanceKey).getUserData(
                TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(
                    vm!!.room.roomID
                )
            )
        } else if (vm!!.room.type == RoomType.TYPE_GROUP) {
            vm!!.groupDataFromManager = getInstance(instanceKey).getGroupData(
                vm!!.room.roomID
            )
        }
        vm!!.sharedMedias.clear()
    }

    private fun initView() {
        flLoading = findViewById(R.id.fl_loading)
        ivButtonBack = findViewById(R.id.iv_button_back)
        ivSaving = findViewById(R.id.iv_loading_image)
        tvTitle = findViewById(R.id.tv_title)
        tvLoadingText = findViewById(R.id.tv_loading_text)
        rvChatProfile = findViewById(R.id.rv_chat_profile)
        window.setBackgroundDrawable(null)
        updateView()
        if (!vm!!.isGroupMemberProfile) {
            // Show loading on start
            vm!!.loadingItem = TapChatProfileItemModel(TapChatProfileItemModel.TYPE_LOADING_LAYOUT)
            vm!!.adapterItems.add(vm!!.loadingItem)

            // Load shared medias
            Thread {
                TAPDataManager.getInstance(instanceKey)
                    .getRoomMedias(0L, vm!!.room.roomID, sharedMediaListener)
            }
                .start()
        }

        // Setup recycler view
        adapter = TapChatProfileAdapter(instanceKey, vm!!.adapterItems, chatProfileInterface, glide)
        glm = object : GridLayoutManager(this, 3) {
            override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        glm.setSpanSizeLookup(object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter!!.getItemAt(position).type == TapChatProfileItemModel.TYPE_MEDIA_THUMBNAIL) {
                    1
                } else {
                    3
                }
            }
        })
        rvChatProfile.setAdapter(adapter)
        rvChatProfile.setLayoutManager(glm)
        rvChatProfile.addOnScrollListener(scrollListener)
        val recyclerAnimator = rvChatProfile.getItemAnimator() as SimpleItemAnimator?
        if (null != recyclerAnimator) {
            recyclerAnimator.supportsChangeAnimations = false
        }
        ivButtonBack.setOnClickListener(View.OnClickListener { v: View? -> onBackPressed() })
        flLoading.setOnClickListener(View.OnClickListener { v: View? -> })

        // Update room data
        if (vm!!.room.type == RoomType.TYPE_PERSONAL) {
            TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(
                TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(vm!!.room.roomID),
                getUserView
            )
        } else if (vm!!.room.type == RoomType.TYPE_GROUP) {
            TAPDataManager.getInstance(instanceKey).getChatRoomData(vm!!.room.roomID, getRoomView)
            if (!vm!!.isGroupMemberProfile) {
                // Change title to Group Details
                tvTitle.setText(R.string.tap_group_details)
            }
        }
    }

    private fun updateView() {
        // Set profile detail item
        val imageURL: TAPImageURL?
        var itemLabel: String? = ""
        var itemSubLabel = ""
        var textStyleResource = 0
        if (null != vm!!.userDataFromManager &&
            !vm!!.userDataFromManager.fullname.isEmpty()
        ) {
            // Set name & avatar from contact manager
            imageURL = vm!!.userDataFromManager.imageURL
            itemLabel = vm!!.userDataFromManager.fullname
            if (null != vm!!.userDataFromManager.username &&
                !vm!!.userDataFromManager.username!!.isEmpty()
            ) {
                itemSubLabel = "@" + vm!!.userDataFromManager.username
                textStyleResource = R.style.tapChatProfileUsernameStyle
            }
        } else if (null != vm!!.groupDataFromManager &&
            !vm!!.groupDataFromManager.name.isEmpty()
        ) {
            // Set name & avatar from group manager
            imageURL = vm!!.groupDataFromManager.imageURL
            itemLabel = vm!!.groupDataFromManager.name
            if (null != vm!!.groupDataFromManager.participants &&
                !vm!!.groupDataFromManager.participants!!.isEmpty()
            ) {
                itemSubLabel = String.format(
                    getString(R.string.tap_format_d_group_member_count),
                    vm!!.groupDataFromManager.participants!!.size
                )
                textStyleResource = R.style.tapChatProfileMemberCountStyle
            }
        } else if (vm!!.isGroupMemberProfile) {
            // Set name & avatar from passed member profile intent
            imageURL = vm!!.groupMemberUser.imageURL
            itemLabel = vm!!.groupMemberUser.fullname
            if (null != vm!!.groupMemberUser.username &&
                !vm!!.groupMemberUser.username!!.isEmpty()
            ) {
                itemSubLabel = "@" + vm!!.groupMemberUser.username
                textStyleResource = R.style.tapChatProfileUsernameStyle
            }
        } else {
            // Set name & avatar from passed room intent
            imageURL = vm!!.room.imageURL
            itemLabel = vm!!.room.name
            if (null != vm!!.room.participants &&
                !vm!!.room.participants!!.isEmpty()
            ) {
                itemSubLabel = String.format(
                    getString(R.string.tap_format_d_group_member_count),
                    vm!!.room.participants!!.size
                )
                textStyleResource = R.style.tapChatProfileMemberCountStyle
            }
        }
        vm!!.adapterItems.remove(vm!!.profileDetailItem)
        vm!!.profileDetailItem = TapChatProfileItemModel(
            imageURL,
            itemLabel,
            itemSubLabel,
            textStyleResource
        )
        vm!!.adapterItems.add(0, vm!!.profileDetailItem)

        // Update room menu
        vm!!.adapterItems.removeAll(vm!!.menuItems)
        vm!!.menuItems = generateChatProfileMenu()
        vm!!.adapterItems.addAll(1, vm!!.menuItems)
        if (null != adapter) {
            adapter!!.items = vm!!.adapterItems
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun generateChatProfileMenu(): List<TapChatProfileItemModel> {
        val menuItems: MutableList<TapChatProfileItemModel> = ArrayList()
        if (!vm!!.isGroupMemberProfile) {
            // Notification
            val menuNotification = TapChatProfileItemModel(
                ChatProfileMenuType.MENU_NOTIFICATION,
                getString(R.string.tap_notifications),
                R.drawable.tap_ic_notification_orange,
                R.color.tapIconChatProfileMenuNotificationInactive,
                R.style.tapChatProfileMenuLabelStyle
            )
            menuNotification.isChecked = !vm!!.room.isMuted

            // Room color
            val menuRoomColor = TapChatProfileItemModel(
                ChatProfileMenuType.MENU_ROOM_COLOR,
                getString(R.string.tap_conversation_color),
                R.drawable.tap_ic_color_grey,
                R.color.tapIconChatProfileMenuConversationColor,
                R.style.tapChatProfileMenuLabelStyle
            )

            // Search chat
            val menuRoomSearchChat = TapChatProfileItemModel(
                ChatProfileMenuType.MENU_ROOM_SEARCH_CHAT,
                getString(R.string.tap_search_chat),
                R.drawable.tap_ic_search_grey_small,
                R.color.tapIconChatProfileMenuSearchChat,
                R.style.tapChatProfileMenuLabelStyle
            )

            // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
//        menuItems.add(menuNotification);
//        menuItems.add(menuRoomColor);
//        menuItems.add(menuRoomSearchChat);
            if (vm!!.room.type == RoomType.TYPE_PERSONAL) {
                //// Personal chat room

                // Add to contacts
                if (!TapUI.getInstance(instanceKey).isAddContactDisabled && TapUI.getInstance(
                        instanceKey
                    ).isAddToContactsButtonInChatProfileVisible
                ) {
                    val contact = TAPContactManager.getInstance(instanceKey).getUserData(
                        TAPChatManager.getInstance(instanceKey)
                            .getOtherUserIdFromRoom(vm!!.room.roomID)
                    )
                    if (null == contact || null == contact.isContact || contact.isContact == 0) {
                        val menuAddToContact = TapChatProfileItemModel(
                            ChatProfileMenuType.MENU_ADD_TO_CONTACTS,
                            getString(R.string.tap_add_to_contacts),
                            R.drawable.tap_ic_add_circle_orange,
                            R.color.tapIconGroupMemberProfileMenuAddToContacts,
                            R.style.tapChatProfileMenuLabelStyle
                        )
                        menuItems.add(menuAddToContact)
                    }
                }
                if (intent.getBooleanExtra(Extras.IS_NON_PARTICIPANT_USER_PROFILE, false)) {
                    // Send message
                    val menuSendMessage = TapChatProfileItemModel(
                        ChatProfileMenuType.MENU_SEND_MESSAGE,
                        getString(R.string.tap_send_message),
                        R.drawable.tap_ic_send_message_orange,
                        R.color.tapIconGroupMemberProfileMenuSendMessage,
                        R.style.tapChatProfileMenuLabelStyle
                    )
                    menuItems.add(menuSendMessage)
                }

                // Block user
                val menuBlock = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_BLOCK,
                    getString(R.string.tap_block_user),
                    R.drawable.tap_ic_block_red,
                    R.color.tapIconChatProfileMenuBlockUser,
                    R.style.tapChatProfileMenuLabelStyle
                )

                // Clear chat
                val menuClearChat = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_CLEAR_CHAT,
                    getString(R.string.tap_clear_chat),
                    R.drawable.tap_ic_delete_red,
                    R.color.tapIconChatProfileMenuClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle
                )

                // TODO: 9 May 2019 TEMPORARILY DISABLED FEATURE
//            menuItems.add(2, menuBlock);
//            menuItems.add(menuClearChat);
                if (TapUI.getInstance(instanceKey).isReportButtonInChatProfileVisible) {
                    // Report user
                    val menuReport = TapChatProfileItemModel(
                        ChatProfileMenuType.MENU_REPORT,
                        getString(R.string.tap_report_user),
                        R.drawable.tap_ic_flag_black,
                        R.color.tapIconChatProfileMenuReportUserOrGroup,
                        R.style.tapChatProfileMenuDestructiveLabelStyle
                    )
                    menuItems.add(menuReport)
                }
            } else if (vm!!.room.type == RoomType.TYPE_GROUP && null != vm!!.room.admins &&
                vm!!.room.admins!!
                    .contains(TAPChatManager.getInstance(instanceKey).activeUser.userID)
            ) {
                //// Group chat where the active user is admin

                // View members
                val menuViewMembers = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_VIEW_MEMBERS,
                    getString(R.string.tap_view_members),
                    R.drawable.tap_ic_members_orange,
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle
                )
                menuItems.add(menuViewMembers)

                // Edit group
                val menuEditGroup = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_EDIT_GROUP,
                    getString(R.string.tap_edit_group),
                    R.drawable.tap_ic_edit_orange,
                    R.color.tapIconGroupProfileMenuEditGroup,
                    R.style.tapChatProfileMenuLabelStyle
                )
                menuItems.add(menuEditGroup)
                if (TapUI.getInstance(instanceKey).isReportButtonInChatProfileVisible) {
                    // Report group
                    val menuReport = TapChatProfileItemModel(
                        ChatProfileMenuType.MENU_REPORT,
                        getString(R.string.tap_report_group),
                        R.drawable.tap_ic_flag_black,
                        R.color.tapIconChatProfileMenuReportUserOrGroup,
                        R.style.tapChatProfileMenuDestructiveLabelStyle
                    )
                    menuItems.add(menuReport)
                }
                if (null != vm!!.room.participants &&
                    1 < vm!!.room.participants!!.size
                ) {
                    // Exit group
                    val menuExitGroup = TapChatProfileItemModel(
                        ChatProfileMenuType.MENU_EXIT_GROUP,
                        getString(R.string.tap_leave_group),
                        R.drawable.tap_ic_logout_red,
                        R.color.tapIconChatProfileMenuClearChat,
                        R.style.tapChatProfileMenuDestructiveLabelStyle
                    )
                    menuItems.add(menuExitGroup)
                } else {
                    // Delete group
                    val menuDeleteGroup = TapChatProfileItemModel(
                        ChatProfileMenuType.MENU_DELETE_GROUP,
                        getString(R.string.tap_delete_group),
                        R.drawable.tap_ic_delete_red,
                        R.color.tapIconChatProfileMenuClearChat,
                        R.style.tapChatProfileMenuDestructiveLabelStyle
                    )
                    menuItems.add(menuDeleteGroup)
                }
            } else if (vm!!.room.type == RoomType.TYPE_GROUP && null != vm!!.room.participants && 1 < vm!!.room.participants!!.size) {
                //// Group chat with more than 1 member

                // View members
                val menuViewMembers = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_VIEW_MEMBERS,
                    getString(R.string.tap_view_members),
                    R.drawable.tap_ic_members_orange,
                    R.color.tapIconGroupProfileMenuViewMembers,
                    R.style.tapChatProfileMenuLabelStyle
                )
                menuItems.add(menuViewMembers)
                if (TapUI.getInstance(instanceKey).isReportButtonInChatProfileVisible) {
                    // Report group
                    val menuReport = TapChatProfileItemModel(
                        ChatProfileMenuType.MENU_REPORT,
                        getString(R.string.tap_report_group),
                        R.drawable.tap_ic_flag_black,
                        R.color.tapIconChatProfileMenuReportUserOrGroup,
                        R.style.tapChatProfileMenuDestructiveLabelStyle
                    )
                    menuItems.add(menuReport)
                }

                // Exit group
                val menuExitGroup = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_EXIT_GROUP,
                    getString(R.string.tap_leave_group),
                    R.drawable.tap_ic_logout_red,
                    R.color.tapIconChatProfileMenuClearChat,
                    R.style.tapChatProfileMenuDestructiveLabelStyle
                )
                menuItems.add(menuExitGroup)
            }
        } else {
            //// Group chat member profile

            // Add to contacts
            if (!TapUI.getInstance(instanceKey).isAddContactDisabled && TapUI.getInstance(
                    instanceKey
                ).isAddToContactsButtonInChatProfileVisible
            ) {
                val contact = TAPContactManager.getInstance(instanceKey).getUserData(
                    vm!!.groupMemberUser.userID
                )
                if (null == contact || null == contact.isContact || contact.isContact == 0) {
                    val menuAddToContact = TapChatProfileItemModel(
                        ChatProfileMenuType.MENU_ADD_TO_CONTACTS,
                        getString(R.string.tap_add_to_contacts),
                        R.drawable.tap_ic_add_circle_orange,
                        R.color.tapIconGroupMemberProfileMenuAddToContacts,
                        R.style.tapChatProfileMenuLabelStyle
                    )
                    menuItems.add(menuAddToContact)
                }
            }

            // Send message
            val menuSendMessage = TapChatProfileItemModel(
                ChatProfileMenuType.MENU_SEND_MESSAGE,
                getString(R.string.tap_send_message),
                R.drawable.tap_ic_send_message_orange,
                R.color.tapIconGroupMemberProfileMenuSendMessage,
                R.style.tapChatProfileMenuLabelStyle
            )
            menuItems.add(menuSendMessage)

            // Promote admin
            if (null != vm!!.room.admins &&
                vm!!.room.admins!!
                    .contains(TAPChatManager.getInstance(instanceKey).activeUser.userID) &&
                !vm!!.room.admins!!.contains(vm!!.groupMemberUser.userID)
            ) {
                val menuPromoteAdmin = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_PROMOTE_ADMIN,
                    getString(R.string.tap_promote_admin),
                    R.drawable.tap_ic_appoint_admin,
                    R.color.tapIconGroupMemberProfileMenuPromoteAdmin,
                    R.style.tapChatProfileMenuLabelStyle
                )
                menuItems.add(menuPromoteAdmin)
            } else if (null != vm!!.room.admins &&
                vm!!.room.admins!!
                    .contains(TAPChatManager.getInstance(instanceKey).activeUser.userID)
            ) {
                val menuDemoteAdmin = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_DEMOTE_ADMIN,
                    getString(R.string.tap_demote_admin),
                    R.drawable.tap_ic_demote_admin,
                    R.color.tapIconGroupMemberProfileMenuDemoteAdmin,
                    R.style.tapChatProfileMenuLabelStyle
                )
                menuItems.add(menuDemoteAdmin)
            }

            // Remove member
            if (null != vm!!.room.admins &&
                vm!!.room.admins!!
                    .contains(TAPChatManager.getInstance(instanceKey).activeUser.userID)
            ) {
                val menuRemoveMember = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_REMOVE_MEMBER,
                    getString(R.string.tap_remove_group_member),
                    R.drawable.tap_ic_delete_red,
                    R.color.tapIconGroupMemberProfileMenuRemoveMember,
                    R.style.tapChatProfileMenuDestructiveLabelStyle
                )
                menuItems.add(menuRemoveMember)
            }
            if (TapUI.getInstance(instanceKey).isReportButtonInChatProfileVisible) {
                // Report user
                val menuReport = TapChatProfileItemModel(
                    ChatProfileMenuType.MENU_REPORT,
                    getString(R.string.tap_report_user),
                    R.drawable.tap_ic_flag_black,
                    R.color.tapIconChatProfileMenuReportUserOrGroup,
                    R.style.tapChatProfileMenuDestructiveLabelStyle
                )
                menuItems.add(menuReport)
            }
        }
        return menuItems
    }

    private fun toggleNotification(isNotificationOn: Boolean) {
        Log.e(TAG, "toggleNotification: $isNotificationOn")
    }

    private fun changeRoomColor() {
        Log.e(TAG, "changeRoomColor: ")
    }

    private fun openEditGroup() {
        TAPEditGroupSubjectActivity.start(this, instanceKey, vm!!.room)
    }

    private fun searchChat() {
        Log.e(TAG, "searchChat: ")
    }

    private fun blockUser() {
        Log.e(TAG, "blockUser: ")
    }

    private fun clearChat() {
        Log.e(TAG, "clearChat: ")
    }

    private fun viewMembers() {
        start(this@TAPChatProfileActivity, instanceKey, vm!!.room)
    }

    private fun showExitChatDialog() {
        TapTalkDialog.Builder(this)
            .setTitle(this.getString(R.string.tap_clear_and_exit_chat))
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setMessage(this.getString(R.string.tap_leave_group_confirmation))
            .setPrimaryButtonTitle(this.getString(R.string.tap_ok))
            .setPrimaryButtonListener { v: View? ->
                vm!!.loadingStartText = getString(R.string.tap_loading)
                vm!!.loadingEndText = getString(R.string.tap_left_group)
                TAPDataManager.getInstance(instanceKey)
                    .leaveChatRoom(vm!!.room.roomID, deleteRoomView)
            }
            .setSecondaryButtonTitle(this.getString(R.string.tap_cancel))
            .setSecondaryButtonListener { v: View? -> }
            .show()
    }

    private fun addToContacts() {
        if (vm!!.isGroupMemberProfile) {
            TAPDataManager.getInstance(instanceKey)
                .addContactApi(vm!!.groupMemberUser.userID, addContactView)
        } else if (vm!!.room.type == RoomType.TYPE_PERSONAL) {
            TAPDataManager.getInstance(instanceKey).addContactApi(
                TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(
                    vm!!.room.roomID
                ), addContactView
            )
        }
    }

    private fun openChatRoom(userModel: TAPUserModel) {
        TapUIChatActivity.start(
            this,
            instanceKey,
            TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                TAPChatManager.getInstance(instanceKey).activeUser.userID,
                userModel.userID
            ),
            userModel.fullname,
            userModel.imageURL,
            RoomType.TYPE_PERSONAL,
            ""
        ) // TODO: 15 October 2019 SET ROOM COLOR
        val intent = Intent()
        intent.putExtra(Extras.CLOSE_ACTIVITY, true)
        setResult(RESULT_OK, intent)
        onBackPressed()
    }

    private fun promoteAdmin() {
        vm!!.loadingStartText = getString(R.string.tap_updating)
        vm!!.loadingEndText = getString(R.string.tap_promoted_admin)
        TAPDataManager.getInstance(instanceKey).promoteGroupAdmins(
            vm!!.room.roomID,
            Arrays.asList(vm!!.groupMemberUser.userID), userActionView
        )
    }

    private fun showDemoteAdminDialog() {
        TapTalkDialog.Builder(this)
            .setTitle(getString(R.string.tap_demote_admin))
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setMessage(getString(R.string.tap_demote_admin_confirmation))
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setPrimaryButtonListener { v: View? ->
                vm!!.loadingStartText = getString(R.string.tap_updating)
                vm!!.loadingEndText = getString(R.string.tap_demoted_admin)
                TAPDataManager.getInstance(instanceKey).demoteGroupAdmins(
                    vm!!.room.roomID,
                    Arrays.asList(vm!!.groupMemberUser.userID),
                    userActionView
                )
            }
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setSecondaryButtonListener { view: View? -> }
            .show()
    }

    private fun showRemoveMemberDialog() {
        TapTalkDialog.Builder(this)
            .setTitle(getString(R.string.tap_remove_group_member))
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setMessage(getString(R.string.tap_remove_member_confirmation))
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setPrimaryButtonListener { v: View? ->
                vm!!.loadingStartText = getString(R.string.tap_removing)
                vm!!.loadingEndText = getString(R.string.tap_removed_member)
                TAPDataManager.getInstance(instanceKey).removeRoomParticipant(
                    vm!!.room.roomID,
                    Arrays.asList(vm!!.groupMemberUser.userID),
                    userActionView
                )
            }
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setSecondaryButtonListener { view: View? -> }
            .show()
    }

    private fun showDeleteChatRoomDialog() {
        TapTalkDialog.Builder(this)
            .setTitle(this.getString(R.string.tap_delete_chat_room))
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setMessage(this.getString(R.string.tap_delete_group_confirmation))
            .setPrimaryButtonTitle(this.getString(R.string.tap_ok))
            .setPrimaryButtonListener { v: View? ->
                vm!!.loadingStartText = getString(R.string.tap_loading)
                vm!!.loadingEndText = getString(R.string.tap_group_deleted)
                TAPDataManager.getInstance(instanceKey).deleteChatRoom(vm!!.room, deleteRoomView)
            }
            .setSecondaryButtonTitle(this.getString(R.string.tap_cancel))
            .setSecondaryButtonListener { v: View? -> }
            .show()
    }

    private fun triggerReportButtonTapped() {
        if (vm!!.room.type == RoomType.TYPE_PERSONAL) {
            TAPChatManager.getInstance(instanceKey)
                .triggerChatProfileReportUserButtonTapped(this, vm!!.room, vm!!.userDataFromManager)
        } else {
            TAPChatManager.getInstance(instanceKey)
                .triggerChatProfileReportGroupButtonTapped(this, vm!!.room)
        }
    }

    private fun startVideoDownload(message: TAPMessageModel) {
        if (!TAPUtils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm!!.pendingDownloadMessage = message
            ActivityCompat.requestPermissions(
                this@TAPChatProfileActivity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            )
        } else {
            // Download file
            vm!!.pendingDownloadMessage = null
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message)
        }
        notifyItemChanged(message)
    }

    private fun notifyItemChanged(mediaMessage: TAPMessageModel) {
        runOnUiThread {
            adapter!!.notifyItemChanged(
                vm!!.menuItems.size + 2 + vm!!.sharedMedias.indexOf(
                    mediaMessage
                )
            )
        }
    }

    private fun showSharedMediaLoading() {
        if (vm!!.adapterItems.contains(vm!!.loadingItem)) {
            return
        }
        vm!!.adapterItems.add(vm!!.loadingItem)
        adapter!!.setMediaThumbnailStartIndex(vm!!.adapterItems.indexOf(vm!!.sharedMediaSectionTitle) + 1)
        adapter!!.notifyItemInserted(adapter!!.itemCount - 1)
    }

    private fun hideSharedMediaLoading() {
        if (!vm!!.adapterItems.contains(vm!!.loadingItem)) {
            return
        }
        val index = vm!!.adapterItems.indexOf(vm!!.loadingItem)
        vm!!.adapterItems.removeAt(index)
        adapter!!.setMediaThumbnailStartIndex(vm!!.adapterItems.indexOf(vm!!.sharedMediaSectionTitle) + 1)
        adapter!!.notifyItemRemoved(index)
    }

    private fun showLoadingPopup(message: String) {
        vm!!.isApiCallOnProgress = true
        runOnUiThread {
            ivSaving!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tap_ic_loading_progress_circle_white
                )
            )
            if (null == ivSaving!!.animation) {
                TAPUtils.rotateAnimateInfinitely(this, ivSaving)
            }
            tvLoadingText!!.text = message
            flLoading!!.visibility = View.VISIBLE
        }
    }

    private fun hideLoadingPopup(message: String) {
        //vm.setApiCallOnProgress(false);
        runOnUiThread {
            ivSaving!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tap_ic_checklist_pumpkin
                )
            )
            ivSaving!!.clearAnimation()
            tvLoadingText!!.text = message
            flLoading!!.setOnClickListener { v: View? -> hideLoadingPopup() }
            Handler().postDelayed({ this.hideLoadingPopup() }, 1000L)
        }
    }

    private fun hideLoadingPopup() {
        vm!!.isApiCallOnProgress = false
        flLoading!!.visibility = View.GONE
    }

    private fun showErrorDialog(title: String, message: String) {
        TapTalkDialog.Builder(this)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setTitle(title)
            .setCancelable(true)
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .show()
    }

    interface ChatProfileInterface {
        fun onMenuClicked(item: TapChatProfileItemModel)
        fun onMediaClicked(item: TAPMessageModel, ivThumbnail: ImageView?, isMediaReady: Boolean)
        fun onCancelDownloadClicked(item: TAPMessageModel)
        fun onReloadSharedMedia()
    }

    private val chatProfileInterface: ChatProfileInterface = object : ChatProfileInterface {
        override fun onMenuClicked(item: TapChatProfileItemModel) {
            when (item.menuId) {
                ChatProfileMenuType.MENU_NOTIFICATION -> toggleNotification(item.isChecked)
                ChatProfileMenuType.MENU_ROOM_COLOR -> changeRoomColor()
                ChatProfileMenuType.MENU_ROOM_SEARCH_CHAT -> searchChat()
                ChatProfileMenuType.MENU_BLOCK -> blockUser()
                ChatProfileMenuType.MENU_CLEAR_CHAT -> clearChat()
                ChatProfileMenuType.MENU_VIEW_MEMBERS -> viewMembers()
                ChatProfileMenuType.MENU_EDIT_GROUP -> openEditGroup()
                ChatProfileMenuType.MENU_EXIT_GROUP -> showExitChatDialog()
                ChatProfileMenuType.MENU_ADD_TO_CONTACTS -> addToContacts()
                ChatProfileMenuType.MENU_SEND_MESSAGE -> if (intent.getBooleanExtra(
                        Extras.IS_NON_PARTICIPANT_USER_PROFILE,
                        false
                    )
                ) {
                    openChatRoom(
                        TAPContactManager.getInstance(instanceKey).getUserData(
                            TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(
                                vm!!.room.roomID
                            )
                        )
                    )
                } else {
                    openChatRoom(vm!!.groupMemberUser)
                }
                ChatProfileMenuType.MENU_PROMOTE_ADMIN -> promoteAdmin()
                ChatProfileMenuType.MENU_DEMOTE_ADMIN -> showDemoteAdminDialog()
                ChatProfileMenuType.MENU_REMOVE_MEMBER -> showRemoveMemberDialog()
                ChatProfileMenuType.MENU_DELETE_GROUP -> showDeleteChatRoomDialog()
                ChatProfileMenuType.MENU_REPORT -> triggerReportButtonTapped()
            }
        }

        override fun onMediaClicked(
            item: TAPMessageModel,
            ivThumbnail: ImageView?,
            isMediaReady: Boolean
        ) {
            if (item.type == TAPDefaultConstant.MessageType.TYPE_IMAGE && isMediaReady) {
                // Preview image detail
                TAPImageDetailPreviewActivity.start(
                    this@TAPChatProfileActivity,
                    instanceKey,
                    item,
                    ivThumbnail
                )
            } else if (item.type == TAPDefaultConstant.MessageType.TYPE_IMAGE) {
                // Download image
                TAPFileDownloadManager.getInstance(instanceKey)
                    .downloadImage(this@TAPChatProfileActivity, item)
                notifyItemChanged(item)
            } else if (item.type == TAPDefaultConstant.MessageType.TYPE_VIDEO && isMediaReady && null != item.data) {
                val videoUri =
                    TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(item)
                if (null == videoUri) {
                    // Prompt download
                    val fileID = item.data!![MessageData.FILE_ID] as String?
                    TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache(fileID)
                    notifyItemChanged(item)
                    TapTalkDialog.Builder(this@TAPChatProfileActivity)
                        .setTitle(getString(R.string.tap_error_could_not_find_file))
                        .setMessage(getString(R.string.tap_error_redownload_file))
                        .setCancelable(true)
                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                        .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                        .setPrimaryButtonListener { v: View? -> startVideoDownload(item) }
                        .show()
                } else {
                    // Open video player
                    TAPVideoPlayerActivity.start(
                        this@TAPChatProfileActivity,
                        instanceKey,
                        videoUri,
                        item
                    )
                }
            } else if (item.type == TAPDefaultConstant.MessageType.TYPE_VIDEO) {
                // Download video
                startVideoDownload(item)
            }
        }

        override fun onCancelDownloadClicked(item: TAPMessageModel) {
            TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(item.localID)
            notifyItemChanged(item)
        }

        override fun onReloadSharedMedia() {
            // TODO: 15 October 2019
        }
    }
    private val getRoomView: TAPDefaultDataView<TAPCreateRoomResponse> =
        object : TAPDefaultDataView<TAPCreateRoomResponse?>() {
            override fun onSuccess(response: TAPCreateRoomResponse) {
                vm!!.room = response.room
                vm!!.room.participants = response.participants
                vm!!.room.admins = response.admins
                getInstance(instanceKey).addGroupData(vm!!.room)
                updateView()
            }
        }
    private val getUserView: TAPDefaultDataView<TAPGetUserResponse> =
        object : TAPDefaultDataView<TAPGetUserResponse?>() {
            override fun onSuccess(response: TAPGetUserResponse) {
                val user = response.user
                TAPContactManager.getInstance(instanceKey).updateUserData(user)
                vm!!.room.imageURL = user.imageURL
                vm!!.room.name = user.fullname
                updateView()
            }
        }
    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        recyclerView.elevation = TAPUtils.dpToPx(2).toFloat()
                    } else {
                        recyclerView.elevation = 0f
                    }
                }
            }
        }
    private val deleteRoomView: TAPDefaultDataView<TAPCommonResponse> =
        object : TAPDefaultDataView<TAPCommonResponse?>() {
            override fun startLoading() {
                showLoadingPopup(vm!!.loadingStartText)
            }

            override fun onSuccess(response: TAPCommonResponse) {
                if (response.success) {
                    TAPOldDataManager.getInstance(instanceKey).cleanRoomPhysicalData(
                        vm!!.room.roomID,
                        object : TAPDatabaseListener<Any?>() {
                            override fun onDeleteFinished() {
                                TAPDataManager.getInstance(instanceKey).deleteMessageByRoomId(
                                    vm!!.room.roomID,
                                    object : TAPDatabaseListener<Any?>() {
                                        override fun onDeleteFinished() {
                                            //hideLoadingPopup(vm.getLoadingEndText());
                                            getInstance(instanceKey).removeGroupData(vm!!.room.roomID)
                                            getInstance(instanceKey).refreshRoomList = true
                                            runOnUiThread {
                                                ivSaving!!.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        this@TAPChatProfileActivity,
                                                        R.drawable.tap_ic_checklist_pumpkin
                                                    )
                                                )
                                                ivSaving!!.clearAnimation()
                                                tvLoadingText!!.text = vm!!.loadingEndText
                                                Handler().postDelayed({
                                                    vm!!.isApiCallOnProgress = false
                                                    flLoading!!.visibility = View.GONE
                                                    setResult(RESULT_OK)
                                                    finish()
                                                    overridePendingTransition(
                                                        R.anim.tap_stay,
                                                        R.anim.tap_slide_right
                                                    )
                                                }, 1000L)
                                            }
                                        }
                                    })
                            }
                        })
                } else {
                    hideLoadingPopup()
                    TapTalkDialog.Builder(this@TAPChatProfileActivity)
                        .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                        .setTitle(getString(R.string.tap_failed))
                        .setMessage(
                            if (null != response.message) response.message else resources.getString(
                                R.string.tap_error_message_general
                            )
                        )
                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                        .show()
                }
            }

            override fun onError(error: TAPErrorModel) {
                hideLoadingPopup()
                showErrorDialog(getString(R.string.tap_error), error.message)
            }

            override fun onError(errorMessage: String) {
                hideLoadingPopup()
                showErrorDialog(
                    getString(R.string.tap_error),
                    getString(R.string.tap_error_message_general)
                )
            }
        }
    private val addContactView: TAPDefaultDataView<TAPAddContactResponse> =
        object : TAPDefaultDataView<TAPAddContactResponse?>() {
            override fun startLoading() {
                showLoadingPopup(getString(R.string.tap_adding))
            }

            override fun onSuccess(response: TAPAddContactResponse) {
                val newContact = response.user.setUserAsContact()
                //TAPDataManager.getInstance(instanceKey).insertMyContactToDatabase(new TAPDatabaseListener<TAPUserModel>() {
                //}, newContact);
                TAPContactManager.getInstance(instanceKey).updateUserData(newContact)
                hideLoadingPopup(getString(R.string.tap_added_contact))
                updateView()
            }

            override fun onError(error: TAPErrorModel) {
                hideLoadingPopup()
                showErrorDialog(getString(R.string.tap_error), error.message)
            }

            override fun onError(errorMessage: String) {
                hideLoadingPopup()
                showErrorDialog(getString(R.string.tap_error), errorMessage)
            }
        }
    private val userActionView: TAPDefaultDataView<TAPCreateRoomResponse> =
        object : TAPDefaultDataView<TAPCreateRoomResponse?>() {
            override fun startLoading() {
                showLoadingPopup(vm!!.loadingStartText)
            }

            override fun onSuccess(response: TAPCreateRoomResponse) {
                vm!!.room = response.room
                vm!!.room.participants = response.participants
                vm!!.room.admins = response.admins
                getInstance(instanceKey).addGroupData(vm!!.room)
                hideLoadingPopup(vm!!.loadingEndText)
                Handler().postDelayed({
                    val intent = Intent()
                    intent.putExtra(Extras.ROOM, vm!!.room)
                    setResult(RESULT_OK, intent)
                    finish()
                    overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
                }, 1000L)
            }

            override fun onError(error: TAPErrorModel) {
                hideLoadingPopup()
                showErrorDialog(getString(R.string.tap_error), error.message)
            }

            override fun onError(errorMessage: String) {
                hideLoadingPopup()
                showErrorDialog(
                    getString(R.string.tap_error),
                    getString(R.string.tap_error_message_general)
                )
            }
        }
    private val sharedMediaListener: TAPDatabaseListener<TAPMessageEntity> =
        object : TAPDatabaseListener<TAPMessageEntity?>() {
            override fun onSelectFinished(entities: List<TAPMessageEntity>) {
                Thread {
                    if (0 == entities.size && 0 == vm!!.sharedMedias.size) {
                        // No shared media
                        vm!!.isFinishedLoadingSharedMedia = true
                        runOnUiThread { rvChatProfile!!.post { hideSharedMediaLoading() } }
                    } else {
                        // Has shared media
                        val previousSize = vm!!.sharedMedias.size
                        if (0 == previousSize) {
                            // First load
                            vm!!.sharedMediaSectionTitle =
                                TapChatProfileItemModel(getString(R.string.tap_shared_media))
                            vm!!.adapterItems.add(vm!!.sharedMediaSectionTitle)
                            adapter!!.setMediaThumbnailStartIndex(vm!!.adapterItems.indexOf(vm!!.sharedMediaSectionTitle) + 1)
                            runOnUiThread {
                                if (TAPDefaultConstant.MAX_ITEMS_PER_PAGE <= entities.size) {
                                    sharedMediaPagingScrollListener = OnScrollChangedListener {
                                        if (!vm!!.isFinishedLoadingSharedMedia && glm!!.findLastVisibleItemPosition() > vm!!.menuItems.size + vm!!.sharedMedias.size - TAPDefaultConstant.MAX_ITEMS_PER_PAGE / 2) {
                                            // Load more if view holder is visible
                                            if (!vm!!.isLoadingSharedMedia) {
                                                vm!!.isLoadingSharedMedia = true
                                                showSharedMediaLoading()
                                                Thread {
                                                    TAPDataManager.getInstance(instanceKey)
                                                        .getRoomMedias(
                                                            vm!!.lastSharedMediaTimestamp,
                                                            vm!!.room.roomID,
                                                            this
                                                        )
                                                }.start()
                                            }
                                        }
                                    }
                                    rvChatProfile!!.viewTreeObserver.addOnScrollChangedListener(
                                        sharedMediaPagingScrollListener
                                    )
                                }
                            }
                        }
                        if (TAPDefaultConstant.MAX_ITEMS_PER_PAGE > entities.size) {
                            // No more medias in database
                            // TODO: 10 May 2019 CALL API BEFORE?
                            vm!!.isFinishedLoadingSharedMedia = true
                            runOnUiThread {
                                rvChatProfile!!.viewTreeObserver.removeOnScrollChangedListener(
                                    sharedMediaPagingScrollListener
                                )
                            }
                        }
                        for (entity in entities) {
                            val mediaMessage = TAPMessageModel.fromMessageEntity(entity)
                            vm!!.addSharedMedia(mediaMessage)
                            vm!!.adapterItems.add(TapChatProfileItemModel(mediaMessage))
                        }
                        vm!!.lastSharedMediaTimestamp =
                            vm!!.sharedMedias[vm!!.sharedMedias.size - 1].created
                        vm!!.isLoadingSharedMedia = false
                        runOnUiThread {
                            rvChatProfile!!.post {
                                hideSharedMediaLoading()
                                rvChatProfile!!.post {
                                    adapter!!.notifyItemRangeInserted(
                                        vm!!.menuItems.size + 2 + previousSize,
                                        entities.size
                                    )
                                }
                            }
                        }
                    }
                }.start()
            }
        }
    private val downloadProgressReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
            if (null == action || null == localID) {
                return
            }
            when (action) {
                DownloadBroadcastEvent.DownloadProgressLoading, DownloadBroadcastEvent.DownloadFinish, DownloadBroadcastEvent.DownloadFailed -> runOnUiThread {
                    notifyItemChanged(
                        vm!!.getSharedMedia(localID)
                    )
                }
            }
        }
    }

    companion object {
        private val TAG = TAPChatProfileActivity::class.java.simpleName
        fun start(
            context: Activity,
            instanceKey: String?,
            room: TAPRoomModel,
            user: TAPUserModel?,
            isNonParticipantUserProfile: Boolean
        ) {
            start(context, instanceKey, room, user, null, isNonParticipantUserProfile)
        }

        @JvmOverloads
        fun start(
            context: Activity,
            instanceKey: String?,
            room: TAPRoomModel,
            user: TAPUserModel?,
            isAdmin: Boolean? = null,
            isNonParticipantUserProfile: Boolean = false
        ) {
            if (null != user && TAPChatManager.getInstance(instanceKey).activeUser.userID == user.userID) {
                return
            }
            val intent = Intent(context, TAPChatProfileActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            intent.putExtra(Extras.ROOM, room)
            if (null != isAdmin) {
                intent.putExtra(TAPDefaultConstant.K_USER, user)
                intent.putExtra(Extras.IS_ADMIN, isAdmin)
                context.startActivityForResult(intent, RequestCode.GROUP_OPEN_MEMBER_PROFILE)
            } else if (room.type == RoomType.TYPE_PERSONAL) {
                if (isNonParticipantUserProfile) {
                    intent.putExtra(Extras.IS_NON_PARTICIPANT_USER_PROFILE, true)
                }
                context.startActivity(intent)
            } else if (room.type == RoomType.TYPE_GROUP && null != user) {
                intent.putExtra(TAPDefaultConstant.K_USER, user)
                context.startActivityForResult(intent, RequestCode.OPEN_MEMBER_PROFILE)
            } else if (room.type == RoomType.TYPE_GROUP) {
                context.startActivityForResult(intent, RequestCode.OPEN_GROUP_PROFILE)
            }
            context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
        }
    }
}