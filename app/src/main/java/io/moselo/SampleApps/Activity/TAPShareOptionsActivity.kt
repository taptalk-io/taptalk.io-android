package io.moselo.SampleApps.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.moselo.SampleApps.Adapter.TAPShareOptionsAdapter
import io.moselo.SampleApps.SampleApplication
import io.moselo.SampleApps.`interface`.TAPShareOptionsInterface
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomListModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel.TAPRoomListViewModelFactory
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.activity_share_options.*
import java.io.File
import java.util.*

class TAPShareOptionsActivity : TAPBaseActivity() {

    private var roomModel: TAPRoomModel? = null
    private var vm: TAPRoomListViewModel? = null
    private var adapter: TAPShareOptionsAdapter? = null
    private lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_options)

        vm = ViewModelProvider(this,
                TAPRoomListViewModelFactory(
                        application, instanceKey))
                .get(TAPRoomListViewModel::class.java)
        glide = Glide.with(this)
        instanceKey = SampleApplication.INSTANCE_KEY
        adapter = TAPShareOptionsAdapter(instanceKey, vm!!.roomList, glide, roomListener)

        val llm = object : LinearLayoutManager(this, VERTICAL, false) {
            override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        rv_room_list.adapter = adapter
        rv_room_list.layoutManager = llm
        rv_room_list.setHasFixedSize(true)
        OverScrollDecoratorHelper.setUpOverScroll(rv_room_list, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        val messageAnimator = rv_room_list.itemAnimator as SimpleItemAnimator
        messageAnimator.supportsChangeAnimations = false
        btn_send_message.setOnClickListener { handleIntentData(intent, roomModel) }
        TAPDataManager.getInstance(instanceKey).getRoomList(false, listener)
        iv_close_btn.setOnClickListener { onBackPressed() }
    }

    // TODO: 20/04/21 handle onbackpressed MU

    private val listener = object: TAPDatabaseListener<TAPMessageEntity>(){

        override fun onSelectFinishedWithUnreadCount(entities: MutableList<TAPMessageEntity>?, unreadMap: MutableMap<String, Int>?, mentionCount: MutableMap<String, Int>?) {
            val messageModels: MutableList<TAPRoomListModel> = ArrayList()
            vm?.roomPointer?.clear()
            for (entity in entities!!) {
                val model = TAPChatManager.getInstance(instanceKey).convertToModel(entity)
                val roomModel = TAPRoomListModel.buildWithLastMessage(model)
                messageModels.add(roomModel)
                vm?.addRoomPointer(roomModel)
            }
            vm?.roomList = messageModels
            reloadLocalDataAndUpdateUILogic()
        }
    }

    private fun reloadLocalDataAndUpdateUILogic() {
        runOnUiThread {
            if (null != adapter) {
                if (0 == vm!!.roomList.size) {
                    // Room list is empty
                    tv_empty_room.visibility = View.VISIBLE
                    rv_room_list.visibility = View.GONE
                } else {
                    adapter?.addRoomList(vm!!.roomList)
                    rv_room_list.visibility = View.VISIBLE
                    rv_room_list.scrollToPosition(0)
                    tv_empty_room.visibility = View.GONE
                }
            }
        }
    }

    private val roomListener = object : TAPShareOptionsInterface {
        override fun onRoomSelected(v: View?, item: TAPRoomListModel?, position: Int) {
            handleIntentData(intent, item?.lastMessage?.room)
        }

        override fun onMultipleRoomSelected(v: View?, item: TAPRoomListModel?, position: Int): Boolean {
            if (vm!!.isSelecting) {
                // Select room when other room is already selected
                onRoomSelected(item, position)
            } else {
                // Open chat room on click
                if (TAPApiManager.getInstance(instanceKey).isLoggedOut) {
                    // Return if logged out (active user is null)
                    return true
                }
                val myUserID = TAPChatManager.getInstance(instanceKey).activeUser.userID
                if ("$myUserID-$myUserID" != item!!.lastMessage.room.roomID) {
                    TapUIChatActivity.start(this@TAPShareOptionsActivity, instanceKey, item.lastMessage.room, item.typingUsers)
                } else {
                    Toast.makeText(this@TAPShareOptionsActivity, getString(io.taptalk.TapTalk.R.string.tap_error_invalid_room), Toast.LENGTH_SHORT).show()
                }
            }
            return true
        }
    }

    private fun onRoomSelected(item: TAPRoomListModel?, position: Int) {
        val roomID = item?.lastMessage?.room?.roomID
        if (!vm!!.selectedRooms.containsKey(roomID)) {
            // Room selected
            vm!!.selectedRooms[roomID] = item
        } else {
            // Room deselected
            vm!!.selectedRooms.remove(roomID)
        }
        adapter?.notifyItemChanged(position)
    }

    private fun handleIntentData(intent: Intent, roomModel: TAPRoomModel?) {
        // TODO: 21/04/21 handle multiple chat rooms MU
        when(intent.action) {
            Intent.ACTION_SEND -> {
                when {
                    intent.type == "text/plain" -> {
                        handleTextType(intent, roomModel)
                    }
                    intent.type?.startsWith("image/") == true -> {
                        handleImageType(intent, roomModel)
                    }
                    intent.type?.startsWith("video/") == true -> {
                        handleVideoType(intent, roomModel)
                    }
                    else -> {
                        handleFileType(intent, roomModel)
                    }
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                when {
                    intent.type?.startsWith("image/") == true -> {
                        handleSendMultipleImages(intent)
                    }
                    intent.type?.startsWith("video/") == true -> {
                        handleSendMultipleVideos(intent)
                    }
                    else -> {
                        handleSendMultipleFiles(intent)
                    }
                }
            }
        }
    }

    private fun handleFileType(intent: Intent, roomModel: TAPRoomModel?) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            val file = File(TAPFileUtils.getInstance().getFilePath(this, it))
            TapCoreMessageManager.getInstance().sendFileMessage(file, roomModel, object : TapCoreSendMessageListener() {
                override fun onStart(message: TAPMessageModel?) {
                    super.onStart(message)
                    runOnUiThread {
                        showPopupLoading(getString(R.string.tap_loading))
                    }
                }

                override fun onSuccess(message: TAPMessageModel?) {
                    super.onSuccess(message)
                    runOnUiThread {
                        hidePopupLoading()
                        deleteCache()
                    }
                }
            })
        }
    }

    private fun handleVideoType(intent: Intent, roomModel: TAPRoomModel?) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            TapCoreMessageManager.getInstance().sendVideoMessage(it, "share video", roomModel, object : TapCoreSendMessageListener() {
                override fun onStart(message: TAPMessageModel?) {
                    super.onStart(message)
                    runOnUiThread {
                        showPopupLoading(getString(R.string.tap_loading))
                    }
                }

                override fun onSuccess(message: TAPMessageModel?) {
                    super.onSuccess(message)
                    runOnUiThread {
                        hidePopupLoading()
                    }
                }
            })
        }
    }

    private fun handleImageType(intent: Intent, roomModel: TAPRoomModel?) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            TapCoreMessageManager.getInstance().sendImageMessage(it, "share image", roomModel, object : TapCoreSendMessageListener() {
                override fun onStart(message: TAPMessageModel?) {
                    super.onStart(message)
                    runOnUiThread {
                        showPopupLoading(getString(R.string.tap_loading))
                    }
                }

                override fun onSuccess(message: TAPMessageModel?) {
                    super.onSuccess(message)
                    runOnUiThread {
                        hidePopupLoading()
                    }
                }
            })
        }
    }

    private fun handleTextType(intent: Intent, roomModel: TAPRoomModel?) {
        if (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) == null) {
            //text type with string data
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                TapCoreMessageManager.getInstance().sendTextMessage(it, roomModel, object : TapCoreSendMessageListener() {
                    override fun onSuccess(message: TAPMessageModel?) {
                        super.onSuccess(message)
                        Toast.makeText(this@TAPShareOptionsActivity, "SUCCES SEND MESSAGE", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            // text type with file data
            handleFileType(intent, roomModel)
        }

    }

    private fun handleSendMultipleImages(intent: Intent) {
        val images = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
        if (images != null) {
            var loadingCounter = 0
            for (item in images) {
                runOnUiThread {
                    showPopupLoading(getString(R.string.tap_loading))
                }
                // TODO: 13/04/21 caption for first image only MU
                TapCoreMessageManager.getInstance().sendImageMessage(item as? Uri, "", roomModel, object : TapCoreSendMessageListener() {
                    override fun onStart(message: TAPMessageModel?) {
                        super.onStart(message)
                    }

                    override fun onSuccess(message: TAPMessageModel?) {
                        super.onSuccess(message)
                        loadingCounter++
                        if (loadingCounter == images.size) {
                            runOnUiThread {
                                hidePopupLoading()
                            }
                        }
                    }

                    override fun onError(errorCode: String?, errorMessage: String?) {
                        super.onError(errorCode, errorMessage)
                        loadingCounter++
                        if (loadingCounter == images.size) {
                            runOnUiThread {
                                hidePopupLoading()
                            }
                        }
                    }
                })
            }
        }
    }

    private fun handleSendMultipleVideos(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
            // TODO: 21/04/21 handle multiple videos MU
        }
    }

    private fun handleSendMultipleFiles(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
            // TODO: 21/04/21 handle multiple files MU
        }
    }

    private fun showPopupLoading(message: String) {
        popup_loading.findViewById<ImageView>(R.id.iv_loading_image).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white))
        if (null == popup_loading.findViewById<ImageView>(R.id.iv_loading_image).animation)
            TAPUtils.rotateAnimateInfinitely(this, popup_loading.findViewById<ImageView>(R.id.iv_loading_image))
        popup_loading.findViewById<TextView>(R.id.tv_loading_text)?.text = message
        popup_loading.findViewById<FrameLayout>(R.id.popup_loading).visibility = View.VISIBLE
    }

    private fun hidePopupLoading() {
        popup_loading.findViewById<FrameLayout>(R.id.popup_loading).visibility = View.GONE
    }

    fun deleteCache() {
        try {
            val dir: File = cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteDir(dir: File?) {
        if (dir != null && dir.isDirectory) {
            val children = dir.listFiles()
            for (i in children.indices) {
                if (children[i].name.equals("share")) {
                    children[i].deleteRecursively()
                }
            }
        }
    }
}