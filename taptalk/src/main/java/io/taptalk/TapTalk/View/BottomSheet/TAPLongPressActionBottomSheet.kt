package io.taptalk.TapTalk.View.BottomSheet

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.DATA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.*
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Interface.TapLongPressInterface
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TapLongPressMenuItem
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapLongPressAdapter
import kotlinx.android.synthetic.main.tap_fragment_long_press_action_bottom_sheet.*

class TAPLongPressActionBottomSheet : BottomSheetDialogFragment {

    enum class LongPressType {
        CHAT_BUBBLE_TYPE,
        LINK_TYPE,
        EMAIL_TYPE,
        PHONE_TYPE,
        MENTION_TYPE,
        IMAGE_TYPE,
        SHARED_MEDIA_TYPE,
        SCHEDULED_TYPE
    }

    private var instanceKey = ""
    private var longPressType: LongPressType = LongPressType.CHAT_BUBBLE_TYPE
    private var message: TAPMessageModel? = null
    private var urlMessage = ""
    private var linkifyResult = ""
    private var bitmap: Bitmap? = null
    private val onClickListener = View.OnClickListener { dismiss() }
    private var bottomSheetListener: TAPAttachmentListener? = null
    private var starredMessageIds: ArrayList<String> = arrayListOf()
    private var pinnedMessageIds: ArrayList<String> = arrayListOf()

    constructor() : super()

    constructor(instanceKey: String, longPressType: LongPressType, urlMessage: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.urlMessage = urlMessage
        this.bottomSheetListener = bottomSheetListener
        this.linkifyResult = linkifyResult
    }

    constructor(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, urlMessage: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.message = message
        this.urlMessage = urlMessage
        this.bottomSheetListener = bottomSheetListener
        this.linkifyResult = linkifyResult
    }

