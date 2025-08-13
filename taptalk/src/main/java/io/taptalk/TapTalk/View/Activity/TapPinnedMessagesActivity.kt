package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_LEFT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_RIGHT
import io.taptalk.TapTalk.Helper.*
import io.taptalk.TapTalk.Listener.TAPChatListener
import io.taptalk.TapTalk.Listener.TapCoreGetOlderMessageListener
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener
import io.taptalk.TapTalk.Manager.*
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel
import io.taptalk.TapTalk.databinding.TapActivityStarredMessagesBinding

class TapPinnedMessagesActivity : TAPBaseActivity() {
    private lateinit var vb : TapActivityStarredMessagesBinding
    private lateinit var vm : TAPChatViewModel
    private lateinit var glide : RequestManager
    private lateinit var messageAdapter: TAPMessageAdapter
    private lateinit var messageLayoutManager: LinearLayoutManager
    private lateinit var messageAnimator: SimpleItemAnimator
    private lateinit var endlessScrollListener: TAPEndlessScrollListener
    private var pageNumber = 1

    companion object {
        const val PAGE_SIZE = 50
        fun start(
            context: Context,
            instanceKey: String?,
            room: TAPRoomModel
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapPinnedMessagesActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.ROOM, room)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_PINNED_MESSAGES
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityStarredMessagesBinding.inflate(layoutInflater)
        setContentView(vb.root)
        glide = Glide.with(this)
        initRoom()
    }

    override fun onResume() {
        super.onResume()
        loadMessagesFromApi()
    }

    private fun initRoom() {
        if (initViewModel()) {
            initView()

        }
        else if (vm.messageModels.size == 0) {
            initView()
        }
    }

    private fun initViewModel(): Boolean {
        vm = ViewModelProvider(
            this,
            TAPChatViewModel.TAPChatViewModelFactory(
                application, instanceKey
            )
        )
            .get(TAPChatViewModel::class.java)
        if (null == vm.room) {
            vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        }
        if (null == vm.myUserModel) {
            vm.myUserModel = TAPChatManager.getInstance(instanceKey).activeUser
        }
        if (null == vm.room) {
            Toast.makeText(
                TapTalk.appContext,
                getString(R.string.tap_error_room_not_found),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return false
        }
        if (null == vm.otherUserModel && TAPDefaultConstant.RoomType.TYPE_PERSONAL == vm.room.type) {
            if (TAPUtils.isSavedMessagesRoom(vm.room.roomID, instanceKey)) {
                vm.otherUserModel = TAPChatManager.getInstance(instanceKey).activeUser
            }
            else {
                vm.otherUserModel =
                    TAPContactManager.getInstance(instanceKey).getUserData(vm.otherUserID)
            }
        }

        // Updated 2020/02/10
        if (TAPDefaultConstant.RoomType.TYPE_PERSONAL != vm.room.type &&
            TAPGroupManager.getInstance(instanceKey).checkIsRoomDataAvailable(vm.room.roomID)
        ) {
            val room = TAPGroupManager.getInstance(instanceKey).getGroupData(vm.room.roomID)
            if (null != room && null != room.name && room.name.isNotEmpty()) {
                vm.room = room
            }
        }
        if (null != intent.getStringExtra(TAPDefaultConstant.Extras.JUMP_TO_MESSAGE) && !vm.isInitialAPICallFinished) {
            vm.tappedMessageLocalID = intent.getStringExtra(TAPDefaultConstant.Extras.JUMP_TO_MESSAGE)
        }
        return null != vm.myUserModel && (null != vm.otherUserModel || TAPDefaultConstant.RoomType.TYPE_PERSONAL != vm.room.type)
    }

    private fun initView() {
        window.setBackgroundDrawable(null)
        vb.tvTitle.text = getString(R.string.tap_pinned_messages)
        vb.clUnpinAll.isVisible = true
        // Initialize chat message RecyclerView
        messageAdapter =
            TAPMessageAdapter(instanceKey, glide, chatListener, vm, TAPMessageAdapter.RoomType.PINNED)
        messageAdapter.setMessages(vm.messageModels)
        messageLayoutManager = object : LinearLayoutManager(this, VERTICAL, true) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        vb.rvStarredMessages.instanceKey = instanceKey
        vb.rvStarredMessages.adapter = messageAdapter
        vb.rvStarredMessages.layoutManager = messageLayoutManager
        vb.rvStarredMessages.setHasFixedSize(false)
        vb.rvStarredMessages.recycledViewPool.setMaxRecycledViews(TYPE_BUBBLE_IMAGE_LEFT, 0)
        vb.rvStarredMessages.recycledViewPool.setMaxRecycledViews(TYPE_BUBBLE_IMAGE_RIGHT, 0)
        vb.rvStarredMessages.recycledViewPool.setMaxRecycledViews(TYPE_BUBBLE_VIDEO_LEFT, 0)
        vb.rvStarredMessages.recycledViewPool.setMaxRecycledViews(TYPE_BUBBLE_VIDEO_RIGHT, 0)
        vb.rvStarredMessages.recycledViewPool.setMaxRecycledViews(TYPE_BUBBLE_PRODUCT_LIST, 0)
        messageAnimator = vb.rvStarredMessages.itemAnimator as SimpleItemAnimator
        messageAnimator.supportsChangeAnimations = false
        vb.rvStarredMessages.itemAnimator = null

        // Listener for scroll pagination
        endlessScrollListener = object : TAPEndlessScrollListener(messageLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadMessagesFromApi()
            }
        }
        vb.ivButtonBack.setOnClickListener { onBackPressed() }
        vb.clUnpinAll.setOnClickListener{
            showLoading()
            TapCoreMessageManager.getInstance(instanceKey).getPinnedMessageIDs(vm.room.roomID, object : TapCoreGetStringArrayListener() {
                override fun onSuccess(arrayList: ArrayList<String>) {
                    super.onSuccess(arrayList)
                    TapCoreMessageManager.getInstance(instanceKey).unpinMessage(vm.room.roomID, arrayList, object : TapCoreGetStringArrayListener() {
                        override fun onSuccess(arrayList: ArrayList<String>) {
                            super.onSuccess(arrayList)
                            hideLoading()
                            intent.putExtra(TAPDefaultConstant.Extras.IS_NEED_REFRESH, true)
                            setResult(RESULT_OK, intent)
                            finish()
                        }

                        override fun onError(errorCode: String?, errorMessage: String?) {
                            super.onError(errorCode, errorMessage)
                            hideLoading()
                            showErrorDialog(errorMessage)
                        }
                    })
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    super.onError(errorCode, errorMessage)
                    hideLoading()
                    showErrorDialog(errorMessage)
                }
            })
        }

        updateMessageDecoration()
        checkMessageList()
    }

