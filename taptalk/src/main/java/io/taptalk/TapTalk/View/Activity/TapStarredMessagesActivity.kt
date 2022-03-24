package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant.*
import io.taptalk.TapTalk.Helper.TAPEndlessScrollListener
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TAPChatListener
import io.taptalk.TapTalk.Listener.TapCoreGetOlderMessageListener
import io.taptalk.TapTalk.Manager.*
import io.taptalk.TapTalk.Manager.TAPGroupManager.Companion.getInstance
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel.TAPChatViewModelFactory
import kotlinx.android.synthetic.main.tap_activity_starred_messages.*

class TapStarredMessagesActivity : TAPBaseActivity() {
    private lateinit var vm : TAPChatViewModel
    private lateinit var glide : RequestManager
    private lateinit var messageAdapter: TAPMessageAdapter
    private lateinit var messageLayoutManager: LinearLayoutManager
    private lateinit var messageAnimator: SimpleItemAnimator
    private lateinit var endlessScrollListener: TAPEndlessScrollListener
    private var pageNumber = 1

    companion object {
        const val PAGE_SIZE = 50
    }

    fun start(
        context: Activity,
        instanceKey: String?,
        room: TAPRoomModel
    ) {
        val intent = Intent(context, TapStarredMessagesActivity::class.java)
        intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
        intent.putExtra(Extras.ROOM, room)
        context.startActivityForResult(intent, RequestCode.OPEN_STARRED_MESSAGES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_starred_messages)
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

        } else if (vm.messageModels.size == 0) {
            initView()
        }
    }

    private fun initViewModel(): Boolean {
        vm = ViewModelProvider(
            this,
            TAPChatViewModelFactory(
                application, instanceKey
            )
        )
            .get(TAPChatViewModel::class.java)
        if (null == vm.room) {
            vm.room = intent.getParcelableExtra(Extras.ROOM)
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
        if (null == vm.otherUserModel && RoomType.TYPE_PERSONAL == vm.room.type) {
            vm.otherUserModel =
                TAPContactManager.getInstance(instanceKey).getUserData(vm.otherUserID)
        }

        // Updated 2020/02/10
        if (RoomType.TYPE_PERSONAL != vm.room.type &&
            getInstance(instanceKey).checkIsRoomDataAvailable(vm.room.roomID)
        ) {
            val room = getInstance(instanceKey).getGroupData(vm.room.roomID)
            if (null != room && null != room.name && room.name.isNotEmpty()) {
                vm.room = room
            }
        }
        if (null != intent.getStringExtra(Extras.JUMP_TO_MESSAGE) && !vm.isInitialAPICallFinished) {
            vm.tappedMessageLocalID = intent.getStringExtra(Extras.JUMP_TO_MESSAGE)
        }
        return null != vm.myUserModel && (null != vm.otherUserModel || RoomType.TYPE_PERSONAL != vm.room.type)
    }

    private fun initView() {
        window.setBackgroundDrawable(null)

        // Initialize chat message RecyclerView

        // Initialize chat message RecyclerView
        messageAdapter =
            TAPMessageAdapter(instanceKey, glide, chatListener, vm.messageMentionIndexes, TAPMessageAdapter.RoomType.STARRED)
        messageAdapter.setMessages(vm.messageModels)
        messageLayoutManager = object : LinearLayoutManager(this, VERTICAL, false) {
            override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        rv_starred_messages.instanceKey = instanceKey
        rv_starred_messages.adapter = messageAdapter
        rv_starred_messages.layoutManager = messageLayoutManager
        rv_starred_messages.setHasFixedSize(false)
        rv_starred_messages.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_IMAGE_LEFT, 0)
        rv_starred_messages.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_IMAGE_RIGHT, 0)
        rv_starred_messages.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_VIDEO_LEFT, 0)
        rv_starred_messages.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_VIDEO_RIGHT, 0)
        rv_starred_messages.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_PRODUCT_LIST, 0)
        messageAnimator = rv_starred_messages.itemAnimator as SimpleItemAnimator
        messageAnimator.supportsChangeAnimations = false
        rv_starred_messages.itemAnimator = null

        // Listener for scroll pagination
        endlessScrollListener = object : TAPEndlessScrollListener(messageLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadMessagesFromApi()
            }
        }
        iv_button_back.setOnClickListener { onBackPressed() }
    }

    private fun loadMessagesFromApi() {
        if (vm.isHasMoreData) {
            showLoadingOlderMessagesIndicator()
            TapCoreMessageManager.getInstance(instanceKey).getStarredMessages(
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
                        Toast.makeText(this@TapStarredMessagesActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun showLoadingOlderMessagesIndicator() {
        hideLoadingOlderMessagesIndicator()
        rv_starred_messages.post {
            runOnUiThread {
                vm.addMessagePointer(vm.getLoadingIndicator(true))
                messageAdapter.addItem(vm.getLoadingIndicator(false)) // Add loading indicator to last index
                messageAdapter.notifyItemInserted(messageAdapter.itemCount - 1)
            }
        }
    }

    private fun hideLoadingOlderMessagesIndicator() {
        rv_starred_messages.post {
            runOnUiThread {
                if (!messageAdapter.items.contains(vm.getLoadingIndicator(false))) {
                    return@runOnUiThread
                }
                val index = messageAdapter.items.indexOf(vm.getLoadingIndicator(false))
                vm.removeMessagePointer(LOADING_INDICATOR_LOCAL_ID)
                if (index >= 0) {
                    messageAdapter.removeMessage(vm.getLoadingIndicator(false))
                    if (null != messageAdapter.getItemAt(index)) {
                        messageAdapter.notifyItemChanged(index)
                    } else {
                        messageAdapter.notifyItemRemoved(index)
                    }
                    updateMessageDecoration()
                }
                checkMessageList()
            }
        }
    }

    private fun updateMessageDecoration() {
        // Update decoration for the top item in recycler view
        runOnUiThread {
            if (rv_starred_messages.itemDecorationCount > 0) {
                rv_starred_messages.removeItemDecorationAt(0)
            }
            rv_starred_messages.addItemDecoration(
                TAPVerticalDecoration(
                    0,
                    TAPUtils.dpToPx(16),
                    messageAdapter.itemCount - 1
                )
            )
        }
    }

    private fun checkMessageList() {
        if (messageAdapter.items.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
            val starredMessageIds : MutableList<String> = mutableListOf()
            for (item in vm.messageModels) {
                starredMessageIds.add(item.localID)
            }
            messageAdapter.setStarredMessageIds(starredMessageIds)
        }
    }

    private fun showEmptyState() {
        rv_starred_messages.visibility = View.GONE
        g_empty_starred_messages.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        rv_starred_messages.visibility = View.VISIBLE
        g_empty_starred_messages.visibility = View.GONE
    }

    private val chatListener = object : TAPChatListener() {
        override fun onOutsideClicked(message: TAPMessageModel?) {
            super.onOutsideClicked(message)
            val intent = Intent()
            intent.putExtra(Extras.MESSAGE, message)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

}