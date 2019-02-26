package io.taptalk.TapTalk.View.BottomSheet

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.taptalk.TapTalk.Model.TAPAttachmentModel
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

    constructor(longPressType : LongPressType) {
        this.longPressType = longPressType
    }

    companion object {
        fun newInstance(longPressType: LongPressType): TAPLongPressActionBottomSheet {
            val fragment: TAPLongPressActionBottomSheet = TAPLongPressActionBottomSheet(longPressType)
            val args: Bundle = Bundle()
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
        var longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createChatBubbleLongPressMenu())
        when(longPressType) {
            LongPressType.EMAIL_TYPE ->{
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createEmailLongPressMenu())
            }
            LongPressType.LINK_TYPE ->{
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createLinkLongPressMenu())
            }
            LongPressType.PHONE_TYPE ->{
                longPressAdapter = TAPAttachmentAdapter(TAPAttachmentModel.createPhoneLongPressMenu())
            }
        }
        rv_long_press.adapter = longPressAdapter
        rv_long_press.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_long_press.setHasFixedSize(true)
    }
}