    constructor(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener, starredMessageIds: ArrayList<String>, pinnedMessageIds: ArrayList<String>) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.message = message
        this.bottomSheetListener = bottomSheetListener
        this.starredMessageIds = starredMessageIds
        this.pinnedMessageIds = pinnedMessageIds
    }

    constructor(instanceKey: String, longPressType: LongPressType, bitmap: Bitmap, bottomSheetListener: TAPAttachmentListener) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.bottomSheetListener = bottomSheetListener
        this.bitmap = bitmap
    }

    constructor(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.message = message
        this.bottomSheetListener = bottomSheetListener
    }

    companion object {
        // Chat Bubble
        fun newInstance(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener, starredMessageIds: ArrayList<String>, pinnedMessageIds: ArrayList<String>): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(instanceKey, longPressType, message, bottomSheetListener, starredMessageIds, pinnedMessageIds)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        // Span (email, phone, url)
        fun newInstance(instanceKey: String, longPressType: LongPressType, url: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(instanceKey, longPressType, url, linkifyResult, bottomSheetListener)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        // Mention
        fun newInstance(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, url: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(instanceKey, longPressType, message, url, linkifyResult, bottomSheetListener)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        // Save Profile Picture
        fun newInstance(instanceKey: String, longPressType: LongPressType, bitmap: Bitmap, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(instanceKey, longPressType, bitmap, bottomSheetListener)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        // Shared Media
        fun newInstance(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(instanceKey, longPressType, message, bottomSheetListener)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        private const val TWO_DAYS_IN_MILLIS : Long = 172800000
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tap_fragment_long_press_action_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ENABLE LINK LONG PRESS
//        if (null == message && longPressType != LongPressType.IMAGE_TYPE && longPressType != LongPressType.LINK_TYPE) {
//            dismiss()
//            return
//        }

//        var longPressAdapter = TAPAttachmentAdapter(instanceKey, listOf(), message, bottomSheetListener, onClickListener)
//        var longPressAdapter = TapLongPressAdapter(listOf(), message!!, longPressListener)
        var longPressAdapter: TapLongPressAdapter? = null

        when (longPressType) {
            LongPressType.CHAT_BUBBLE_TYPE -> {
                if (message == null) {
                    dismiss()
                    return
                }
                if (message!!.isFailedSend == true) {
//                    longPressAdapter = TAPAttachmentAdapter(createFailedMessageBubbleLongPressMenu(), message!!, bottomSheetListener, onClickListener)
                    dismiss()
                    return
                } else if (message!!.isDeleted == true) {
                    dismiss()
                    return
                } else if (!message!!.isSending!!) {
                    val menus = TAPChatManager.getInstance(instanceKey).getMessageLongPressMenuItems(context, message)
                    if (menus.isNullOrEmpty()) {
                        dismiss()
                        return
                    }
                    longPressAdapter = TapLongPressAdapter(menus, message!!, longPressListener)

//                    when (TapUI.getInstance(instanceKey).getLongPressMenuForMessageType(message!!.type)) {
//                        TYPE_TEXT_MESSAGE -> {
//                            val menus = createTextBubbleLongPressMenu(message!!)
//                            if (menus.isEmpty()) {
//                                dismiss()
//                            } else {
//                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
//                            }
//                        }
//                        TYPE_IMAGE_MESSAGE -> {
//                            val menus = createImageBubbleLongPressMenu(message!!)
//                            if (menus.isEmpty()) {
//                                dismiss()
//                            } else {
//                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
//                            }
//                        }
//                        TYPE_VIDEO_MESSAGE -> {
//                            val menus = createVideoBubbleLongPressMenu(message!!)
//                            if (menus.isEmpty()) {
//                                dismiss()
//                            } else {
//                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
//                            }
//                        }
//                        TYPE_FILE_MESSAGE -> {
//                            val menus = createFileBubbleLongPressMenu(message!!)
//                            if (menus.isEmpty()) {
//                                dismiss()
//                            } else {
//                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
//                            }
//                        }
//                        TYPE_VOICE_MESSAGE -> {
//                            val menus = createVoiceBubbleLongPressMenu(message!!)
//                            if (menus.isEmpty()) {
//                                dismiss()
//                            } else {
//                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
//                            }
//                        }
//                        TYPE_LOCATION_MESSAGE -> {
//                            val menus = createLocationBubbleLongPressMenu(message!!)
//                            if (menus.isEmpty()) {
//                                dismiss()
//                            } else {
//                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
//                            }
//                        }
//                        else -> {
//                            dismiss()
//                        }
//                    }
                } else {
                    dismiss()
                    return
                }
            }
            LongPressType.EMAIL_TYPE -> {
                val menus = createEmailLongPressMenu()
                Log.e(">>>>>>>>>>>>>>>>", "onViewCreated EMAIL_TYPE: ${TAPUtils.toJsonString(menus)}")
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.LINK_TYPE -> {
                val menus = createLinkLongPressMenu()
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
//                    Log.e(">>>>>>", "onViewCreated LINK_TYPE menus: ${TAPUtils.toJsonString(menus)})")
//                    Log.e(">>>>>>", "onViewCreated LINK_TYPE message: ${TAPUtils.toJsonString(message)}")
//                    Log.e(">>>>>>", "onViewCreated LINK_TYPE longPressListener: $longPressListener")
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.PHONE_TYPE -> {
                val menus = createPhoneLongPressMenu()
                Log.e(">>>>>>>>>>>>>>>>", "onViewCreated PHONE_TYPE: ${TAPUtils.toJsonString(menus)}")
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.MENTION_TYPE -> {
                val menus = if (linkifyResult.substring(1) == TAPChatManager.getInstance(instanceKey).activeUser.username) {
                    createCopyLongPressMenu()
                } else {
                    createMentionLongPressMenu()
                }
                if (menus.isNullOrEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.IMAGE_TYPE -> {
                val menus = createSaveImageLongPressMenu()
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.SHARED_MEDIA_TYPE -> {
                val menus = createSharedMediaLongPressMenu()
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.SCHEDULED_TYPE -> {
                val menus = createScheduledMessageLongPressMenu()
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
        }

        if (longPressAdapter == null) {
            dismiss()
            return
        }
        rv_long_press.adapter = longPressAdapter
        rv_long_press.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_long_press.setHasFixedSize(true)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private val longPressListener = object : TapLongPressInterface {
        override fun onLongPressMenuItemSelected(longPressMenuItem: TapLongPressMenuItem?, messageModel: TAPMessageModel?) {
            dismiss()
            if (longPressMenuItem == null || activity == null) {
                return
            }
            TAPChatManager.getInstance(instanceKey).triggerLongPressMenuItemSelected(activity, longPressMenuItem, message)
        }
    }

//    private fun createFailedMessageBubbleLongPressMenu(): List<TAPAttachmentModel?> {
//        // TODO: 10 April 2019 ADD LONG PRESS MENU FOR FAILED MESSAGES
//        return ArrayList()
//    }
//
//    private fun createTextBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
//        val imageResIds: MutableList<Int> = ArrayList()
//        val titleResIds: MutableList<Int> = ArrayList()
//        val ids: MutableList<Int> = ArrayList()
//
//        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled &&
//            !TAPDataManager.getInstance(instanceKey).blockedUserIds.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(messageModel.room?.roomID))
//        ) {
//            // Reply
//            imageResIds.add(R.drawable.tap_ic_reply_orange)
//            titleResIds.add(R.string.tap_reply)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
//                messageModel.room.type != RoomType.TYPE_TRANSACTION
//        ) {
//            // Forward
//            imageResIds.add(R.drawable.tap_ic_forward_orange)
//            titleResIds.add(R.string.tap_forward)
//            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
//            // Copy
//            imageResIds.add(R.drawable.tap_ic_copy_orange)
//            titleResIds.add(R.string.tap_copy)
//            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
//        }
//
//        if (TapUI.getInstance(instanceKey).isStarMessageMenuEnabled) {
//            // Star
//            if (starredMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_star_filled_primary)
//                titleResIds.add(R.string.tap_unstar)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_star_outline)
//                titleResIds.add(R.string.tap_star)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isEditMessageMenuEnabled &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!! &&
//            messageModel.forwardFrom?.localID.isNullOrEmpty() &&
//            System.currentTimeMillis() - messageModel.created < TWO_DAYS_IN_MILLIS
//        ) {
//            // Edit
//            imageResIds.add(R.drawable.tap_ic_edit_orange)
//            titleResIds.add(R.string.tap_edit)
//            ids.add(TAPAttachmentModel.LONG_PRESS_EDIT)
//        }
//
//        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled) {
//            // Pin
//            if (pinnedMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_unpin_message_orange)
//                titleResIds.add(R.string.tap_unpin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_pin_message_orange)
//                titleResIds.add(R.string.tap_pin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled &&
//            messageModel.room.type != TYPE_PERSONAL &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            imageResIds.add(R.drawable.tap_ic_info_outline_primary)
//            titleResIds.add(R.string.tap_message_info)
//            ids.add(TAPAttachmentModel.LONG_PRESS_MESSAGE_INFO)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
//                null != TAPChatManager.getInstance(instanceKey).activeUser &&
//                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//                null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            // Delete
//            imageResIds.add(R.drawable.tap_ic_delete_red)
//            titleResIds.add(R.string.tap_delete_message)
//            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
//        }
//
//        if (TapUI.getInstance(instanceKey).isReportMessageMenuEnabled &&
//            TAPChatManager.getInstance(instanceKey).activeUser.userID != messageModel.user.userID
//        ) {
//            // Report
//            imageResIds.add(R.drawable.tap_ic_warning_triangle_red)
//            titleResIds.add(R.string.tap_report)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPORT)
//        }
//
//        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
//        val size = imageResIds.size
//        for (index in 0 until size) {
//            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
//        }
//        return attachMenus
//    }
//
//    private fun createImageBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
//        val imageResIds: MutableList<Int> = ArrayList()
//        val titleResIds: MutableList<Int> = ArrayList()
//        val ids: MutableList<Int> = ArrayList()
//        val messageData = messageModel.data
//
//        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled &&
//            !TAPDataManager.getInstance(instanceKey).blockedUserIds.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(messageModel.room?.roomID))
//        ) {
//            // Reply
//            imageResIds.add(R.drawable.tap_ic_reply_orange)
//            titleResIds.add(R.string.tap_reply)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
//            messageModel.room.type != RoomType.TYPE_TRANSACTION
//        ) {
//            // Forward
//            imageResIds.add(R.drawable.tap_ic_forward_orange)
//            titleResIds.add(R.string.tap_forward)
//            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
//        }
//
//        if (null != messageData) {
//            val caption = messageData[MessageData.CAPTION] as String?
//            if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled &&
//                    !caption.isNullOrEmpty()
//            ) {
//                // Copy
//                imageResIds.add(R.drawable.tap_ic_copy_orange)
//                titleResIds.add(R.string.tap_copy)
//                ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
//            }
//
//            val fileID = messageData[MessageData.FILE_ID] as String?
//            if (!TapUI.getInstance(instanceKey).isSaveMediaToGalleryMenuDisabled &&
//                    ((!fileID.isNullOrEmpty() && TAPCacheManager.getInstance(TapTalk.appContext).containsCache(fileID)) ||
//                    null != messageData[MessageData.FILE_URL])
//            ) {
//                // Save image to gallery
//                imageResIds.add(R.drawable.tap_ic_download_orange)
//                titleResIds.add(R.string.tap_save_to_gallery)
//                ids.add(TAPAttachmentModel.LONG_PRESS_SAVE_IMAGE_GALLERY)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isStarMessageMenuEnabled) {
//            // Star
//            if (starredMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_star_filled_primary)
//                titleResIds.add(R.string.tap_unstar)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_star_outline)
//                titleResIds.add(R.string.tap_star)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isEditMessageMenuEnabled &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!! &&
//            messageModel.forwardFrom?.localID.isNullOrEmpty() &&
//            System.currentTimeMillis() - messageModel.created < TWO_DAYS_IN_MILLIS
//        ) {
//            // Edit
//            imageResIds.add(R.drawable.tap_ic_edit_orange)
//            titleResIds.add(R.string.tap_edit)
//            ids.add(TAPAttachmentModel.LONG_PRESS_EDIT)
//        }
//
//        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled) {
//            // Pin
//            if (pinnedMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_unpin_message_orange)
//                titleResIds.add(R.string.tap_unpin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_pin_message_orange)
//                titleResIds.add(R.string.tap_pin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled &&
//            messageModel.room.type != TYPE_PERSONAL &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            imageResIds.add(R.drawable.tap_ic_info_outline_primary)
//            titleResIds.add(R.string.tap_message_info)
//            ids.add(TAPAttachmentModel.LONG_PRESS_MESSAGE_INFO)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
//                null != TAPChatManager.getInstance(instanceKey).activeUser &&
//                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//                null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            // Delete
//            imageResIds.add(R.drawable.tap_ic_delete_red)
//            titleResIds.add(R.string.tap_delete_message)
//            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
//        }
//
//        if (TapUI.getInstance(instanceKey).isReportMessageMenuEnabled &&
//            TAPChatManager.getInstance(instanceKey).activeUser.userID != messageModel.user.userID
//        ) {
//            // Report
//            imageResIds.add(R.drawable.tap_ic_warning_triangle_red)
//            titleResIds.add(R.string.tap_report)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPORT)
//        }
//
//        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
//        val size = imageResIds.size
//        for (index in 0 until size) {
//            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
//        }
//        return attachMenus
//    }
//
//    private fun createVideoBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
//        val imageResIds: MutableList<Int> = ArrayList()
//        val titleResIds: MutableList<Int> = ArrayList()
//        val ids: MutableList<Int> = ArrayList()
//        val messageData = messageModel.data
//
//        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled &&
//            !TAPDataManager.getInstance(instanceKey).blockedUserIds.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(messageModel.room?.roomID))
//        ) {
//            // Reply
//            imageResIds.add(R.drawable.tap_ic_reply_orange)
//            titleResIds.add(R.string.tap_reply)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
//            messageModel.room.type != RoomType.TYPE_TRANSACTION
//        ) {
//            // Forward
//            imageResIds.add(R.drawable.tap_ic_forward_orange)
//            titleResIds.add(R.string.tap_forward)
//            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
//        }
//
//        if (null != messageData) {
//            val caption = messageData[MessageData.CAPTION] as String?
//            if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled &&
//                    !caption.isNullOrEmpty()
//            ) {
//                // Copy
//                imageResIds.add(R.drawable.tap_ic_copy_orange)
//                titleResIds.add(R.string.tap_copy)
//                ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
//            }
//
//            if (!TapUI.getInstance(instanceKey).isSaveMediaToGalleryMenuDisabled &&
//                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)) {
//                // Save video to gallery
//                imageResIds.add(R.drawable.tap_ic_download_orange)
//                titleResIds.add(R.string.tap_save_to_gallery)
//                ids.add(TAPAttachmentModel.LONG_PRESS_SAVE_VIDEO_GALLERY)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isStarMessageMenuEnabled) {
//            // Star
//            if (starredMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_star_filled_primary)
//                titleResIds.add(R.string.tap_unstar)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_star_outline)
//                titleResIds.add(R.string.tap_star)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isEditMessageMenuEnabled &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!! &&
//            messageModel.forwardFrom?.localID.isNullOrEmpty() &&
//            System.currentTimeMillis() - messageModel.created < TWO_DAYS_IN_MILLIS
//        ) {
//            // Edit
//            imageResIds.add(R.drawable.tap_ic_edit_orange)
//            titleResIds.add(R.string.tap_edit)
//            ids.add(TAPAttachmentModel.LONG_PRESS_EDIT)
//        }
//
//        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled) {
//            // Pin
//            if (pinnedMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_unpin_message_orange)
//                titleResIds.add(R.string.tap_unpin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_pin_message_orange)
//                titleResIds.add(R.string.tap_pin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled &&
//            messageModel.room.type != TYPE_PERSONAL &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            imageResIds.add(R.drawable.tap_ic_info_outline_primary)
//            titleResIds.add(R.string.tap_message_info)
//            ids.add(TAPAttachmentModel.LONG_PRESS_MESSAGE_INFO)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
//                null != TAPChatManager.getInstance(instanceKey).activeUser &&
//                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//                null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            // Delete
//            imageResIds.add(R.drawable.tap_ic_delete_red)
//            titleResIds.add(R.string.tap_delete_message)
//            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
//        }
//
//        if (TapUI.getInstance(instanceKey).isReportMessageMenuEnabled &&
//            TAPChatManager.getInstance(instanceKey).activeUser.userID != messageModel.user.userID
//        ) {
//            // Report
//            imageResIds.add(R.drawable.tap_ic_warning_triangle_red)
//            titleResIds.add(R.string.tap_report)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPORT)
//        }
//
//        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
//        val size = imageResIds.size
//        for (index in 0 until size) {
//            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
//        }
//        return attachMenus
//    }
//
//    private fun createFileBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
//        val imageResIds: MutableList<Int> = ArrayList()
//        val titleResIds: MutableList<Int> = ArrayList()
//        val ids: MutableList<Int> = ArrayList()
//        val messageData = messageModel.data
//
//        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled &&
//            !TAPDataManager.getInstance(instanceKey).blockedUserIds.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(messageModel.room?.roomID))
//        ) {
//            // Reply
//            imageResIds.add(R.drawable.tap_ic_reply_orange)
//            titleResIds.add(R.string.tap_reply)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
//            messageModel.room.type != RoomType.TYPE_TRANSACTION
//        ) {
//            // Forward
//            imageResIds.add(R.drawable.tap_ic_forward_orange)
//            titleResIds.add(R.string.tap_forward)
//            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
//        }
//
//        if (null != messageData) {
//            if (!TapUI.getInstance(instanceKey).isSaveDocumentMenuDisabled &&
//                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)
//            ) {
//                // Save to downloads
//                imageResIds.add(R.drawable.tap_ic_download_orange)
//                titleResIds.add(R.string.tap_save_to_downloads)
//                ids.add(TAPAttachmentModel.LONG_PRESS_SAVE_DOWNLOADS)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isStarMessageMenuEnabled) {
//            // Star
//            if (starredMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_star_filled_primary)
//                titleResIds.add(R.string.tap_unstar)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_star_outline)
//                titleResIds.add(R.string.tap_star)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled) {
//            // Pin
//            if (pinnedMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_unpin_message_orange)
//                titleResIds.add(R.string.tap_unpin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_pin_message_orange)
//                titleResIds.add(R.string.tap_pin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled &&
//            messageModel.room.type != TYPE_PERSONAL &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            imageResIds.add(R.drawable.tap_ic_info_outline_primary)
//            titleResIds.add(R.string.tap_message_info)
//            ids.add(TAPAttachmentModel.LONG_PRESS_MESSAGE_INFO)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
//                null != TAPChatManager.getInstance(instanceKey).activeUser &&
//                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//                null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            // Delete
//            imageResIds.add(R.drawable.tap_ic_delete_red)
//            titleResIds.add(R.string.tap_delete_message)
//            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
//        }
//
//        if (TapUI.getInstance(instanceKey).isReportMessageMenuEnabled &&
//            TAPChatManager.getInstance(instanceKey).activeUser.userID != messageModel.user.userID
//        ) {
//            // Report
//            imageResIds.add(R.drawable.tap_ic_warning_triangle_red)
//            titleResIds.add(R.string.tap_report)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPORT)
//        }
//
//        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
//        val size = imageResIds.size
//        for (index in 0 until size) {
//            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
//        }
//        return attachMenus
//    }
//
//    private fun createVoiceBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
//        val imageResIds: MutableList<Int> = ArrayList()
//        val titleResIds: MutableList<Int> = ArrayList()
//        val ids: MutableList<Int> = ArrayList()
//
//        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled &&
//            !TAPDataManager.getInstance(instanceKey).blockedUserIds.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(messageModel.room?.roomID))
//        ) {
//            // Reply
//            imageResIds.add(R.drawable.tap_ic_reply_orange)
//            titleResIds.add(R.string.tap_reply)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
//            messageModel.room.type != RoomType.TYPE_TRANSACTION
//        ) {
//            // Forward
//            imageResIds.add(R.drawable.tap_ic_forward_orange)
//            titleResIds.add(R.string.tap_forward)
//            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
//        }
//
//        if (TapUI.getInstance(instanceKey).isStarMessageMenuEnabled) {
//            // Star
//            if (starredMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_star_filled_primary)
//                titleResIds.add(R.string.tap_unstar)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_star_outline)
//                titleResIds.add(R.string.tap_star)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled) {
//            // Pin
//            if (pinnedMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_unpin_message_orange)
//                titleResIds.add(R.string.tap_unpin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_pin_message_orange)
//                titleResIds.add(R.string.tap_pin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled &&
//            messageModel.room.type != TYPE_PERSONAL &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            imageResIds.add(R.drawable.tap_ic_info_outline_primary)
//            titleResIds.add(R.string.tap_message_info)
//            ids.add(TAPAttachmentModel.LONG_PRESS_MESSAGE_INFO)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            // Delete
//            imageResIds.add(R.drawable.tap_ic_delete_red)
//            titleResIds.add(R.string.tap_delete_message)
//            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
//        }
//
//        if (TapUI.getInstance(instanceKey).isReportMessageMenuEnabled &&
//            TAPChatManager.getInstance(instanceKey).activeUser.userID != messageModel.user.userID
//        ) {
//            // Report
//            imageResIds.add(R.drawable.tap_ic_warning_triangle_red)
//            titleResIds.add(R.string.tap_report)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPORT)
//        }
//
//        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
//        val size = imageResIds.size
//        for (index in 0 until size) {
//            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
//        }
//        return attachMenus
//    }
//
//
//    private fun createLocationBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
//        val imageResIds: MutableList<Int> = ArrayList()
//        val titleResIds: MutableList<Int> = ArrayList()
//        val ids: MutableList<Int> = ArrayList()
//
//        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled &&
//            !TAPDataManager.getInstance(instanceKey).blockedUserIds.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(messageModel.room?.roomID))
//        ) {
//            // Reply
//            imageResIds.add(R.drawable.tap_ic_reply_orange)
//            titleResIds.add(R.string.tap_reply)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
//                messageModel.room.type != RoomType.TYPE_TRANSACTION
//        ) {
//            // Forward
//            imageResIds.add(R.drawable.tap_ic_forward_orange)
//            titleResIds.add(R.string.tap_forward)
//            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
//            // Copy
//            imageResIds.add(R.drawable.tap_ic_copy_orange)
//            titleResIds.add(R.string.tap_copy)
//            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
//        }
//
//        if (TapUI.getInstance(instanceKey).isStarMessageMenuEnabled) {
//            // Star
//            if (starredMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_star_filled_primary)
//                titleResIds.add(R.string.tap_unstar)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_star_outline)
//                titleResIds.add(R.string.tap_star)
//                ids.add(TAPAttachmentModel.LONG_PRESS_STAR)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled) {
//            // Pin
//            if (pinnedMessageIds.contains(messageModel.messageID)) {
//                imageResIds.add(R.drawable.tap_ic_unpin_message_orange)
//                titleResIds.add(R.string.tap_unpin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            } else {
//                imageResIds.add(R.drawable.tap_ic_pin_message_orange)
//                titleResIds.add(R.string.tap_pin)
//                ids.add(TAPAttachmentModel.LONG_PRESS_PIN)
//            }
//        }
//
//        if (TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled &&
//            messageModel.room.type != TYPE_PERSONAL &&
//            null != TAPChatManager.getInstance(instanceKey).activeUser &&
//            messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//            null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            imageResIds.add(R.drawable.tap_ic_info_outline_primary)
//            titleResIds.add(R.string.tap_message_info)
//            ids.add(TAPAttachmentModel.LONG_PRESS_MESSAGE_INFO)
//        }
//
//        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
//                null != TAPChatManager.getInstance(instanceKey).activeUser &&
//                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
//                null != messageModel.isSending && !messageModel.isSending!!
//        ) {
//            // Delete
//            imageResIds.add(R.drawable.tap_ic_delete_red)
//            titleResIds.add(R.string.tap_delete_message)
//            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
//        }
//
//        if (TapUI.getInstance(instanceKey).isReportMessageMenuEnabled &&
//            TAPChatManager.getInstance(instanceKey).activeUser.userID != messageModel.user.userID
//        ) {
//            // Report
//            imageResIds.add(R.drawable.tap_ic_warning_triangle_red)
//            titleResIds.add(R.string.tap_report)
//            ids.add(TAPAttachmentModel.LONG_PRESS_REPORT)
//        }
//
//        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
//        val size = imageResIds.size
//        for (index in 0 until size) {
//            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
//        }
//        return attachMenus
//    }

    private fun createLinkLongPressMenu(): List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()
        val info = HashMap<String, Any>()
        info[DATA] = linkifyResult

        if (!TapUI.getInstance(instanceKey).isOpenLinkMenuDisabled) {
            // Open link
            longPressMenus.add(TapLongPressMenuItem(
                OPEN_LINK,
                getString(R.string.tap_open),
                R.drawable.tap_ic_open_link_orange,
                info
            ))
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            longPressMenus.add(TapLongPressMenuItem(
                COPY,
                getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ))
        }

        return longPressMenus
    }

    private fun createEmailLongPressMenu(): List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()

        if (!TapUI.getInstance(instanceKey).isComposeEmailMenuDisabled) {
            // Compose email
            val info = HashMap<String, Any>()
            info[DATA] = linkifyResult
            longPressMenus.add(TapLongPressMenuItem(
                COMPOSE,
                getString(R.string.tap_compose),
                R.drawable.tap_ic_mail_orange,
                info
            ))
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            val address = linkifyResult.replace("mailto:", "")
            val info = HashMap<String, Any>()
            info[DATA] = address
            longPressMenus.add(TapLongPressMenuItem(
                COPY,
                getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ))
        }

        return longPressMenus
    }

    private fun createPhoneLongPressMenu(): List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()
        val info = HashMap<String, Any>()
        info[DATA] = urlMessage

        if (!TapUI.getInstance(instanceKey).isDialNumberMenuDisabled) {
            // Dial number
            longPressMenus.add(TapLongPressMenuItem(
                CALL,
                getString(R.string.tap_call),
                R.drawable.tap_ic_call_orange,
                info
            ))
        }

        if (!TapUI.getInstance(instanceKey).isSendSMSMenuDisabled) {
            // Send SMS
            longPressMenus.add(TapLongPressMenuItem(
                SMS,
                getString(R.string.tap_send_sms),
                R.drawable.tap_ic_sms_orange,
                info
            ))
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            longPressMenus.add(TapLongPressMenuItem(
                COPY,
                getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ))
        }

        return longPressMenus
    }

    private fun createMentionLongPressMenu(): List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()
        val info = HashMap<String, Any>()
        info[DATA] = linkifyResult.substring(1)

        if (!TapUI.getInstance(instanceKey).isViewProfileMenuDisabled) {
            // View profile
            longPressMenus.add(TapLongPressMenuItem(
                VIEW_PROFILE,
                getString(R.string.tap_view_profile),
                R.drawable.tap_ic_contact_orange,
                info
            ))
        }

        if (!TapUI.getInstance(instanceKey).isSendMessageMenuDisabled) {
            // Send message
            longPressMenus.add(TapLongPressMenuItem(
                SEND_MESSAGE,
                getString(R.string.tap_send_message),
                R.drawable.tap_ic_sms_orange,
                info
            ))
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            longPressMenus.add(TapLongPressMenuItem(
                COPY,
                getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ))
        }

        return longPressMenus
    }

    private fun createCopyLongPressMenu(): List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()
        val info = HashMap<String, Any>()
        info[DATA] = linkifyResult.substring(1)
        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            longPressMenus.add(TapLongPressMenuItem(
                COPY,
                getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ))
        }
        return longPressMenus
    }

    private fun createSaveImageLongPressMenu(): List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()

        // Save Profile Picture
        if (bitmap != null) {
            val info = HashMap<String, Any>()
            info[DATA] = bitmap!!
            longPressMenus.add(TapLongPressMenuItem(
                SAVE,
                getString(R.string.tap_save_image),
                R.drawable.tap_ic_download_orange
            ))
        }
        return longPressMenus
    }

    private fun createSharedMediaLongPressMenu(): List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()
        // View in Chat
        longPressMenus.add(TapLongPressMenuItem(
            VIEW_IN_CHAT,
            getString(R.string.tap_view_in_chat),
            R.drawable.tap_ic_view_in_chat
        ))
        return longPressMenus
    }

    private fun createScheduledMessageLongPressMenu() : List<TapLongPressMenuItem> {
        val longPressMenus: MutableList<TapLongPressMenuItem> = ArrayList()
        // Send Now
        longPressMenus.add(TapLongPressMenuItem(
            SEND_NOW,
            getString(R.string.tap_send_now),
            R.drawable.tap_ic_circle_arrow_up_primary
        ))
        // Reschedule
        longPressMenus.add(TapLongPressMenuItem(
            RESCHEDULE,
            getString(R.string.tap_reschedule),
            R.drawable.tap_ic_clock_grey
        ))
        // Copy
        longPressMenus.add(TapLongPressMenuItem(
            COPY,
            getString(R.string.tap_copy),
            R.drawable.tap_ic_copy_orange
        ))
        // Edit
        longPressMenus.add(TapLongPressMenuItem(
            EDIT,
            getString(R.string.tap_edit),
            R.drawable.tap_ic_edit_orange
        ))
        // Delete
        longPressMenus.add(TapLongPressMenuItem(
            DELETE,
            getString(R.string.tap_delete),
            R.drawable.tap_ic_delete_red
        ))
        return longPressMenus
    }
}
