package io.taptalk.TapTalk.View.Activity

import android.Manifest
import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_FILE
import io.taptalk.TapTalk.Helper.*
import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui.FilePickerActivity
import io.taptalk.TapTalk.Interface.TapLongPressInterface
import io.taptalk.TapTalk.Interface.TapTalkActionInterface
import io.taptalk.TapTalk.Listener.*
import io.taptalk.TapTalk.Manager.*
import io.taptalk.TapTalk.Manager.TAPFileUploadManager.BitmapInterface
import io.taptalk.TapTalk.Manager.TAPGroupManager.Companion.getInstance
import io.taptalk.TapTalk.Manager.TapUI.LongPressMenuType
import io.taptalk.TapTalk.Model.*
import io.taptalk.TapTalk.Model.ResponseModel.*
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPCustomKeyboardAdapter
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter
import io.taptalk.TapTalk.View.Adapter.TapUserMentionListAdapter
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.Companion.newInstance
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType
import io.taptalk.TapTalk.View.BottomSheet.TapTimePickerBottomSheetFragment
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel.TAPChatViewModelFactory
import kotlinx.android.synthetic.main.tap_activity_chat.*
import kotlinx.android.synthetic.main.tap_layout_popup_loading_screen.*
import kotlinx.android.synthetic.main.tap_layout_popup_loading_screen.view.*
import kotlinx.android.synthetic.main.tap_layout_user_mention_list.*
import kotlinx.android.synthetic.main.tap_layout_user_mention_list.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class TapScheduledMessageActivity: TAPBaseActivity() {

    private val glide : RequestManager by lazy {
        Glide.with(this)
    }

    private lateinit var messageAdapter: TAPMessageAdapter
    private lateinit var customKeyboardAdapter: TAPCustomKeyboardAdapter
    private lateinit var userMentionListAdapter: TapUserMentionListAdapter
    private lateinit var messageLayoutManager: LinearLayoutManager
    private lateinit var messageAnimator: SimpleItemAnimator
    private lateinit var linkHandler: Handler
    private lateinit var linkRunnable: Runnable
    private lateinit var vm: TAPChatViewModel
    private var textMessage : String? = null
    private var textTime : Long? = null

    companion object {
        fun start(
            context: Activity,
            instanceKey: String?,
            room: TAPRoomModel
        ) {
            val intent = Intent(context, TapScheduledMessageActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            intent.putExtra(Extras.ROOM, room)
            context.startActivityForResult(intent, RequestCode.OPEN_SCHEDULED_MESSAGES)
        }

        fun start(
            context: Activity,
            instanceKey: String?,
            room: TAPRoomModel,
            message: String?,
            scheduledTime: Long?,
        ) {
            val intent = Intent(context, TapScheduledMessageActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            intent.putExtra(Extras.ROOM, room)
            intent.putExtra(MESSAGE, message)
            intent.putExtra(Extras.TIME, scheduledTime)
            context.startActivityForResult(intent, RequestCode.OPEN_SCHEDULED_MESSAGES)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_chat)
        tv_room_name.text = getString(R.string.tap_scheduled_message)
        cl_room_status.isVisible = false
        iv_voice_note.isVisible = false
        fl_connection_status.isVisible = false
        linkHandler = Handler(mainLooper)
        if (initViewModel())
            initView()
        registerBroadcastManager()
        TAPChatManager.getInstance(instanceKey).addChatListener(chatListener)
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener)
        textMessage = intent.getStringExtra(MESSAGE)
        textTime = intent.getLongExtra(Extras.TIME, -1)
    }

    override fun onResume() {
        super.onResume()
        getScheduledMessages()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (rv_custom_keyboard.isVisible) {
            hideKeyboards()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(this, broadcastReceiver)
        TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener)
        TAPConnectionManager.getInstance(instanceKey).removeSocketListener(socketListener)
        TAPFileDownloadManager.getInstance(instanceKey).clearFailedDownloads() // Remove failed download list from active room

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.DEFAULT)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.SEND_IMAGE_FROM_CAMERA -> {
                    if (null != data && null != data.data) {
                        vm.cameraImageUri = data.data
                    }
                    if (null == vm.cameraImageUri) {
                        return
                    }
                    val imageCameraUris = ArrayList<TAPMediaPreviewModel>()
                    imageCameraUris.add(
                        TAPMediaPreviewModel.Builder(
                            vm.cameraImageUri,
                            MessageType.TYPE_IMAGE,
                            true
                        )
                    )
                    openMediaPreviewPage(imageCameraUris)
                }
                RequestCode.SEND_MEDIA_FROM_GALLERY -> {
                    if (null == data) {
                        return
                    }
                    var galleryMediaPreviews = ArrayList<TAPMediaPreviewModel>()
                    val clipData = data.clipData
                    if (null != clipData) {
                        // Multiple media selection
                        galleryMediaPreviews =
                            TAPUtils.getPreviewsFromClipData(this@TapScheduledMessageActivity, clipData, true)
                    } else {
                        // Single media selection
                        val uri = data.data
                        galleryMediaPreviews.add(
                            TAPMediaPreviewModel.Builder(
                                uri,
                                TAPUtils.getMessageTypeFromFileUri(this@TapScheduledMessageActivity, uri),
                                true
                            )
                        )
                    }
                    openMediaPreviewPage(galleryMediaPreviews)
                }
                RequestCode.SEND_MEDIA_FROM_PREVIEW -> {
                    val medias = data?.getParcelableArrayListExtra<TAPMediaPreviewModel>(Extras.MEDIA_PREVIEWS)
                    if (null != medias && 0 < medias.size) {
                        val timePicker = TapTimePickerBottomSheetFragment(object : TAPGeneralListener<Long>() {
                            override fun onClick(position: Int, item: Long?) {
                                for (media in medias) {
                                    if (media.type == MessageType.TYPE_IMAGE) {
                                        try {
                                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, media.uri)
                                            if (bitmap != null) {
                                                TAPFileUploadManager.getInstance(instanceKey).createAndResizeImageFile(bitmap, IMAGE_MAX_DIMENSION, object : BitmapInterface {
                                                    override fun onBitmapReady(bitmap: Bitmap) {
                                                        val imagePath = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "", "")
                                                        if (imagePath != null && !imagePath.isEmpty()) {
                                                            val uri = Uri.parse(imagePath)
                                                            media.uri = uri
                                                        }
                                                        TAPChatManager.getInstance(instanceKey).createImageMessageModelAndAddToUploadQueue(this@TapScheduledMessageActivity, vm.room, media.uri, media.caption)
                                                    }

                                                    override fun onBitmapError() {
                                                        TAPChatManager.getInstance(instanceKey).createImageMessageModelAndAddToUploadQueue(this@TapScheduledMessageActivity, vm.room, media.uri, media.caption)
                                                    }
                                                })
                                            }
                                        } catch (e: IOException) {
                                            TAPChatManager.getInstance(instanceKey).createImageMessageModelAndAddToUploadQueue(this@TapScheduledMessageActivity, vm.room, media.uri, media.caption)
                                            e.printStackTrace()
                                        }
                                    } else if (media.type == MessageType.TYPE_VIDEO) {
                                        TAPChatManager.getInstance(instanceKey).createImageMessageModelAndAddToUploadQueue(this@TapScheduledMessageActivity, vm.room, media.uri, media.caption)
                                    }
                                }
                            }
                        })
                        timePicker.show(supportFragmentManager, "")
                    }
                }
                RequestCode.PICK_LOCATION -> {
                    val address =
                        if (data?.getStringExtra(Location.LOCATION_NAME) == null) "" else data.getStringExtra(
                            Location.LOCATION_NAME
                        )!!
                    val latitude = data?.getDoubleExtra(Location.LATITUDE, 0.0)
                    val longitude = data?.getDoubleExtra(Location.LONGITUDE, 0.0)
                    val timePicker = TapTimePickerBottomSheetFragment(object : TAPGeneralListener<Long>() {
                        override fun onClick(position: Int, item: Long?) {
                            super.onClick(position, item)
                            TAPChatManager.getInstance(instanceKey)
                                .sendLocationMessage(vm.room, address, latitude, longitude, item)
                        }
                    })
                    timePicker.show(supportFragmentManager, "")
                }
                RequestCode.SEND_FILE -> {
                    if (null == data) {
                        return
                    }
                    var tempFile: File? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        var uri: Uri? = null
                        if (null != data.clipData) {
                            for (i in 0 until data.clipData!!.itemCount) {
                                uri = data.clipData!!.getItemAt(i).uri
                            }
                        } else {
                            uri = data.data
                        }
                        if (uri != null) {
                            // Write temporary file to cache for upload for Android 11+
                            tempFile = TAPFileUtils.createTemporaryCachedFile(this, uri)
                        }
                    } else {
                        val filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
                        if (!filePath.isNullOrEmpty()) {
                            tempFile = File(filePath)
                        }
                    }

                    if (tempFile != null) {
                        if (TAPFileUploadManager.getInstance(instanceKey)
                                .isSizeAllowedForUpload(tempFile.length())
                        ) {
                            val timePicker = TapTimePickerBottomSheetFragment(object : TAPGeneralListener<Long>() {
                                override fun onClick(position: Int, item: Long?) {
                                    super.onClick(position, item)
                                    TAPChatManager.getInstance(instanceKey)
                                        .sendFileMessage(
                                            this@TapScheduledMessageActivity,
                                            vm.room,
                                            tempFile,
                                            item
                                        )
                                }
                            })
                            timePicker.show(supportFragmentManager, "")
                        } else {
                            TapTalkDialog.Builder(this@TapScheduledMessageActivity)
                                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                .setTitle(getString(R.string.tap_sorry))
                                .setMessage(
                                    String.format(
                                        getString(R.string.tap_format_s_maximum_file_size),
                                        TAPUtils.getStringSizeLengthFile(
                                            TAPFileUploadManager.getInstance(instanceKey).maxFileUploadSize
                                        )
                                    )
                                )
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .show()
                        }
                    }
                }
                RequestCode.OPEN_GROUP_PROFILE -> if (data != null) {
                    val messageModel = data.getParcelableExtra<TAPMessageModel>(MESSAGE)
                    if (messageModel != null) {
                        goToMessage(messageModel)
                    } else {
                        vm.isDeleteGroup = true
                        closeActivity()
                    }
                }
                RequestCode.OPEN_MEMBER_PROFILE -> if (data != null) {
                    val message = data.getParcelableExtra<TAPMessageModel>(MESSAGE)
                    if (message != null) {
                        goToMessage(message)
                    } else if (data.getBooleanExtra(Extras.CLOSE_ACTIVITY, false)) {
                        closeActivity()
                    }
                }
                RequestCode.OPEN_PERSONAL_PROFILE -> if (data != null) {
                    val message = data.getParcelableExtra<TAPMessageModel>(MESSAGE)
                    if (message != null) {
                        scrollToMessage(message.localID)
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PermissionRequest.PERMISSION_CAMERA_CAMERA, PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA -> vm.cameraImageUri =
                    TAPUtils.takePicture(
                        instanceKey,
                        this@TapScheduledMessageActivity,
                        RequestCode.SEND_IMAGE_FROM_CAMERA
                    )
                PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY -> TAPUtils.pickMediaFromGallery(
                    this@TapScheduledMessageActivity,
                    RequestCode.SEND_MEDIA_FROM_GALLERY,
                    true
                )
                PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE -> messageAdapter.notifyDataSetChanged()
                PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_FILE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON)
                        TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity, SEND_FILE)
                    } else {
                        TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity)
                    }
                }
                PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO -> attachmentListener.onSaveVideoToGallery(vm.pendingDownloadMessage)
                PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE -> startFileDownload(
                    vm.pendingDownloadMessage
                )
                PermissionRequest.PERMISSION_LOCATION -> TAPUtils.openLocationPicker(
                    this@TapScheduledMessageActivity,
                    instanceKey
                )
            }
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
            setOtherUserModel(
                TAPContactManager.getInstance(instanceKey).getUserData(vm.otherUserID)
            )
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        window.setBackgroundDrawable(null)

        // Set room name
        if (TAPUtils.isSavedMessagesRoom(vm.room.roomID, instanceKey)) {
            tv_room_name.setText(R.string.tap_saved_messages)
        } else if (vm.room.type == RoomType.TYPE_PERSONAL && null != vm.otherUserModel &&
            (null == vm.otherUserModel.deleted || vm.otherUserModel.deleted!! <= 0L) &&
            vm.otherUserModel.fullname.isNotEmpty()
        ) {
            tv_room_name.text = vm.otherUserModel.fullname
        } else {
            tv_room_name.text = vm.room.name
        }
        if (!TapUI.getInstance(instanceKey).isProfileButtonVisible) {
            civ_room_image.visibility = View.GONE
            v_room_image.visibility = View.GONE
            tv_room_image_label.visibility = View.GONE
        } else if (null != vm.room && RoomType.TYPE_PERSONAL == vm.room.type && null != vm.otherUserModel &&
            null != vm.otherUserModel.deleted && vm.otherUserModel.deleted!! > 0L
        ) {
            glide.load(R.drawable.tap_ic_deleted_user).fitCenter().into(civ_room_image)
            ImageViewCompat.setImageTintList(civ_room_image, null)
            tv_room_image_label.visibility = View.GONE
        } else if (null != vm.room && RoomType.TYPE_PERSONAL == vm.room.type && TAPUtils.isSavedMessagesRoom(vm.room.roomID, instanceKey)
        ) {
            glide.load(R.drawable.tap_ic_bookmark_round).fitCenter().into(civ_room_image)
            ImageViewCompat.setImageTintList(civ_room_image, null)
            tv_room_image_label.visibility = View.GONE
        } else if (null != vm.room && RoomType.TYPE_PERSONAL == vm.room
                .type && null != vm.otherUserModel && null != vm.otherUserModel
                .imageURL.thumbnail &&
            vm.otherUserModel.imageURL.thumbnail.isNotEmpty()
        ) {
            // Load user avatar URL
            loadProfilePicture(
                vm.otherUserModel.imageURL.thumbnail,
                civ_room_image,
                tv_room_image_label
            )
            vm.room.imageURL = vm.otherUserModel.imageURL
        } else if (null != vm.room && !vm.room.isDeleted && null != vm.room
                .imageURL && vm.room.imageURL!!.thumbnail.isNotEmpty()
        ) {
            // Load room image
            loadProfilePicture(
                vm.room.imageURL!!.thumbnail,
                civ_room_image,
                tv_room_image_label
            )
        } else {
            loadInitialsToProfilePicture(civ_room_image, tv_room_image_label)
        }

        // TODO: 1 February 2019 SET ROOM ICON FROM ROOM MODEL
        if (null != vm.otherUserModel && null != vm.otherUserModel
                .userRole && null != vm.otherUserModel.userRole!!
                .iconURL && null != vm.room && RoomType.TYPE_PERSONAL == vm.room
                .type &&
            vm.otherUserModel.userRole!!.iconURL.isNotEmpty()
        ) {
            glide.load(vm.otherUserModel.userRole!!.iconURL).into(iv_room_icon)
            iv_room_icon.visibility = View.VISIBLE
        } else {
            iv_room_icon.visibility = View.GONE
        }

