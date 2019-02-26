package io.taptalk.TapTalk.View.BottomSheet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Model.TAPAttachmentModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Adapter.TAPAttachmentAdapter
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_fragment_long_press_action_bottom_sheet.*

class TAPLongPressActionBottomSheet : BottomSheetDialogFragment {

    enum class LongPressType {
        CHAT_BUBBLE_TYPE,
        LINK_TYPE,
        EMAIL_TYPE,
        PHONE_TYPE
    }

    var longPressType: LongPressType = LongPressType.CHAT_BUBBLE_TYPE

    constructor() : super()

    constructor(longPressType: LongPressType, urlMessage: String) {
        this.longPressType = longPressType
        this.urlMessage = urlMessage

    }

    constructor(longPressType: LongPressType, message: TAPMessageModel) {
        this.longPressType = longPressType
        this.message = message
    }

    var message: TAPMessageModel? = null
    var urlMessage = ""
    val onClickListener = View.OnClickListener { dismiss() }

    companion object {
        fun newInstance(longPressType: LongPressType, url: String): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(longPressType, url)
            val args: Bundle = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(longPressType: LongPressType, message: TAPMessageModel): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(longPressType, message)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    val bottomSheetListener = object : TAPAttachmentListener() {
        override fun onCopySelected(text: String) {
            val clipboard = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(text, text)
            clipboard.primaryClip = clip
        }

        override fun onReplySelected() {
            super.onReplySelected()
        }

        override fun onForwardSelected() {
            super.onForwardSelected()
        }

        override fun onOpenLinkSelected() {
            super.onOpenLinkSelected()
        }

        override fun onComposeSelected() {
            super.onComposeSelected()
        }

        override fun onPhoneCallSelected() {
            super.onPhoneCallSelected()
        }

        override fun onPhoneSmsSelected() {
            super.onPhoneSmsSelected()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tap_fragment_long_press_action_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createChatBubbleLongPressMenu(), message,
                bottomSheetListener, onClickListener)
        when (longPressType) {
            LongPressType.EMAIL_TYPE -> {
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createEmailLongPressMenu(), urlMessage,
                        bottomSheetListener, onClickListener)
            }
            LongPressType.LINK_TYPE -> {
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createLinkLongPressMenu(), urlMessage,
                        bottomSheetListener, onClickListener)
            }
            LongPressType.PHONE_TYPE -> {
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createPhoneLongPressMenu(), urlMessage,
                        bottomSheetListener, onClickListener)
            }
        }
        rv_long_press.adapter = longPressAdapter
        rv_long_press.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_long_press.setHasFixedSize(true)
    }
}
