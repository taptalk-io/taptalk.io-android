package io.taptalk.TapTalk.View.BottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.*
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Model.TAPAttachmentModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Adapter.TAPAttachmentAdapter
import io.taptalk.TapTalk.R
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

    constructor(longPressType: LongPressType, urlMessage: String, linkifyResult: String, bottomSheetListener: TAPAttachmentListener) {
        this.longPressType = longPressType
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
            val args: Bundle = Bundle()
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

        var longPressAdapter = TAPAttachmentAdapter(listOf(), message,
                bottomSheetListener, onClickListener)
        when (longPressType) {
            LongPressType.CHAT_BUBBLE_TYPE -> {
                if (message?.failedSend == true) {
//                    longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createFailedMessageBubbleLongPressMenu(),
//                            message, bottomSheetListener, onClickListener)
                    dismiss()
                } else if (message?.isDeleted == true) {
                    dismiss()
                } else if (!message?.sending!!) {
                    when (message?.type) {
                        TYPE_IMAGE -> {
                            longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createImageBubbleLongPressMenu(message),
                                    message, bottomSheetListener, onClickListener)
                        }
                        TYPE_VIDEO -> {
                            longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createVideoBubbleLongPressMenu(message),
                                    message, bottomSheetListener, onClickListener)
                        }
                        TYPE_FILE -> {
                            longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createFileBubbleLongPressMenu(message),
                                    message, bottomSheetListener, onClickListener)
                        }
                        TYPE_LOCATION -> {
                            longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createLocationBubbleLongPressMenu(message),
                                    message, bottomSheetListener, onClickListener)
                        }
                        TYPE_TEXT -> {
                            longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createTextBubbleLongPressMenu(message),
                                    message, bottomSheetListener, onClickListener)
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
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createEmailLongPressMenu(),
                        urlMessage, linkifyResult, bottomSheetListener, onClickListener)
            }
            LongPressType.LINK_TYPE -> {
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createLinkLongPressMenu(),
                        urlMessage, linkifyResult, bottomSheetListener, onClickListener)
            }
            LongPressType.PHONE_TYPE -> {
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createPhoneLongPressMenu(),
                        urlMessage, linkifyResult, bottomSheetListener, onClickListener)
            }
        }
        rv_long_press.adapter = longPressAdapter
        rv_long_press.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        rv_long_press.setHasFixedSize(true)
    }
}
