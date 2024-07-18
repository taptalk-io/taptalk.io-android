package io.moselo.SampleApps.Activity

import android.Manifest
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
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
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.*
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalk.ViewModel.TAPShareOptionsViewModel
import io.taptalk.TapTalkSample.R
import io.taptalk.TapTalkSample.databinding.ActivityShareOptionsBinding
import java.io.File
import java.util.*

class TAPShareOptionsActivity : TAPBaseActivity() {

    private lateinit var binding: ActivityShareOptionsBinding
    private lateinit var glide: RequestManager
    private var vm: TAPShareOptionsViewModel? = null
    private var adapter: TAPShareOptionsAdapter? = null
    private var selectedAdapter: TAPShareOptionsSelectedAdapter? = null
    private var searchAdapter: TAPShareOptionsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this, TAPShareOptionsViewModel.TAPShareOptionsViewModelFactory(application)).get(TAPShareOptionsViewModel::class.java)
        glide = Glide.with(this)
        instanceKey = SampleApplication.INSTANCE_KEY
        adapter = TAPShareOptionsAdapter(instanceKey, vm?.roomList!!, vm!!, glide, roomListener)
        searchAdapter = TAPShareOptionsAdapter(instanceKey, vm?.searchRoomResults!!, vm!!, glide, roomListener)

        val llm = LinearLayoutManager(this, VERTICAL, false)
        binding.rvRoomList.adapter = adapter
        binding.rvRoomList.layoutManager = llm
        binding.rvRoomList.setHasFixedSize(true)
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvRoomList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        val messageAnimator = binding.rvRoomList.itemAnimator as SimpleItemAnimator
        messageAnimator.supportsChangeAnimations = false

        val searchLlm = LinearLayoutManager(this, VERTICAL, false)
        binding.rvSearchList.adapter = searchAdapter
        binding.rvSearchList.layoutManager = searchLlm
        binding.rvSearchList.setHasFixedSize(true)
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvSearchList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        val searchAnimator = binding.rvSearchList.itemAnimator as SimpleItemAnimator
        searchAnimator.supportsChangeAnimations = false
        binding.rvSearchList.addOnScrollListener(object : OnScrollListener() {
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
        binding.btnSendMessage.setOnClickListener {
            if ((!intent.type?.startsWith("text")!! ||
                !intent.type?.startsWith("image")!! ||
                !intent.type?.startsWith("video")!!) &&
                !TAPUtils.hasPermissions(this, *TAPUtils.getStoragePermissions(true))
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    TAPUtils.getStoragePermissions(true),
                    TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
                )
            }
            else {
                val list = vm?.selectedRooms
                val recipient: String
                val keys: List<String> = ArrayList(list?.keys)
                recipient = if (list?.size == 1) {
                    list[keys[0]]!!.lastMessage.room.name
                } else {
                    String.format(getString(R.string.df_recipients), list?.size)
                }
                TapTalkDialog.Builder(this)
                        .setTitle(getString(io.taptalk.TapTalk.R.string.tap_send_message))
                        .setMessage(String.format(getString(R.string.send_this_message_to_sf), recipient))
                        .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                        .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_send))
                        .setPrimaryButtonListener { handleIntentData(intent) }
                        .setSecondaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_cancel))
                        .setSecondaryButtonListener { }
                        .show()
            }
        }

        //max caption length is 100 for single image/video
        if (intent.action == Intent.ACTION_SEND && (intent.type!!.startsWith("image/") || intent.type!!.startsWith("video/"))) {
            binding.etCaption.filters += InputFilter.LengthFilter(100)
        }

        binding.ivCloseBtn.setOnClickListener { onBackPressed() }
        binding.ivSearchIcon.setOnClickListener { showSearchBar() }
        binding.ivButtonClearText.setOnClickListener { binding.etSearch.setText("") }
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
            val blockedUserIDs = TAPDataManager.getInstance(instanceKey).blockedUserIds
            for (entity in entities!!) {
                val model = TAPMessageModel.fromMessageEntity(entity)
                val roomModel = TAPRoomListModel.buildWithLastMessage(model)
                roomModel.type = TAPRoomListModel.Type.SELECTABLE_ROOM
                if (TAPUtils.isSavedMessagesRoom(model.room.roomID, instanceKey) && TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled) {
                    messageModels.add(0, roomModel)
                }
                else if (model.room.type != TYPE_PERSONAL ||
                         !blockedUserIDs.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(model.room.roomID))
                ) {
                    messageModels.add(roomModel)
                }
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
                    binding.tvEmptyRoom.visibility = View.VISIBLE
                    binding.rvRoomList.visibility = View.GONE
                } else {
                    adapter?.addRoomList(vm!!.roomList)
                    binding.rvRoomList.visibility = View.VISIBLE
                    binding.rvRoomList.scrollToPosition(0)
                    binding.tvEmptyRoom.visibility = View.GONE
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
            val myId = TAPChatManager.getInstance(instanceKey).activeUser.userID
            val blockedUserIDs = TAPDataManager.getInstance(instanceKey).blockedUserIds
            if (entities.isNotEmpty()) {
                for (entity in entities) {
                    // Exclude active user's own room
                    if (TAPUtils.isSavedMessagesRoom(entity.roomID, instanceKey)) {
                        val result = TAPRoomListModel()
                        val room = TAPRoomModel.Builder(entity)
                        if (result.lastMessage == null) {
                            result.lastMessage = TAPMessageModel()
                        }
                        result.lastMessage.room = room
                        result.type = TAPRoomListModel.Type.SELECTABLE_CONTACT
                        if (TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled) {
                            vm?.groupContacts?.add(0, result)
                        } else {
                            vm?.groupContacts?.add(result)
                        }
                    }
                    else if (entity.roomID != TAPChatManager.getInstance(instanceKey).arrangeRoomId(myId, myId) &&
                             (entity.roomType != TYPE_PERSONAL || !blockedUserIDs.contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(entity.roomID)))
                    ) {
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
                        if (room.type == TYPE_PERSONAL) {
                            if (vm?.personalContacts!!.isEmpty()) {
                                val personalSectionTitle = TAPRoomListModel(TAPRoomListModel.Type.SECTION)
                                personalSectionTitle.title = getString(R.string.contacts)
                                vm?.personalContacts?.add(personalSectionTitle)
                            }
                            vm?.personalContacts?.add(result)
                        } else if (room.type == TYPE_GROUP) {
                            if (vm?.groupContacts!!.isEmpty() || (vm?.groupContacts!!.size == 1 && TAPUtils.isSavedMessagesRoom(vm?.groupContacts!![0].lastMessage.room.roomID, instanceKey))) {
                                val groupSectionTitle = TAPRoomListModel(TAPRoomListModel.Type.SECTION)
                                groupSectionTitle.title = getString(R.string.groups)
                                vm?.groupContacts?.add(groupSectionTitle)
                            }
                            vm?.groupContacts?.add(result)
                        }
                    }
                }
                runOnUiThread {
                    binding.tvEmptyRoom.visibility = View.GONE
                    binding.rvSearchList.visibility = View.VISIBLE
                    TAPDataManager.getInstance(instanceKey).searchContactsByName(vm?.searchKeyword, contactSearchListener)
                }
            } else {
                val result = TAPRoomListModel()
                val room = TAPRoomModel.Builder(
                    String.format("%s-%s", myId, myId),
                    getString(io.taptalk.TapTalk.R.string.tap_saved_messages),
                    TYPE_PERSONAL,
                    TAPImageURL("", ""),
                    ""
                )
                result.lastMessage = TAPMessageModel()
                result.lastMessage.room = room
                result.type = TAPRoomListModel.Type.SELECTABLE_CONTACT
                vm?.groupContacts?.add(0, result)
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
            when {
                entities.isNotEmpty() -> {
                    runOnUiThread {
                        binding.tvEmptyRoom.visibility = View.GONE
                        binding.rvSearchList.visibility = View.VISIBLE
                    }
                    val blockedUserIDs = TAPDataManager.getInstance(instanceKey).blockedUserIds
                    for (contact in entities) {
                        val result = TAPRoomListModel(TAPRoomListModel.Type.SELECTABLE_CONTACT)
                        // Convert contact to room model
                        val room = TAPRoomModel(
                            TAPChatManager.getInstance(instanceKey).arrangeRoomId(TAPChatManager.getInstance(instanceKey).activeUser.userID, contact.userID),
                            contact.fullname,
                            TYPE_PERSONAL,
                            contact.imageURL,
                            ""
                        )
                        // Check if result already contains contact from chat room query / user is blocked
                        if (!vm?.resultContainsRoom(room.roomID)!! && !blockedUserIDs.contains(contact.userID)) {
                            result.lastMessage.room = room
                            if (room.type == TYPE_PERSONAL) {
                                if (vm?.personalContacts!!.isEmpty()) {
                                    val personalSectionTitle = TAPRoomListModel(TAPRoomListModel.Type.SECTION)
                                    personalSectionTitle.title = getString(R.string.contacts)
                                    vm?.personalContacts?.add(personalSectionTitle)
                                }
                                vm?.personalContacts?.add(result)
                            } else if (room.type == TYPE_GROUP) {
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
                }
                vm?.getSearchResults().isNullOrEmpty() -> {
                    //room and contact list empty
                    runOnUiThread {
                        binding.tvEmptyRoom.visibility = View.VISIBLE
                        binding.rvSearchList.visibility = View.GONE
                    }
                }
                else -> {
                    //room or contact not empty
                    runOnUiThread {
                        searchAdapter?.setItems(vm?.getSearchResults(), false)
                    }
                }
            }
        }
    }

    private val searchTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.etSearch.removeTextChangedListener(this)
            if (s.isEmpty()) {
                vm?.pendingSearch = ""
                binding.ivButtonClearText.visibility = View.GONE
            } else {
                binding.ivButtonClearText.visibility = View.VISIBLE
            }
            startSearch(binding.etSearch.text.toString())
            binding.etSearch.addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun showToolbar() {
        vm?.isSelecting = false
        TAPUtils.dismissKeyboard(this)
        binding.tvToolbarTitle.visibility = View.VISIBLE
        binding.etSearch.visibility = View.GONE
        binding.etSearch.removeTextChangedListener(searchTextWatcher)
        binding.etSearch.setText("")
        binding.ivSearchIcon.visibility = View.VISIBLE
        binding.ivButtonClearText.visibility = View.GONE
        (binding.clToolbar.background as TransitionDrawable).reverseTransition(TAPDefaultConstant.SHORT_ANIMATION_TIME)
        reloadLocalDataAndUpdateUILogic()
        binding.rvSearchList.visibility = View.GONE
    }

    private fun showSearchBar() {
        vm?.isSelecting = true
        binding.tvToolbarTitle.visibility = View.GONE
        binding.etSearch.visibility = View.VISIBLE
        binding.etSearch.addTextChangedListener(searchTextWatcher)
        binding.ivSearchIcon.visibility = View.GONE
        TAPUtils.showKeyboard(this, binding.etSearch)
        (binding.clToolbar.background as TransitionDrawable).startTransition(TAPDefaultConstant.SHORT_ANIMATION_TIME)
        binding.rvRoomList.visibility = View.GONE
        binding.rvSearchList.visibility = View.VISIBLE
        startSearch(binding.etSearch.text.toString())
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
        binding.gSelectedRooms.visibility = View.VISIBLE
        selectedAdapter = TAPShareOptionsSelectedAdapter(vm?.selectedRooms!!.values.toList(), roomListener, instanceKey)
        val selectedLlm = LinearLayoutManager(this, HORIZONTAL, false)
        binding.rvSelected.adapter = selectedAdapter
        binding.rvSelected.layoutManager = selectedLlm
        binding.rvSelected.setHasFixedSize(true)
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvSelected, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        val animator = binding.rvSelected.itemAnimator as SimpleItemAnimator
        animator.supportsChangeAnimations = false

    }

    private val roomListener = object : TAPShareOptionsInterface {
        override fun onRoomSelected(item: TAPRoomListModel?) {
            val roomID = item?.lastMessage?.room?.roomID
            if (!vm!!.selectedRooms?.containsKey(roomID)!!) {
                vm!!.selectedRooms!![roomID!!] = item
                if (binding.gSelectedRooms.visibility == View.GONE) {
                    showSelectedRecipients()
                }
                val recipients = vm?.selectedRooms?.size
                if (recipients!! > 1) {
                    binding.tvToolbarTitle.text = String.format(getString(R.string.df_recipients), recipients)
                    binding.tvSelected.text = String.format(getString(R.string.selected_df_recipients), recipients)
                }
                else if (vm?.selectedRooms?.size!! == 1) {
                    binding.tvToolbarTitle.text = getString(R.string.one_recipient)
                    binding.tvSelected.text = getString(R.string.selected_one_recipient)
                }
                selectedAdapter?.items = vm?.selectedRooms!!.values.toList()
                binding.rvSelected.scrollToPosition(recipients - 1)
                adapter?.notifyDataSetChanged()
                searchAdapter?.notifyDataSetChanged()
            }
        }

        override fun onRoomDeselected(item: TAPRoomListModel?, position: Int) {
            val roomID = item?.lastMessage?.room?.roomID
            if (vm!!.selectedRooms?.containsKey(roomID)!!) {
                vm!!.selectedRooms!!.remove(roomID)
                if (vm!!.selectedRooms!!.isEmpty()) {
                    binding.gSelectedRooms.visibility = View.GONE
                    binding.tvToolbarTitle.text = getString(R.string.select_chat)
                }

                val recipients = vm?.selectedRooms?.size
                if (recipients!! > 1) {
                    binding.tvToolbarTitle.text = String.format(getString(R.string.df_recipients), recipients)
                    binding.tvSelected.text = String.format(getString(R.string.selected_df_recipients), recipients)
                } else if (vm?.selectedRooms?.size!! == 1) {
                    binding.tvToolbarTitle.text = getString(R.string.one_recipient)
                    binding.tvSelected.text = getString(R.string.selected_one_recipient)
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
            val caption = binding.etCaption.text.toString()
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
//        val file: File? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Write temporary file to cache for upload for Android 11+
            val file = TAPFileUtils.createTemporaryCachedFile(this, uri)
//        } else {
//            File(TAPFileUtils.getFilePath(this, uri))
//        }
        if (file == null) {
            return
        }
        if (caption.isNotEmpty()) {
            TapCoreMessageManager.getInstance().sendTextMessage(caption, roomModel, object : TapCoreSendMessageListener() {})
        }
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
            if (caption.isNotEmpty()) {
                TapCoreMessageManager.getInstance().sendTextMessage(caption, roomModel, object : TapCoreSendMessageListener() {})
            }
            TapCoreMessageManager.getInstance().sendTextMessage(intent.getStringExtra(Intent.EXTRA_TEXT), roomModel, object : TapCoreSendMessageListener() {})
        } else {
            // text type with file data
            handleFileType(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri, roomModel, caption)
        }
    }

    private fun handleSendMultipleImages(intent: Intent, roomModel: TAPRoomModel?, caption: String) {
        val images = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
        if (images != null) {
            if (caption.isNotEmpty()) {
                TapCoreMessageManager.getInstance().sendTextMessage(caption, roomModel, object : TapCoreSendMessageListener() {})
            }
            for (item in images) {
                TapCoreMessageManager.getInstance().sendImageMessage(item as? Uri, "", roomModel, object : TapCoreSendMessageListener() {})
            }
        }
    }

    private fun handleSendMultipleVideos(intent: Intent, roomModel: TAPRoomModel?, caption: String) {
        val video = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
        if (video != null) {
            if (caption.isNotEmpty()) {
                TapCoreMessageManager.getInstance().sendTextMessage(caption, roomModel, object : TapCoreSendMessageListener() {})
            }
            for (item in video) {
                TapCoreMessageManager.getInstance().sendVideoMessage(item as? Uri, "", roomModel, object : TapCoreSendMessageListener() {})
            }
        }
    }

    private fun handleSendMultipleFiles(intent: Intent, roomModel: TAPRoomModel?, caption: String) {
        var firstCaption: String = caption
        val filUri = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
        if (filUri != null) {
            for (item in filUri) {
                val file = File(TAPFileUtils.getFilePath(this, item as? Uri))
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
            if (children != null) {
                for (i in children.indices) {
                    if (children[i].name.equals("share")) {
                        children[i].deleteRecursively()
                    }
                }
            }
        }
    }
}
