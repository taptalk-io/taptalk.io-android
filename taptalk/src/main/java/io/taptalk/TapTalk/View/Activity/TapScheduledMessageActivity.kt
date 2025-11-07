package io.taptalk.TapTalk.View.Activity

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
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
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BroadcastEvent.OPEN_CHAT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BroadcastEvent.REOPEN_ATTACHMENT_BOTTOM_SHEET
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BroadcastEvent.REOPEN_TIME_PICKER_BOTTOM_SHEET
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType
import io.taptalk.TapTalk.Const.TAPDefaultConstant.CHARACTER_LIMIT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.PlayPauseVoiceNote
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COPY_MESSAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.DATA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.TIME
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Location
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressChatBubble
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressEmail
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressLink
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressMention
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressPhone
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest
import io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_SCHEDULED_MESSAGES
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_FILE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType
import io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPTimeFormatter
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TapLongPressInterface
import io.taptalk.TapTalk.Interface.TapTalkActionInterface
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Listener.TAPChatListener
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Listener.TAPSocketListener
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Manager.TAPCacheManager
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPConnectionManager
import io.taptalk.TapTalk.Manager.TAPContactManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPEncryptorManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Manager.TAPFileUploadManager
import io.taptalk.TapTalk.Manager.TAPGroupManager.Companion.getInstance
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Manager.TapUI.LongPressMenuType
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapGetScheduledMessageListResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapIdsResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.Model.TapLongPressMenuItem
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPCustomKeyboardAdapter
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter
import io.taptalk.TapTalk.View.Adapter.TapUserMentionListAdapter
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType
import io.taptalk.TapTalk.View.BottomSheet.TapTimePickerBottomSheetFragment
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel.TAPChatViewModelFactory
import io.taptalk.TapTalk.ViewModel.TapScheduledMessageViewModel
import io.taptalk.TapTalk.databinding.TapActivityChatBinding
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
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TapScheduledMessageActivity: TAPBaseActivity() {

    private val glide : RequestManager by lazy {
        Glide.with(this)
    }

    private val svm: TapScheduledMessageViewModel by lazy {
        ViewModelProvider(this)[TapScheduledMessageViewModel::class.java]
    }

    private lateinit var vb: TapActivityChatBinding
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
            start(context, instanceKey, room, null, null)
        }

        fun start(
            context: Activity,
            instanceKey: String?,
            room: TAPRoomModel,
            message: String?,
            scheduledTime: Long?,
        ) {
            val intent = Intent(context, TapScheduledMessageActivity::class.java)
            intent.putExtra(INSTANCE_KEY, instanceKey)
            intent.putExtra(ROOM, room)
            intent.putExtra(MESSAGE, message)
            intent.putExtra(TIME, scheduledTime)
            context.startActivityForResult(intent, OPEN_SCHEDULED_MESSAGES)
            context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityChatBinding.inflate(layoutInflater)
        setContentView(vb.root)
        if (finishIfNotLoggedIn()) {
            return
        }

        vb.tvRoomName.text = getString(R.string.tap_scheduled_message)
        vb.clRoomStatus.isVisible = false
        vb.ivVoiceNote.isVisible = false
        vb.flConnectionStatus.isVisible = false
        linkHandler = Handler(mainLooper)
        if (initViewModel()) {
            initView()
        }
        registerBackgroundBroadcastManager()
        TAPChatManager.getInstance(instanceKey).addChatListener(chatListener)
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener)
        textMessage = intent.getStringExtra(MESSAGE)
        textTime = intent.getLongExtra(TIME, -1)
    }

    override fun onResume() {
        super.onResume()
        getScheduledMessages()
        registerForegroundBroadcastReceiver()
    }

    override fun onPause() {
        super.onPause()
        TAPBroadcastManager.unregister(this, foregroundBroadcastReceiver)
    }

    override fun onBackPressed() {
        try {
            super.onBackPressed()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        if (vb.rvCustomKeyboard.isVisible) {
            hideKeyboards()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(this, backgroundBroadcastReceiver)
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
                            TYPE_IMAGE,
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
                        galleryMediaPreviews = TAPUtils.getPreviewsFromClipData(this@TapScheduledMessageActivity, clipData, true)
                    }
                    else {
                        // Single media selection
                        val uri = data.data
                        val preview = TAPUtils.getPreviewFromUri(this@TapScheduledMessageActivity, uri, true)
                        if (preview != null) {
                            galleryMediaPreviews.add(preview)
                        }
                    }
                    if (galleryMediaPreviews.isNotEmpty()) {
                        openMediaPreviewPage(galleryMediaPreviews)
                    }
                }
                RequestCode.SEND_MEDIA_FROM_PREVIEW -> {
                    val medias = data?.getParcelableArrayListExtra<TAPMediaPreviewModel>(Extras.MEDIA_PREVIEWS)
                    if (null != medias && 0 < medias.size) {
                        svm.pendingScheduledMessageType = TYPE_IMAGE
                        svm.pendingScheduledMessageTime = System.currentTimeMillis()
                        svm.pendingScheduledMessageMedias = medias
                        val timePicker = TapTimePickerBottomSheetFragment(svm, getMediaScheduledMessageListener(medias))
                        timePicker.show(supportFragmentManager, "")
                    }
                }
                RequestCode.PICK_LOCATION -> {
                    val address = if (data?.getStringExtra(Location.LOCATION_NAME) == null) {
                        ""
                    }
                    else {
                        data.getStringExtra(Location.LOCATION_NAME) ?: ""
                    }
                    val latitude = data?.getDoubleExtra(Location.LATITUDE, 0.0) ?: 0.0
                    val longitude = data?.getDoubleExtra(Location.LONGITUDE, 0.0) ?: 0.0
                    svm.pendingScheduledMessageType = TYPE_LOCATION
                    svm.pendingScheduledMessageTime = System.currentTimeMillis()
                    svm.pendingScheduledMessageText = address
                    svm.pendingScheduledMessageLatitude = latitude
                    svm.pendingScheduledMessageLongitude = longitude
                    val timePicker = TapTimePickerBottomSheetFragment(svm, getLocationScheduledMessageListener(address, latitude, longitude))
                    timePicker.show(supportFragmentManager, "")
                }
                RequestCode.SEND_FILE -> {
                    if (null == data) {
                        return
                    }
                    var tempFile: File? = null
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        var uri: Uri? = null
                        if (null != data.clipData) {
                            for (i in 0 until data.clipData!!.itemCount) {
                                uri = data.clipData!!.getItemAt(i).uri
                            }
                        }
                        else {
                            uri = data.data
                        }
                        if (uri != null) {
                            // Write temporary file to cache for upload for Android 11+
                            tempFile = TAPFileUtils.createTemporaryCachedFile(this, uri)
                        }
//                    }
//                    else {
//                        val filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
//                        if (!filePath.isNullOrEmpty()) {
//                            tempFile = File(filePath)
//                        }
//                    }

                    if (tempFile != null) {
                        if (TAPFileUploadManager.getInstance(instanceKey).isSizeAllowedForUpload(tempFile.length())) {
                            svm.pendingScheduledMessageType = TYPE_FILE
                            svm.pendingScheduledMessageTime = System.currentTimeMillis()
                            svm.pendingScheduledMessageFile = tempFile
                            val timePicker = TapTimePickerBottomSheetFragment(svm, getFileScheduledMessageListener(tempFile))
                            timePicker.show(supportFragmentManager, "")
                        }
                        else {
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
                    }
                    else {
                        vm.isDeleteGroup = true
                        closeActivity()
                    }
                }
                RequestCode.OPEN_MEMBER_PROFILE -> if (data != null) {
                    val message = data.getParcelableExtra<TAPMessageModel>(MESSAGE)
                    if (message != null) {
                        goToMessage(message)
                    }
                    else if (data.getBooleanExtra(Extras.CLOSE_ACTIVITY, false)) {
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
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (TAPUtils.allPermissionsGranted(grantResults)) {
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
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON)
                        TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity, SEND_FILE)
//                    }
                //                    else {
//                        TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity)
//                    }
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
        vm = ViewModelProvider(this, TAPChatViewModelFactory(application, instanceKey)).get(TAPChatViewModel::class.java)
        if (null == vm.room) {
            vm.room = intent.getParcelableExtra(ROOM)
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
            vb.tvRoomName.setText(R.string.tap_saved_messages)
        }
        else if (vm.room.type == RoomType.TYPE_PERSONAL && null != vm.otherUserModel &&
            (null == vm.otherUserModel.deleted || vm.otherUserModel.deleted!! <= 0L) &&
            vm.otherUserModel.fullname.isNotEmpty()
        ) {
            vb.tvRoomName.text = vm.otherUserModel.fullname
        }
        else {
            vb.tvRoomName.text = vm.room.name
        }
        if (!TapUI.getInstance(instanceKey).isProfileButtonVisible) {
            vb.civRoomImage.visibility = View.GONE
            vb.vRoomImage.visibility = View.GONE
            vb.tvRoomImageLabel.visibility = View.GONE
        }
        else if (null != vm.room && RoomType.TYPE_PERSONAL == vm.room.type && null != vm.otherUserModel &&
            null != vm.otherUserModel.deleted && vm.otherUserModel.deleted!! > 0L
        ) {
            glide.load(R.drawable.tap_ic_deleted_user).fitCenter().into(vb.civRoomImage)
            ImageViewCompat.setImageTintList(vb.civRoomImage, null)
            vb.tvRoomImageLabel.visibility = View.GONE
        }
        else if (null != vm.room && RoomType.TYPE_PERSONAL == vm.room.type && TAPUtils.isSavedMessagesRoom(vm.room.roomID, instanceKey)
        ) {
            glide.load(R.drawable.tap_ic_bookmark_round).fitCenter().into(vb.civRoomImage)
            ImageViewCompat.setImageTintList(vb.civRoomImage, null)
            vb.tvRoomImageLabel.visibility = View.GONE
        }
        else if (null != vm.room && RoomType.TYPE_PERSONAL == vm.room
                .type && null != vm.otherUserModel && null != vm.otherUserModel
                .imageURL.thumbnail &&
            vm.otherUserModel.imageURL.thumbnail.isNotEmpty()
        ) {
            // Load user avatar URL
            loadProfilePicture(
                vm.otherUserModel.imageURL.thumbnail,
                vb.civRoomImage,
                vb.tvRoomImageLabel
            )
            vm.room.imageURL = vm.otherUserModel.imageURL
        }
        else if (null != vm.room && !vm.room.isDeleted && null != vm.room
                .imageURL && vm.room.imageURL!!.thumbnail.isNotEmpty()
        ) {
            // Load room image
            loadProfilePicture(
                vm.room.imageURL!!.thumbnail,
                vb.civRoomImage,
                vb.tvRoomImageLabel
            )
        }
        else {
            loadInitialsToProfilePicture(vb.civRoomImage, vb.tvRoomImageLabel)
        }

        // TODO: 1 February 2019 SET ROOM ICON FROM ROOM MODEL
        if (null != vm.otherUserModel &&
            null != vm.otherUserModel.userRole &&
            null != vm.otherUserModel.userRole!!.iconURL &&
            null != vm.room &&
            RoomType.TYPE_PERSONAL == vm.room.type &&
            vm.otherUserModel.userRole!!.iconURL.isNotEmpty()
        ) {
            glide.load(vm.otherUserModel.userRole!!.iconURL).into(vb.ivRoomIcon)
            vb.ivRoomIcon.visibility = View.VISIBLE
        }
        else {
            vb.ivRoomIcon.visibility = View.GONE
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
        vb.rvMessageList.instanceKey = instanceKey
        vb.rvMessageList.adapter = messageAdapter
        vb.rvMessageList.layoutManager = messageLayoutManager
        vb.rvMessageList.setHasFixedSize(false)
        // FIXME: 9 November 2018 IMAGES/VIDEOS CURRENTLY NOT RECYCLED TO PREVENT INCONSISTENT DIMENSIONS
        vb.rvMessageList.recycledViewPool.setMaxRecycledViews(BubbleType.TYPE_BUBBLE_IMAGE_LEFT, 0)
        vb.rvMessageList.recycledViewPool.setMaxRecycledViews(BubbleType.TYPE_BUBBLE_IMAGE_RIGHT, 0)
        vb.rvMessageList.recycledViewPool.setMaxRecycledViews(BubbleType.TYPE_BUBBLE_VIDEO_LEFT, 0)
        vb.rvMessageList.recycledViewPool.setMaxRecycledViews(BubbleType.TYPE_BUBBLE_VIDEO_RIGHT, 0)
        vb.rvMessageList.recycledViewPool.setMaxRecycledViews(BubbleType.TYPE_BUBBLE_PRODUCT_LIST, 0)
        messageAnimator = vb.rvMessageList.itemAnimator as SimpleItemAnimator
        messageAnimator.supportsChangeAnimations = false
        vb.rvMessageList.itemAnimator = null
        vb.rvMessageList.addOnScrollListener(messageListScrollListener)
        //        OverScrollDecoratorHelper.setUpOverScroll(vb.rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL); FIXME: 8 Apr 2020 DISABLED OVERSCROLL DECORATOR
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
            vb.rvCustomKeyboard.adapter = customKeyboardAdapter
            vb.rvCustomKeyboard.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
            vb.ivChatMenuArea.setOnClickListener{ toggleCustomKeyboard() }
        }
        else {
            // Disable custom keyboard
            vm.isCustomKeyboardEnabled = false
            vb.ivChatMenu.visibility = View.GONE
            vb.ivChatMenuArea.visibility = View.GONE
        }
        showAttachmentButton()

        if (vm.messageModels.isNotEmpty()) {
            showMessageList()
            updateMessageDecoration()
        }
        
        val containerTransition: LayoutTransition = vb.clContainer.layoutTransition
        containerTransition.addTransitionListener(containerTransitionListener)
        vb.etChat.addTextChangedListener(chatWatcher)
        vb.etChat.onFocusChangeListener = chatFocusChangeListener

        vb.vRoomImage.setOnClickListener { openRoomProfile() }
        vb.ivButtonBack.setOnClickListener { closeActivity() }
        vb.ivCancelReply.setOnClickListener { hideQuoteLayout() }
        vb.ivAttach.setOnClickListener { openAttachMenu() }
        vb.ivSendArea.setOnClickListener {
            hideKeyboards()
            val text = vb.etChat.text.toString().trim { it <= ' ' }
            if (vm.quoteAction != null && vm.quotedMessage != null) {
                buildAndSendTextOrLinkMessage(text, vm.quotedMessage.created)
            }
            else if (text.isNotEmpty()) {
                svm.pendingScheduledMessageType = TYPE_TEXT
                svm.pendingScheduledMessageTime = System.currentTimeMillis()
                svm.pendingScheduledMessageText = text
                val timePicker = TapTimePickerBottomSheetFragment(svm, getTextScheduledMessageListener(text))
                timePicker.show(supportFragmentManager, "")
            }
        }
        vb.ivToBottom.setOnClickListener { scrollToBottom() }
        vb.flMessageList.setOnClickListener {
            chatListener.onOutsideClicked(TAPMessageModel())
        }
        vb.layoutPopupLoadingScreen.flLoading.setOnClickListener { }
        // TODO: 05/10/22 handle schedule message icon MU
        if (TapUI.getInstance(instanceKey).isLinkPreviewInMessageEnabled) {
            vb.ivCloseLink.setOnClickListener {
                vm.linkHashMap.remove(MessageData.TITLE)
                vm.linkHashMap.remove(MessageData.DESCRIPTION)
                vm.linkHashMap.remove(MessageData.IMAGE)
                vm.linkHashMap.remove(MessageData.TYPE)
                hideLinkPreview(false)
            }
        }
        vb.ivAttach.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_attachment_ripple)
        vb.ivToBottom.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_scroll_to_bottom_ripple)

        if (vm.isOnBottom &&
            vb.rvMessageList.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
            messageAdapter.itemCount > 0
        ) {
            vb.rvMessageList.scrollToPosition(0)
        }
    }

    private fun showAttachmentButton() {
        // Show / hide attachment button
        if (TapUI.getInstance(instanceKey).isDocumentAttachmentDisabled &&
            TapUI.getInstance(instanceKey).isCameraAttachmentDisabled &&
            TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled &&
            TapUI.getInstance(instanceKey).isLocationAttachmentDisabled
        ) {
            vb.ivAttach.visibility = View.GONE
            vb.etChat.setPadding(
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(6),
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(6)
            )
        }
        else {
            vb.ivAttach.visibility = View.VISIBLE
            vb.etChat.setPadding(
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(6),
                TAPUtils.dpToPx(48),
                TAPUtils.dpToPx(6)
            )
        }
        vb.ivSchedule.visibility = View.GONE
    }

    private fun registerBackgroundBroadcastManager() {
        TAPBroadcastManager.register(
            this,
            backgroundBroadcastReceiver,
            UploadProgressLoading,
            UploadProgressFinish,
            UploadFailed,
            UploadCancelled,
            DownloadProgressLoading,
            DownloadFinish,
            DownloadFailed,
            DownloadFile,
        )
    }

    private fun registerForegroundBroadcastReceiver() {
        TAPBroadcastManager.register(
            this,
            foregroundBroadcastReceiver,
            OpenFile,
            CancelDownload,
            PlayPauseVoiceNote,
            LongPressChatBubble,
            LongPressEmail,
            LongPressLink,
            LongPressPhone,
            LongPressMention,
            REOPEN_ATTACHMENT_BOTTOM_SHEET,
            REOPEN_TIME_PICKER_BOTTOM_SHEET
        )
    }

    private fun showLoadingOlderMessagesIndicator() {
        hideLoadingOlderMessagesIndicator()
        vb.rvMessageList.post {
            runOnUiThread { // Add loading indicator to last index
                val position = if (messageAdapter.itemCount > 0) {
                    messageAdapter.itemCount - 1
                }
                else {
                    0
                }
                vm.addMessagePointer(vm.getLoadingIndicator(true))
                messageAdapter.addItem(vm.getLoadingIndicator(false))
                messageAdapter.notifyItemInserted(position)
            }
        }
    }

    private fun hideLoadingOlderMessagesIndicator() {
        vb.rvMessageList.post {
            runOnUiThread {
                val loadingIndicator = vm.getLoadingIndicator(false)
                if (messageAdapter.items != null && !messageAdapter.items.contains(loadingIndicator)) {
                    return@runOnUiThread
                }
                val index = if (messageAdapter.items == null) {
                    0
                }
                else {
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
                    }
                    else {
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
            vm.messageModels.clear()
            vm.messagePointer.clear()
            messageAdapter.clearItems()
            vm.dateSeparators.clear()

            // Add response messages
            if (response?.items?.isNotEmpty() == true) {
                var previousModel : TapScheduledMessageModel? = null

                for (scheduledMessage in response.items) {
                    if (scheduledMessage.message != null) {
                        val message = TAPEncryptorManager.getInstance().decryptMessage(scheduledMessage.message)
                        if (message != null) {
                            // hide sending icon for scheduled message page only
                            message.isSending = false
                            message.messageID = scheduledMessage.id.toString()
                            // update status text
                            if (scheduledMessage.scheduledTime != null) {
//                                message.messageStatusText = TAPTimeFormatter.formatClock(scheduledMessage.scheduledTime)
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
                            vm.addMessagePointer(message)
                            previousModel = TapScheduledMessageModel(scheduledMessage)
                        }
                    }
                }
            }

            // Add local messages
            if (TAPChatManager.getInstance(instanceKey).pendingScheduledMessages.isNotEmpty()) {
                for (scheduledMessage in TAPChatManager.getInstance(instanceKey).pendingScheduledMessages) {
                    if (scheduledMessage.message != null && scheduledMessage.message.room?.roomID == vm.room.roomID) {
                        if (!vm.messagePointer.containsKey(scheduledMessage.message.localID)) {
                            addNewMessage(scheduledMessage.message, scheduledMessage.scheduledTime!!)
                        }
                    }
                }
            }
            if (TAPChatManager.getInstance(instanceKey).uploadingScheduledMessages.isNotEmpty()) {
                for (scheduledMessage in TAPChatManager.getInstance(instanceKey).uploadingScheduledMessages) {
                    if (scheduledMessage.value.message != null && scheduledMessage.value.message!!.room?.roomID == vm.room.roomID) {
                        if (!vm.messagePointer.containsKey(scheduledMessage.value.message!!.localID)) {
                            addNewMessage(scheduledMessage.value.message!!, scheduledMessage.value.scheduledTime!!)
                        }
                    }
                }
            }

            if (messageAdapter.items.isEmpty()) {
                runOnUiThread {
                    if (vb.llEmptyScheduledMessage.visibility == View.GONE) {
                        vb.llEmptyScheduledMessage.visibility = View.VISIBLE
                    }
                    hideMessageList()
                }
            }
            else {
                runOnUiThread {
                    if (vb.llEmptyScheduledMessage.visibility == View.VISIBLE) {
                        vb.llEmptyScheduledMessage.visibility = View.GONE
                    }
                    showMessageList()
                    updateMessageDecoration()
                    messageAdapter.notifyDataSetChanged()
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

//        override fun onScheduledMessageSent(scheduledMessage: TapScheduledMessageModel?) {
//            super.onScheduledMessageSent(scheduledMessage)
//        }
//
//        override fun onScheduledSendFailed(scheduledMessage: TapScheduledMessageModel?) {
//            super.onScheduledSendFailed(scheduledMessage)
//        }

        override fun onRetrySendMessage(message: TAPMessageModel) {
            if (null == vm.room || message.room.roomID != vm.room.roomID) {
                return
            }
            vm.delete(message.localID)
            if (message.type == TYPE_IMAGE || message.type == TYPE_VIDEO || message.type == MessageType.TYPE_FILE || message.type == MessageType.TYPE_VOICE) {
                if (null != message.data && null != message.data!![MessageData.FILE_ID] && null != message.data!![FILE_URL] &&
                    (message.data!![MessageData.FILE_ID] as String?)!!.isNotEmpty() &&
                    (message.data!![FILE_URL] as String?)!!.isNotEmpty()
                ) {
                    // Resend message
                    TAPChatManager.getInstance(instanceKey).resendMessage(message)
                }
                else if (null != message.data &&
                    null != message.data!![MessageData.FILE_URI] &&
                    (message.data!![MessageData.FILE_URI] as String?)!!.isNotEmpty()
                ) {
                    // Re-upload image/video
                    TAPChatManager.getInstance(instanceKey)
                        .retryUpload(this@TapScheduledMessageActivity, message)
                }
                else {
                    // Data not found
                    Toast.makeText(
                        this@TapScheduledMessageActivity,
                        getString(R.string.tap_error_resend_media_data_not_found),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else {
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
            }
            else {
                val user =
                    TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username)
                if (null != user) {
                    TAPChatManager.getInstance(instanceKey)
                        .triggerUserMentionTapped(this@TapScheduledMessageActivity, message, user, false)
                }
                else {
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
                vb.rvMessageList.smoothScrollToPosition(0)
            }
        }

        override fun onLayoutLoaded(message: TAPMessageModel) {
            if ( /*message.getUser().getUserID().equals(vm.getMyUserModel().getUserID()) ||*/messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                // Scroll recycler to bottom when image finished loading if message is sent by user or recycler is on bottom
                vb.rvMessageList.smoothScrollToPosition(0)
            }
        }

        override fun onRequestUserData(message: TAPMessageModel) {
            super.onRequestUserData(message)
            if (message.forwardFrom != null) {
                TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(
                    message.forwardFrom!!.userID,
                    object : TAPDefaultDataView<TAPGetUserResponse?>() {
                        override fun onSuccess(response: TAPGetUserResponse?) {
                            response?.user?.let {
                                TAPContactManager.getInstance(instanceKey).updateUserData(it)
                            }
                            messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(vm.messagePointer[message.localID]))
                        }
                    })
            }
        }
    }


    private fun closeActivity() {
        vb.rvCustomKeyboard.visibility = View.GONE
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
        TAPChatManager.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(this@TapScheduledMessageActivity, vm.getRoom(), groupMember)
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

        runOnUiThread {
            if (tvAvatarLabel === vb.tvMyAvatarLabelEmpty) {
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
                    TAPUtils.getInitials(
                        TAPChatManager.getInstance(instanceKey).activeUser.fullname,
                        2
                    )
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
    }

    private fun showMessageList() {
        runOnUiThread {
            vb.flMessageList.visibility = View.VISIBLE
        }
    }

    private fun hideMessageList() {
        runOnUiThread {
            vb.flMessageList.visibility = View.GONE
        }
    }

    private fun updateMessageDecoration() {
        // Update decoration for the top item in recycler view
        runOnUiThread {
            if (vb.rvMessageList.itemDecorationCount > 0) {
                vb.rvMessageList.removeItemDecorationAt(0)
            }
            vb.rvMessageList.addItemDecoration(
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
        vm.setQuotedMessage(message, QuoteAction.EDIT, false)
        runOnUiThread {
            vb.clQuoteLayout.visibility = View.VISIBLE
            // Add other quotable message type here
            if ((message.type == TYPE_IMAGE || message.type == TYPE_VIDEO) && null != message.data) {
                // Show image quote
                vb.vQuoteLayoutDecoration.setVisibility(View.GONE)
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
                    vb.rcivQuoteLayoutImage.setImageDrawable(drawable)
                }
                else {
                    // Show small thumbnail
                    val thumbnail: Drawable = BitmapDrawable(
                        resources,
                        TAPFileUtils.decodeBase64(
                            (if (null == message.data!![MessageData.THUMBNAIL]
                            ) "" else message.data!![MessageData.THUMBNAIL]) as String?
                        )
                    )
                    vb.rcivQuoteLayoutImage.setImageDrawable(thumbnail)
                }
                vb.rcivQuoteLayoutImage.colorFilter = null
                vb.rcivQuoteLayoutImage.background = null
                vb.rcivQuoteLayoutImage.scaleType = ImageView.ScaleType.CENTER_CROP
                vb.rcivQuoteLayoutImage.visibility = View.VISIBLE
                vb.tvQuoteLayoutTitle.setText(R.string.tap_edit_message)
                vb.tvQuoteLayoutContent.text = message.body
                vb.tvQuoteLayoutContent.maxLines = 1
                vb.etChat.filters = arrayOf<InputFilter>(
                    LengthFilter(
                        TapTalk.getMaxCaptionLength(
                            instanceKey
                        )
                    )
                )
                vb.etChat.setText(message.data!![MessageData.CAPTION].toString())
            }
            else if (null != message.data && null != message.data!![FILE_URL] && (message.type == TYPE_IMAGE || message.type == TYPE_VIDEO)
            ) {
                // Show image quote from file URL
                glide.load(message.data!![FILE_URL] as String?)
                    .into(vb.rcivQuoteLayoutImage)
                vb.rcivQuoteLayoutImage.colorFilter = null
                vb.rcivQuoteLayoutImage.background = null
                vb.rcivQuoteLayoutImage.scaleType = ImageView.ScaleType.CENTER_CROP
                vb.rcivQuoteLayoutImage.visibility = View.VISIBLE
                vb.vQuoteLayoutDecoration.visibility = View.GONE
                vb.tvQuoteLayoutTitle.setText(R.string.tap_edit_message)
                vb.tvQuoteLayoutContent.text = message.body
                vb.tvQuoteLayoutContent.maxLines = 1
                vb.etChat.filters = arrayOf<InputFilter>(
                    LengthFilter(
                        TapTalk.getMaxCaptionLength(
                            instanceKey
                        )
                    )
                )
                vb.etChat.setText(message.data!![MessageData.CAPTION].toString())
            }
            else if (null != message.data && null != message.data!![MessageData.IMAGE_URL]
            ) {
                // Show image quote from image URL
                glide.load(message.data!![MessageData.IMAGE_URL] as String?).into(vb.rcivQuoteLayoutImage)
                vb.rcivQuoteLayoutImage.colorFilter = null
                vb.rcivQuoteLayoutImage.background = null
                vb.rcivQuoteLayoutImage.scaleType = ImageView.ScaleType.CENTER_CROP
                vb.rcivQuoteLayoutImage.visibility = View.VISIBLE
                vb.vQuoteLayoutDecoration.visibility = View.GONE
                vb.tvQuoteLayoutTitle.setText(R.string.tap_edit_message)
                vb.tvQuoteLayoutContent.text = message.body
                vb.tvQuoteLayoutContent.maxLines = 1
                vb.etChat.filters = arrayOf<InputFilter>(
                    LengthFilter(
                        TapTalk.getMaxCaptionLength(
                            instanceKey
                        )
                    )
                )
                vb.etChat.setText(message.data!![MessageData.CAPTION].toString())
            }
            else {
                // Show text quote
                vb.vQuoteLayoutDecoration.visibility = View.VISIBLE
                vb.rcivQuoteLayoutImage.visibility = View.GONE
                vb.tvQuoteLayoutTitle.setText(R.string.tap_edit_message)
                vb.tvQuoteLayoutContent.text = message.body
                vb.tvQuoteLayoutContent.maxLines = 2
                vb.etChat.filters = arrayOf<InputFilter>(LengthFilter(CHARACTER_LIMIT))
                vb.etChat.setText(message.body)
            }
            val hadFocus: Boolean = vb.etChat.hasFocus()
            TAPUtils.showKeyboard(this, vb.etChat)
            Handler(mainLooper).postDelayed({ vb.etChat.requestFocus() }, 300L)
            if (!hadFocus && vb.etChat.selectionEnd == 0) {
                vb.etChat.setSelection(vb.etChat.text.length)
            }
        }
    }

    private fun hideQuoteLayout() {
        vb.etChat.filters = arrayOf<InputFilter>()
        vb.etChat.setText("")
        vm.setQuotedMessage(null, 0, false)
        vm.setForwardedMessages(null, 0)
        val hasFocus: Boolean = vb.etChat.hasFocus()
        if (vb.clQuoteLayout.visibility == View.VISIBLE) {
            runOnUiThread {
                vb.clQuoteLayout.visibility = View.GONE
                if (hasFocus) {
                    vb.clQuoteLayout.post{ vb.etChat.requestFocus() }
                }
            }
        }
    }


    private fun scrollToBottom() {
        vb.ivToBottom.setVisibility(View.GONE)
        vb.rvMessageList.scrollToPosition(0)
        vm.isOnBottom = true
        vm.clearUnreadMessages()
        vm.clearUnreadMentions()
    }

    private fun toggleCustomKeyboard() {
        if (vb.rvCustomKeyboard.visibility == View.VISIBLE) {
            showNormalKeyboard()
        }
        else {
            showCustomKeyboard()
        }
    }

    private fun showNormalKeyboard() {
        runOnUiThread {
            vb.rvCustomKeyboard.visibility = View.GONE
            vb.ivChatMenuArea.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu_ripple))
            vb.ivChatMenu.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_ic_burger_white
                )
            )
            vb.ivChatMenu.setColorFilter(
                ContextCompat.getColor(
                    TapTalk.appContext,
                    R.color.tapIconChatComposerBurgerMenu
                )
            )
            TAPUtils.showKeyboard(this, vb.etChat)
        }
    }

    private fun showCustomKeyboard() {
        runOnUiThread {
            TAPUtils.dismissKeyboard(this)
            vb.etChat.clearFocus()
            Handler().postDelayed({
                vb.rvCustomKeyboard.visibility = View.VISIBLE
                vb.ivChatMenuArea.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_show_keyboard_ripple))
                vb.ivChatMenu.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@TapScheduledMessageActivity,
                        R.drawable.tap_ic_keyboard_white
                    )
                )
                vb.ivChatMenu.setColorFilter(
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
            vb.rvCustomKeyboard.visibility = View.GONE
            vb.ivChatMenuArea.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu_ripple))
            vb.ivChatMenu.setImageDrawable(
                ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_ic_burger_white
                )
            )
            vb.ivChatMenu.setColorFilter(
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
            }
            else if (message != null) {
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
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON)
                    TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity, SEND_FILE)
//                }
            //                else {
//                    TAPUtils.openDocumentPicker(this@TapScheduledMessageActivity)
//                }
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
                    Toast.makeText(this@TapScheduledMessageActivity, R.string.tap_error_unable_to_send_sms, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSaveImageToGallery(message: TAPMessageModel) {
                if (!TAPUtils.hasPermissions(
                        this@TapScheduledMessageActivity,
                        *TAPUtils.getStoragePermissions(false)
                    )
                ) {
                    // Request storage permission
                    vm.pendingDownloadMessage = message
                    ActivityCompat.requestPermissions(
                        this@TapScheduledMessageActivity,
                        TAPUtils.getStoragePermissions(false),
                        PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE
                    )
                }
                else if (null != message.data) {
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
//                        }
//                        else if (null != fileUrl && fileUrl.isNotEmpty()) {
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
                                    TAPUtils.getMimeTypeFromMessage(message),
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
                        *TAPUtils.getStoragePermissions(false)
                    )
                ) {
                    // Request storage permission
                    vm.pendingDownloadMessage = message
                    ActivityCompat.requestPermissions(
                        this@TapScheduledMessageActivity,
                        TAPUtils.getStoragePermissions(false),
                        PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO
                    )
                }
                else if (null != message.data) {
                    val fileID = message.data!![MessageData.FILE_ID] as String?
                    val fileUrl = message.data!![FILE_URL] as String?
                    if ((!fileID.isNullOrEmpty() || !fileUrl.isNullOrEmpty())) {
                        vm.pendingDownloadMessage = null
                        writeFileToDisk(message)
                    }
                }
            }

            override fun onSaveToDownloads(message: TAPMessageModel) {
                if (null != message.data) {
                    val fileID = message.data!![MessageData.FILE_ID] as String?
                    val fileUrl = message.data!![FILE_URL] as String?
                    if ((!fileID.isNullOrEmpty() || !fileUrl.isNullOrEmpty())) {
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
                }
                else {
                    val user =
                        TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username)
                    if (null != user) {
                        TAPChatManager.getInstance(instanceKey)
                            .triggerUserMentionTapped(this@TapScheduledMessageActivity, message, user, false)
                    }
                    else {
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
                }
                else {
                    val user =
                        TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username)
                    if (null != user) {
                        TapUI.getInstance(instanceKey).openChatRoomWithOtherUser(this@TapScheduledMessageActivity, user)
                        closeActivity()
                    }
                    else {
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
                if (message == null) {
                    return
                }
                svm.pendingScheduledMessageType = TYPE_UNREAD_MESSAGE_IDENTIFIER
                svm.pendingScheduledMessageTime = message.created
                svm.pendingRescheduledMessage = message
                val timePicker = TapTimePickerBottomSheetFragment(svm, getRescheduledMessageListener(message), message.created)
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
            if ((messageModel.type == MessageType.TYPE_TEXT || messageModel.type == MessageType.TYPE_LINK) && !TextUtils.isEmpty(message)) {
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
            }
            else if (messageModel.type == TYPE_IMAGE || messageModel.type == TYPE_VIDEO) {
                val data = messageModel.data
                if (data != null) {
                    data[MessageData.CAPTION] = message
                    messageModel.data = data
                }
            }
            else {
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
        }
        else if (!TextUtils.isEmpty(message)) {
            val firstUrl = vm.linkHashMap[MessageData.URL]
            if (!firstUrl.isNullOrEmpty()) {
                // send message as link
                val data = HashMap<String, Any?>()
                data[MessageData.TITLE] = vm.linkHashMap[MessageData.TITLE]
                data[MessageData.DESCRIPTION] = vm.linkHashMap[MessageData.DESCRIPTION]
                data[MessageData.IMAGE] = vm.linkHashMap[MessageData.IMAGE]
                data[MessageData.TYPE] = vm.linkHashMap[MessageData.TYPE]
                data[MessageData.URL] = firstUrl
                TAPChatManager.getInstance(instanceKey).sendLinkMessageWithRoomModel(message, vm.room, data, scheduledTime)
            }
            else {
                // send message as text
                TAPChatManager.getInstance(instanceKey).sendTextMessageWithRoomModel(message, vm.room, scheduledTime)
            }
            vb.rvMessageList.post{ scrollToBottom() }
        }
        vb.etChat.setText("")
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
        }
        else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread {
                // Remove empty chat layout if still shown
                if (null == newMessage.isHidden || !newMessage.isHidden!!) {
                     if (vb.llEmptyScheduledMessage.visibility == View.VISIBLE) {
                         vb.llEmptyScheduledMessage.visibility = View.GONE
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
                    if (TYPE_IMAGE == newMessage.type && ownMessage) {
                        TAPFileUploadManager.getInstance(instanceKey)
                            .removeUploadProgressMap(newMessage.localID)
                    }
                }
                else {
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
                    }
                    else if (ownMessage && !(newMessage.action == SystemMessageAction.PIN_MESSAGE || newMessage.action == SystemMessageAction.UNPIN_MESSAGE)) {
                        // Scroll recycler to bottom if own message
                        scrollToBottom()
                    }
                    else {
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
        }
        else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread {
                // Remove empty chat layout if still shown
                if (null == newMessage.isHidden || !newMessage.isHidden!!) {
                    if (vb.llEmptyScheduledMessage.visibility == View.VISIBLE) {
                        vb.llEmptyScheduledMessage.visibility = View.GONE
                    }
                    showMessageList()
                }
            }
            updateMessageMentionIndexes(newMessage)
            var previousMessage : TAPMessageModel? = null
            for (message in messageAdapter.items) {
                if (scheduledTime < message.created) {
                    previousMessage = message
                }
                else {
                    break
                }
            }
            val index = if (previousMessage == null) {
                0
            }
            else {
                messageAdapter.items.indexOf(previousMessage) + 1
            }
            val currentDate = TAPTimeFormatter.formatDate(newMessage.created)
            if ((null == newMessage.isHidden || !newMessage.isHidden!!) &&
                newMessage.type != MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                newMessage.type != MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER &&
                newMessage.type != MessageType.TYPE_DATE_SEPARATOR &&
                (null == previousMessage || currentDate != TAPTimeFormatter.formatDate(previousMessage.created))
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
                vb.ivToBottom.visibility = View.GONE
                vb.rvMessageList.scrollToPosition(0)
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
            }
            else {
                // Scroll to message
                runOnUiThread {
                    messageLayoutManager.scrollToPositionWithOffset(
                        messageAdapter.items.indexOf(message), TAPUtils.dpToPx(128)
                    )
                    vb.rvMessageList.post {
                        if (messageLayoutManager.findFirstVisibleItemPosition() > 0) {
                            vm.isOnBottom = false
                            vb.ivToBottom.visibility = View.VISIBLE
                        }
                        messageAdapter.highlightMessage(message)
                    }
                }
            }
        }
        else {
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
        }
        else {
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
                // Show/hide vb.ivToBottom
                if (messageLayoutManager.findFirstVisibleItemPosition() <= vm.firstVisibleItemIndex) {
                    vm.isOnBottom = true
                    vb.ivToBottom.visibility = View.GONE
                    vm.clearUnreadMessages()
                }
                else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.firstVisibleItemIndex && !vm.isScrollFromKeyboard) {
                    vm.isOnBottom = false
                    vb.ivToBottom.visibility = View.VISIBLE
                }
                else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.firstVisibleItemIndex) {
                    vm.isOnBottom = false
                    vb.ivToBottom.visibility = View.VISIBLE
                    vm.isScrollFromKeyboard = false
                }
            }
        }
    
    private fun setLinkRunnable(text: String): Runnable {
        return object : Runnable {
            var firstUrl = TAPUtils.getFirstUrlFromString(text)
            override fun run() {
                if (firstUrl.isNotEmpty() && !firstUrl.startsWith("http://") && !firstUrl.startsWith("https://")) {
                    firstUrl = "http://$firstUrl"
                }
                if (firstUrl.isNotEmpty() && firstUrl != vm.linkHashMap[MessageData.URL]) {
                    // Contains url
                    showLinkPreview(firstUrl)
                    Observable.fromCallable {
                        val linkMap = HashMap<String, String>()
                        val document: Document = Jsoup.connect(firstUrl).get()
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
                                            vb.etChat.text.toString()
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
                                            vb.etChat.text.toString()
                                        )
                                    )
                                ) {
                                    hideLinkPreview(true)
                                }
                            }

                            override fun onCompleted() {}
                        })
                }
                else {
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
            if (s.isNotEmpty() && s.toString().trim { it <= ' ' }.isNotEmpty()) {
                if (TapUI.getInstance(instanceKey).isLinkPreviewInMessageEnabled) {
                    // Delay 0.3 sec before url check
                    linkRunnable = setLinkRunnable(s.toString())
                    linkHandler.postDelayed(linkRunnable, 300)
                }
                // Hide chat menu and enable send button when EditText is filled
                vb.ivChatMenu.setVisibility(View.GONE)
                vb.ivChatMenuArea.setVisibility(View.GONE)
                if (vm.quoteAction == QuoteAction.EDIT) {
                    var caption = if (vm.quotedMessage.data != null) vm.quotedMessage.data!![MessageData.CAPTION] else ""
                    caption = caption ?: ""
                    if ((vm.quotedMessage.type == MessageType.TYPE_TEXT || vm.quotedMessage.type == MessageType.TYPE_LINK) &&
                        vm.quotedMessage.body == s.toString() ||
                        (vm.quotedMessage.type == TYPE_IMAGE || vm.quotedMessage.type == TYPE_VIDEO) &&
                        caption == s.toString()
                    ) {
                        setSendButtonDisabled()
                    }
                    else {
                        setSendButtonEnabled()
                    }
                }
                else {
                    setSendButtonEnabled()
                }
                checkAndSearchUserMentionList()
                //checkAndHighlightTypedText();
            }
            else if (s.length > 0) {
                // Hide chat menu but keep send button disabled if trimmed text is empty
                vb.ivChatMenu.setVisibility(View.GONE)
                vb.ivChatMenuArea.setVisibility(View.GONE)
                setSendButtonDisabled()
                hideUserMentionList()
                hideLinkPreview(true)
            }
            else {
                if (vm.isCustomKeyboardEnabled && s.isEmpty()) {
                    // Show chat menu if text is empty
                    vb.ivChatMenu.visibility = View.VISIBLE
                    vb.ivChatMenuArea.visibility = View.VISIBLE
                }
                else {
                    vb.ivChatMenu.visibility = View.GONE
                    vb.ivChatMenuArea.visibility = View.GONE
                }
                var caption = if (vm.quotedMessage != null && vm.quotedMessage
                        .data != null
                ) vm.quotedMessage.data!![MessageData.CAPTION] else ""
                caption = caption ?: ""
                if (vm.quoteAction == QuoteAction.FORWARD ||
                    vm.quoteAction == QuoteAction.EDIT &&
                    (vm.quotedMessage.type == TYPE_IMAGE || vm.quotedMessage.type == TYPE_VIDEO) &&
                    caption != s.toString()
                ) {
                    // Enable send button if message to forward exists
                    setSendButtonEnabled()
                }
                else {
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
        vb.tvLinkContent.text = url
        vb.clLink.visibility = View.VISIBLE
        vm.clearLinkHashMap()
    }

    private fun updateLinkPreview(linkTitle: String, linkContent: String?, imageUrl: String?) {
        vb.tvLinkTitle.text = linkTitle
        vb.tvLinkTitle.setTextColor(ContextCompat.getColor(this, R.color.tapTitleLabelColor))
        if (linkContent.isNullOrEmpty()) {
            vb.tvLinkContent.visibility = View.GONE
        }
        else {
            vb.tvLinkContent.visibility = View.VISIBLE
            vb.tvLinkContent.text = linkContent
        }
        if (imageUrl != null && imageUrl.isEmpty()) {
            vb.rcivLink.setVisibility(View.GONE)
        }
        else {
            vb.rcivLink.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tap_bg_white_rounded_8dp
                )
            )
            vb.rcivLink.setPadding(0, 0, 0, 0)
            glide.load(imageUrl).fitCenter().into(vb.rcivLink)
            vb.rcivLink.setVisibility(View.VISIBLE)
        }
    }

    private fun hideLinkPreview(isClearLinkMap: Boolean) {
        if (isClearLinkMap) {
            vm.clearLinkHashMap()
        }
        else {
            if (vm.quotedMessage != null) {
                vb.clQuoteLayout.visibility = View.VISIBLE
            }
        }
        vb.clLink.visibility = View.GONE
        vb.tvLinkTitle.setText(R.string.tap_loading_dots)
        vb.tvLinkTitle.setTextColor(ContextCompat.getColor(this, R.color.tapMediaLinkColor))
        vb.tvLinkContent.text = ""
        vb.tvLinkContent.visibility = View.VISIBLE
        glide.load(R.drawable.tap_ic_link_white).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(vb.rcivLink)
        val padding = TAPUtils.dpToPx(8)
        vb.rcivLink.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_rounded_primary_8dp)
        vb.rcivLink.setPadding(padding, padding, padding, padding)
        vb.rcivLink.visibility = View.VISIBLE
    }

    private fun setSendButtonDisabled() {
        vb.ivSendArea.setImageDrawable(
            ContextCompat.getDrawable(
                this@TapScheduledMessageActivity,
                R.drawable.tap_bg_chat_composer_send_inactive_ripple
            )
        )
        vb.ivSend.setColorFilter(
            ContextCompat.getColor(
                TapTalk.appContext,
                R.color.tapIconChatComposerSendInactive
            )
        )
        vb.ivSendArea.setEnabled(false)
    }

    private fun setSendButtonEnabled() {
        vb.ivSendArea.setImageDrawable(
            ContextCompat.getDrawable(
                this@TapScheduledMessageActivity,
                R.drawable.tap_bg_chat_composer_send_ripple
            )
        )
        vb.ivSend.setColorFilter(
            ContextCompat.getColor(
                TapTalk.appContext,
                R.color.tapIconChatComposerSend
            )
        )
        vb.ivSendArea.setEnabled(true)
    }


    private fun checkAndSearchUserMentionList() {
        if (TapUI.getInstance(instanceKey).isMentionUsernameDisabled || vm.roomParticipantsByUsername.isEmpty()) {
            hideUserMentionList()
            return
        }
        val s: String = vb.etChat.text.toString()
        if (!s.contains("@")) {
            // Return if text does not contain @
            hideUserMentionList()
            return
        }
        val cursorIndex: Int = vb.etChat.selectionStart
        var loopIndex: Int = vb.etChat.selectionStart
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
                }
                else {
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
        val originalText: String? = if (message.type == MessageType.TYPE_TEXT || message.type == MessageType.TYPE_LINK) {
                message.body
            }
            else if ((message.type == TYPE_IMAGE || message.type == TYPE_VIDEO) && null != message.data) {
                message.data!![MessageData.CAPTION] as String?
            }
            else if (message.type == MessageType.TYPE_LOCATION && null != message.data) {
                message.data!![MessageData.ADDRESS] as String?
            }
            else {
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
                }
                else {
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
                    }
                    else if (endOfMention && startIndex != -1) {
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
            if (mentionIndexes.isNotEmpty()) {
                vm.messageMentionIndexes[message.localID] = mentionIndexes
            }
        }
    }


    private fun showUserMentionList(
        searchResult: List<TAPUserModel>,
        loopIndex: Int,
        cursorIndex: Int
    ) {
        if (searchResult.isNotEmpty()) {
            // Show search result in list
            userMentionListAdapter = TapUserMentionListAdapter(
                searchResult
            ) { user: TAPUserModel ->
                // Append username to typed text
                if (vb.etChat.text.length >= cursorIndex) {
                    vb.etChat.text.replace(loopIndex + 1, cursorIndex, user.username + " ")
                }
            }
            vb.layoutUserMentionList.rvUserMentionList.setMaxHeight(TAPUtils.dpToPx(160))
            vb.layoutUserMentionList.rvUserMentionList.setAdapter(userMentionListAdapter)
            if (null == vb.layoutUserMentionList.rvUserMentionList.layoutManager) {
                vb.layoutUserMentionList.rvUserMentionList.setLayoutManager(object : LinearLayoutManager(
                    this@TapScheduledMessageActivity, VERTICAL, false
                ) {
                    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                        try {
                            super.onLayoutChildren(recycler, state)
                        }
                        catch (e: java.lang.IndexOutOfBoundsException) {
                            e.printStackTrace()
                        }
                    }
                })
            }
            vb.layoutUserMentionList.clUserMentionList.visibility = View.VISIBLE
        }
        else {
            // Result is empty
            hideUserMentionList()
        }
    }

    private fun hideUserMentionList() {
        val hasFocus: Boolean = vb.etChat.hasFocus()
        vb.layoutUserMentionList.clUserMentionList.visibility = View.GONE
        if (hasFocus) {
            vb.layoutUserMentionList.clUserMentionList.post {
                vb.layoutUserMentionList.rvUserMentionList.adapter = null
                vb.layoutUserMentionList.rvUserMentionList.post(Runnable { vb.etChat.requestFocus() })
            }
        }
    }

    private val chatFocusChangeListener =
        OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && vm.isCustomKeyboardEnabled) {
                vm.isScrollFromKeyboard = true
                vb.rvCustomKeyboard.visibility = View.GONE
                vb.ivChatMenuArea.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@TapScheduledMessageActivity,
                        R.drawable.tap_bg_chat_composer_burger_menu_ripple
                    )
                )
                vb.ivChatMenu.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@TapScheduledMessageActivity,
                        R.drawable.tap_ic_burger_white
                    )
                )
                vb.ivChatMenu.setColorFilter(
                    ContextCompat.getColor(
                        this@TapScheduledMessageActivity,
                        R.color.tapIconChatComposerBurgerMenu
                    )
                )
                vb.etChat.background = ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_bg_chat_composer_text_field_active
                )
                TAPUtils.showKeyboard(this@TapScheduledMessageActivity, vb.etChat)
                if (vb.etChat.text.toString().isNotEmpty()) {
                    vb.ivChatMenu.visibility = View.GONE
                    vb.ivChatMenuArea.visibility = View.GONE
                }
            }
            else if (hasFocus) {
                vm.isScrollFromKeyboard = true
                vb.etChat.background = ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_bg_chat_composer_text_field_active
                )
                TAPUtils.showKeyboard(this@TapScheduledMessageActivity, vb.etChat)
            }
            else {
                vb.etChat.background = ContextCompat.getDrawable(
                    this@TapScheduledMessageActivity,
                    R.drawable.tap_bg_chat_composer_text_field
                )
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
            vb.layoutPopupLoadingScreen.ivLoadingImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tap_ic_loading_progress_circle_white
                )
            )
            if (null == vb.layoutPopupLoadingScreen.ivLoadingImage.animation) {
                TAPUtils.rotateAnimateInfinitely(this, vb.layoutPopupLoadingScreen.ivLoadingImage)
            }
            vb.layoutPopupLoadingScreen.tvLoadingText.visibility = View.INVISIBLE
            vb.layoutPopupLoadingScreen.flLoading.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                if (vb.layoutPopupLoadingScreen.flLoading.visibility == View.VISIBLE) {
                    vb.layoutPopupLoadingScreen.tvLoadingText.text = getString(R.string.tap_cancel)
                    vb.layoutPopupLoadingScreen.tvLoadingText.visibility = View.VISIBLE
                }
            }, 1000L)
        }
    }

    private fun hideLoadingPopup() {
        runOnUiThread { vb.layoutPopupLoadingScreen.flLoading.visibility = View.GONE }
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

    private val backgroundBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            val localID: String?
            val fileUri: Uri?
            when (action) {
                UploadProgressLoading -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    if (vm.messagePointer.containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(vm.messagePointer[localID]))
                    }
                }
                UploadProgressFinish -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    val messageModel = vm.messagePointer[localID]
                    if (vm.messagePointer.containsKey(localID) && intent.hasExtra(
                            UploadBroadcastEvent.UploadImageData
                        ) &&
                        intent.getSerializableExtra(UploadBroadcastEvent.UploadImageData) is HashMap<*, *>
                    ) {
                        // Set image data
                        messageModel?.data =
                            intent.getSerializableExtra(UploadBroadcastEvent.UploadImageData) as HashMap<String?, Any?>?
                    }
                    else if (vm.messagePointer.containsKey(localID) && intent.hasExtra(
                            UploadBroadcastEvent.UploadFileData
                        ) &&
                        intent.getSerializableExtra(UploadBroadcastEvent.UploadFileData) is HashMap<*, *>
                    ) {
                        // Put file data
                        messageModel?.putData(intent.getSerializableExtra(UploadBroadcastEvent.UploadFileData) as HashMap<String?, Any?>?)
                    }
                    messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(messageModel))
                }
                UploadFailed -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    if (vm.messagePointer.containsKey(localID)) {
                        val failedMessageModel = vm.messagePointer[localID]
                        failedMessageModel?.isFailedSend = true
                        failedMessageModel?.isSending = false
                        messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(failedMessageModel))
                    }
                }
                UploadCancelled -> {
                    localID = intent.getStringExtra(UploadBroadcastEvent.UploadLocalID)
                    if (vm.messagePointer.containsKey(localID)) {
                        val cancelledMessageModel = vm.messagePointer[localID]
                        vm.delete(localID)
                        val itemPos: Int = messageAdapter.items.indexOf(cancelledMessageModel)
                        TAPFileUploadManager.getInstance(instanceKey).cancelUpload(
                            this@TapScheduledMessageActivity, cancelledMessageModel,
                            vm.room.roomID
                        )
                        vm.removeFromUploadingList(localID)
                        vm.removeMessagePointer(localID)
                        messageAdapter.removeMessageAt(itemPos)
                    }
                }
                DownloadProgressLoading, DownloadFinish -> {
                    localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
                    if (vm.messagePointer.containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(vm.messagePointer[localID]))
                    }
                }
                DownloadFailed -> {
                    localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
                    TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(localID)
                    if (vm.messagePointer.containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(vm.messagePointer[localID]))
                    }
                }
                DownloadFile -> startFileDownload(
                    intent.getParcelableExtra(MESSAGE)
                )
                CancelDownload -> {
                    localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
                    TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(localID)
                    if (vm.messagePointer.containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.items.indexOf(vm.messagePointer[localID]))
                    }
                }
            }
        }
    }

    private val foregroundBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            val localID: String?
            val fileUri: Uri?
            when (action) {
                OpenFile -> {
                    val message = intent.getParcelableExtra<TAPMessageModel>(MESSAGE)
                    fileUri = intent.getParcelableExtra(MessageData.FILE_URI)
                    vm.openedFileMessage = message
                    if (null != fileUri && null != message!!.data) {
                        if (!TAPUtils.openFile(
                                instanceKey,
                                this@TapScheduledMessageActivity,
                                fileUri,
                                TAPUtils.getMimeTypeFromMessage(message)
                            )
                        ) {
                            showDownloadFileDialog()
                        }
                    }
                    else {
                        showDownloadFileDialog()
                    }
                }
                LongPressChatBubble -> {
                    if (null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val chatBubbleBottomSheet = TAPLongPressActionBottomSheet.newInstance(
                            instanceKey,
                            LongPressType.SCHEDULED_TYPE,
                            (intent.getParcelableExtra<Parcelable>(MESSAGE) as TAPMessageModel?)!!,
                            longPressListener
                        )
                        chatBubbleBottomSheet.show(supportFragmentManager, "")
                        TAPUtils.dismissKeyboard(this@TapScheduledMessageActivity)
                    }
                }
                LongPressLink -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE)
                    ) {
                        val linkBottomSheet = TAPLongPressActionBottomSheet.newInstance(
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
                LongPressEmail -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE) &&
                        null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val emailBottomSheet = TAPLongPressActionBottomSheet.newInstance(
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
                LongPressPhone -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE) &&
                        null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val phoneBottomSheet = TAPLongPressActionBottomSheet.newInstance(
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
                LongPressMention -> {
                    if (null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE) &&
                        null != intent.getParcelableExtra(MESSAGE) &&
                        intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
                    ) {
                        val mentionBottomSheet = TAPLongPressActionBottomSheet.newInstance(
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
                REOPEN_ATTACHMENT_BOTTOM_SHEET -> {
                    openAttachMenu()
                }
                REOPEN_TIME_PICKER_BOTTOM_SHEET -> {
                    var listener: TAPGeneralListener<Long>? = null
                    val messageType = svm.pendingScheduledMessageType
                    if (messageType == TYPE_TEXT) {
                        listener = getTextScheduledMessageListener(svm.pendingScheduledMessageText)
                    }
                    else if (messageType == TYPE_IMAGE && svm.pendingScheduledMessageMedias.isNotEmpty()) {
                        listener = getMediaScheduledMessageListener(svm.pendingScheduledMessageMedias)
                    }
                    else if (messageType == TYPE_FILE && svm.pendingScheduledMessageFile != null) {
                        listener = getFileScheduledMessageListener(svm.pendingScheduledMessageFile!!)
                    }
                    else if (messageType == TYPE_LOCATION) {
                        listener = getLocationScheduledMessageListener(svm.pendingScheduledMessageText, svm.pendingScheduledMessageLatitude, svm.pendingScheduledMessageLongitude)
                    }
                    else if (messageType == TYPE_UNREAD_MESSAGE_IDENTIFIER && svm.pendingRescheduledMessage != null) {
                        listener = getRescheduledMessageListener(svm.pendingRescheduledMessage!!)
                    }
                    if (listener != null) {
                        val timePicker: TapTimePickerBottomSheetFragment = if (System.currentTimeMillis() > svm.pendingScheduledMessageTime) {
                            TapTimePickerBottomSheetFragment(svm, listener)
                        }
                        else {
                            TapTimePickerBottomSheetFragment(svm, listener, svm.pendingScheduledMessageTime)
                        }
                        timePicker.show(supportFragmentManager, "")
                    }
                }
            }
        }
    }

    private fun startFileDownload(message: TAPMessageModel?) {
        if (!TAPUtils.hasPermissions(this, *TAPUtils.getStoragePermissions(true))) {
            // Request storage permission
            vm.pendingDownloadMessage = message
            ActivityCompat.requestPermissions(
                this@TapScheduledMessageActivity,
                TAPUtils.getStoragePermissions(true),
                PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            )
        }
        else {
            // Download file
            vm.pendingDownloadMessage = null
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message)
        }
    }

    private fun showDownloadFileDialog() {
        // Prompt download if file does not exist
        if (null == vm.openedFileMessage) {
            return
        }
        if (null != vm.openedFileMessage.data) {
            val fileId = vm.openedFileMessage.data!![MessageData.FILE_ID] as String?
            var fileUrl = vm.openedFileMessage.data!![FILE_URL] as String?
            if (null != fileUrl) {
                fileUrl = TAPUtils.getUriKeyFromUrl(fileUrl)
            }
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.room.roomID, fileId)
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.room.roomID, fileUrl)
        }
        messageAdapter.notifyItemChanged(
            messageAdapter.items.indexOf(vm.openedFileMessage)
        )
        TapTalkDialog.Builder(this@TapScheduledMessageActivity)
            .setTitle(getString(R.string.tap_error_could_not_find_file))
            .setMessage(getString(R.string.tap_error_redownload_file))
            .setCancelable(true)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setPrimaryButtonListener { v: View? ->
                startFileDownload(
                    vm.openedFileMessage
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
                if (vm.messagePointer.containsKey(localID)) {
                    runOnUiThread {
                        messageAdapter.notifyItemChanged(
                            messageAdapter.items.indexOf(vm.messagePointer[localID])
                        )
                    }
                }
            }
            //TAPFileDownloadManager.getInstance(instanceKey).clearFailedDownloads();
        }
    }

    private fun setOtherUserModel(otherUserModel: TAPUserModel) {
        if (TAPUtils.isSavedMessagesRoom(vm.room.roomID, instanceKey)) {
            vm.otherUserModel = TAPChatManager.getInstance(instanceKey).activeUser
        }
        else {
            vm.otherUserModel = otherUserModel
        }
    }

    private fun getTextScheduledMessageListener(text: String): TAPGeneralListener<Long> {
        val listener = object : TAPGeneralListener<Long>() {
            override fun onClick(position: Int, item: Long?) {
                buildAndSendTextOrLinkMessage(text, item)
            }
        }
        return listener
    }

    private fun getMediaScheduledMessageListener(medias: ArrayList<TAPMediaPreviewModel>): TAPGeneralListener<Long> {
        val listener = object : TAPGeneralListener<Long>() {
            override fun onClick(position: Int, item: Long?) {
                for (media in medias) {
                    if (media.type == TYPE_IMAGE) {
                        TAPChatManager.getInstance(instanceKey).createImageMessageModelAndAddToUploadQueue(this@TapScheduledMessageActivity, vm.room, media.uri, media.caption, item)
                    }
                    else if (media.type == TYPE_VIDEO) {
                        TAPChatManager.getInstance(instanceKey).createVideoMessageModelAndAddToUploadQueue(this@TapScheduledMessageActivity, vm.room, media.uri, media.caption, item)
                    }
                }
            }
        }
        return listener
    }

    private fun getFileScheduledMessageListener(file: File): TAPGeneralListener<Long> {
        val listener = object : TAPGeneralListener<Long>() {
            override fun onClick(position: Int, item: Long?) {
                TAPChatManager.getInstance(instanceKey).sendFileMessage(this@TapScheduledMessageActivity, vm.room, file, item)
            }
        }
        return listener
    }

    private fun getLocationScheduledMessageListener(address: String, latitude: Double, longitude: Double): TAPGeneralListener<Long> {
        val listener = object : TAPGeneralListener<Long>() {
            override fun onClick(position: Int, item: Long?) {
                TAPChatManager.getInstance(instanceKey).sendLocationMessage(vm.room, address, latitude, longitude, item)
            }
        }
        return listener
    }

    private fun getRescheduledMessageListener(message: TAPMessageModel): TAPGeneralListener<Long> {
        val listener = object : TAPGeneralListener<Long>() {
            override fun onClick(position: Int, item: Long?) {
                TAPDataManager.getInstance(instanceKey).editScheduledMessageTime(message.messageID?.toInt() ?: 0, item, commonView)
            }
        }
        return listener
    }
}