    private fun loadMessagesFromApi() {
        if (vm.isHasMoreData) {
            showLoadingOlderMessagesIndicator()
            TapCoreMessageManager.getInstance(instanceKey).getPinnedMessages(
                vm.room.roomID,
                pageNumber,
                PAGE_SIZE,
                object : TapCoreGetOlderMessageListener() {
                    override fun onSuccess(
                        messages: MutableList<TAPMessageModel>?,
                        hasMoreData: Boolean?
                    ) {
                        super.onSuccess(messages, hasMoreData)
                        if (hasMoreData != null)
                            vm.isHasMoreData = hasMoreData
                        hideLoadingOlderMessagesIndicator()
                        pageNumber++
                        if (!messages.isNullOrEmpty()) {
                            vm.messageModels.addAll(messages)
                        }
                        updateMessageDecoration()
                    }

                    override fun onError(errorCode: String?, errorMessage: String?) {
                        super.onError(errorCode, errorMessage)
                        hideLoadingOlderMessagesIndicator()
                        Toast.makeText(this@TapPinnedMessagesActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun showLoadingOlderMessagesIndicator() {
        hideLoadingOlderMessagesIndicator()
        vb.rvStarredMessages.post {
            runOnUiThread {
                vm.addMessagePointer(vm.getLoadingIndicator(true))
                messageAdapter.addItem(vm.getLoadingIndicator(false)) // Add loading indicator to last index
                messageAdapter.notifyItemInserted(messageAdapter.itemCount - 1)
            }
        }
    }

    private fun hideLoadingOlderMessagesIndicator() {
        vb.rvStarredMessages.post {
            runOnUiThread {
                if (!messageAdapter.items.contains(vm.getLoadingIndicator(false))) {
                    return@runOnUiThread
                }
                val index = messageAdapter.items.indexOf(vm.getLoadingIndicator(false))
                vm.removeMessagePointer(TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID)
                if (index >= 0) {
                    messageAdapter.removeMessage(vm.getLoadingIndicator(false))
                    if (null != messageAdapter.getItemAt(index)) {
                        messageAdapter.notifyItemChanged(index)
                    }
                    else {
                        messageAdapter.notifyItemRemoved(index)
                    }
                    updateMessageDecoration()
                }
                checkMessageList()
            }
        }
    }

    private fun showLoading() {
        runOnUiThread {
            vb.layoutPopupLoadingScreen.ivLoadingImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white))
            if (null == vb.layoutPopupLoadingScreen.ivLoadingImage.animation) {
                TAPUtils.rotateAnimateInfinitely(this, vb.layoutPopupLoadingScreen.ivLoadingImage)
            }
            vb.layoutPopupLoadingScreen.tvLoadingText.text = getString(R.string.tap_loading)
            vb.layoutPopupLoadingScreen.flLoading.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        vb.layoutPopupLoadingScreen.flLoading.visibility = View.GONE
    }

    private fun showErrorDialog(message: String?) {
        TapTalkDialog.Builder(this)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setTitle(getString(R.string.tap_error))
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setPrimaryButtonListener(true) { }
            .setCancelable(true)
            .show()
    }

    private fun updateMessageDecoration() {
        // Update decoration for the top item in recycler view
        runOnUiThread {
            while (vb.rvStarredMessages.itemDecorationCount > 0) {
                vb.rvStarredMessages.removeItemDecorationAt(0)
            }
            vb.rvStarredMessages.addItemDecoration(
                TAPVerticalDecoration(
                    TAPUtils.dpToPx(16),
                    0,
                    messageAdapter.itemCount - 1
                )
            )
        }
    }

    private fun checkMessageList() {
        val pinnedMessageIds : MutableList<String> = mutableListOf()
        for (item in vm.messageModels) {
            pinnedMessageIds.add(item.localID)
        }
        messageAdapter.setPinnedMessageIds(pinnedMessageIds)
    }

    private val chatListener = object : TAPChatListener() {
        override fun onOutsideClicked(message: TAPMessageModel?) {
            super.onOutsideClicked(message)
            TAPChatManager.getInstance(instanceKey).triggerPinnedMessageTapped(message)
            val intent = Intent()
            intent.putExtra(TAPDefaultConstant.Extras.MESSAGE, message)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
