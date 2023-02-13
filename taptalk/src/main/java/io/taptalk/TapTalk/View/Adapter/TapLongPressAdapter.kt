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
    private var message: TAPMessageModel,
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
                REPLY -> setComponentColors(
                    R.color.tapIconLongPressActionReply,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                FORWARD -> setComponentColors(
                    R.color.tapIconLongPressActionForward,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                COPY -> setComponentColors(
                    R.color.tapIconLongPressActionCopy,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                SAVE -> setComponentColors(
                    R.color.tapIconLongPressActionSaveToGallery,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                STAR, UNSTAR -> setComponentColors(
                    R.color.tapIconLongPressActionStarMessage,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                EDIT -> setComponentColors(
                    R.color.tapIconLongPressActionEditMessage,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                PIN, UNPIN -> setComponentColors(
                    R.color.tapIconLongPressActionPinMessage,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                INFO -> setComponentColors(
                    R.color.tapIconLongPressMessageInfo,
                    R.style.tapActionSheetDefaultLabelStyle
                )
                DELETE -> setComponentColors(
                    R.color.tapIconLongPressActionDelete,
                    R.style.tapActionSheetDestructiveLabelStyle
                )
                REPORT -> setComponentColors(
                    R.color.tapIconLongPressReport,
                    R.style.tapActionSheetDestructiveLabelStyle
                )
            }
            if (itemCount - 1 == position) {
                vAttachMenuSeparator.visibility = View.GONE
            }
            else {
                vAttachMenuSeparator.visibility = View.VISIBLE
            }
            itemView.setOnClickListener { v: View? -> listener.onMessageLongPressMenuItemSelected(item, message) }
        }

        @SuppressLint("PrivateResource")
        private fun setComponentColors(@ColorRes iconColorRes: Int, @StyleRes textStyleRes: Int) {
            // Set icon color
            ImageViewCompat.setImageTintList(
                ivAttachIcon,
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, iconColorRes))
            )

            // Set text color
            val typedArray = itemView.context.obtainStyledAttributes(textStyleRes, R.styleable.TextAppearance)
            tvAttachTitle.setTextColor(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1))
            typedArray.recycle()
        }
    }
}
