package io.moselo.SampleApps.Activity

import android.Manifest
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.moselo.SampleApps.Adapter.TAPShareOptionsAdapter
import io.moselo.SampleApps.Adapter.TAPShareOptionsSelectedAdapter
import io.moselo.SampleApps.SampleApplication
import io.moselo.SampleApps.listener.TAPShareOptionsInterface
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.Model.*
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalk.ViewModel.TAPShareOptionsViewModel
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.activity_share_options.*
import java.io.File
import java.util.*

class TAPShareOptionsActivity : TAPBaseActivity() {

    private var vm: TAPShareOptionsViewModel? = null
    private var adapter: TAPShareOptionsAdapter? = null
    private var selectedAdapter: TAPShareOptionsSelectedAdapter? = null
    private var searchAdapter: TAPShareOptionsAdapter? = null
    private lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_options)

        vm = ViewModelProvider(this,
                TAPShareOptionsViewModel.TAPShareOptionsViewModelFactory(
                        application))
                .get(TAPShareOptionsViewModel::class.java)
        glide = Glide.with(this)
        instanceKey = SampleApplication.INSTANCE_KEY
        adapter = TAPShareOptionsAdapter(instanceKey, vm?.roomList!!, vm!!, glide, roomListener)
        searchAdapter = TAPShareOptionsAdapter(instanceKey, vm?.searchRoomResults!!, vm!!, glide, roomListener)

        val llm = LinearLayoutManager(this, VERTICAL, false)
        rv_room_list.adapter = adapter
        rv_room_list.layoutManager = llm
        rv_room_list.setHasFixedSize(true)
        OverScrollDecoratorHelper.setUpOverScroll(rv_room_list, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        val messageAnimator = rv_room_list.itemAnimator as SimpleItemAnimator
        messageAnimator.supportsChangeAnimations = false

        val searchLlm = LinearLayoutManager(this, VERTICAL, false)
        rv_search_list.adapter = searchAdapter
        rv_search_list.layoutManager = searchLlm
        rv_search_list.setHasFixedSize(true)
        OverScrollDecoratorHelper.setUpOverScroll(rv_search_list, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        val searchAnimator = rv_search_list.itemAnimator as SimpleItemAnimator
        searchAnimator.supportsChangeAnimations = false
        rv_search_list.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                TAPUtils.dismissKeyboard(this@TAPShareOptionsActivity)
            }
        })

        // check if user logged in
        if (!TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            TAPLoginActivity.start(this, instanceKey)
            finish()
        }

        TAPDataManager.getInstance(instanceKey).getRoomList(false, listener)
        btn_send_message.setOnClickListener {
            if ((!intent.type?.startsWith("text")!!
                    || !intent.type?.startsWith("image")!!
                    || !intent.type?.startsWith("video")!!)
                    && !TAPUtils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(
                            this, arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE)
            } else {
                val list = vm?.selectedRooms
                val recipient: String
                val keys: List<String> = ArrayList(list?.keys)
                recipient = if (list?.size == 1) {
                    list[keys[0]]!!.lastMessage.room.roomName
                } else {
                    String.format(getString(R.string.df_recipients), list?.size)
                }
                TapTalkDialog.Builder(this)
                        .setTitle(getString(R.string.tap_send_message))
                        .setMessage(String.format(getString(R.string.send_this_message_to_sf), recipient))
                        .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                        .setPrimaryButtonTitle(getString(R.string.tap_send))
                        .setPrimaryButtonListener { handleIntentData(intent) }
                        .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                        .setSecondaryButtonListener { }
                        .show()
            }
        }
        iv_close_btn.setOnClickListener { onBackPressed() }
        iv_search_icon.setOnClickListener { showSearchBar() }
        iv_button_clear_text.setOnClickListener { et_search.setText("") }
    }

    override fun onBackPressed() {
        if (vm!!.isSelecting) {
            showToolbar()
        } else {
            super.onBackPressed()
            overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_right)
        }
    }

    private val listener = object: TAPDatabaseListener<TAPMessageEntity>(){

        override fun onSelectFinishedWithUnreadCount(entities: MutableList<TAPMessageEntity>?, unreadMap: MutableMap<String, Int>?, mentionCount: MutableMap<String, Int>?) {
            val messageModels: MutableList<TAPRoomListModel> = ArrayList()
            vm?.roomPointer?.clear()
            for (entity in entities!!) {
                val model = TAPChatManager.getInstance(instanceKey).convertToModel(entity)
                val roomModel = TAPRoomListModel.buildWithLastMessage(model)
                roomModel.type = TAPRoomListModel.Type.SELECTABLE_ROOM
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
                if (0 == vm!!.roomList?.size) {
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
            vm?.searchState = vm?.STATE_IDLE
        }
    }

    private val roomSearchListener: TAPDatabaseListener<TAPMessageEntity> = object : TAPDatabaseListener<TAPMessageEntity>() {
        override fun onSelectedRoomList(entities: List<TAPMessageEntity>, unreadMap: Map<String, Int>, mentionMap: Map<String, Int>) {
            if (vm?.searchState == vm?.STATE_PENDING && vm?.pendingSearch?.isNotEmpty()!!) {
                vm?.searchState = vm?.STATE_IDLE
                startSearch(vm?.pendingSearch)
                return
            } else if (vm?.searchState != vm?.STATE_SEARCHING) {
                return
            }
            if (entities.isNotEmpty()) {
                for (entity in entities) {
                    val myId = TAPChatManager.getInstance(instanceKey).activeUser.userID
                    // Exclude active user's own room
                    if (entity.roomID != TAPChatManager.getInstance(instanceKey).arrangeRoomId(myId, myId)) {
                        val result = TAPRoomListModel()
                        // Convert message to room model
                        val room = TAPRoomModel.Builder(entity)
                        val unreadCount = unreadMap[room.roomID]
                        if (null != unreadCount) {
                            room.unreadCount = unreadCount
                        }
                        if (result.lastMessage == null) {
                            result.lastMessage = TAPMessageModel()
                        }
                        result.lastMessage.room = room
                        result.type = TAPRoomListModel.Type.SELECTABLE_CONTACT
                        if (room.roomType == TYPE_PERSONAL) {
                            if (vm?.personalContacts!!.isEmpty()) {
                                val personalSectionTitle = TAPRoomListModel(TAPRoomListModel.Type.SECTION)
                                personalSectionTitle.title = getString(R.string.contacts)
                                vm?.personalContacts?.add(personalSectionTitle)
                            }
                            vm?.personalContacts?.add(result)
                        } else if (room.roomType == TYPE_GROUP) {
                            if (vm?.groupContacts!!.isEmpty()) {
                                val groupSectionTitle = TAPRoomListModel(TAPRoomListModel.Type.SECTION)
                                groupSectionTitle.title = getString(R.string.groups)
                                vm?.groupContacts?.add(groupSectionTitle)
                            }
                            vm?.groupContacts?.add(result)
                        }
                    }
                }
                runOnUiThread {
                    tv_empty_room.visibility = View.GONE
                    rv_search_list.visibility = View.VISIBLE
                    TAPDataManager.getInstance(instanceKey).searchContactsByName(vm?.searchKeyword, contactSearchListener)
                }
            } else {
                TAPDataManager.getInstance(instanceKey).searchContactsByName(vm?.searchKeyword, contactSearchListener)
            }
        }
    }

    private val contactSearchListener: TAPDatabaseListener<TAPUserModel> = object : TAPDatabaseListener<TAPUserModel>() {
        override fun onSelectFinished(entities: List<TAPUserModel>) {
            if (vm?.searchState == vm?.STATE_PENDING && vm?.pendingSearch?.isNotEmpty()!!) {
                vm?.searchState = vm?.STATE_IDLE
                startSearch(vm?.pendingSearch)
                return
            } else if (vm?.searchState != vm?.STATE_SEARCHING) {
                return
            }
            vm?.pendingSearch = ""
            vm?.searchState = vm?.STATE_IDLE
            if (entities.isNotEmpty()) {
                runOnUiThread {
                    tv_empty_room.visibility = View.GONE
                    rv_search_list.visibility = View.VISIBLE
                }
                for (contact in entities) {
                    val result = TAPRoomListModel(TAPRoomListModel.Type.SELECTABLE_CONTACT)
                    // Convert contact to room model
                    val room = TAPRoomModel(
                            TAPChatManager.getInstance(instanceKey).arrangeRoomId(TAPChatManager.getInstance(instanceKey).activeUser.userID, contact.userID),
                            contact.name,
                            TYPE_PERSONAL,
                            contact.avatarURL,
                            ""
                    )
                    // Check if result already contains contact from chat room query
                    if (!vm?.resultContainsRoom(room.roomID)!!) {
                        result.lastMessage.room = room
                        if (room.roomType == TYPE_PERSONAL) {
                            if (vm?.personalContacts!!.isEmpty()) {
                                val personalSectionTitle = TAPRoomListModel(TAPRoomListModel.Type.SECTION)
                                personalSectionTitle.title = getString(R.string.contacts)
                                vm?.personalContacts?.add(personalSectionTitle)
                            }
                            vm?.personalContacts?.add(result)
                        } else if (room.roomType == TYPE_GROUP) {
                            if (vm?.groupContacts!!.isEmpty()) {
                                val groupSectionTitle = TAPRoomListModel(TAPRoomListModel.Type.SECTION)
                                groupSectionTitle.title = getString(R.string.groups)
                                vm?.groupContacts?.add(groupSectionTitle)
                            }
                            vm?.groupContacts?.add(result)
                        }
                    }
                }
                runOnUiThread {
                    searchAdapter?.setItems(vm?.getSearchResults(), false)
                }
            } else {
                runOnUiThread {
                    tv_empty_room.visibility = View.VISIBLE
                    rv_search_list.visibility = View.GONE
                }
            }
        }
    }

    private val searchTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            et_search.removeTextChangedListener(this)
            if (s.isEmpty()) {
                vm?.pendingSearch = ""
                iv_button_clear_text.visibility = View.GONE
            } else {
                iv_button_clear_text.visibility = View.VISIBLE
            }
            startSearch(et_search.text.toString())
            et_search.addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun showToolbar() {
        vm?.isSelecting = false
        TAPUtils.dismissKeyboard(this)
        tv_toolbar_title.visibility = View.VISIBLE
        et_search.visibility = View.GONE
        et_search.removeTextChangedListener(searchTextWatcher)
        et_search.setText("")
        iv_search_icon.visibility = View.VISIBLE
        iv_button_clear_text.visibility = View.GONE
        (cl_toolbar.background as TransitionDrawable).reverseTransition(TAPDefaultConstant.SHORT_ANIMATION_TIME)
        reloadLocalDataAndUpdateUILogic()
        rv_search_list.visibility = View.GONE
    }

    private fun showSearchBar() {
        vm?.isSelecting = true
        tv_toolbar_title.visibility = View.GONE
        et_search.visibility = View.VISIBLE
        et_search.addTextChangedListener(searchTextWatcher)
        iv_search_icon.visibility = View.GONE
        TAPUtils.showKeyboard(this, et_search)
        (cl_toolbar.background as TransitionDrawable).startTransition(TAPDefaultConstant.SHORT_ANIMATION_TIME)
        rv_room_list.visibility = View.GONE
        rv_search_list.visibility = View.VISIBLE
        startSearch(et_search.text.toString())
    }

    private fun startSearch(keyword: String?) {
        vm!!.clearSearchResults()
        vm?.searchKeyword = keyword?.toLowerCase(Locale.getDefault())?.trim { it <= ' ' }
        searchAdapter?.searchKeyword = vm?.searchKeyword!!
        if (vm?.searchState == vm!!.STATE_IDLE) {
            // Search with keyword
            vm?.searchState = vm!!.STATE_SEARCHING
            TAPDataManager.getInstance(instanceKey).searchAllRoomsFromDatabase(vm?.searchKeyword, roomSearchListener)
        } else {
            // Set search as pending
            vm?.pendingSearch = vm?.searchKeyword
            vm?.searchState = vm!!.STATE_PENDING
        }
    }

    private fun showSelectedRecipients() {
        g_selected_rooms.visibility = View.VISIBLE
        selectedAdapter = TAPShareOptionsSelectedAdapter(vm?.selectedRooms!!.values.toList(), roomListener)
        val selectedLlm = LinearLayoutManager(this, HORIZONTAL, false)
        rv_selected.adapter = selectedAdapter
        rv_selected.layoutManager = selectedLlm
        rv_selected.setHasFixedSize(true)
        OverScrollDecoratorHelper.setUpOverScroll(rv_selected, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        val animator = rv_selected.itemAnimator as SimpleItemAnimator
        animator.supportsChangeAnimations = false

    }

    private val roomListener = object : TAPShareOptionsInterface {
        override fun onRoomSelected(item: TAPRoomListModel?) {
            val roomID = item?.lastMessage?.room?.roomID
            if (!vm!!.selectedRooms?.containsKey(roomID)!!) {
                vm!!.selectedRooms!![roomID!!] = item
                if (g_selected_rooms.visibility == View.GONE) {
                    showSelectedRecipients()
                }
                val recipients = vm?.selectedRooms?.size
                if (recipients!! > 1) {
                    tv_toolbar_title.text = String.format(getString(R.string.df_recipients), recipients)
                    tv_selected.text = String.format(getString(R.string.selected_df_recipients), recipients)
                } else if (vm?.selectedRooms?.size!! == 1) {
                    tv_toolbar_title.text = getString(R.string.one_recipient)
                    tv_selected.text = getString(R.string.selected_one_recipient)
                }
                selectedAdapter?.items = vm?.selectedRooms!!.values.toList()
                rv_selected.scrollToPosition(recipients - 1)
                adapter?.notifyDataSetChanged()
                searchAdapter?.notifyDataSetChanged()
            }
        }

        override fun onRoomDeselected(item: TAPRoomListModel?, position: Int) {
            val roomID = item?.lastMessage?.room?.roomID
            if (vm!!.selectedRooms?.containsKey(roomID)!!) {
                vm!!.selectedRooms!!.remove(roomID)
                if (vm!!.selectedRooms!!.isEmpty()) {
                    g_selected_rooms.visibility = View.GONE
                    tv_toolbar_title.text = getString(R.string.select_chat)
                }

                val recipients = vm?.selectedRooms?.size
                if (recipients!! > 1) {
                    tv_toolbar_title.text = String.format(getString(R.string.df_recipients), recipients)
                    tv_selected.text = String.format(getString(R.string.selected_df_recipients), recipients)
                } else if (vm?.selectedRooms?.size!! == 1) {
                    tv_toolbar_title.text = getString(R.string.one_recipient)
                    tv_selected.text = getString(R.string.selected_one_recipient)
                }
                selectedAdapter?.items = vm?.selectedRooms!!.values.toList()
                adapter?.notifyDataSetChanged()
                searchAdapter?.notifyDataSetChanged()
            }
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
        if (!vm!!.selectedRooms?.containsKey(roomID)!!) {
            // Room selected
            vm!!.selectedRooms!![roomID!!] = item
        } else {
            // Room deselected
            vm!!.selectedRooms?.remove(roomID)
        }
        adapter?.notifyItemChanged(position)
    }

    private fun handleIntentData(intent: Intent) {
        val selectedRooms = vm?.selectedRooms
        if (selectedRooms!!.isNotEmpty()) {
            val caption = et_caption.text.toString()
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    val data = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM)
                    for (roomListModel in selectedRooms.values) {
                        when {
                            intent.type == "text/plain" -> {
                                handleTextType(intent, roomListModel.lastMessage.room, caption)
                            }
                            intent.type?.startsWith("image/") == true -> {
                                handleImageType(data as Uri, roomListModel.lastMessage.room, caption)
                            }
                            intent.type?.startsWith("video/") == true -> {
                                handleVideoType(data as Uri, roomListModel.lastMessage.room, caption)
                            }
                            else -> {
                                handleFileType(data as Uri, roomListModel.lastMessage.room, caption)
                            }
                        }
                    }
                }
                Intent.ACTION_SEND_MULTIPLE -> {
                    for (roomListModel in selectedRooms.values) {
                        when {
                            intent.type?.startsWith("image/") == true -> {
                                handleSendMultipleImages(intent, roomListModel.lastMessage.room, caption)
                            }
                            intent.type?.startsWith("video/") == true -> {
                                handleSendMultipleVideos(intent, roomListModel.lastMessage.room, caption)
                            }
                            else -> {
                                handleSendMultipleFiles(intent, roomListModel.lastMessage.room, caption)
                            }
                        }
                    }
                }
            }
            onShareFinished(selectedRooms)
        }
    }

    private fun handleFileType(uri: Uri, roomModel: TAPRoomModel?, caption: String) {
        val file = File(TAPFileUtils.getInstance().getFilePath(this, uri))
        TapCoreMessageManager.getInstance().sendTextMessage(caption, roomModel, object : TapCoreSendMessageListener() {})
        TapCoreMessageManager.getInstance().sendFileMessage(file, roomModel, object : TapCoreSendMessageListener() {})
    }

    private fun handleVideoType(uri: Uri, roomModel: TAPRoomModel?, caption: String) {
        TapCoreMessageManager.getInstance().sendVideoMessage(uri, caption, roomModel, object : TapCoreSendMessageListener() {})
    }

    private fun handleImageType(uri: Uri, roomModel: TAPRoomModel?, caption: String) {
        TapCoreMessageManager.getInstance().sendImageMessage(uri, caption, roomModel, object : TapCoreSendMessageListener() {})
    }

    private fun handleTextType(intent: Intent, roomModel: TAPRoomModel?, caption: String) {
        if (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) == null) {
            // text type with string data
            TapCoreMessageManager.getInstance().sendTextMessage(caption, roomModel, object : TapCoreSendMessageListener() {})
            TapCoreMessageManager.getInstance().sendTextMessage(intent.getStringExtra(Intent.EXTRA_TEXT), roomModel, object : TapCoreSendMessageListener() {})
        } else {
            // text type with file data
            handleFileType(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri, roomModel, caption)
        }
    }

    private fun handleSendMultipleImages(intent: Intent, roomModel: TAPRoomModel?, caption: String) {
        var firstCaption: String = caption
        val images = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
        if (images != null) {
            for (item in images) {
                TapCoreMessageManager.getInstance().sendImageMessage(item as? Uri, firstCaption, roomModel, object : TapCoreSendMessageListener() {})
                if (firstCaption.isNotEmpty()) {
                    firstCaption = ""
                }
            }
        }
    }

    private fun handleSendMultipleVideos(intent: Intent, roomModel: TAPRoomModel?, caption: String) {
        var firstCaption: String = caption
        val video = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
        if (video != null) {
            for (item in video) {
                TapCoreMessageManager.getInstance().sendVideoMessage(item as? Uri, firstCaption, roomModel, object : TapCoreSendMessageListener() {})
                if (firstCaption.isNotEmpty()) {
                    firstCaption = ""
                }
            }
        }
    }

    private fun handleSendMultipleFiles(intent: Intent, roomModel: TAPRoomModel?, caption: String) {
        var firstCaption: String = caption
        val filUri = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
        if (filUri != null) {
            for (item in filUri) {
                val file = File(TAPFileUtils.getInstance().getFilePath(this, item as? Uri))
                if (firstCaption.isNotEmpty()) {
                    TapCoreMessageManager.getInstance().sendTextMessage(firstCaption, roomModel, object : TapCoreSendMessageListener() {})
                    firstCaption = ""
                }
                TapCoreMessageManager.getInstance().sendFileMessage(file, roomModel, object : TapCoreSendMessageListener() {})
            }
        }
    }

    private fun onShareFinished(list: MutableMap<String, TAPRoomListModel>) {
        val keys: List<String> = ArrayList(list.keys)
        deleteCache()
        finishAffinity()
        if (list.size > 1) {
            //open room list
            TapUIRoomListActivity.start(this, instanceKey)
        } else if (list.size == 1) {
            //open chat room
            TapUIRoomListActivity.start(this, instanceKey, list[keys[0]]?.lastMessage?.room)
        }
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