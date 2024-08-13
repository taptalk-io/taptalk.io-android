package io.taptalk.TapTalk.View.Adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.*
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Interface.TapLongPressInterface
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TapLongPressMenuItem
import io.taptalk.TapTalk.R

class TapLongPressAdapter(
    longPressMenuItems: List<TapLongPressMenuItem>,
    private var message: TAPMessageModel?,
    private var listener: TapLongPressInterface
) : TAPBaseAdapter<TapLongPressMenuItem?, TAPBaseViewHolder<TapLongPressMenuItem?>?>() {

    init {
        items = longPressMenuItems
    }

//    private var onClickListener: View.OnClickListener
//    private var messageToCopy: String? = ""
//    private var linkifyResult = ""
//    private var message: TAPMessageModel? = null
//    private var imagePosition = -1
//    private var bitmap: Bitmap? = null

//    constructor(
//        instanceKey: String,
//        items: List<TapLongPressMenuItem?>?,
//        bitmap: Bitmap?,
//        attachmentListener: TAPAttachmentListener,
//        onClickListener: View.OnClickListener
//    ) {
//        this.instanceKey = instanceKey
//        setItems(items)
//        this.bitmap = bitmap
//        this.listener = attachmentListener
//        this.onClickListener = onClickListener
//    }
//
//    constructor(
//        instanceKey: String,
//        imagePosition: Int,
//        items: List<TapLongPressMenuItem?>?,
//        attachmentListener: TAPAttachmentListener,
//        onClickListener: View.OnClickListener
//    ) {
//        this.instanceKey = instanceKey
//        setItems(items)
//        this.imagePosition = imagePosition
//        this.listener = attachmentListener
//        this.onClickListener = onClickListener
//    }
//
//    constructor(
//        instanceKey: String,
//        items: List<TapLongPressMenuItem?>?,
//        messageToCopy: String?,
//        linkifyResult: String,
//        attachmentListener: TAPAttachmentListener,
//        onClickListener: View.OnClickListener
//    ) {
//        this.instanceKey = instanceKey
//        setItems(items)
//        this.listener = attachmentListener
//        this.messageToCopy = messageToCopy
//        this.onClickListener = onClickListener
//        this.linkifyResult = linkifyResult
//    }
//
//    // Mention long press menu
//    constructor(
//        instanceKey: String,
//        items: List<TapLongPressMenuItem?>?,
//        message: TAPMessageModel?,
//        messageToCopy: String?,
//        linkifyResult: String,
//        attachmentListener: TAPAttachmentListener,
//        onClickListener: View.OnClickListener
//    ) {
//        this.instanceKey = instanceKey
//        setItems(items)
//        this.listener = attachmentListener
//        this.message = message
//        this.messageToCopy = messageToCopy
//        this.onClickListener = onClickListener
//        this.linkifyResult = linkifyResult
//    }
//
//    constructor(
//        instanceKey: String,
//        items: List<TapLongPressMenuItem?>?,
//        message: TAPMessageModel?,
//        attachmentListener: TAPAttachmentListener,
//        onClickListener: View.OnClickListener
//    ) {
//        this.instanceKey = instanceKey
//        setItems(items)
//        this.listener = attachmentListener
//        this.onClickListener = onClickListener
//        if (null != message) {
//            this.message = message
//            when (TapUI.getInstance(instanceKey).getLongPressMenuForMessageType(message.type)) {
//                LongPressMenuType.TYPE_IMAGE_MESSAGE, LongPressMenuType.TYPE_VIDEO_MESSAGE ->
//                    // TODO: 4 March 2019 TEMPORARY CLIPBOARD FOR IMAGE & VIDEO
//                    if (null != message.data) {
//                        messageToCopy = message.data!![MessageData.CAPTION] as String?
//                    }
//                LongPressMenuType.TYPE_LOCATION_MESSAGE -> if (null != message.data) {
//                    messageToCopy = message.data!![MessageData.ADDRESS] as String?
//                }
//                else -> messageToCopy = message.body
//            }
//        }
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TAPBaseViewHolder<TapLongPressMenuItem?> {
        return LongPressMenuViewHolder(parent, R.layout.tap_cell_attachment_menu)
    }

    inner class LongPressMenuViewHolder(parent: ViewGroup, itemLayoutId: Int): TAPBaseViewHolder<TapLongPressMenuItem?>(parent, itemLayoutId) {

        private val ivAttachIcon: ImageView = itemView.findViewById(R.id.iv_attach_icon)
        private val tvAttachTitle: TextView = itemView.findViewById(R.id.tv_attach_title)
        private val vAttachMenuSeparator: View = itemView.findViewById(R.id.v_attach_menu_separator)

        override fun onBind(item: TapLongPressMenuItem?, position: Int) {
            if (item == null) {
                return
            }
            ivAttachIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.iconRes))
            tvAttachTitle.text = item.text
            when (item.id) {
                REPLY -> {
                    setComponentColors(R.color.tapIconLongPressActionReply, R.color.tapActionSheetDefaultLabelColor)
                }
                FORWARD -> {
                    setComponentColors(R.color.tapIconLongPressActionForward, R.color.tapActionSheetDefaultLabelColor)
                }
                COPY -> {
                    setComponentColors(R.color.tapIconLongPressActionCopy, R.color.tapActionSheetDefaultLabelColor)
                }
                SAVE -> {
                    setComponentColors(R.color.tapIconLongPressActionSaveToGallery, R.color.tapActionSheetDefaultLabelColor)
                }
                STAR, UNSTAR -> {
                    setComponentColors(R.color.tapIconLongPressActionStarMessage, R.color.tapActionSheetDefaultLabelColor)
                }
                EDIT -> {
                    setComponentColors(R.color.tapIconLongPressActionEditMessage, R.color.tapActionSheetDefaultLabelColor)
                }
                PIN, UNPIN -> {
                    setComponentColors(R.color.tapIconLongPressActionPinMessage, R.color.tapActionSheetDefaultLabelColor)
                }
                INFO -> {
                    setComponentColors(R.color.tapIconLongPressMessageInfo, R.color.tapActionSheetDefaultLabelColor)
                }
                DELETE -> {
                    setComponentColors(R.color.tapIconLongPressActionDelete, R.color.tapActionSheetDestructiveLabelColor)
                }
                REPORT -> {
                    setComponentColors(R.color.tapIconLongPressReport, R.color.tapActionSheetDestructiveLabelColor)
                }
                OPEN_LINK -> {
                    setComponentColors(R.color.tapIconLongPressActionOpenLink, R.color.tapActionSheetDefaultLabelColor)
                }
                COMPOSE -> {
                    setComponentColors(R.color.tapIconLongPressActionComposeEmail, R.color.tapActionSheetDefaultLabelColor)
                }
                CALL -> {
                    setComponentColors(R.color.tapIconLongPressActionCallNumber, R.color.tapActionSheetDefaultLabelColor)
                }
                SMS -> {
                    setComponentColors(R.color.tapIconLongPressActionSmsNumber, R.color.tapActionSheetDefaultLabelColor)
                }
                VIEW_PROFILE -> {
                    setComponentColors(R.color.tapIconLongPressActionViewProfile, R.color.tapActionSheetDefaultLabelColor)
                }
                SEND_MESSAGE -> {
                    setComponentColors(R.color.tapIconLongPressActionSendMessage, R.color.tapActionSheetDefaultLabelColor)
                }
                RESCHEDULE -> {
                    setComponentColors(R.color.tapIconLongPressReschedule, R.color.tapActionSheetDefaultLabelColor)
                }
                else -> {
                    setComponentColors(R.color.tapColorPrimaryIcon, R.color.tapActionSheetDefaultLabelColor)
                }
            }
            if (itemCount - 1 == position) {
                vAttachMenuSeparator.visibility = View.GONE
            }
            else {
                vAttachMenuSeparator.visibility = View.VISIBLE
            }
            itemView.setOnClickListener { v: View? -> listener.onLongPressMenuItemSelected(item, message) }
        }

        @SuppressLint("PrivateResource")
        private fun setComponentColors(@ColorRes iconColorRes: Int, @ColorRes textColorRes: Int) {
            // Set icon color
            ImageViewCompat.setImageTintList(ivAttachIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, iconColorRes)))

            // Set text color
            tvAttachTitle.setTextColor(ContextCompat.getColor(itemView.context, textColorRes))
        }
    }
}
