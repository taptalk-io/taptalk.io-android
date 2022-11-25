package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.ResponseModel.TapGetMessageDetailResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter
import io.taptalk.TapTalk.View.Adapter.TapMessageInfoAdapter
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel
import io.taptalk.TapTalk.ViewModel.TapMessageInfoViewModel
import kotlinx.android.synthetic.main.tap_activity_message_info.*

class TapMessageInfoActivity : TAPBaseActivity() {

    private val vm : TapMessageInfoViewModel by lazy {
        ViewModelProvider(this)[TapMessageInfoViewModel::class.java]
    }

    companion object {
        fun start(
            context: Context,
            instanceKey: String?,
            message: TAPMessageModel,
            room: TAPRoomModel,
            isStarred: Boolean,
            isPinned: Boolean
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapMessageInfoActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.MESSAGE, message)
                intent.putExtra(TAPDefaultConstant.Extras.ROOM, room)
                intent.putExtra(TAPDefaultConstant.Extras.IS_STARRED, isStarred)
                intent.putExtra(TAPDefaultConstant.Extras.IS_PINNED, isPinned)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_message_info)
        if (intent.getParcelableExtra<TAPMessageModel>(TAPDefaultConstant.Extras.MESSAGE) != null) {
            vm.message = intent.getParcelableExtra(TAPDefaultConstant.Extras.MESSAGE)
        }
        if (intent.getParcelableExtra<TAPRoomModel>(TAPDefaultConstant.Extras.ROOM) != null) {
            vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        }
        vm.isStarred = intent.getBooleanExtra(TAPDefaultConstant.Extras.IS_STARRED, false)
        vm.isPinned = intent.getBooleanExtra(TAPDefaultConstant.Extras.IS_PINNED, false)
        val glide = Glide.with(this)
        val chatViewModel = TAPChatViewModel(application, instanceKey)
        chatViewModel.room = vm.room
        val messageAdapter = TAPMessageAdapter(instanceKey, glide, chatViewModel)
        messageAdapter.setMessages(listOf(vm.message))
        if (vm.isStarred) {
            messageAdapter.setStarredMessageIds(mutableListOf(vm.message?.messageID))
        }
        if (vm.isPinned) {
            messageAdapter.setPinnedMessageIds(mutableListOf(vm.message?.messageID))
        }
        rv_message_list.adapter = messageAdapter
        rv_message_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_message_list.setHasFixedSize(true)

        val resultList = ArrayList<TapMessageRecipientModel>()
        val adapter = TapMessageInfoAdapter(resultList)
        rv_message_info.adapter = adapter
        rv_message_info.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_message_info.setHasFixedSize(true)

    }

    override fun onResume() {
        super.onResume()
        TAPDataManager.getInstance(instanceKey).getMessageDetails(vm.message?.messageID, messageInfoView)
    }

    private val messageInfoView = object : TAPDefaultDataView<TapGetMessageDetailResponse>() {
        override fun startLoading() {
            super.startLoading()
            ll_loading.visibility = View.VISIBLE
        }

        override fun endLoading() {
            super.endLoading()
            ll_loading.visibility = View.GONE
        }

        override fun onSuccess(response: TapGetMessageDetailResponse?) {
            super.onSuccess(response)
            vm.readList.clear()
            val resultList = ArrayList<TapMessageRecipientModel>()
            if (response?.readBy != null) {
                vm.readList.addAll(ArrayList(response.readBy))
            }
            vm.deliveredList.clear()
            if (response?.deliveredTo != null) {
                vm.deliveredList.addAll(ArrayList(response.deliveredTo))
            }
            if (vm.readList.isNotEmpty() && !TapUI.getInstance(instanceKey).isReadStatusHidden) {
                resultList.add(TapMessageRecipientModel(vm.readList.size.toLong(), null))
                resultList.addAll(vm.readList)
                if (vm.deliveredList.isNotEmpty()) {
                    resultList.add(TapMessageRecipientModel(null, vm.deliveredList.size.toLong()))
                    resultList.addAll(vm.deliveredList)
                }
            } else {
                resultList.add(TapMessageRecipientModel(null, vm.deliveredList.size.toLong()))
                if (vm.deliveredList.isNotEmpty()) {
                    resultList.addAll(vm.deliveredList)
                }
            }
            (rv_message_info.adapter as TapMessageInfoAdapter).items = resultList
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            endLoading()
            Toast.makeText(this@TapMessageInfoActivity, error?.message, Toast.LENGTH_SHORT).show()
            finish()
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            endLoading()
            Toast.makeText(this@TapMessageInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}