package io.taptalk.TapTalk.View.BottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Manager.TAPCacheManager
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Manager.TapUI.LongPressMenuType.*
import io.taptalk.TapTalk.Model.TAPAttachmentModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPAttachmentAdapter
import kotlinx.android.synthetic.main.tap_fragment_long_press_action_bottom_sheet.*
import java.util.*

class TAPLongPressActionBottomSheet : BottomSheetDialogFragment {

    enum class LongPressType {
        CHAT_BUBBLE_TYPE,
        LINK_TYPE,
        EMAIL_TYPE,
        PHONE_TYPE,
        MENTION_TYPE
    }

    private var instanceKey = ""
    private var longPressType: LongPressType = LongPressType.CHAT_BUBBLE_TYPE
    private var message: TAPMessageModel? = null
    private var urlMessage = ""
    private var linkifyResult = ""
    private val onClickListener = View.OnClickListener { dismiss() }
    private var bottomSheetListener: TAPAttachmentListener? = null

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

    constructor(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.message = message
        this.bottomSheetListener = bottomSheetListener
    }
    
    companion object {
        // Chat Bubble
        fun newInstance(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(instanceKey, longPressType, message, bottomSheetListener)
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tap_fragment_long_press_action_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (null == message) {
            dismiss()
            return
        }

        var longPressAdapter = TAPAttachmentAdapter(instanceKey, listOf(), message, bottomSheetListener, onClickListener)

        when (longPressType) {
            LongPressType.CHAT_BUBBLE_TYPE -> {
                if (message!!.failedSend == true) {
//                    longPressAdapter = TAPAttachmentAdapter(createFailedMessageBubbleLongPressMenu(), message!!, bottomSheetListener, onClickListener)
                    dismiss()
                } else if (message!!.isDeleted == true) {
                    dismiss()
                } else if (!message!!.sending!!) {
                    when (TapUI.getInstance(instanceKey).getLongPressMenuForMessageType(message!!.type)) {
                        TYPE_TEXT_MESSAGE -> {
                            val menus = createTextBubbleLongPressMenu(message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_IMAGE_MESSAGE -> {
                            val menus = createImageBubbleLongPressMenu(message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_VIDEO_MESSAGE -> {
                            val menus = createVideoBubbleLongPressMenu(message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_FILE_MESSAGE -> {
                            val menus = createFileBubbleLongPressMenu(message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_LOCATION_MESSAGE -> {
                            val menus = createLocationBubbleLongPressMenu(message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        else -> {
                            dismiss()
                        }
                    }
                } else {
                    dismiss()
                }
            }
            LongPressType.EMAIL_TYPE -> {
                val menus = createEmailLongPressMenu()
                if (menus.isEmpty()) {
                    dismiss()
                } else {
                    longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, urlMessage, linkifyResult, bottomSheetListener, onClickListener)
                }
            }
            LongPressType.LINK_TYPE -> {
                val menus = createLinkLongPressMenu()
                if (menus.isEmpty()) {
                    dismiss()
                } else {
                    longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, urlMessage, linkifyResult, bottomSheetListener, onClickListener)
                }
            }
            LongPressType.PHONE_TYPE -> {
                val menus = createPhoneLongPressMenu()
                if (menus.isEmpty()) {
                    dismiss()
                } else {
                    longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, urlMessage, linkifyResult, bottomSheetListener, onClickListener)
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
                } else {
                    longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, urlMessage, linkifyResult, bottomSheetListener, onClickListener)
                }
            }
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

    private fun createFailedMessageBubbleLongPressMenu(): List<TAPAttachmentModel?> {
        // TODO: 10 April 2019 ADD LONG PRESS MENU FOR FAILED MESSAGES
        return ArrayList()
    }

    private fun createTextBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange)
            titleResIds.add(R.string.tap_reply)
            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
        }

        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
                messageModel.room.roomType != RoomType.TYPE_TRANSACTION
        ) {
            // Forward
            imageResIds.add(R.drawable.tap_ic_forward_orange)
            titleResIds.add(R.string.tap_forward)
            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange)
            titleResIds.add(R.string.tap_copy)
            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
                null != TAPChatManager.getInstance(instanceKey).activeUser &&
                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
                null != messageModel.sending && !messageModel.sending!!
        ) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red)
            titleResIds.add(R.string.tap_delete)
            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    // TODO: 4 March 2019 TEMPORARILY DISABLED FORWARD
    private fun createImageBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()
        val messageData = messageModel.data

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange)
            titleResIds.add(R.string.tap_reply)
            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
        }

        if (null != messageData) {
            val caption = messageData[MessageData.CAPTION] as String?
            if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled &&
                    !caption.isNullOrEmpty()
            ) {
                // Copy
                imageResIds.add(R.drawable.tap_ic_copy_orange)
                titleResIds.add(R.string.tap_copy)
                ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
            }

            val fileID = messageData[MessageData.FILE_ID] as String?
            if (!TapUI.getInstance(instanceKey).isSaveMediaToGalleryMenuDisabled &&
                    ((!fileID.isNullOrEmpty() && TAPCacheManager.getInstance(TapTalk.appContext).containsCache(fileID)) ||
                    null != messageData[MessageData.FILE_URL])
            ) {
                // Save image to gallery
                imageResIds.add(R.drawable.tap_ic_download_orange)
                titleResIds.add(R.string.tap_save_to_gallery)
                ids.add(TAPAttachmentModel.LONG_PRESS_SAVE_IMAGE_GALLERY)
            }
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
                null != TAPChatManager.getInstance(instanceKey).activeUser &&
                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
                null != messageModel.sending && !messageModel.sending!!
        ) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red)
            titleResIds.add(R.string.tap_delete)
            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    // TODO: 4 March 2019 TEMPORARILY DISABLED FORWARD
    private fun createVideoBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()
        val messageData = messageModel.data

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange)
            titleResIds.add(R.string.tap_reply)
            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
        }

        if (null != messageData) {
            val caption = messageData[MessageData.CAPTION] as String?
            if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled &&
                    !caption.isNullOrEmpty()
            ) {
                // Copy
                imageResIds.add(R.drawable.tap_ic_copy_orange)
                titleResIds.add(R.string.tap_copy)
                ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
            }

            if (!TapUI.getInstance(instanceKey).isSaveMediaToGalleryMenuDisabled &&
                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)) {
                // Save video to gallery
                imageResIds.add(R.drawable.tap_ic_download_orange)
                titleResIds.add(R.string.tap_save_to_gallery)
                ids.add(TAPAttachmentModel.LONG_PRESS_SAVE_VIDEO_GALLERY)
            }
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
                null != TAPChatManager.getInstance(instanceKey).activeUser &&
                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
                null != messageModel.sending && !messageModel.sending!!
        ) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red)
            titleResIds.add(R.string.tap_delete)
            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    // TODO: 4 March 2019 TEMPORARILY DISABLED FORWARD
    private fun createFileBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()
        val messageData = messageModel.data

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange)
            titleResIds.add(R.string.tap_reply)
            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
        }

        if (null != messageData) {
            if (!TapUI.getInstance(instanceKey).isSaveDocumentMenuDisabled &&
                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)
            ) {
                // Save to downloads
                imageResIds.add(R.drawable.tap_ic_download_orange)
                titleResIds.add(R.string.tap_save_to_downloads)
                ids.add(TAPAttachmentModel.LONG_PRESS_SAVE_DOWNLOADS)
            }
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
                null != TAPChatManager.getInstance(instanceKey).activeUser &&
                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
                null != messageModel.sending && !messageModel.sending!!
        ) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red)
            titleResIds.add(R.string.tap_delete)
            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    private fun createLocationBubbleLongPressMenu(messageModel: TAPMessageModel): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange)
            titleResIds.add(R.string.tap_reply)
            ids.add(TAPAttachmentModel.LONG_PRESS_REPLY)
        }

        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled &&
                messageModel.room.roomType != RoomType.TYPE_TRANSACTION
        ) {
            // Forward
            imageResIds.add(R.drawable.tap_ic_forward_orange)
            titleResIds.add(R.string.tap_forward)
            ids.add(TAPAttachmentModel.LONG_PRESS_FORWARD)
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange)
            titleResIds.add(R.string.tap_copy)
            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled &&
                null != TAPChatManager.getInstance(instanceKey).activeUser &&
                messageModel.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID &&
                null != messageModel.sending && !messageModel.sending!!
        ) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red)
            titleResIds.add(R.string.tap_delete)
            ids.add(TAPAttachmentModel.LONG_PRESS_DELETE)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    private fun createLinkLongPressMenu(): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()

        if (!TapUI.getInstance(instanceKey).isOpenLinkMenuDisabled) {
            // Open link
            imageResIds.add(R.drawable.tap_ic_open_link_orange)
            titleResIds.add(R.string.tap_open)
            ids.add(TAPAttachmentModel.LONG_PRESS_OPEN_LINK)
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange)
            titleResIds.add(R.string.tap_copy)
            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    private fun createEmailLongPressMenu(): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()

        if (!TapUI.getInstance(instanceKey).isComposeEmailMenuDisabled) {
            // Compose email
            imageResIds.add(R.drawable.tap_ic_mail_orange)
            titleResIds.add(R.string.tap_compose)
            ids.add(TAPAttachmentModel.LONG_PRESS_COMPOSE_EMAIL)
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange)
            titleResIds.add(R.string.tap_copy)
            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    private fun createPhoneLongPressMenu(): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()

        if (!TapUI.getInstance(instanceKey).isDialNumberMenuDisabled) {
            // Dial number
            imageResIds.add(R.drawable.tap_ic_call_orange)
            titleResIds.add(R.string.tap_call)
            ids.add(TAPAttachmentModel.LONG_PRESS_CALL)
        }

        if (!TapUI.getInstance(instanceKey).isSendSMSMenuDisabled) {
            // Send SMS
            imageResIds.add(R.drawable.tap_ic_sms_orange)
            titleResIds.add(R.string.tap_send_sms)
            ids.add(TAPAttachmentModel.LONG_PRESS_SEND_SMS)
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange)
            titleResIds.add(R.string.tap_copy)
            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    private fun createMentionLongPressMenu(): List<TAPAttachmentModel> {
        val imageResIds: MutableList<Int> = ArrayList()
        val titleResIds: MutableList<Int> = ArrayList()
        val ids: MutableList<Int> = ArrayList()

        if (!TapUI.getInstance(instanceKey).isViewProfileMenuDisabled) {
            // View profile
            imageResIds.add(R.drawable.tap_ic_contact_orange)
            titleResIds.add(R.string.tap_view_profile)
            ids.add(TAPAttachmentModel.LONG_PRESS_VIEW_PROFILE)
        }

        if (!TapUI.getInstance(instanceKey).isSendMessageMenuDisabled) {
            // Send message
            imageResIds.add(R.drawable.tap_ic_sms_orange)
            titleResIds.add(R.string.tap_send_message)
            ids.add(TAPAttachmentModel.LONG_PRESS_SEND_MESSAGE)
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange)
            titleResIds.add(R.string.tap_copy)
            ids.add(TAPAttachmentModel.LONG_PRESS_COPY)
        }

        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        val size = imageResIds.size
        for (index in 0 until size) {
            attachMenus.add(TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]))
        }
        return attachMenus
    }

    private fun createCopyLongPressMenu(): List<TAPAttachmentModel> {
        val attachMenus: MutableList<TAPAttachmentModel> = ArrayList()
        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            attachMenus.add(TAPAttachmentModel(
                    R.drawable.tap_ic_copy_orange,
                    R.string.tap_copy,
                    TAPAttachmentModel.LONG_PRESS_COPY))
        }
        return attachMenus
    }
}
