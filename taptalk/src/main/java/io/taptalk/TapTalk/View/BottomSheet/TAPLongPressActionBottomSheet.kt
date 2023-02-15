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
    private var bottomSheetListener: TapLongPressInterface? = null
    private var starredMessageIds: ArrayList<String> = arrayListOf()
    private var pinnedMessageIds: ArrayList<String> = arrayListOf()

    constructor() : super()

    constructor(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel?, urlMessage: String, linkifyResult: String, bottomSheetListener: TapLongPressInterface) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.message = message
        this.urlMessage = urlMessage
        this.bottomSheetListener = bottomSheetListener
        this.linkifyResult = linkifyResult
    }

    constructor(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TapLongPressInterface, starredMessageIds: ArrayList<String>, pinnedMessageIds: ArrayList<String>) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.message = message
        this.bottomSheetListener = bottomSheetListener
        this.starredMessageIds = starredMessageIds
        this.pinnedMessageIds = pinnedMessageIds
    }

    constructor(instanceKey: String, longPressType: LongPressType, bitmap: Bitmap, bottomSheetListener: TapLongPressInterface) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.bottomSheetListener = bottomSheetListener
        this.bitmap = bitmap
    }

    constructor(instanceKey: String, longPressType: LongPressType, message: TAPMessageModel, bottomSheetListener: TapLongPressInterface) {
        this.instanceKey = instanceKey
        this.longPressType = longPressType
        this.message = message
        this.bottomSheetListener = bottomSheetListener
    }

    companion object {

        var listeners: ArrayList<TapLongPressInterface> = ArrayList()

        // Chat Bubble
        fun newInstance(
            instanceKey: String,
            longPressType: LongPressType,
            message: TAPMessageModel,
            bottomSheetListener: TapLongPressInterface,
            starredMessageIds: ArrayList<String>,
            pinnedMessageIds: ArrayList<String>
        ): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(instanceKey,
                longPressType,
                message,
                bottomSheetListener,
                starredMessageIds,
                pinnedMessageIds)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        // Span (email, phone, url), mention
        fun newInstance(
            instanceKey: String,
            longPressType: LongPressType,
            message: TAPMessageModel?,
            url: String,
            linkifyResult: String,
            bottomSheetListener: TapLongPressInterface
        ): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(
                instanceKey,
                longPressType,
                message,
                url,
                linkifyResult,
                bottomSheetListener
            )
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        // Save Profile Picture
        fun newInstance(
            instanceKey: String,
            longPressType: LongPressType,
            bitmap: Bitmap,
            bottomSheetListener: TapLongPressInterface
        ): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(
                instanceKey,
                longPressType,
                bitmap,
                bottomSheetListener
            )
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        // Shared Media
        fun newInstance(
            instanceKey: String,
            longPressType: LongPressType,
            message: TAPMessageModel,
            bottomSheetListener: TapLongPressInterface
        ): TAPLongPressActionBottomSheet {
            val fragment = TAPLongPressActionBottomSheet(
                instanceKey,
                longPressType,
                message,
                bottomSheetListener)
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

        bottomSheetListener?.let { listeners.add(it) }

        var longPressAdapter: TapLongPressAdapter? = null

        when (longPressType) {
            LongPressType.CHAT_BUBBLE_TYPE -> {
                if (message == null) {
                    dismiss()
                    return
                }
                val menus = TAPChatManager.getInstance(instanceKey).getMessageLongPressMenuItems(context, message)
                if (menus.isNullOrEmpty()) {
                    dismiss()
                    return
                }
                longPressAdapter = TapLongPressAdapter(menus, message!!, longPressListener)
            }
            LongPressType.EMAIL_TYPE -> {
                val menus = TAPChatManager.getInstance(instanceKey).getEmailLongPressMenuItems(context, message, linkifyResult.replace("mailto:", ""))
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.LINK_TYPE -> {
                val menus = TAPChatManager.getInstance(instanceKey).getLinkLongPressMenuItems(context, message, linkifyResult)
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.PHONE_TYPE -> {
                val menus = TAPChatManager.getInstance(instanceKey).getPhoneLongPressMenuItems(context, message, urlMessage)
                Log.e(">>>>>>>>>>>>>>>>", "onViewCreated PHONE_TYPE: ${TAPUtils.toJsonString(menus)}")
                if (menus.isEmpty()) {
                    dismiss()
                    return
                } else {
                    longPressAdapter = TapLongPressAdapter(menus, message, longPressListener)
                }
            }
            LongPressType.MENTION_TYPE -> {
                val menus = TAPChatManager.getInstance(instanceKey).getMentionLongPressMenuItems(context, message, linkifyResult.substring(1))
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
                val menus = TAPChatManager.getInstance(instanceKey).getScheduledMessageLongPressMenuItems(context, message)
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

    override fun onDestroyView() {
        super.onDestroyView()

        bottomSheetListener?.let { listeners.remove(it) }
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
}
