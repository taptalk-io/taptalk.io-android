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
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity
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

    var longPressType: LongPressType = LongPressType.CHAT_BUBBLE_TYPE

    constructor() : super()

    constructor(longPressType: LongPressType, urlMessage: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener) {
        this.longPressType = longPressType
        this.urlMessage = urlMessage
        this.bottomSheetListener = bottomSheetListener
        this.linkifyResult = linkifyResult
    }

    constructor(longPressType: LongPressType, message: TAPMessageModel, urlMessage: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener) {
        this.longPressType = longPressType
        this.message = message
        this.urlMessage = urlMessage
        this.bottomSheetListener = bottomSheetListener
        this.linkifyResult = linkifyResult
    }

    constructor(longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener) {
        this.longPressType = longPressType
        this.message = message
        this.bottomSheetListener = bottomSheetListener
    }

    var message: TAPMessageModel? = null
    var urlMessage = ""
    var linkifyResult = ""
    val onClickListener = View.OnClickListener { dismiss() }
    var bottomSheetListener: TAPAttachmentListener? = null

    companion object {
        fun newInstance(longPressType: LongPressType, url: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(longPressType, url, linkifyResult, bottomSheetListener)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(longPressType: LongPressType, message: TAPMessageModel, url: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(longPressType, message, url, linkifyResult, bottomSheetListener)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TAPAttachmentListener): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(longPressType, message, bottomSheetListener)
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

        val instanceKey = try {
            val activity = context as TapUIChatActivity
            activity.instanceKey
        } catch (e: Exception) {
            dismiss()
            return
        }

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
                            val menus = createTextBubbleLongPressMenu(instanceKey, message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_IMAGE_MESSAGE -> {
                            val menus = createImageBubbleLongPressMenu(instanceKey, message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_VIDEO_MESSAGE -> {
                            val menus = createVideoBubbleLongPressMenu(instanceKey, message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_FILE_MESSAGE -> {
                            val menus = createFileBubbleLongPressMenu(instanceKey, message!!)
                            if (menus.isEmpty()) {
                                dismiss()
                            } else {
                                longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, message, bottomSheetListener, onClickListener)
                            }
                        }
                        TYPE_LOCATION_MESSAGE -> {
                            val menus = createLocationBubbleLongPressMenu(instanceKey, message!!)
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
                val menus = createEmailLongPressMenu(instanceKey)
                if (menus.isEmpty()) {
                    dismiss()
                } else {
                    longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, urlMessage, linkifyResult, bottomSheetListener, onClickListener)
                }
            }
            LongPressType.LINK_TYPE -> {
                val menus = createLinkLongPressMenu(instanceKey)
                if (menus.isEmpty()) {
                    dismiss()
                } else {
                    longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, urlMessage, linkifyResult, bottomSheetListener, onClickListener)
                }
            }
            LongPressType.PHONE_TYPE -> {
                val menus = createPhoneLongPressMenu(instanceKey)
                if (menus.isEmpty()) {
                    dismiss()
                } else {
                    longPressAdapter = TAPAttachmentAdapter(instanceKey, menus, urlMessage, linkifyResult, bottomSheetListener, onClickListener)
                }
            }
            LongPressType.MENTION_TYPE -> {
                val menus = if (linkifyResult.substring(1) == TAPChatManager.getInstance(instanceKey).activeUser.username) {
                    createCopyLongPressMenu(instanceKey)
                } else {
                    createMentionLongPressMenu(instanceKey)
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

    private fun createTextBubbleLongPressMenu(instanceKey: String?, messageModel: TAPMessageModel): List<TAPAttachmentModel> {
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
    private fun createImageBubbleLongPressMenu(instanceKey: String?, messageModel: TAPMessageModel): List<TAPAttachmentModel> {
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
                // Save to gallery
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
    private fun createVideoBubbleLongPressMenu(instanceKey: String?, messageModel: TAPMessageModel): List<TAPAttachmentModel> {
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
                // Save to gallery
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
    private fun createFileBubbleLongPressMenu(instanceKey: String?, messageModel: TAPMessageModel): List<TAPAttachmentModel> {
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

    private fun createLocationBubbleLongPressMenu(instanceKey: String?, messageModel: TAPMessageModel): List<TAPAttachmentModel> {
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

    private fun createLinkLongPressMenu(instanceKey: String?): List<TAPAttachmentModel> {
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

    private fun createEmailLongPressMenu(instanceKey: String?): List<TAPAttachmentModel> {
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

    private fun createPhoneLongPressMenu(instanceKey: String?): List<TAPAttachmentModel> {
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

    private fun createMentionLongPressMenu(instanceKey: String?): List<TAPAttachmentModel>? {
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

    private fun createCopyLongPressMenu(instanceKey: String?): List<TAPAttachmentModel> {
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