//        // Set typing status
//        if (getIntent().getBooleanExtra(IS_TYPING, false)) {
//            vm.setOtherUserTyping(true);
//            showTypingIndicator();
//        }
        if (null != intent.getStringExtra(Extras.GROUP_TYPING_MAP)) {
            try {
                val tempGroupTyping = intent.getStringExtra(Extras.GROUP_TYPING_MAP)
                val gson = Gson()
                val typingType =
                    object : TypeToken<LinkedHashMap<String?, TAPUserModel?>?>() {}.type
                vm.groupTyping = gson.fromJson<LinkedHashMap<String, TAPUserModel>>(
                    tempGroupTyping,
                    typingType
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Initialize chat message RecyclerView
        messageAdapter = TAPMessageAdapter(instanceKey, glide, chatListener, vm)
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
        rv_message_list.instanceKey = instanceKey
        rv_message_list.adapter = messageAdapter
        rv_message_list.layoutManager = messageLayoutManager
        rv_message_list.setHasFixedSize(false)
        // FIXME: 9 November 2018 IMAGES/VIDEOS CURRENTLY NOT RECYCLED TO PREVENT INCONSISTENT DIMENSIONS
        rv_message_list.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_IMAGE_LEFT, 0)
        rv_message_list.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_IMAGE_RIGHT, 0)
        rv_message_list.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_VIDEO_LEFT, 0)
        rv_message_list.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_VIDEO_RIGHT, 0)
        rv_message_list.recycledViewPool
            .setMaxRecycledViews(BubbleType.TYPE_BUBBLE_PRODUCT_LIST, 0)
        messageAnimator = rv_message_list.itemAnimator as SimpleItemAnimator
        messageAnimator.supportsChangeAnimations = false
        rv_message_list.itemAnimator = null
        rv_message_list.addOnScrollListener(messageListScrollListener)
        //        OverScrollDecoratorHelper.setUpOverScroll(rv_message_list, OverScrollDecoratorHelper.ORIENTATION_VERTICAL); FIXME: 8 Apr 2020 DISABLED OVERSCROLL DECORATOR
        // TODO: disable swipe message MU
        // Initialize custom keyboard
        vm.customKeyboardItems = TAPChatManager.getInstance(instanceKey)
            .getCustomKeyboardItems(vm.room, vm.myUserModel, vm.otherUserModel)
        if (null != vm.customKeyboardItems && vm.customKeyboardItems.size > 0) {
            // Enable custom keyboard
            vm.isCustomKeyboardEnabled = true
            customKeyboardAdapter = TAPCustomKeyboardAdapter(
                vm.customKeyboardItems
            ) { customKeyboardItemModel: TAPCustomKeyboardItemModel? ->
                TAPChatManager.getInstance(instanceKey).triggerCustomKeyboardItemTapped(
                    this@TapScheduledMessageActivity,
                    customKeyboardItemModel,
                    vm.room,
                    vm.myUserModel,
                    vm.otherUserModel
                )
            }
            rv_custom_keyboard.adapter = customKeyboardAdapter
            rv_custom_keyboard.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
            iv_chat_menu_area.setOnClickListener{ toggleCustomKeyboard() }
        } else {
            // Disable custom keyboard
            vm.isCustomKeyboardEnabled = false
            iv_chat_menu.visibility = View.GONE
            iv_chat_menu_area.visibility = View.GONE
        }
        showAttachmentButton()
        
        val containerTransition: LayoutTransition = cl_container.layoutTransition
        containerTransition.addTransitionListener(containerTransitionListener)
        et_chat.addTextChangedListener(chatWatcher)
        et_chat.onFocusChangeListener = chatFocusChangeListener

        v_room_image.setOnClickListener { openRoomProfile() }
        iv_button_back.setOnClickListener { closeActivity() }
        iv_cancel_reply.setOnClickListener { hideQuoteLayout() }
        iv_attach.setOnClickListener { openAttachMenu() }
        iv_send_area.setOnClickListener {
            hideKeyboards()
            if (vm.quoteAction != null && vm.quotedMessage != null) {
                buildAndSendTextOrLinkMessage(et_chat.text.toString().trim { it <= ' ' }, vm.quotedMessage.created)
            } else {
                val timePicker = TapTimePickerBottomSheetFragment(object : TAPGeneralListener<Long>() {
                    override fun onClick(position: Int, item: Long?) {
                        super.onClick(position, item)
                        buildAndSendTextOrLinkMessage(et_chat.text.toString().trim { it <= ' ' }, item)
                    }
                })
                timePicker.show(supportFragmentManager, "")
            }
        }
        iv_to_bottom.setOnClickListener { scrollToBottom() }
        fl_message_list.setOnClickListener {
            chatListener.onOutsideClicked(TAPMessageModel())
        }
        fl_loading.setOnClickListener { }
        // TODO: 05/10/22 handle schedule message icon MU
        if (TapUI.getInstance(instanceKey).isLinkPreviewInMessageEnabled) {
            iv_close_link.setOnClickListener {
                vm.linkHashMap.remove(MessageData.TITLE)
                vm.linkHashMap.remove(MessageData.DESCRIPTION)
                vm.linkHashMap.remove(MessageData.IMAGE)
                vm.linkHashMap.remove(MessageData.TYPE)
                hideLinkPreview(false)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv_attach.background = getDrawable(R.drawable.tap_bg_chat_composer_attachment_ripple)
            iv_to_bottom.background = getDrawable(R.drawable.tap_bg_scroll_to_bottom_ripple)
        }
    }

    private fun showAttachmentButton() {
        // Show / hide attachment button
        if (TapUI.getInstance(instanceKey).isDocumentAttachmentDisabled &&
            TapUI.getInstance(instanceKey).isCameraAttachmentDisabled &&
            TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled &&
            TapUI.getInstance(instanceKey).isLocationAttachmentDisabled
        ) {
            iv_attach.visibility = View.GONE
            et_chat.setPadding(
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(6),
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(6)
            )
        } else {
            iv_attach.visibility = View.VISIBLE
            et_chat.setPadding(
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(6),
                TAPUtils.dpToPx(48),
                TAPUtils.dpToPx(6)
            )
        }
        iv_schedule.visibility = View.GONE
    }

    private fun registerBroadcastManager() {
        TAPBroadcastManager.register(
            this, broadcastReceiver,
            UploadBroadcastEvent.UploadProgressLoading,
            UploadBroadcastEvent.UploadProgressFinish,
            UploadBroadcastEvent.UploadFailed,
            UploadBroadcastEvent.UploadCancelled,
            DownloadBroadcastEvent.DownloadProgressLoading,
            DownloadBroadcastEvent.DownloadFinish,
            DownloadBroadcastEvent.DownloadFailed,
            DownloadBroadcastEvent.DownloadFile,
            DownloadBroadcastEvent.OpenFile,
            DownloadBroadcastEvent.CancelDownload,
            DownloadBroadcastEvent.PlayPauseVoiceNote,
            LongPressBroadcastEvent.LongPressChatBubble,
            LongPressBroadcastEvent.LongPressEmail,
            LongPressBroadcastEvent.LongPressLink,
            LongPressBroadcastEvent.LongPressPhone,
            LongPressBroadcastEvent.LongPressMention
        )
    }

    private fun showLoadingOlderMessagesIndicator() {
        hideLoadingOlderMessagesIndicator()
        rv_message_list.post {
            runOnUiThread { // Add loading indicator to last index
                val position = if (messageAdapter.itemCount > 0) {
                    messageAdapter.itemCount - 1
                } else {
                    0
                }
                vm.addMessagePointer(vm.getLoadingIndicator(true))
                messageAdapter.addItem(vm.getLoadingIndicator(false))
                messageAdapter.notifyItemInserted(position)
            }
        }
    }

    private fun hideLoadingOlderMessagesIndicator() {
        rv_message_list.post {
            runOnUiThread {
                val loadingIndicator = vm.getLoadingIndicator(false)
                if (messageAdapter.items != null && !messageAdapter.items.contains(loadingIndicator)) {
                    return@runOnUiThread
                }
                val index = if (messageAdapter.items == null) {
                    0
                } else {
                    messageAdapter.items.indexOf(loadingIndicator)
                }
                vm.removeMessagePointer(LOADING_INDICATOR_LOCAL_ID)
                if (index >= 0 && index < messageAdapter.itemCount && messageAdapter.getItemAt(index)
                        .type == MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER
                ) {
                    messageAdapter.removeMessage(loadingIndicator)
                    messageAdapter.setMessages(vm.messageModels)
                    if (null != messageAdapter.getItemAt(index)) {
                        messageAdapter.notifyItemChanged(index)
                    } else {
                        messageAdapter.notifyItemRemoved(index)
                    }
                    updateMessageDecoration()
                }
            }
        }
    }

    private fun getScheduledMessages() {
        TAPDataManager.getInstance(instanceKey).getScheduledMessages(vm.room.roomID, getScheduledMessagesView)
    }

    private val getScheduledMessagesView = object : TAPDefaultDataView<TapGetScheduledMessageListResponse>() {
        override fun startLoading() {
            super.startLoading()
            showMessageList()
            showLoadingOlderMessagesIndicator()
        }

        override fun endLoading() {
            super.endLoading()
            hideLoadingOlderMessagesIndicator()
        }

        override fun onSuccess(response: TapGetScheduledMessageListResponse?) {
            super.onSuccess(response)
            if (response?.items?.isNotEmpty() == true) {
                messageAdapter.clearItems()
                vm.dateSeparators.clear()
                var previousModel : TapScheduledMessageModel? = null

                for (scheduledMessage in response.items) {
                    if (scheduledMessage.message != null) {
                        val message = TAPEncryptorManager.getInstance().decryptMessage(scheduledMessage.message)
                        // hide sending icon for scheduled message page only
                        message.isSending = false
                        message.messageID = scheduledMessage.id.toString()
                        // update status text
                        if (scheduledMessage.scheduledTime != null) {
                            message.messageStatusText = TAPTimeFormatter.formatClock(scheduledMessage.scheduledTime)
                            message.created = scheduledMessage.scheduledTime
                        }
                        // add date separator
                        if (previousModel == null || TAPTimeFormatter.formatDate(previousModel.scheduledTime!!) != TAPTimeFormatter.formatDate(scheduledMessage.scheduledTime!!)) {
                            val dateSeparator = generateDateSeparator(scheduledMessage.scheduledTime)
                            vm.dateSeparators[dateSeparator.body] = dateSeparator
                            vm.dateSeparatorIndexes[dateSeparator.body] = messageAdapter.items.indexOf(previousModel?.message)
                            messageAdapter.addMessage(dateSeparator)
                        }
                        messageAdapter.addMessage(message)
                        previousModel = TapScheduledMessageModel(scheduledMessage)
                    }
                }
                if (TAPChatManager.getInstance(instanceKey).pendingScheduledMessages.isNotEmpty()) {
                    for (message in TAPChatManager.getInstance(instanceKey).pendingScheduledMessages) {
                        if (message.message?.room?.roomID == vm.room.roomID && message != null) {
                            if (!messageAdapter.items.contains(message.message)) {
                                addNewMessage(message.message!!, message.scheduledTime!!)
                            }
                        }
                    }
                }
                runOnUiThread {
                    if (ll_empty_scheduled_message.visibility == View.VISIBLE) {
                        ll_empty_scheduled_message.visibility = View.GONE
                    }
                    showMessageList()
                    updateMessageDecoration()
                    messageAdapter.notifyDataSetChanged()
                }
            } else {
                runOnUiThread {
                    if (ll_empty_scheduled_message.visibility == View.GONE) {
                        ll_empty_scheduled_message.visibility = View.VISIBLE
                    }
                    hideMessageList()
                }
            }

            if (textMessage != null && textTime != null) {
                buildAndSendTextOrLinkMessage(textMessage, textTime)
                textMessage = null
                textTime = null
            }
            if (!vm.isInitialAPICallFinished) {
                vm.isInitialAPICallFinished = true
            }
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            hideLoadingOlderMessagesIndicator()
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            hideLoadingOlderMessagesIndicator()
        }
    }

    private val chatListener: TAPChatListener = object : TAPChatListener() {

        override fun onCreateScheduledMessage(scheduledMessage: TapScheduledMessageModel?) {
            super.onCreateScheduledMessage(scheduledMessage)
            if (null == vm.room || null == scheduledMessage?.message || scheduledMessage.message.room.roomID != vm.room.roomID) {
                return
            }
            runOnUiThread {
                if (scheduledMessage.scheduledTime != null) {
                    addNewMessage(scheduledMessage.message, scheduledMessage.scheduledTime)
                }
                hideQuoteLayout()
            }
        }

        override fun onGetScheduledMessageList() {
            super.onGetScheduledMessageList()
            getScheduledMessages()
        }

        override fun onRetrySendMessage(message: TAPMessageModel) {
            if (null == vm.room || message.room.roomID != vm.room.roomID) {
                return
            }
            vm.delete(message.localID)
            if (message.type == MessageType.TYPE_IMAGE || message.type == MessageType.TYPE_VIDEO || message.type == MessageType.TYPE_FILE || message.type == MessageType.TYPE_VOICE) {
                if (null != message.data && null != message.data!![MessageData.FILE_ID] && null != message.data!![FILE_URL] &&
                    (message.data!![MessageData.FILE_ID] as String?)!!.isNotEmpty() &&
                    (message.data!![FILE_URL] as String?)!!.isNotEmpty()
                ) {
                    // Resend message
                    TAPChatManager.getInstance(instanceKey).resendMessage(message)
                } else if (null != message.data &&
                    null != message.data!![MessageData.FILE_URI] &&
                    (message.data!![MessageData.FILE_URI] as String?)!!.isNotEmpty()
                ) {
                    // Re-upload image/video
                    TAPChatManager.getInstance(instanceKey)
                        .retryUpload(this@TapScheduledMessageActivity, message)
                } else {
                    // Data not found
                    Toast.makeText(
                        this@TapScheduledMessageActivity,
                        getString(R.string.tap_error_resend_media_data_not_found),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                // Resend message
                TAPChatManager.getInstance(instanceKey).resendMessage(message)
            }
        }

        override fun onSendFailed(message: TAPMessageModel) {
            if (null == vm.room || message.room.roomID != vm.room.roomID) {
                return
            }
            vm.updateMessagePointer(message)
            vm.removeMessagePointer(message.localID)
            runOnUiThread {
                messageAdapter.notifyItemRangeChanged(0, messageAdapter.getItemCount())
            }
        }

        override fun onMentionClicked(message: TAPMessageModel, username: String) {
            if (null == vm.room || message.room.roomID != vm.room.roomID) {
                return
            }
            val participant = vm.roomParticipantsByUsername[username]
            if (null != participant) {
                TAPChatManager.getInstance(instanceKey)
                    .triggerUserMentionTapped(this@TapScheduledMessageActivity, message, participant, true)
            } else {
                val user =
                    TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username)
                if (null != user) {
                    TAPChatManager.getInstance(instanceKey)
                        .triggerUserMentionTapped(this@TapScheduledMessageActivity, message, user, false)
                } else {
//                    callApiGetUserByUsername(username, message)
                }
            }
        }

        override fun onMessageQuoteClicked(message: TAPMessageModel) {
            if (null == vm.room || message.room.roomID != vm.room.roomID) {
                return
            }
            TAPChatManager.getInstance(instanceKey)
                .triggerMessageQuoteTapped(this@TapScheduledMessageActivity, message)
            if (((null != message.replyTo) && (
                        null != message.replyTo!!.localID) &&
                        !message.replyTo!!.localID.isEmpty() && (
                        message.replyTo!!.messageType != -1) && 
                        (((null == message.forwardFrom) || (
                                null == message.forwardFrom!!.fullname) ||
                                message.forwardFrom!!.fullname.isEmpty())))
            ) {
                scrollToMessage(message.replyTo!!.localID)
            }
        }

        override fun onOutsideClicked(message: TAPMessageModel) {
            hideKeyboards()
        }

        override fun onBubbleExpanded() {
            if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                rv_message_list.smoothScrollToPosition(0)
            }
        }

        override fun onLayoutLoaded(message: TAPMessageModel) {
            if ( /*message.getUser().getUserID().equals(vm.getMyUserModel().getUserID()) ||*/messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                // Scroll recycler to bottom when image finished loading if message is sent by user or recycler is on bottom
                rv_message_list.smoothScrollToPosition(0)
            }
        }

        override fun onRequestUserData(message: TAPMessageModel) {
            super.onRequestUserData(message)
            if (message.forwardFrom != null) {
                TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(
                    message.forwardFrom!!.userID,
                    object : TAPDefaultDataView<TAPGetUserResponse?>() {
                        fun onSuccess(response: TAPGetUserResponse) {
                            super.onSuccess(response)
                            TAPContactManager.getInstance(instanceKey).updateUserData(response.user)
                            messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(vm.messagePointer[message.localID]))
                        }
                    })
            }
        }
    }


    private fun closeActivity() {
        rv_custom_keyboard.visibility = View.GONE
        onBackPressed()
    }


    private fun openRoomProfile() {
        TAPChatManager.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(
            this@TapScheduledMessageActivity,
            vm.getRoom(),
            vm.getOtherUserModel()
        )
    }

    private fun openGroupMemberProfile(groupMember: TAPUserModel) {
        TAPChatManager.getInstance(instanceKey)
            .triggerChatRoomProfileButtonTapped(this@TapScheduledMessageActivity, vm.getRoom(), groupMember)
    }

    private fun loadProfilePicture(image: String, imageView: ImageView, tvAvatarLabel: TextView) {
        if (imageView.visibility == View.GONE) {
            return
        }
        glide.load(image).listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable?>,
                isFirstResource: Boolean
            ): Boolean {
                runOnUiThread {
                    loadInitialsToProfilePicture(
                        imageView,
                        tvAvatarLabel
                    )
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any,
                target: Target<Drawable?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                ImageViewCompat.setImageTintList(imageView, null)
                tvAvatarLabel.visibility = View.GONE
                return false
            }
        }).into(imageView)
    }

    private fun loadInitialsToProfilePicture(imageView: ImageView, tvAvatarLabel: TextView) {
        if (imageView.visibility == View.GONE) {
            return
        }
        if (tvAvatarLabel === tv_my_avatar_label_empty) {
            ImageViewCompat.setImageTintList(
                imageView,
                ColorStateList.valueOf(
                    TAPUtils.getRandomColor(
                        this,
                        TAPChatManager.getInstance(instanceKey).activeUser.fullname
                    )
                )
            )
            tvAvatarLabel.text =
                TAPUtils.getInitials(TAPChatManager.getInstance(instanceKey).activeUser.fullname, 2)
        } else {
            ImageViewCompat.setImageTintList(
                imageView,
                ColorStateList.valueOf(TAPUtils.getRandomColor(this, vm.room.name))
            )
            tvAvatarLabel.text = TAPUtils.getInitials(
                vm.room.name,
                if (vm.room.type == RoomType.TYPE_PERSONAL) 2 else 1
            )
        }
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                this@TapScheduledMessageActivity,
                R.drawable.tap_bg_circle_9b9b9b
            )
        )
        tvAvatarLabel.visibility = View.VISIBLE
    }

    private fun showMessageList() {
        fl_message_list.visibility = View.VISIBLE
    }

    private fun hideMessageList() {
        fl_message_list.visibility = View.GONE
    }

    private fun updateMessageDecoration() {
        // Update decoration for the top item in recycler view
        runOnUiThread {
            if (rv_message_list.itemDecorationCount > 0) {
                rv_message_list.removeItemDecorationAt(0)
            }
            rv_message_list.addItemDecoration(
                TAPVerticalDecoration(
                    TAPUtils.dpToPx(10),
                    0,
                    messageAdapter.itemCount - 1
                )
            )
        }
    }

    private fun showEditLayout(message: TAPMessageModel?) {
        if (null == message) {
            return
        }
        vm.setQuotedMessage(message, QuoteAction.EDIT)
        runOnUiThread {
            cl_quote_layout.visibility = View.VISIBLE
            // Add other quotable message type here
            if ((message.type == MessageType.TYPE_IMAGE || message.type == MessageType.TYPE_VIDEO) && null != message.data) {
                // Show image quote
                v_quote_layout_decoration.setVisibility(View.GONE)
                // TODO: 29 January 2019 IMAGE MIGHT NOT EXIST IN CACHE
                val drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(message)
//                var drawable: Drawable? = null
//                val fileID =
//                    message.data!![MessageData.FILE_ID] as String?
//                if (null != fileID && fileID.isNotEmpty()) {
//                    drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(fileID)
//                }
//                if (null == drawable) {
//                    val fileUrl =
//                        message.data!![MessageData.FILE_URL] as String?
//                    if (null != fileUrl && fileUrl.isNotEmpty()) {
//                        drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(
//                            TAPUtils.removeNonAlphaNumeric(fileUrl)
//                                .lowercase(Locale.getDefault())
//                        )
//                    }
//                }
                if (null != drawable) {
                    rciv_quote_layout_image.setImageDrawable(drawable)
                } else {
                    // Show small thumbnail
                    val thumbnail: Drawable = BitmapDrawable(
                        resources,
                        TAPFileUtils.decodeBase64(
                            (if (null == message.data!![MessageData.THUMBNAIL]
                            ) "" else message.data!![MessageData.THUMBNAIL]) as String?
                        )
                    )
                    rciv_quote_layout_image.setImageDrawable(thumbnail)
                }
                rciv_quote_layout_image.colorFilter = null
                rciv_quote_layout_image.background = null
                rciv_quote_layout_image.scaleType = ImageView.ScaleType.CENTER_CROP
                rciv_quote_layout_image.visibility = View.VISIBLE
                tv_quote_layout_title.setText(R.string.tap_edit_message)
                tv_quote_layout_content.text = message.body
                tv_quote_layout_content.maxLines = 1
                et_chat.filters = arrayOf<InputFilter>(
                    LengthFilter(
                        TapTalk.getMaxCaptionLength(
                            instanceKey
                        )
                    )
                )
                et_chat.setText(message.data!![MessageData.CAPTION].toString())
            } else if (null != message.data && null != message.data!![FILE_URL] && (message.type == MessageType.TYPE_IMAGE || message.type == MessageType.TYPE_VIDEO)
            ) {
                // Show image quote from file URL
                glide.load(message.data!![FILE_URL] as String?)
                    .into(rciv_quote_layout_image)
                rciv_quote_layout_image.colorFilter = null
                rciv_quote_layout_image.background = null
                rciv_quote_layout_image.scaleType = ImageView.ScaleType.CENTER_CROP
                rciv_quote_layout_image.visibility = View.VISIBLE
                v_quote_layout_decoration.visibility = View.GONE
                tv_quote_layout_title.setText(R.string.tap_edit_message)
                tv_quote_layout_content.text = message.body
                tv_quote_layout_content.maxLines = 1
                et_chat.filters = arrayOf<InputFilter>(
                    LengthFilter(
                        TapTalk.getMaxCaptionLength(
                            instanceKey
                        )
                    )
                )
                et_chat.setText(message.data!![MessageData.CAPTION].toString())
            } else if (null != message.data && null != message.data!![MessageData.IMAGE_URL]
            ) {
                // Show image quote from image URL
                glide.load(message.data!![MessageData.IMAGE_URL] as String?)
                    .into(rciv_quote_layout_image)
                rciv_quote_layout_image.colorFilter = null
                rciv_quote_layout_image.background = null
                rciv_quote_layout_image.scaleType = ImageView.ScaleType.CENTER_CROP
                rciv_quote_layout_image.visibility = View.VISIBLE
                v_quote_layout_decoration.visibility = View.GONE
                tv_quote_layout_title.setText(R.string.tap_edit_message)
                tv_quote_layout_content.text = message.body
                tv_quote_layout_content.maxLines = 1
                et_chat.filters = arrayOf<InputFilter>(
                    LengthFilter(
                        TapTalk.getMaxCaptionLength(
                            instanceKey
                        )
                    )
                )
                et_chat.setText(message.data!![MessageData.CAPTION].toString())
            } else {
                // Show text quote
                v_quote_layout_decoration.visibility = View.VISIBLE
                rciv_quote_layout_image.visibility = View.GONE
                tv_quote_layout_title.setText(R.string.tap_edit_message)
                tv_quote_layout_content.text = message.body
                tv_quote_layout_content.maxLines = 2
                et_chat.filters = arrayOf<InputFilter>(LengthFilter(CHARACTER_LIMIT))
                et_chat.setText(message.body)
            }
            val hadFocus: Boolean = et_chat.hasFocus()
            TAPUtils.showKeyboard(this, et_chat)
            Handler(mainLooper).postDelayed({ et_chat.requestFocus() }, 300L)
            if (!hadFocus && et_chat.selectionEnd == 0) {
                et_chat.setSelection(et_chat.text.length)
            }
        }
    }

    private fun hideQuoteLayout() {
        et_chat.filters = arrayOf<InputFilter>()
        et_chat.setText("")
        vm.setQuotedMessage(null, 0)
        vm.setForwardedMessages(null, 0)
        val hasFocus: Boolean = et_chat.hasFocus()
        if (cl_quote_layout.visibility == View.VISIBLE) {
            runOnUiThread {
                cl_quote_layout.visibility = View.GONE
                if (hasFocus) {
                    cl_quote_layout.post{ et_chat.requestFocus() }
                }
            }
        }
    }


    private fun scrollToBottom() {
        iv_to_bottom.setVisibility(View.GONE)
        rv_message_list.scrollToPosition(0)
        vm.isOnBottom = true
        vm.clearUnreadMessages()
        vm.clearUnreadMentions()
    }

    private fun toggleCustomKeyboard() {
        if (rv_custom_keyboard.visibility == View.VISIBLE) {
            showNormalKeyboard()
        } else {
            showCustomKeyboard()
        }
    }

    private fun showNormalKeyboard() {
        runOnUiThread {
            rv_custom_keyboard.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_chat_menu_area.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple))
            } else {
                iv_chat_menu_area.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.tap_bg_chat_composer_burger_menu
                    )
                )
            }
            iv_chat_menu.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_ic_burger_white
                )
            )
            iv_chat_menu.setColorFilter(
                ContextCompat.getColor(
                    TapTalk.appContext,
                    R.color.tapIconChatComposerBurgerMenu
                )
            )
            TAPUtils.showKeyboard(this, et_chat)
        }
    }

    private fun showCustomKeyboard() {
        runOnUiThread {
            TAPUtils.dismissKeyboard(this)
            et_chat.clearFocus()
            Handler().postDelayed({
                rv_custom_keyboard.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iv_chat_menu_area.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_show_keyboard_ripple))
                } else {
                    iv_chat_menu_area.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.tap_bg_chat_composer_show_keyboard
                        )
                    )
                }
                iv_chat_menu.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@TapScheduledMessageActivity,
                        R.drawable.tap_ic_keyboard_white
                    )
                )
                iv_chat_menu.setColorFilter(
                    ContextCompat.getColor(
                        TapTalk.appContext,
                        R.color.tapIconChatComposerShowKeyboard
                    )
                )
            }, 150L)
        }
    }

    private fun hideKeyboards() {
        runOnUiThread {
            rv_custom_keyboard.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_chat_menu_area.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple))
            } else {
                iv_chat_menu_area.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.tap_bg_chat_composer_burger_menu
                    )
                )
            }
            iv_chat_menu.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_ic_burger_white
                )
            )
            iv_chat_menu.setColorFilter(
                ContextCompat.getColor(
                    TapTalk.appContext,
                    R.color.tapIconChatComposerBurgerMenu
                )
            )
            TAPUtils.dismissKeyboard(this)
        }
    }


    private fun openAttachMenu() {
        TAPUtils.dismissKeyboard(this)
        val attachBottomSheet = TAPAttachmentBottomSheet(instanceKey, attachmentListener)
        attachBottomSheet.show(supportFragmentManager, "")
    }

    private fun handleLongPressMenuSelected(
        message: TAPMessageModel?,
        longPressMenuItem: TapLongPressMenuItem
    ) {
        var data: String? = ""
        if (longPressMenuItem.userInfo != null && longPressMenuItem.userInfo[DATA] != null) {
            data = longPressMenuItem.userInfo[DATA] as String?
        }
        when (longPressMenuItem.id) {
            LongPressMenuID.REPLY -> if (message != null) {
                attachmentListener.onReplySelected(message)
            }
            LongPressMenuID.FORWARD -> if (message != null) {
                attachmentListener.onForwardSelected(message)
            }
            LongPressMenuID.COPY -> if (data!!.isNotEmpty()) {
                attachmentListener.onCopySelected(data)
            } else if (message != null) {
                when (TapUI.getInstance(instanceKey).getLongPressMenuForMessageType(message.type)) {
                    LongPressMenuType.TYPE_IMAGE_MESSAGE,
                    LongPressMenuType.TYPE_VIDEO_MESSAGE,
                    LongPressMenuType.TYPE_FILE_MESSAGE,
                    LongPressMenuType.TYPE_VOICE_MESSAGE -> {
                        // TODO: 4 March 2019 TEMPORARY CLIPBOARD FOR IMAGE & VIDEO
                        if (null != message.data && message.data!![MessageData.CAPTION] is String) {
                            attachmentListener.onCopySelected(message.data!![MessageData.CAPTION] as String?)
                        }
                    }
                    LongPressMenuType.TYPE_LOCATION_MESSAGE -> {
                        if (null != message.data && message.data!![MessageData.ADDRESS] is String) {
                            attachmentListener.onCopySelected(message.data!![MessageData.ADDRESS] as String?)
                        }
                    }
                    else -> attachmentListener.onCopySelected(message.body)
                }
            }
            LongPressMenuID.SAVE -> if (message != null) {
                when (TapUI.getInstance(instanceKey).getLongPressMenuForMessageType(message.type)) {
                    LongPressMenuType.TYPE_IMAGE_MESSAGE -> attachmentListener.onSaveImageToGallery(
                        message
                    )
                    LongPressMenuType.TYPE_VIDEO_MESSAGE -> attachmentListener.onSaveVideoToGallery(
                        message
                    )
                    else -> attachmentListener.onSaveToDownloads(message)
                }
            }
            LongPressMenuID.STAR, LongPressMenuID.UNSTAR -> if (message != null) {
                attachmentListener.onMessageStarred(message)
            }
            LongPressMenuID.EDIT -> if (message != null) {
                attachmentListener.onEditMessage(message)
            }
            LongPressMenuID.PIN, LongPressMenuID.UNPIN -> if (message != null) {
                attachmentListener.onMessagePinned(message)
            }
            LongPressMenuID.INFO -> if (message != null) {
                attachmentListener.onViewMessageInfo(message)
            }
            LongPressMenuID.DELETE -> if (message != null) {
                attachmentListener.onDeleteMessage(vm.room.roomID, message)
            }
            LongPressMenuID.REPORT -> if (message != null) {
                attachmentListener.onReportMessage(message)
            }
            LongPressMenuID.OPEN_LINK -> if (data!!.isNotEmpty()) {
                attachmentListener.onOpenLinkSelected(data)
            }
            LongPressMenuID.COMPOSE -> if (data!!.isNotEmpty()) {
                attachmentListener.onComposeSelected(data)
            }
            LongPressMenuID.CALL -> if (data!!.isNotEmpty()) {
                attachmentListener.onPhoneCallSelected(data)
            }
            LongPressMenuID.SMS -> if (data!!.isNotEmpty()) {
                attachmentListener.onPhoneSmsSelected(data)
            }
            LongPressMenuID.VIEW_PROFILE -> if (data!!.isNotEmpty() && message != null) {
                attachmentListener.onViewProfileSelected(data, message)
            }
            LongPressMenuID.SEND_MESSAGE -> if (data!!.isNotEmpty()) {
                attachmentListener.onSendMessageSelected(data)
            }
            LongPressMenuID.SEND_NOW -> if (message != null) {
                attachmentListener.onSendNow(message)
            }
            LongPressMenuID.RESCHEDULE -> if (message != null) {
                attachmentListener.onRescheduleMessage(message)
            }
        }
    }

    private val longPressListener = TapLongPressInterface { longPressMenuItem: TapLongPressMenuItem?, messageModel: TAPMessageModel? ->
        handleLongPressMenuSelected(messageModel, longPressMenuItem!!)
    }

    private val attachmentListener: TAPAttachmentListener =
        object : TAPAttachmentListener(instanceKey) {
            override fun onCameraSelected() {
                vm.cameraImageUri = TAPUtils.takePicture(
                    instanceKey,
                    this@TapScheduledMessageActivity,
                    RequestCode.SEND_IMAGE_FROM_CAMERA
                )
            }

            override fun onGallerySelected() {
                TAPUtils.pickMediaFromGallery(
                    this@TapScheduledMessageActivity,
                    RequestCode.SEND_MEDIA_FROM_GALLERY,
                    true
                )
            }

            override fun onLocationSelected() {
                TAPUtils.openLocationPicker(this@TapScheduledMessageActivity, instanceKey)
            }

            override fun onDocumentSelected() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON)
                    TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity, SEND_FILE)
                } else {
                    TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity)
                }
            }

            override fun onCopySelected(text: String) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(text, text)
                clipboard.setPrimaryClip(clip)
            }

            override fun onOpenLinkSelected(url: String) {
                TAPUtils.openUrl(instanceKey, this@TapScheduledMessageActivity, url)
            }

            override fun onComposeSelected(emailRecipient: String) {
                TAPUtils.composeEmail(this@TapScheduledMessageActivity, emailRecipient)
            }

            override fun onPhoneCallSelected(phoneNumber: String) {
                TAPUtils.openDialNumber(this@TapScheduledMessageActivity, phoneNumber)
            }

            override fun onPhoneSmsSelected(phoneNumber: String) {
                if (!TAPUtils.composeSMS(this@TapScheduledMessageActivity, phoneNumber)) {
                    Toast.makeText(this@TapScheduledMessageActivity, R.string.error_unable_to_send_sms, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSaveImageToGallery(message: TAPMessageModel) {
                if (!TAPUtils.hasPermissions(
                        this@TapScheduledMessageActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    // Request storage permission
                    vm.pendingDownloadMessage = message
                    ActivityCompat.requestPermissions(
                        this@TapScheduledMessageActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE
                    )
                } else if (null != message.data && null != message.data!![MessageData.MEDIA_TYPE]) {
                    Thread {
                        vm.pendingDownloadMessage = null
                        var bitmap: Bitmap? = TAPCacheManager.getInstance(this@TapScheduledMessageActivity).getBitmapDrawable(message)?.bitmap
                        if (bitmap == null) {
                            val fileUrl = message.data!![FILE_URL] as String?
                            // Get bitmap from url
                            try {
                                val imageUrl = URL(fileUrl)
                                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                            } catch (e: MalformedURLException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
//                        var bitmap: Bitmap? = null
//                        val fileID =
//                            message.data!![MessageData.FILE_ID] as String?
//                        val fileUrl =
//                            message.data!![MessageData.FILE_URL] as String?
//                        if (null != fileID && fileID.isNotEmpty()) {
//                            // Get bitmap from cache
//                            bitmap =
//                                TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable(
//                                    message.data!![MessageData.FILE_ID] as String?
//                                ).bitmap
//                        } else if (null != fileUrl && fileUrl.isNotEmpty()) {
//                            // Get bitmap from url
//                            try {
//                                val imageUrl = URL(
//                                    message.data!![MessageData.FILE_URL] as String?
//                                )
//                                bitmap =
//                                    BitmapFactory.decodeStream(
//                                        imageUrl.openConnection().getInputStream()
//                                    )
//                            } catch (e: MalformedURLException) {
//                                e.printStackTrace()
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                        }
                        if (null != bitmap) {
                            TAPFileDownloadManager.getInstance(instanceKey)
                                .writeImageFileToDisk(this@TapScheduledMessageActivity,
                                    System.currentTimeMillis(),
                                    bitmap,
                                    message.data!![MessageData.MEDIA_TYPE] as String?,
                                    object : TapTalkActionInterface {
                                        override fun onSuccess(message: String) {
                                            runOnUiThread {
                                                Toast.makeText(
                                                    this@TapScheduledMessageActivity,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onError(errorMessage: String) {
                                            runOnUiThread {
                                                Toast.makeText(
                                                    this@TapScheduledMessageActivity,
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    })
                        }
                    }.start()
                }
            }

            override fun onSaveVideoToGallery(message: TAPMessageModel) {
                if (!TAPUtils.hasPermissions(
                        this@TapScheduledMessageActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    // Request storage permission
                    vm.pendingDownloadMessage = message
                    ActivityCompat.requestPermissions(
                        this@TapScheduledMessageActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO
                    )
                } else if (null != message.data) {
                    val fileID = message.data!![MessageData.FILE_ID] as String?
                    val fileUrl = message.data!![FILE_URL] as String?
                    if ((null != fileID && fileID.isNotEmpty() ||
                                null != fileUrl && fileUrl.isNotEmpty()) &&
                        null != message.data!![MessageData.MEDIA_TYPE]
                    ) {
                        vm.pendingDownloadMessage = null
                        writeFileToDisk(message)
                    }
                }
            }

            override fun onSaveToDownloads(message: TAPMessageModel) {
                if (null != message.data) {
                    val fileID = message.data!![MessageData.FILE_ID] as String?
                    val fileUrl = message.data!![FILE_URL] as String?
                    if ((null != fileID && fileID.isNotEmpty() ||
                                null != fileUrl && fileUrl.isNotEmpty()) &&
                        null != message.data!![MessageData.MEDIA_TYPE]
                    ) {
                        writeFileToDisk(message)
                    }
                }
            }

            private fun writeFileToDisk(message: TAPMessageModel) {
                TAPFileDownloadManager.getInstance(instanceKey).writeFileToDisk(
                    this@TapScheduledMessageActivity,
                    message,
                    object : TapTalkActionInterface {
                        override fun onSuccess(message: String) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@TapScheduledMessageActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onError(errorMessage: String) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@TapScheduledMessageActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            }

            override fun onViewProfileSelected(username: String, message: TAPMessageModel) {
                val participant: TAPUserModel? = vm.roomParticipantsByUsername[username]
                if (null != participant) {
                    TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(
                        this@TapScheduledMessageActivity,
                        message,
                        participant,
                        true
                    )
                } else {
                    val user =
                        TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username)
                    if (null != user) {
                        TAPChatManager.getInstance(instanceKey)
                            .triggerUserMentionTapped(this@TapScheduledMessageActivity, message, user, false)
                    } else {
//                        callApiGetUserByUsername(username, message)
                    }
                }
            }

            override fun onSendMessageSelected(username: String) {
                val participant: TAPUserModel? = vm.getRoomParticipantsByUsername().get(username)
                if (null != participant) {
                    TapUI.getInstance(instanceKey)
                        .openChatRoomWithOtherUser(this@TapScheduledMessageActivity, participant)
                    closeActivity()
                } else {
                    val user =
                        TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username)
                    if (null != user) {
                        TapUI.getInstance(instanceKey)
                            .openChatRoomWithOtherUser(this@TapScheduledMessageActivity, user)
                        closeActivity()
                    } else {
//                        callApiGetUserByUsername(username, null)
                    }
                }
            }

            override fun onEditMessage(message: TAPMessageModel) {
                super.onEditMessage(message)
                if (null == vm.room || message.room.roomID != vm.room.roomID) {
                    return
                }
                showEditLayout(message)
            }

            override fun onDeleteMessage(roomID: String?, message: TAPMessageModel?) {
                super.onDeleteMessage(roomID, message)
                val deletedIds = listOf(message?.messageID?.toInt())
                TAPDataManager.getInstance(instanceKey).deleteScheduledMessages(deletedIds, roomID, idsView)
            }

            override fun onSendNow(message: TAPMessageModel?) {
                super.onSendNow(message)
                val sentIds = listOf(message?.messageID?.toInt())
                TAPDataManager.getInstance(instanceKey).sendScheduledMessageNow(sentIds, vm.room.roomID, idsView)
            }

            override fun onRescheduleMessage(message: TAPMessageModel?) {
                super.onRescheduleMessage(message)
                val timePicker = TapTimePickerBottomSheetFragment(object : TAPGeneralListener<Long>() {
                    override fun onClick(position: Int, item: Long?) {
                        super.onClick(position, item)
                        TAPDataManager.getInstance(instanceKey).editScheduledMessageTime(message?.messageID?.toInt(), item, commonView)
                    }
                }, message?.created)
                timePicker.show(supportFragmentManager, "")
            }
        }

    private val idsView = object : TAPDefaultDataView<TapIdsResponse>() {
        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            showErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            showErrorDialog(errorMessage)
        }
    }

    private val commonView = object : TAPDefaultDataView<TAPCommonResponse>() {
        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            showErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            showErrorDialog(errorMessage)
        }
    }

    private fun openMediaPreviewPage(mediaPreviews: ArrayList<TAPMediaPreviewModel>) {
        TAPMediaPreviewActivity.start(
            this@TapScheduledMessageActivity,
            instanceKey,
            mediaPreviews,
            ArrayList(vm.roomParticipantsByUsername.values)
        )
    }

    private fun buildAndSendTextOrLinkMessage(message: String?, scheduledTime : Long?) {
        if (vm.quotedMessage != null && vm.quoteAction == QuoteAction.EDIT) {
            // edit message
            val messageModel = vm.quotedMessage
            if ((messageModel.type == MessageType.TYPE_TEXT || messageModel.type == MessageType.TYPE_LINK) && !TextUtils.isEmpty(
                    message
                )
            ) {
                messageModel.body = message
                var data = messageModel.data
                if (data == null) {
                    data = HashMap()
                }
                data[MessageData.TITLE] = vm.linkHashMap[MessageData.TITLE]
                data[MessageData.DESCRIPTION] = vm.linkHashMap[MessageData.DESCRIPTION]
                data[MessageData.IMAGE] = vm.linkHashMap[MessageData.IMAGE]
                data[MessageData.TYPE] = vm.linkHashMap[MessageData.TYPE]
                data[MessageData.URL] = vm.linkHashMap[MessageData.URL]
                messageModel.data = data
            } else if (messageModel.type == MessageType.TYPE_IMAGE || messageModel.type == MessageType.TYPE_VIDEO) {
                val data = messageModel.data
                if (data != null) {
                    data[MessageData.CAPTION] = message
                    messageModel.data = data
                }
            } else {
                return
            }
            TAPChatManager.getInstance(instanceKey)
                .editScheduledMessage(messageModel.messageID!!.toInt(), messageModel, message, object : TapCommonListener() {
                    override fun onError(errorCode: String?, errorMessage: String?) {
                        super.onError(errorCode, errorMessage)
                        if (errorCode == ClientErrorCodes.ERROR_CODE_CAPTION_EXCEEDS_LIMIT) {
                            TapTalkDialog.Builder(this@TapScheduledMessageActivity)
                                .setTitle(getString(R.string.tap_error_unable_to_edit_message))
                                .setMessage(errorMessage)
                                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                .show()
                        }
                    }
                }, true)
            hideQuoteLayout()
        } else if (!TextUtils.isEmpty(message)) {
            val firstUrl = vm.linkHashMap[MessageData.URL]
            if (firstUrl != null && firstUrl.isNotEmpty()) {
                // send message as link
                val data = HashMap<String, Any?>()
                data[MessageData.TITLE] = vm.linkHashMap[MessageData.TITLE]
                data[MessageData.DESCRIPTION] = vm.linkHashMap[MessageData.DESCRIPTION]
                data[MessageData.IMAGE] = vm.linkHashMap[MessageData.IMAGE]
                data[MessageData.TYPE] = vm.linkHashMap[MessageData.TYPE]
                data[MessageData.URL] = firstUrl
                TAPChatManager.getInstance(instanceKey).sendLinkMessage(message, data, scheduledTime)
            } else {
                // send message as text
                TAPChatManager.getInstance(instanceKey).sendTextMessage(message, scheduledTime)
            }
            rv_message_list.post{ scrollToBottom() }
        }
        et_chat.setText("")
    }

    private fun generateDateSeparator(time: Long?) : TAPMessageModel {
        if (time == null) {
            return TAPMessageModel()
        }
        return TAPMessageModel.Builder(
            String.format(getString(R.string.tap_format_s_scheduled_for), TAPTimeFormatter.formatScheduledDate(time)),
            vm.room,
            MessageType.TYPE_DATE_SEPARATOR,
            Date(time).time.minus(TimeUnit.DAYS.toMillis(1)) +1,
            vm.myUserModel,
            "",
            null
        )
    }

    private fun updateMessage(newMessage: TAPMessageModel) {
        if (vm.containerAnimationState == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage)
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread {
                // Remove empty chat layout if still shown
                if (null == newMessage.isHidden || !newMessage.isHidden!!) {
                     if (ll_empty_scheduled_message.visibility == View.VISIBLE) {
                         ll_empty_scheduled_message.visibility = View.GONE
                    }
                    showMessageList()
                }
            }
            // Replace pending message with new message
            val newID = newMessage.localID
            val ownMessage =
                newMessage.user.userID == TAPChatManager.getInstance(instanceKey).activeUser.userID
            runOnUiThread {
                if (vm.messagePointer.containsKey(newID)) {
                    // Update message instead of adding when message pointer already contains the same local ID
                    val index =
                        messageAdapter.items.indexOf(vm.messagePointer[newID])
                    vm.updateMessagePointer(newMessage)
                    messageAdapter.notifyItemChanged(index)
                    if (MessageType.TYPE_IMAGE == newMessage.type && ownMessage) {
                        TAPFileUploadManager.getInstance(instanceKey)
                            .removeUploadProgressMap(newMessage.localID)
                    }
                } else {
                    // Check previous message date and add new message
                    val previousMessage = messageAdapter.getItemAt(0)
                    val currentDate =
                        TAPTimeFormatter.formatDate(newMessage.created)
                    if ((null == newMessage.isHidden || !newMessage.isHidden!!) && newMessage.type != MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER && newMessage.type != MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER && newMessage.type != MessageType.TYPE_DATE_SEPARATOR &&
                        (null != previousMessage && currentDate != TAPTimeFormatter
                            .formatDate(previousMessage.created))
                    ) {
                        // Generate date separator if first message or date is different
                        val dateSeparator = generateDateSeparator(newMessage.created)
                        if (!vm.dateSeparators.containsKey(dateSeparator.body)) {
                            vm.dateSeparators[dateSeparator.body] = dateSeparator
                            vm.dateSeparatorIndexes[dateSeparator.body] = 0
                            runOnUiThread { messageAdapter.addMessage(dateSeparator) }
                        }
                    }

                    // Add new message
                    runOnUiThread { messageAdapter.addMessage(newMessage) }
                    vm.addMessagePointer(newMessage)
                    if (vm.isOnBottom && !ownMessage) {
                        // Scroll recycler to bottom if recycler is already on bottom
                        vm.isScrollFromKeyboard = true
                        scrollToBottom()
                    } else if (ownMessage && !(newMessage.action == SystemMessageAction.PIN_MESSAGE || newMessage.action == SystemMessageAction.UNPIN_MESSAGE)) {
                        // Scroll recycler to bottom if own message
                        scrollToBottom()
                    } else {
                        // Message from other people is received when recycler is scrolled up
                        if (newMessage.action == null || newMessage.action != SystemMessageAction.UNPIN_MESSAGE) {
                            vm.addUnreadMessage(newMessage)
                        }
                    }
                }
                updateMessageDecoration()
            }
            updateFirstVisibleMessageIndex()
        }
    }

    private fun addNewMessage(newMessage: TAPMessageModel, scheduledTime: Long) {
        newMessage.created = scheduledTime
        if (vm.containerAnimationState == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage)
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread {
                // Remove empty chat layout if still shown
                if (null == newMessage.isHidden || !newMessage.isHidden!!) {
                    if (ll_empty_scheduled_message.visibility == View.VISIBLE) {
                        ll_empty_scheduled_message.visibility = View.GONE
                    }
                    showMessageList()
                }
            }
            updateMessageMentionIndexes(newMessage)
            var previousMessage : TAPMessageModel? = null
            for (message in messageAdapter.items) {
                if (scheduledTime < message.created) {
                    previousMessage = message
                } else {
                    break
                }
            }
            val index = if (previousMessage == null) {
                0
            } else {
                messageAdapter.items.indexOf(previousMessage) + 1
            }
            val currentDate = TAPTimeFormatter.formatDate(newMessage.created)
            if ((null == newMessage.isHidden || !newMessage.isHidden!!) && newMessage.type != MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER && newMessage.type != MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER && newMessage.type != MessageType.TYPE_DATE_SEPARATOR &&
                (null == previousMessage || currentDate != TAPTimeFormatter
                    .formatDate(previousMessage.created))
            ) {
                // Generate date separator if first message or date is different
                val dateSeparator = generateDateSeparator(scheduledTime)
                if (!vm.dateSeparators.containsKey(dateSeparator.body)) {
                    vm.dateSeparators[dateSeparator.body] = dateSeparator
                    vm.dateSeparatorIndexes[dateSeparator.body] = index
                    runOnUiThread {
                        messageAdapter.addMessage(dateSeparator, index, true)
                    }
                }
            }
            runOnUiThread { messageAdapter.addMessage(newMessage, index, true) }
            vm.addMessagePointer(newMessage)
            runOnUiThread {
                iv_to_bottom.visibility = View.GONE
                rv_message_list.scrollToPosition(0)
                updateMessageDecoration()
            }
            updateFirstVisibleMessageIndex()
        }
    }

    private fun updateFirstVisibleMessageIndex() {
        Thread {
            vm.firstVisibleItemIndex = 0
            var message = messageAdapter.getItemAt(vm.firstVisibleItemIndex)
            while (null != message && null != message.isHidden && message.isHidden!!) {
                vm.firstVisibleItemIndex = vm.firstVisibleItemIndex + 1
                message = messageAdapter.getItemAt(vm.firstVisibleItemIndex)
            }
        }.start()
    }

    private fun scrollToMessage(localID: String) {
        val message: TAPMessageModel? = vm.messagePointer[localID]
        if (null != message) {
            vm.tappedMessageLocalID = null
            if (null != message.isDeleted && message.isDeleted!! ||
                null != message.isHidden && message.isHidden!!
            ) {
                // Message does not exist
                runOnUiThread {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.tap_error_could_not_find_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Scroll to message
                runOnUiThread {
                    messageLayoutManager.scrollToPositionWithOffset(
                        messageAdapter.items.indexOf(message), TAPUtils.dpToPx(128)
                    )
                    rv_message_list.post(Runnable {
                        if (messageLayoutManager.findFirstVisibleItemPosition() > 0) {
                            vm.isOnBottom = false
                            iv_to_bottom.visibility = View.VISIBLE
                        }
                        messageAdapter.highlightMessage(message)
                    })
                }
            }
        } else {
            // Message not found
            runOnUiThread {
                Toast.makeText(
                    this,
                    resources.getString(R.string.tap_error_could_not_find_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun goToMessage(message: TAPMessageModel) {
        if (vm.room.roomID == message.room.roomID) {
            scrollToMessage(message.localID)
        } else {
            val roomIntent = Intent(OPEN_CHAT)
            roomIntent.putExtra(MESSAGE, message)
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(roomIntent)
            closeActivity()
        }
    }

    private val messageListScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // Show/hide iv_to_bottom
                if (messageLayoutManager.findFirstVisibleItemPosition() <= vm.firstVisibleItemIndex) {
                    vm.isOnBottom = true
                    iv_to_bottom.visibility = View.GONE
                    vm.clearUnreadMessages()
                } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.firstVisibleItemIndex && !vm.isScrollFromKeyboard) {
                    vm.isOnBottom = false
                    iv_to_bottom.visibility = View.VISIBLE
                } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.firstVisibleItemIndex) {
                    vm.isOnBottom = false
                    iv_to_bottom.visibility = View.VISIBLE
                    vm.isScrollFromKeyboard = false
                }
            }
        }
    
    private fun setLinkRunnable(text: String): Runnable {
        return object : Runnable {
            var firstUrl = TAPUtils.getFirstUrlFromString(text)
            override fun run() {
                if (!firstUrl.isEmpty() && !firstUrl.startsWith("http://") && !firstUrl.startsWith("https://")) {
                    firstUrl = "http://$firstUrl"
                }
                if (!firstUrl.isEmpty() && firstUrl != vm.getLinkHashMap().get(MessageData.URL)) {
                    // Contains url
                    showLinkPreview(firstUrl)
                    Observable.fromCallable {
                        val document: Document
                        val linkMap =
                            HashMap<String, String>()
                        document = Jsoup.connect(firstUrl).get()
                        linkMap[MessageData.TITLE] = document.title()
                        linkMap[MessageData.URL] = firstUrl
                        var img: Element? = document.selectFirst("meta[property='og:image']")
                        if (img != null) {
                            linkMap[MessageData.IMAGE] = img.attr("content")
                        }
                        else {
                            img = document.selectFirst("img")
                            if (img != null) {
                                linkMap[MessageData.IMAGE] = img.absUrl("src")
                            }
                        }
                        val desc: Element? = document.selectFirst("meta[property='og:description']")
                        if (desc != null) {
                            linkMap[MessageData.DESCRIPTION] = desc.attr("content")
                        }
                        val type: Element? = document.selectFirst("meta[property='og:type']")
                        if (type != null) {
                            linkMap[MessageData.TYPE] = type.attr("content")
                        }
                        linkMap
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<HashMap<String, String>> {
                            override fun onNext(linkMap: HashMap<String, String>) {
                                if (firstUrl == TAPUtils.setUrlWithProtocol(
                                        TAPUtils.getFirstUrlFromString(
                                            et_chat.text.toString()
                                        )
                                    )
                                ) {
                                    vm.linkHashMap = linkMap
                                    updateLinkPreview(
                                        linkMap[MessageData.TITLE].orEmpty(),
                                        linkMap[MessageData.DESCRIPTION],
                                        if (linkMap[MessageData.IMAGE] == null) "" else linkMap[MessageData.IMAGE]
                                    )
                                }
                            }

                            override fun onError(e: Throwable) {
                                if (firstUrl == TAPUtils.setUrlWithProtocol(
                                        TAPUtils.getFirstUrlFromString(
                                            et_chat.text.toString()
                                        )
                                    )
                                ) {
                                    hideLinkPreview(true)
                                }
                            }

                            override fun onCompleted() {}
                        })
                } else {
                    // Text only
                    hideLinkPreview(true)
                }
            }
        }
    }


    private val chatWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (::linkRunnable.isInitialized) {
                linkHandler.removeCallbacks(linkRunnable)
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length > 0 && s.toString().trim { it <= ' ' }.length > 0) {
                if (TapUI.getInstance(instanceKey).isLinkPreviewInMessageEnabled) {
                    // Delay 0.3 sec before url check
                    linkRunnable = setLinkRunnable(s.toString())
                    linkHandler.postDelayed(linkRunnable, 300)
                }
                // Hide chat menu and enable send button when EditText is filled
                iv_chat_menu.setVisibility(View.GONE)
                iv_chat_menu_area.setVisibility(View.GONE)
                if (vm.getQuoteAction() == QuoteAction.EDIT) {
                    var caption =
                        if (vm.getQuotedMessage().getData() != null) vm.getQuotedMessage().getData()!!.get(MessageData.CAPTION) else ""
                    caption = caption ?: ""
                    if ((vm.getQuotedMessage()
                            .getType() == MessageType.TYPE_TEXT || vm.getQuotedMessage()
                            .getType() == MessageType.TYPE_LINK) && vm.getQuotedMessage()
                            .getBody() == s.toString() || (vm.getQuotedMessage()
                            .getType() == MessageType.TYPE_IMAGE || vm.getQuotedMessage()
                            .getType() == MessageType.TYPE_VIDEO) && caption == s.toString()
                    ) {
                        setSendButtonDisabled()
                    } else {
                        setSendButtonEnabled()
                    }
                } else {
                    setSendButtonEnabled()
                }
                checkAndSearchUserMentionList()
                //checkAndHighlightTypedText();
            } else if (s.length > 0) {
                // Hide chat menu but keep send button disabled if trimmed text is empty
                iv_chat_menu.setVisibility(View.GONE)
                iv_chat_menu_area.setVisibility(View.GONE)
                setSendButtonDisabled()
                hideUserMentionList()
                hideLinkPreview(true)
            } else {
                if (vm.isCustomKeyboardEnabled && s.isEmpty()) {
                    // Show chat menu if text is empty
                    iv_chat_menu.visibility = View.VISIBLE
                    iv_chat_menu_area.visibility = View.VISIBLE
                } else {
                    iv_chat_menu.visibility = View.GONE
                    iv_chat_menu_area.visibility = View.GONE
                }
                var caption = if (vm.quotedMessage != null && vm.quotedMessage
                        .data != null
                ) vm.quotedMessage.data!![MessageData.CAPTION] else ""
                caption = caption ?: ""
                if (vm.getQuoteAction() == QuoteAction.FORWARD || vm.getQuoteAction() == QuoteAction.EDIT && (vm.getQuotedMessage()
                        .getType() == MessageType.TYPE_IMAGE || vm.getQuotedMessage()
                        .getType() == MessageType.TYPE_VIDEO) &&
                    caption != s.toString()
                ) {
                    // Enable send button if message to forward exists
                    setSendButtonEnabled()
                } else {
                    // Disable send button
                    setSendButtonDisabled()
                }
                hideUserMentionList()
                hideLinkPreview(true)
            }
        }

        override fun afterTextChanged(p0: Editable?) {
            
        }
    }

    private fun showLinkPreview(url: String) {
        tv_link_content.text = url
        cl_link.setVisibility(View.VISIBLE)
        vm.clearLinkHashMap()
    }

    private fun updateLinkPreview(linkTitle: String, linkContent: String?, imageUrl: String?) {
        tv_link_title.setText(linkTitle)
        tv_link_title.setTextColor(ContextCompat.getColor(this, R.color.tapTitleLabelColor))
        if (linkContent == null || linkContent.isEmpty()) {
            tv_link_content.setVisibility(View.GONE)
        } else {
            tv_link_content.setVisibility(View.VISIBLE)
            tv_link_content.setText(linkContent)
        }
        if (imageUrl != null && imageUrl.isEmpty()) {
            rciv_link.setVisibility(View.GONE)
        } else {
            rciv_link.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tap_bg_white_rounded_8dp
                )
            )
            rciv_link.setPadding(0, 0, 0, 0)
            glide.load(imageUrl).fitCenter().into(rciv_link)
            rciv_link.setVisibility(View.VISIBLE)
        }
    }

    private fun hideLinkPreview(isClearLinkMap: Boolean) {
        if (isClearLinkMap) {
            vm.clearLinkHashMap()
        } else {
            if (vm.quotedMessage != null) {
                cl_quote_layout.visibility = View.VISIBLE
            }
        }
        cl_link.visibility = View.GONE
        tv_link_title.setText(R.string.tap_loading_dots)
        tv_link_title.setTextColor(ContextCompat.getColor(this, R.color.tapMediaLinkColor))
        tv_link_content.text = ""
        tv_link_content.visibility = View.VISIBLE
        glide.load(R.drawable.tap_ic_link_white).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(rciv_link)
        val padding = TAPUtils.dpToPx(8)
        rciv_link.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_rounded_primary_8dp)
        rciv_link.setPadding(padding, padding, padding, padding)
        rciv_link.visibility = View.VISIBLE
    }

    private fun setSendButtonDisabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv_send_area.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_bg_chat_composer_send_inactive_ripple
                )
            )
        } else {
            iv_send_area.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_bg_chat_composer_send_inactive
                )
            )
        }
        iv_send.setColorFilter(
            ContextCompat.getColor(
                TapTalk.appContext,
                R.color.tapIconChatComposerSendInactive
            )
        )
        iv_send_area.setEnabled(false)
    }

    private fun setSendButtonEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv_send_area.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_bg_chat_composer_send_ripple
                )
            )
        } else {
            iv_send_area.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_bg_chat_composer_send
                )
            )
        }
        iv_send.setColorFilter(
            ContextCompat.getColor(
                TapTalk.appContext,
                R.color.tapIconChatComposerSend
            )
        )
        iv_send_area.setEnabled(true)
    }


    private fun checkAndSearchUserMentionList() {
        if (TapUI.getInstance(instanceKey).isMentionUsernameDisabled || vm.roomParticipantsByUsername.isEmpty()) {
            hideUserMentionList()
            return
        }
        val s: String = et_chat.text.toString()
        if (!s.contains("@")) {
            // Return if text does not contain @
            hideUserMentionList()
            return
        }
        val cursorIndex: Int = et_chat.selectionStart
        var loopIndex: Int = et_chat.selectionStart
        while (loopIndex > 0) {
            // Loop text from cursor index to the left
            loopIndex--
            val c = s[loopIndex]
            if (c == ' ' || c == '\n') {
                // Found space before @, return
                hideUserMentionList()
                return
            }
            if (c == '@') {
                // Found @, start searching user
                val keyword = s.substring(loopIndex + 1, cursorIndex).lowercase(Locale.getDefault())
                if (keyword.isEmpty()) {
                    // Show all participants
                    val searchResult: MutableList<TAPUserModel> =
                        ArrayList(vm.roomParticipantsByUsername.values)
                    searchResult.remove(vm.myUserModel)
                    showUserMentionList(searchResult, loopIndex, cursorIndex)
                } else {
                    // Search participants from keyword
                    val finalLoopIndex = loopIndex
                    Thread {
                        val searchResult: MutableList<TAPUserModel> =
                            ArrayList()
                        for ((_, value) in vm.roomParticipantsByUsername) {
                            if (null != value.username &&
                                value.username != vm.myUserModel
                                    .username &&
                                (value.fullname
                                    .lowercase(Locale.getDefault())
                                    .contains(keyword) ||
                                        value.username!!
                                            .lowercase(Locale.getDefault())
                                            .contains(keyword))
                            ) {
                                // Add result if name/username matches and not self
                                searchResult.add(value)
                            }
                        }
                        runOnUiThread {
                            showUserMentionList(
                                searchResult,
                                finalLoopIndex,
                                cursorIndex
                            )
                        }
                    }.start()
                }
                return
            }
        }
        hideUserMentionList()
    }

    private fun updateMessageMentionIndexes(message: TAPMessageModel) {
        vm.messageMentionIndexes.remove(message.localID)
        if (TapUI.getInstance(instanceKey).isMentionUsernameDisabled || vm.room.type == RoomType.TYPE_PERSONAL) {
            return
        }
        val originalText: String?
        originalText =
            if (message.type == MessageType.TYPE_TEXT || message.type == MessageType.TYPE_LINK) {
                message.body
            } else if ((message.type == MessageType.TYPE_IMAGE || message.type == MessageType.TYPE_VIDEO) && null != message.data) {
                message.data!![MessageData.CAPTION] as String?
            } else if (message.type == MessageType.TYPE_LOCATION && null != message.data) {
                message.data!![MessageData.ADDRESS] as String?
            } else {
                return
            }
        if (null == originalText) {
            return
        }
        val mentionIndexes: MutableList<Int> = ArrayList()
        if (originalText.contains("@")) {
            val length = originalText.length
            var startIndex = -1
            for (i in 0 until length) {
                if (originalText[i] == '@' && startIndex == -1) {
                    // Set index of @ (mention start index)
                    startIndex = i
                } else {
                    val endOfMention = originalText[i] == ' ' ||
                            originalText[i] == '\n'
                    if (i == length - 1 && startIndex != -1) {
                        // End of string (mention end index)
                        val endIndex = if (endOfMention) i else i + 1
                        //String username = originalText.substring(startIndex + 1, endIndex);
                        //if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        if (endIndex > startIndex + 1) {
                            mentionIndexes.add(startIndex)
                            mentionIndexes.add(endIndex)
                        }
                        //}
                        startIndex = -1
                    } else if (endOfMention && startIndex != -1) {
                        // End index for mentioned username
                        //String username = originalText.substring(startIndex + 1, i);
                        //if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        if (i > startIndex + 1) {
                            mentionIndexes.add(startIndex)
                            mentionIndexes.add(i)
                        }
                        //}
                        startIndex = -1
                    }
                }
            }
            if (!mentionIndexes.isEmpty()) {
                vm.messageMentionIndexes[message.localID] = mentionIndexes
            }
        }
    }


    private fun showUserMentionList(
        searchResult: List<TAPUserModel>,
        loopIndex: Int,
        cursorIndex: Int
    ) {
        if (!searchResult.isEmpty()) {
            // Show search result in list
            userMentionListAdapter = TapUserMentionListAdapter(
                searchResult
            ) { user: TAPUserModel ->
                // Append username to typed text
                if (et_chat.text.length >= cursorIndex) {
                    et_chat.text.replace(loopIndex + 1, cursorIndex, user.username + " ")
                }
            }
            cl_user_mention_list.rv_user_mention_list.setMaxHeight(TAPUtils.dpToPx(160))
            cl_user_mention_list.rv_user_mention_list.setAdapter(userMentionListAdapter)
            if (null == cl_user_mention_list.rv_user_mention_list.getLayoutManager()) {
                cl_user_mention_list.rv_user_mention_list.setLayoutManager(object : LinearLayoutManager(
                    this@TapScheduledMessageActivity, VERTICAL, false
                ) {
                    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                        try {
                            super.onLayoutChildren(recycler, state)
                        } catch (e: java.lang.IndexOutOfBoundsException) {
                            e.printStackTrace()
                        }
                    }
                })
            }
            cl_user_mention_list.setVisibility(View.VISIBLE)
        } else {
            // Result is empty
            hideUserMentionList()
        }
    }

    private fun hideUserMentionList() {
        val hasFocus: Boolean = et_chat.hasFocus()
        cl_user_mention_list.visibility = View.GONE
        if (hasFocus) {
            cl_user_mention_list.post({
                cl_user_mention_list.rv_user_mention_list.adapter = null
                cl_user_mention_list.rv_user_mention_list.post(Runnable { et_chat.requestFocus() })
            })
        }
    }

    private val chatFocusChangeListener =
        OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && vm.isCustomKeyboardEnabled) {
                vm.isScrollFromKeyboard = true
                rv_custom_keyboard.setVisibility(View.GONE)
                iv_chat_menu_area.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@TapScheduledMessageActivity,
                        R.drawable.tap_bg_chat_composer_burger_menu_ripple
                    )
                )
                iv_chat_menu.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@TapScheduledMessageActivity,
                        R.drawable.tap_ic_burger_white
                    )
                )
                iv_chat_menu.setColorFilter(
                    ContextCompat.getColor(
                        TapTalk.appContext,
                        R.color.tapIconChatComposerBurgerMenu
                    )
                )
                TAPUtils.showKeyboard(this@TapScheduledMessageActivity, et_chat)
                if (et_chat.text.toString().isNotEmpty()) {
                    iv_chat_menu.visibility = View.GONE
                    iv_chat_menu_area.visibility = View.GONE
                }
            } else if (hasFocus) {
                vm.isScrollFromKeyboard = true
                TAPUtils.showKeyboard(this@TapScheduledMessageActivity, et_chat)
            }
        }

    private val containerTransitionListener: LayoutTransition.TransitionListener =
        object : LayoutTransition.TransitionListener {
            override fun startTransition(
                layoutTransition: LayoutTransition,
                viewGroup: ViewGroup,
                view: View,
                i: Int
            ) {
                // Change animation state
                if (vm.containerAnimationState != vm.PROCESSING) {
                    vm.containerAnimationState = vm.ANIMATING
                }
            }

            override fun endTransition(
                layoutTransition: LayoutTransition,
                viewGroup: ViewGroup,
                view: View,
                i: Int
            ) {
                if (vm.containerAnimationState == vm.ANIMATING) {
                    processPendingMessages()
                }
            }

            private fun processPendingMessages() {
                vm.containerAnimationState = vm.PROCESSING
                if (vm.pendingRecyclerMessages.size > 0) {
                    // Copy list to prevent concurrent exception
                    val pendingMessages: List<TAPMessageModel> =
                        ArrayList(vm.pendingRecyclerMessages)
                    for (pendingMessage in pendingMessages) {
                        // Loop the copied list to add messages
                        if (vm.containerAnimationState != vm.PROCESSING) {
                            return
                        }
                        updateMessage(pendingMessage)
                    }
                    // Remove added messages from pending message list
                    vm.pendingRecyclerMessages.removeAll(pendingMessages)
                    if (vm.pendingRecyclerMessages.size > 0) {
                        // Redo process if pending message is not empty
                        processPendingMessages()
                        return
                    }
                }
                // Change state to idle when processing finished
                vm.containerAnimationState = vm.IDLE
            }
        }
    
    private fun showLoadingPopup() {
        runOnUiThread {
            iv_loading_image.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tap_ic_loading_progress_circle_white
                )
            )
            if (null == iv_loading_image.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(this, iv_loading_image)
            }
            fl_loading.tv_loading_text.setVisibility(View.INVISIBLE)
            fl_loading.setVisibility(View.VISIBLE)
            Handler().postDelayed({
                if (fl_loading.getVisibility() == View.VISIBLE) {
                    fl_loading.tv_loading_text.setText(getString(R.string.tap_cancel))
                    fl_loading.tv_loading_text.setVisibility(View.VISIBLE)
                }
            }, 1000L)
        }
    }

    private fun hideLoadingPopup() {
        runOnUiThread { fl_loading.visibility = View.GONE }
    }

    private fun showErrorDialog(message: String?) {
        runOnUiThread {
            TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(getString(R.string.tap_error))
                .setCancelable(true)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .show()
        }
    }

    private val socketListener = object : TAPSocketListener() {
        override fun onSocketConnected() {
            if (!vm.isInitialAPICallFinished) {
                // Call Message List API
                getScheduledMessages()
            }
            restartFailedDownloads()
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            val localID: String?
            val fileUri: Uri?
            when (action) {
                UploadBroadcastEvent.UploadProgressLoading -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(
                            messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))
                        )
                    }
                }
                UploadBroadcastEvent.UploadProgressFinish -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    val messageModel = vm.getMessagePointer().get(localID)
                    if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(
                            UploadBroadcastEvent.UploadImageData
                        ) &&
                        intent.getSerializableExtra(UploadBroadcastEvent.UploadImageData) is HashMap<*, *>
                    ) {
                        // Set image data
                        messageModel?.data =
                            intent.getSerializableExtra(UploadBroadcastEvent.UploadImageData) as HashMap<String?, Any?>?
                    } else if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(
                            UploadBroadcastEvent.UploadFileData
                        ) &&
                        intent.getSerializableExtra(UploadBroadcastEvent.UploadFileData) is HashMap<*, *>
                    ) {
                        // Put file data
                        messageModel?.putData(intent.getSerializableExtra(UploadBroadcastEvent.UploadFileData) as HashMap<String?, Any?>?)
                    }
                    messageAdapter.notifyItemChanged(
                        messageAdapter.getItems().indexOf(messageModel)
                    )
                }
                UploadBroadcastEvent.UploadFailed -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    if (vm.getMessagePointer().containsKey(localID)) {
                        val failedMessageModel =
                            vm.getMessagePointer().get(localID)
                        failedMessageModel?.isFailedSend = true
                        failedMessageModel?.isSending = false
                        messageAdapter.notifyItemChanged(
                            messageAdapter.getItems().indexOf(failedMessageModel)
                        )
                    }
                }
                UploadBroadcastEvent.UploadCancelled -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    if (vm.getMessagePointer().containsKey(localID)) {
                        val cancelledMessageModel = vm.getMessagePointer().get(localID)
                        vm.delete(localID)
                        val itemPos: Int = messageAdapter.getItems().indexOf(cancelledMessageModel)
                        TAPFileUploadManager.getInstance(instanceKey).cancelUpload(
                            this@TapScheduledMessageActivity, cancelledMessageModel,
                            vm.getRoom().getRoomID()
                        )
                        vm.removeFromUploadingList(localID)
                        vm.removeMessagePointer(localID)
                        messageAdapter.removeMessageAt(itemPos)
                    }
                }
                DownloadBroadcastEvent.DownloadProgressLoading, DownloadBroadcastEvent.DownloadFinish -> {
                    localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(
                            messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))
                        )
                    }
                }
                DownloadBroadcastEvent.DownloadFailed -> {
                    localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
                    TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(localID)
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(
                            messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))
                        )
                    }
                }
                DownloadBroadcastEvent.DownloadFile -> startFileDownload(
                    intent.getParcelableExtra<TAPMessageModel>(
                        MESSAGE
                    )
                )
                DownloadBroadcastEvent.CancelDownload -> {
                    localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
                    TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(localID)
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(
                            messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))
                        )
                    }
                }
                DownloadBroadcastEvent.OpenFile -> {
                    val message = intent.getParcelableExtra<TAPMessageModel>(MESSAGE)
                    fileUri = intent.getParcelableExtra(MessageData.FILE_URI)
                    vm.setOpenedFileMessage(message)
                    if (null != fileUri && null != message!!.data && null != message.data!![MessageData.MEDIA_TYPE]) {
                        if (!TAPUtils.openFile(
                                instanceKey,
                                this@TapScheduledMessageActivity,
                                fileUri,
                                message.data!![MessageData.MEDIA_TYPE] as String?
                            )
                        ) {
                            showDownloadFileDialog()
                        }
                    } else {
                        showDownloadFileDialog()
                    }
                }
                LongPressBroadcastEvent.LongPressChatBubble -> {
                    if (null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val chatBubbleBottomSheet = newInstance(
                            instanceKey,
                            LongPressType.SCHEDULED_TYPE,
                            (intent.getParcelableExtra<Parcelable>(MESSAGE) as TAPMessageModel?)!!,
                            longPressListener
                        )
                        chatBubbleBottomSheet.show(supportFragmentManager, "")
                        TAPUtils.dismissKeyboard(this@TapScheduledMessageActivity)
                    }
                }
                LongPressBroadcastEvent.LongPressLink -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE)
                    ) {
                        val linkBottomSheet = newInstance(
                            instanceKey, LongPressType.LINK_TYPE,
                            intent.getParcelableExtra(MESSAGE),
                            intent.getStringExtra(COPY_MESSAGE)!!,
                            intent.getStringExtra(URL_MESSAGE)!!,
                            longPressListener
                        )
                        linkBottomSheet.show(supportFragmentManager, "")
                        TAPUtils.dismissKeyboard(this@TapScheduledMessageActivity)
                    }
                }
                LongPressBroadcastEvent.LongPressEmail -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE) &&
                        null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val emailBottomSheet = newInstance(
                            instanceKey, LongPressType.EMAIL_TYPE,
                            intent.getParcelableExtra(MESSAGE),
                            intent.getStringExtra(COPY_MESSAGE)!!,
                            intent.getStringExtra(URL_MESSAGE)!!,
                            longPressListener
                        )
                        emailBottomSheet.show(supportFragmentManager, "")
                        TAPUtils.dismissKeyboard(this@TapScheduledMessageActivity)
                    }
                }
                LongPressBroadcastEvent.LongPressPhone -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE) &&
                        null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val phoneBottomSheet = newInstance(
                            instanceKey, LongPressType.PHONE_TYPE,
                            intent.getParcelableExtra(MESSAGE),
                            intent.getStringExtra(COPY_MESSAGE)!!,
                            intent.getStringExtra(URL_MESSAGE)!!,
                            longPressListener
                        )
                        phoneBottomSheet.show(supportFragmentManager, "")
                        TAPUtils.dismissKeyboard(this@TapScheduledMessageActivity)
                    }
                }
                LongPressBroadcastEvent.LongPressMention -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE) &&
                        null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val mentionBottomSheet = newInstance(
                            instanceKey, LongPressType.MENTION_TYPE,
                            intent.getParcelableExtra(MESSAGE)!!,
                            intent.getStringExtra(COPY_MESSAGE)!!,
                            intent.getStringExtra(URL_MESSAGE)!!,
                            longPressListener
                        )
                        mentionBottomSheet.show(supportFragmentManager, "")
                        TAPUtils.dismissKeyboard(this@TapScheduledMessageActivity)
                    }
                }
            }
        }
    }


    private fun startFileDownload(message: TAPMessageModel?) {
        if (!TAPUtils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm.setPendingDownloadMessage(message)
            ActivityCompat.requestPermissions(
                this@TapScheduledMessageActivity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            )
        } else {
            // Download file
            vm.setPendingDownloadMessage(null)
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message)
        }
    }

    private fun showDownloadFileDialog() {
        // Prompt download if file does not exist
        if (null == vm.getOpenedFileMessage()) {
            return
        }
        if (null != vm.getOpenedFileMessage().getData()) {
            val fileId = vm.getOpenedFileMessage().getData()!!.get(MessageData.FILE_ID) as String?
            var fileUrl = vm.getOpenedFileMessage().getData()!!.get(FILE_URL) as String?
            if (null != fileUrl) {
                fileUrl = TAPUtils.getUriKeyFromUrl(fileUrl)
            }
            TAPFileDownloadManager.getInstance(instanceKey)
                .removeFileMessageUri(vm.getRoom().getRoomID(), fileId)
            TAPFileDownloadManager.getInstance(instanceKey)
                .removeFileMessageUri(vm.getRoom().getRoomID(), fileUrl)
        }
        messageAdapter.notifyItemChanged(
            messageAdapter.getItems().indexOf(vm.getOpenedFileMessage())
        )
        TapTalkDialog.Builder(this@TapScheduledMessageActivity)
            .setTitle(getString(R.string.tap_error_could_not_find_file))
            .setMessage(getString(R.string.tap_error_redownload_file))
            .setCancelable(true)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setPrimaryButtonListener { v: View? ->
                startFileDownload(
                    vm.getOpenedFileMessage()
                )
            }
            .show()
    }

    private fun restartFailedDownloads() {
        if (TAPFileDownloadManager.getInstance(instanceKey).hasFailedDownloads() &&
            TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)
        ) {
            // Notify chat bubbles with failed download
            for (localID in TAPFileDownloadManager.getInstance(instanceKey).failedDownloads) {
                if (vm.getMessagePointer().containsKey(localID)) {
                    runOnUiThread {
                        messageAdapter.notifyItemChanged(
                            messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))
                        )
                    }
                }
            }
            //TAPFileDownloadManager.getInstance(instanceKey).clearFailedDownloads();
        }
    }

    private fun setOtherUserModel(otherUserModel: TAPUserModel) {
        if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
            vm.setOtherUserModel(TAPChatManager.getInstance(instanceKey).activeUser)
        } else {
            vm.setOtherUserModel(otherUserModel)
        }
    }

}