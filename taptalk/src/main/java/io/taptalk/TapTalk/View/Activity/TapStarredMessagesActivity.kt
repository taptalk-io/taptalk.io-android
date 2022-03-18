package io.taptalk.TapTalk.View.Activity

import android.os.Bundle
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
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TAPChatListener
import io.taptalk.TapTalk.Manager.*
import io.taptalk.TapTalk.Manager.TAPGroupManager.Companion.getInstance
import io.taptalk.TapTalk.Model.TAPMessageModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_starred_messages)
        glide = Glide.with(this)
        initRoom()
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
            TAPMessageAdapter(instanceKey, glide, chatListener, vm.messageMentionIndexes)
        messageAdapter.setMessages(vm.messageModels)
        messageLayoutManager = object : LinearLayoutManager(this, VERTICAL, true) {
            override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        messageLayoutManager.stackFromEnd = true
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
                if (!vm.isOnBottom) {
//                    loadMoreMessagesFromDatabase()
                }
            }
        }
        iv_button_back.setOnClickListener { onBackPressed() }
    }

    private val chatListener = object : TAPChatListener() {
        override fun onOutsideClicked(message: TAPMessageModel?) {
            super.onOutsideClicked(message)
            TapUI.getInstance(instanceKey).openChatRoomWithRoomModel(this@TapStarredMessagesActivity, message?.room, message?.localID)
        }
    }

}