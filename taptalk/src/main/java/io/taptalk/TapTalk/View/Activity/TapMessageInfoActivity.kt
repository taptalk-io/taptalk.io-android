package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.media.MediaPlayer.OnSeekCompleteListener
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.PlayPauseVoiceNote
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_PINNED
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_STARRED
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TapCoreGetMessageDetailsListener
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapMessageInfoAdapter
import io.taptalk.TapTalk.ViewModel.TapMessageInfoViewModel
import io.taptalk.TapTalk.databinding.TapActivityMessageInfoBinding

class TapMessageInfoActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityMessageInfoBinding
    private lateinit var adapter: TapMessageInfoAdapter

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
            isPinned: Boolean,
            transitionView: View?
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapMessageInfoActivity::class.java)
                intent.putExtra(INSTANCE_KEY, instanceKey)
                intent.putExtra(MESSAGE, message)
                intent.putExtra(ROOM, room)
                intent.putExtra(IS_STARRED, isStarred)
                intent.putExtra(IS_PINNED, isPinned)
                val options = transitionView?.let {
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context,
                        it,
                        context.getString(R.string.tap_transition_view_message)
                    )
                }
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER, options?.toBundle())
                context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityMessageInfoBinding.inflate(layoutInflater)
        setContentView(vb.root)
        if (intent.getParcelableExtra<TAPMessageModel>(MESSAGE) != null) {
            vm.message = intent.getParcelableExtra(MESSAGE)
        }
        if (vm.message == null) {
            onBackPressed()
        }
        if (intent.getParcelableExtra<TAPRoomModel>(ROOM) != null) {
            vm.room = intent.getParcelableExtra(ROOM)
        }
        vm.isStarred = intent.getBooleanExtra(IS_STARRED, false)
        vm.isPinned = intent.getBooleanExtra(IS_PINNED, false)

        val resultList = listOf(TapMessageRecipientModel())
        adapter = TapMessageInfoAdapter(instanceKey, vm.message!!, resultList)
        vb.rvMessageInfo.adapter = adapter
        vb.rvMessageInfo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        vb.rvMessageInfo.setHasFixedSize(true)
        vb.rvMessageInfo.addItemDecoration(
            TAPVerticalDecoration(
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(16),
                0
            )
        )

        vb.ivButtonBack.setOnClickListener { onBackPressed() }

        supportPostponeEnterTransition()

        TAPBroadcastManager.register(
            this@TapMessageInfoActivity,
            broadcastReceiver,
            UploadProgressLoading,
            UploadProgressFinish,
            UploadFailed,
            UploadCancelled,
            DownloadProgressLoading,
            DownloadFinish,
            DownloadFailed,
            DownloadFile,
            OpenFile,
            CancelDownload,
            PlayPauseVoiceNote
        )
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun onResume() {
        super.onResume()
        if (!vm.isFirstLoadFinished) {
            vb.llLoading.llLoadingContainer.visibility = View.VISIBLE
        }
        TapCoreMessageManager.getInstance(instanceKey).getMessageDetails(vm.message?.messageID, messageInfoView)
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(this, broadcastReceiver)
    }

    private val messageInfoView = object : TapCoreGetMessageDetailsListener() {
        override fun onSuccess(
            message: TAPMessageModel,
            deliveredTo: List<TapMessageRecipientModel>?,
            readBy: List<TapMessageRecipientModel>?
        ) {
            vb.llLoading.llLoadingContainer.visibility = View.GONE

            vm.readList.clear()
            val resultList = ArrayList<TapMessageRecipientModel>()
            if (readBy != null) {
                vm.readList.addAll(ArrayList(readBy))
            }
            vm.deliveredList.clear()
            if (deliveredTo != null) {
                vm.deliveredList.addAll(ArrayList(deliveredTo))
            }

            resultList.add(TapMessageRecipientModel())
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
            adapter.items = resultList

            vb.rvMessageInfo.post {
                addStickyHeaderScrollListener(vm.readList.size, vm.deliveredList.size)
                val layoutManager = vb.rvMessageInfo.layoutManager as LinearLayoutManager?
                if (!vm.isFirstLoadFinished) {
                    vm.isFirstLoadFinished = true
                    layoutManager?.scrollToPositionWithOffset(1, TAPUtils.getScreenHeight() / 2)
                }
            }
        }

        override fun onError(errorCode: String?, errorMessage: String?) {
            vb.llLoading.llLoadingContainer.visibility = View.GONE
            Toast.makeText(this@TapMessageInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun addStickyHeaderScrollListener(readCount: Int, deliveredCount: Int) {
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Handle sticky section header
                if (readCount <= 0 && deliveredCount <= 0) {
                    vb.flStickySectionContainer.visibility = View.GONE
                    return
                }

                val layoutManager = vb.rvMessageInfo.layoutManager as LinearLayoutManager?
                val firstVisibleIndex = layoutManager?.findFirstVisibleItemPosition() ?: -1

                if (firstVisibleIndex < 0 || firstVisibleIndex >= (adapter.itemCount)) {
                    return
                }

                val firstVisibleItem = adapter.getItemAt(firstVisibleIndex)

                if (null == firstVisibleItem/* || binding.clStickySectionContainer.visibility != View.VISIBLE*/) {
                    return
                }

                if (
                    firstVisibleItem.userID.isNullOrEmpty() &&
                    (firstVisibleItem.readTime == null || firstVisibleItem.readTime <= 0L) &&
                    (firstVisibleItem.deliveredTime == null || firstVisibleItem.deliveredTime <= 0L)
                ) {
                    vb.flStickySectionContainer.visibility = View.GONE
                    return
                }

                if (firstVisibleItem.readTime != null && firstVisibleItem.readTime > 0L) {
                    // Set Read by
                    vb.tvStickySectionTitle.text = String.format(getString(R.string.tap_read_by_d_format), readCount)
                    vb.ivStickySectionIcon.setImageDrawable(ContextCompat.getDrawable(this@TapMessageInfoActivity, R.drawable.tap_ic_read_orange))
                }
                else if (firstVisibleItem.deliveredTime != null && firstVisibleItem.deliveredTime > 0L) {
                    // Set Delivered to
                    vb.tvStickySectionTitle.text = String.format(getString(R.string.tap_delivered_to_d_format), deliveredCount)
                    vb.ivStickySectionIcon.setImageDrawable(ContextCompat.getDrawable(this@TapMessageInfoActivity, R.drawable.tap_ic_delivered_grey))
                }

                val secondVisibleCell: View? = layoutManager?.findViewByPosition(firstVisibleIndex + 1)
                val secondVisibleItem = adapter.getItemAt(firstVisibleIndex + 1)

                if (null == secondVisibleCell || null == secondVisibleItem) {
                    return
                }

                val recyclerViewLocation = IntArray(2)
                vb.rvMessageInfo.getLocationOnScreen(recyclerViewLocation)
                val recyclerViewY = recyclerViewLocation[1]

                vb.flStickySectionContainer.visibility = View.VISIBLE

                if (secondVisibleItem.userID.isNullOrEmpty()) {
                    // Animate sticky header translation if second visible item is header
                    val cellLocation = IntArray(2)
                    secondVisibleCell.getLocationOnScreen(cellLocation)

                    val heightDiff = cellLocation[1] - recyclerViewY
                    val headerHeight = vb.flStickySectionContainer.height
                    if (heightDiff < headerHeight) {
                        vb.clStickySectionContainer.translationY = -((headerHeight - heightDiff).toFloat())
                    }
                    else {
                        vb.clStickySectionContainer.translationY = 0f
                    }
                }
                else {
                    vb.clStickySectionContainer.translationY = 0f
                }
            }
        }
        vb.rvMessageInfo.clearOnScrollListeners()
        vb.rvMessageInfo.addOnScrollListener(onScrollListener)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            var localID = intent.getStringExtra(DownloadLocalID)
            if (localID.isNullOrEmpty()) {
                localID = intent.getStringExtra(UploadLocalID)
            }
            val fileUri: Uri?
            var broadcastMessage: TAPMessageModel? = null
            if (null != intent.getParcelableExtra(MESSAGE) &&
                intent.getParcelableExtra<Parcelable>(MESSAGE) is TAPMessageModel
            ) {
                broadcastMessage = intent.getParcelableExtra(MESSAGE)
            }
            when (action) {
                UploadProgressLoading -> {
                    if (vm.message?.localID == localID) {
                        if (broadcastMessage != null) {
                            vm.message = broadcastMessage
                        }
                        adapter.notifyItemChanged(0)
                        adapter.messageAdapter?.notifyItemChanged(0)
                    }
                }

                UploadProgressFinish -> {
                    if (vm.message?.localID == localID &&
                        intent.hasExtra(UploadBroadcastEvent.UploadImageData) &&
                        intent.getSerializableExtra(UploadBroadcastEvent.UploadImageData) is HashMap<*, *>
                    ) {
                        // Set image data
                        vm.message?.data =
                            intent.getSerializableExtra(UploadBroadcastEvent.UploadImageData) as HashMap<String?, Any?>?
                    } else if (vm.message?.localID == localID &&
                        intent.hasExtra(UploadBroadcastEvent.UploadFileData) &&
                        intent.getSerializableExtra(UploadBroadcastEvent.UploadFileData) is HashMap<*, *>
                    ) {
                        // Put file data
                        vm.message?.putData(intent.getSerializableExtra(UploadBroadcastEvent.UploadFileData) as HashMap<String?, Any?>?)
                    }
                    if (broadcastMessage != null) {
                        vm.message = broadcastMessage
                    }
                    adapter.notifyItemChanged(0)
                    adapter.messageAdapter?.notifyItemChanged(0)
                }

                UploadFailed -> {
                    if (vm.message?.localID == localID) {
                        vm.message?.isFailedSend = true
                        vm.message?.isSending = false
                        if (broadcastMessage != null) {
                            vm.message = broadcastMessage
                        }
                        adapter.notifyItemChanged(0)
                        adapter.messageAdapter?.notifyItemChanged(0)
                    }
                }

                UploadCancelled -> {
                    if (vm.message?.localID == localID) {
                        adapter.messageAdapter?.removeMessageAt(0)
                    }
                }

                DownloadProgressLoading, DownloadFinish -> {
                    if (vm.message?.localID == localID) {
                        if (broadcastMessage != null) {
                            vm.message = broadcastMessage
                        }
                        adapter.notifyItemChanged(0)
                        adapter.messageAdapter?.notifyItemChanged(0)
                    }
                }

                DownloadFailed -> {
                    if (vm.message?.localID == localID) {
                        if (broadcastMessage != null) {
                            vm.message = broadcastMessage
                        }
                        adapter.notifyItemChanged(0)
                        adapter.messageAdapter?.notifyItemChanged(0)
                    }
                }

                DownloadFile -> {
                    broadcastMessage?.let { startFileDownload(it) }
                }

                CancelDownload -> {
                    if (vm.message?.localID == localID) {
                        if (broadcastMessage != null) {
                            vm.message = broadcastMessage
                        }
                        adapter.notifyItemChanged(0)
                        adapter.messageAdapter?.notifyItemChanged(0)
                    }
                }

                OpenFile -> {
                    if (broadcastMessage != null && broadcastMessage.room.roomID == vm.message?.room?.roomID) {
                        fileUri = intent.getParcelableExtra(MessageData.FILE_URI)
                        vm.openedFileMessage = broadcastMessage
                        val mimeType = TAPUtils.getMimeTypeFromMessage(broadcastMessage)
                        if (null != fileUri && null != broadcastMessage.data && null != mimeType) {
                            if (!TAPUtils.openFile(
                                    instanceKey,
                                    this@TapMessageInfoActivity,
                                    fileUri,
                                    mimeType
                                )
                            ) {
                                showDownloadFileDialog()
                            }
                        } else {
                            showDownloadFileDialog()
                        }
                    }
                }

                PlayPauseVoiceNote -> {
                    if (broadcastMessage != null && broadcastMessage.room.roomID == vm.message?.room?.roomID) {
                        val isPlaying = intent.getBooleanExtra(MessageData.IS_PLAYING, false)
                        if (isPlaying) {
                            if (vm.mediaPlayer?.isPlaying == true) {
                                pauseVoiceNote()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startFileDownload(message: TAPMessageModel) {
        if (!TAPUtils.hasPermissions(this@TapMessageInfoActivity, *TAPUtils.getStoragePermissions(true))) {
            // Request storage permission
            vm.pendingDownloadMessage = message
            ActivityCompat.requestPermissions(
                this@TapMessageInfoActivity,
                TAPUtils.getStoragePermissions(true),
                TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            )
        } else {
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
        if (null != vm.openedFileMessage?.getData()) {
            val fileId = vm.openedFileMessage?.data?.get(MessageData.FILE_ID) as String? ?: ""
            var fileUrl = vm.openedFileMessage?.data?.get(MessageData.FILE_URL) as String? ?: ""
            if (fileUrl.isNotEmpty()) {
                fileUrl = TAPUtils.getUriKeyFromUrl(fileUrl)
            }
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.message?.room?.roomID ?: "", fileId)
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.message?.room?.roomID ?: "", fileUrl)
        }
        adapter.messageAdapter?.notifyItemChanged(0)
        TapTalkDialog.Builder(this@TapMessageInfoActivity)
            .setTitle(getString(R.string.tap_error_could_not_find_file))
            .setMessage(getString(R.string.tap_error_redownload_file))
            .setCancelable(true)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setPrimaryButtonListener { v: View? ->
                vm.openedFileMessage?.let { startFileDownload(it) }
            }
            .show()
    }

    private fun resumeVoiceNote() {
        try {
            if (vm.mediaPlayer == null) {
                vm.mediaPlayer = MediaPlayer()
                loadMediaPlayer()
                vm.mediaPlayer?.prepareAsync()
            }
            else {
                vm.mediaPlayer?.start()
                if (adapter.messageAdapter?.lastLocalId?.isNotEmpty() == true) {
                    adapter.messageAdapter?.removePlayer()
                    adapter.messageAdapter?.notifyItemChanged(0)
                }
            }
        }
        catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun pauseVoiceNote() {
        vm.isMediaPlaying = false
        vm.pausedPosition = vm.mediaPlayer?.currentPosition ?: 0
        vm.mediaPlayer?.pause()
    }

    private fun loadMediaPlayer() {
        try {
            vm.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            vm.voiceUri?.let { vm.mediaPlayer?.setDataSource(this@TapMessageInfoActivity, it) }
            vm.mediaPlayer?.setOnPreparedListener(preparedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val preparedListener =
        OnPreparedListener { mediaPlayer ->
            vm.voiceDuration = mediaPlayer.duration
            vm.isMediaPlaying = true
            mediaPlayer.seekTo(vm.pausedPosition)
            mediaPlayer.setOnSeekCompleteListener(onSeekListener)
            vm.mediaPlayer = mediaPlayer
            runOnUiThread {
                vm.mediaPlayer?.start()
            }
        }

    private val onSeekListener = OnSeekCompleteListener { mediaPlayer ->
        if (!vm.isSeeking && vm.isMediaPlaying) {
            mediaPlayer.start()
        }
        else {
            vm.pausedPosition = mediaPlayer.currentPosition
            if (vm.pausedPosition >= vm.voiceDuration) {
                vm.pausedPosition = 0
            }
        }
    }
}
