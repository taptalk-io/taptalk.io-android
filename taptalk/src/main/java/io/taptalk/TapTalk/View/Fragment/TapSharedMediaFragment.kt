package io.taptalk.TapTalk.View.Fragment

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.VIEW_IN_CHAT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPTimeFormatter
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TapLongPressInterface
import io.taptalk.TapTalk.Interface.TapSharedMediaInterface
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPCacheManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DOCUMENT
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LINK
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_MEDIA
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity
import io.taptalk.TapTalk.View.Activity.TapSharedMediaActivity
import io.taptalk.TapTalk.View.Adapter.TapSharedMediaAdapter
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.SHARED_MEDIA_TYPE
import io.taptalk.TapTalk.ViewModel.TapSharedMediaViewModel
import io.taptalk.TapTalk.databinding.TapFragmentSharedMediaBinding

class TapSharedMediaFragment(private val instanceKey: String, val type: Int): Fragment() {

    private lateinit var vb : TapFragmentSharedMediaBinding
    private val vm: TapSharedMediaViewModel by lazy {
        ViewModelProvider(this)[TapSharedMediaViewModel::class.java]
    }
    private var sharedMediaAdapter : TapSharedMediaAdapter? = null
    private var sharedMediaGlm: GridLayoutManager? = null
    private var sharedMediaPagingScrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private lateinit var glide: RequestManager

    companion object {
        fun newInstance(instanceKey: String, type: Int, room : TAPRoomModel?): TapSharedMediaFragment {
            val args = Bundle()
            val fragment = TapSharedMediaFragment(instanceKey, type)
            args.putParcelable(ROOM, room)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vb = TapFragmentSharedMediaBinding.inflate(inflater, container, false)
        activity?.let {
            glide = Glide.with(it)
        }
        vm.room = arguments?.getParcelable(ROOM) as TAPRoomModel?
        TAPBroadcastManager.register(
            context,
            downloadProgressReceiver,
            DownloadBroadcastEvent.DownloadProgressLoading,
            DownloadBroadcastEvent.DownloadFinish,
            DownloadBroadcastEvent.DownloadFailed,
            DownloadBroadcastEvent.DownloadFile,
            DownloadBroadcastEvent.OpenFile,
            DownloadBroadcastEvent.CancelDownload
        )
        sharedMediaAdapter = TapSharedMediaAdapter(instanceKey, vm.sharedMediaAdapterItems, glide, sharedMediaAdapterListener)
        sharedMediaGlm = object : GridLayoutManager(context, 3) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                }
                catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        (sharedMediaGlm as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (vm.sharedMediaAdapterItems[position].type == TYPE_IMAGE || vm.sharedMediaAdapterItems[position].type == TYPE_VIDEO) {
                    1
                }
                else {
                    3
                }
            }
        }
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.recyclerView.layoutManager = sharedMediaGlm
        vb.recyclerView.adapter = sharedMediaAdapter
        if (!vm.isFinishedLoading) {
            if (vm.sharedMedias.isEmpty()) {
                vb.recyclerView.visibility = View.GONE
            }
            Thread {
                when (type) {
                    TYPE_MEDIA -> {
                        TAPDataManager.getInstance(instanceKey).getRoomMedias(0L, vm.room?.roomID, sharedMediaListener)
                    }
                    TYPE_LINK -> {
                        TAPDataManager.getInstance(instanceKey).getRoomLinks(0L, vm.room?.roomID, sharedMediaListener)
                    }
                    TYPE_DOCUMENT -> {
                        TAPDataManager.getInstance(instanceKey).getRoomFiles(0L, vm.room?.roomID, sharedMediaListener)
                    }
                }
            }.start()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (TAPUtils.allPermissionsGranted(grantResults)) {
            when (requestCode) {
                PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE -> startVideoDownload(
                    vm.pendingDownloadMessage
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(context, downloadProgressReceiver)
    }

    private val sharedMediaAdapterListener = object : TapSharedMediaInterface {
        override fun onMediaClicked(item: TAPMessageModel, ivThumbnail: ImageView?, isMediaReady: Boolean) {
            if (item.type == TYPE_IMAGE && isMediaReady) {
                // Preview image detail
                TAPImageDetailPreviewActivity.start(
                    context,
                    instanceKey,
                    item,
                    ivThumbnail
                )
            }
            else if (item.type == TYPE_IMAGE) {
                // Download image
                TAPFileDownloadManager.getInstance(instanceKey)
                    .downloadImage(context, item)
                notifyItemChanged(item)
            }
            else if (item.type == TYPE_VIDEO && isMediaReady && null != item.data) {
                val videoUri =
                    TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(item)
                if (null == videoUri) {
                    // Prompt download
                    val fileID = item.data!![FILE_ID] as String?
                    TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache(fileID)
                    notifyItemChanged(item)
                    TapTalkDialog.Builder(context)
                        .setTitle(getString(R.string.tap_error_could_not_find_file))
                        .setMessage(getString(R.string.tap_error_redownload_file))
                        .setCancelable(true)
                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                        .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                        .setPrimaryButtonListener { startVideoDownload(item) }
                        .show()
                }
                else {
                    // Open video player
                    TAPVideoPlayerActivity.start(
                        context,
                        instanceKey,
                        videoUri,
                        item
                    )
                }
            }
            else if (item.type == TYPE_VIDEO) {
                // Download video
                startVideoDownload(item)
            }
        }

        override fun onCancelDownloadClicked(item: TAPMessageModel) {
            TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(item.localID)
            notifyItemChanged(item)
        }

        override fun onDocumentClicked(item: TAPMessageModel) {

        }

        override fun onLinkClicked(item: TAPMessageModel) {
            var url: String = item.data?.get(URL) as String
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            TAPUtils.openUrl(instanceKey, activity, url)
        }

        override fun onItemLongClicked(item: TAPMessageModel) {
            TAPLongPressActionBottomSheet.newInstance(
                instanceKey,
                SHARED_MEDIA_TYPE,
                item,
                longPressListener
            ).show(childFragmentManager, "")
        }
    }

    private val longPressListener =
        TapLongPressInterface { longPressMenuItem, messageModel ->
            if (longPressMenuItem?.id == VIEW_IN_CHAT) {
                val intent = Intent()
                intent.putExtra(MESSAGE, messageModel)
                activity?.setResult(RESULT_OK, intent)
                activity?.finish()
            }
        }

    private val sharedMediaListener: TAPDatabaseListener<TAPMessageEntity> = object : TAPDatabaseListener<TAPMessageEntity>() {
        override fun onSelectFinished(entities: List<TAPMessageEntity>) {
            Thread {
                if (entities.isEmpty() && 0 == vm.sharedMedias.size) {
                    // No shared media in database, load from api
                    vm.isFinishedLoading = true
                    activity?.runOnUiThread {
                        vm.isLoading = true
                        showLoading()
                        Thread {
                            (activity as TapSharedMediaActivity?)?.getMoreSharedMedias(type)
                        }.start()
                    }
                }
                else {
                    // Has shared media
                    val previousSize = vm.sharedMedias.size
                    if (0 == previousSize) {
                        // First load
                        activity?.runOnUiThread {
                            vb.recyclerView.visibility = View.VISIBLE
                            vb.gEmptySharedMedia.visibility = View.GONE
                            if (MAX_ITEMS_PER_PAGE <= entities.size) {
                                sharedMediaPagingScrollListener = ViewTreeObserver.OnScrollChangedListener {
                                    if (!vm.isFinishedLoading &&
                                        (sharedMediaGlm?.findLastVisibleItemPosition() ?: -1) > vm.sharedMedias.size - MAX_ITEMS_PER_PAGE / 2
                                    ) {
                                        // Load more if view holder is visible
                                        if (!vm.isLoading) {
                                            vm.isLoading = true
                                            showSharedMediaLoading()
                                            Thread {
                                                when (type) {
                                                    TYPE_MEDIA -> TAPDataManager.getInstance(instanceKey).getRoomMedias(vm.lastTimestamp, vm.room?.roomID, this)
                                                    TYPE_LINK -> TAPDataManager.getInstance(instanceKey).getRoomLinks(vm.lastTimestamp, vm.room?.roomID, this)
                                                    TYPE_DOCUMENT -> TAPDataManager.getInstance(instanceKey).getRoomFiles(vm.lastTimestamp, vm.room?.roomID, this)
                                                }
                                            }.start()
                                        }
                                    }
                                }
                                vb.recyclerView.viewTreeObserver.addOnScrollChangedListener(sharedMediaPagingScrollListener)
                            }
                        }
                    }
                    if (MAX_ITEMS_PER_PAGE > entities.size) {
                        // No more medias in database, load from api
                        vm.isFinishedLoading = true
                        activity?.runOnUiThread {
                            vb.recyclerView.visibility = View.VISIBLE
                            vb.recyclerView.viewTreeObserver.removeOnScrollChangedListener(sharedMediaPagingScrollListener)
                            sharedMediaPagingScrollListener = ViewTreeObserver.OnScrollChangedListener {
                                if (!vm.isRemoteContentFetched &&
                                    (sharedMediaGlm?.findLastVisibleItemPosition() ?: -1) > vm.sharedMedias.size - MAX_ITEMS_PER_PAGE / 2
                                ) {
                                    // Load more if view holder is visible
                                    if (!vm.isLoading) {
                                        vm.isLoading = true
                                        showSharedMediaLoading()
                                        Thread {
                                            (activity as TapSharedMediaActivity?)?.getMoreSharedMedias(type)
                                        }.start()
                                    }
                                }
                            }
                            vb.recyclerView.viewTreeObserver.addOnScrollChangedListener(sharedMediaPagingScrollListener)
                        }
                    }
                    var previousDate = ""
                    var previousMessage: TAPMessageModel? = null
                    var additionalSeparators = 0
                    for (entity in entities) {
                        val mediaMessage = TAPMessageModel.fromMessageEntity(entity)
                        val currentDate = TAPTimeFormatter.formatMonth(mediaMessage.created)
                        if ((previousMessage == null || !currentDate.equals(previousDate)) && !vm.dateSeparators.containsKey(currentDate)) {
                            val dateSeparator = vm.generateDateSeparator(mediaMessage)
                            previousDate = currentDate
                            previousMessage = mediaMessage
                            vm.dateSeparators[currentDate] = dateSeparator
                            vm.sharedMediaAdapterItems.add(dateSeparator)
                            vm.dateSeparatorIndexes[currentDate] = vm.sharedMediaAdapterItems.indexOf(dateSeparator)
                            additionalSeparators++
                        }
                        vm.addSharedMedia(mediaMessage)
                        vm.sharedMediaAdapterItems.add(mediaMessage)
                        vm.sharedMediaIndexes[mediaMessage.localID] = vm.sharedMediaAdapterItems.indexOf(mediaMessage)
                    }
                    vm.lastTimestamp = vm.sharedMedias[0].created
                    vm.isLoading = false
                    sharedMediaAdapter?.setDateSeparators(vm.dateSeparators)
                    activity?.runOnUiThread {
                        hideSharedMediaLoading()
                        hideLoading()
                        vb.recyclerView.post {
                            sharedMediaAdapter?.notifyItemRangeInserted(vm.sharedMediaAdapterItems.size, entities.size)
                        }
                    }
                }
            }.start()
        }
    }

    fun addRemoteSharedMedias(sharedMedias : List<TAPMessageModel>) {
        var previousDate = ""
        var previousMessage: TAPMessageModel? = null
        var additionalSeparators = 0
        val lastSharedMediaIndex = vm.sharedMedias.size
        val lastSharedMediaSeparatorIndex = vm.sharedMedias.size + vm.dateSeparators.size
        var lastSharedMediaAdapterIndex = vm.sharedMediaAdapterItems.size
        for (item in sharedMedias) {
            val currentDate = TAPTimeFormatter.formatMonth(item.created)
            if (!vm.dateSeparators.containsKey(currentDate) && (previousMessage == null || !currentDate.equals(previousDate))) {
                val dateSeparator = vm.generateDateSeparator(item)
                previousDate = currentDate
                previousMessage = item
                vm.dateSeparators[currentDate] = dateSeparator
                vm.sharedMediaAdapterItems.add(lastSharedMediaSeparatorIndex, dateSeparator)
                vm.dateSeparatorIndexes[currentDate] = vm.sharedMediaAdapterItems.indexOf(dateSeparator)
                lastSharedMediaAdapterIndex = lastSharedMediaSeparatorIndex + 1
                additionalSeparators++
            }
            vm.addSharedMedia(item, lastSharedMediaIndex)
            if (currentDate == previousDate) {
                vm.sharedMediaAdapterItems.add(lastSharedMediaAdapterIndex, item)
            }
            else {
                vm.sharedMediaAdapterItems.add(lastSharedMediaSeparatorIndex, item)
            }
            vm.sharedMediaIndexes[item.localID] = vm.sharedMediaAdapterItems.indexOf(item)
        }
        if (vm.sharedMedias.isEmpty()) {
            activity?.runOnUiThread {
                vb.recyclerView.visibility = View.GONE
                vb.gEmptySharedMedia.visibility = View.VISIBLE
                hideLoading()
            }
        }
        else {
            vm.lastTimestamp = vm.sharedMedias[0].created
            sharedMediaAdapter?.setDateSeparators(vm.dateSeparators)
            activity?.runOnUiThread {
                vb.recyclerView.visibility = View.VISIBLE
                vb.recyclerView.viewTreeObserver.removeOnScrollChangedListener(sharedMediaPagingScrollListener)
                hideSharedMediaLoading()
                hideLoading()
                vb.recyclerView.post {
                    sharedMediaAdapter?.notifyItemRangeInserted(vm.sharedMedias.size, sharedMedias.size)
                }
            }
        }
        vm.isRemoteContentFetched = true
        vm.isLoading = false
    }

    private fun startVideoDownload(message: TAPMessageModel?) {
        if (!TAPUtils.hasPermissions(context, *TAPUtils.getStoragePermissions(false))) {
            // Request storage permission
            activity?.let {
                vm.pendingDownloadMessage = message
                ActivityCompat.requestPermissions(
                    it,
                    TAPUtils.getStoragePermissions(false),
                    PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
                )
            }
        }
        else {
            // Download file
            vm.pendingDownloadMessage = null
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message)
        }
        notifyItemChanged(message)
    }

    private val downloadProgressReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val localID = intent.getStringExtra(DownloadBroadcastEvent.DownloadLocalID)
            if (null == action || null == localID) {
                return
            }
            when (action) {
                DownloadBroadcastEvent.DownloadProgressLoading, DownloadBroadcastEvent.DownloadFinish -> activity?.runOnUiThread {
                    if (vm.getSharedMedia(localID) != null) {
                        notifyItemChanged(
                            vm.getSharedMedia(localID)
                        )
                    }
                }
                DownloadBroadcastEvent.DownloadFailed -> activity?.runOnUiThread {
                    TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(localID)
                    if (vm.getSharedMedia(localID) != null) {
                        notifyItemChanged(
                            vm.getSharedMedia(localID)
                        )
                    }
                }
                DownloadBroadcastEvent.DownloadFile -> activity?.runOnUiThread {
                    startFileDownload(intent.getParcelableExtra(MESSAGE))
                }
                DownloadBroadcastEvent.CancelDownload -> activity?.runOnUiThread {
                    TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(localID)
                    if (vm.sharedMediasMap.containsKey(localID)) {
                        notifyItemChanged(vm.sharedMediasMap[localID])
                    }
                }
                DownloadBroadcastEvent.OpenFile -> activity?.runOnUiThread {
                    val message: TAPMessageModel? = intent.getParcelableExtra(MESSAGE)
                    val fileUri: Uri? = intent.getParcelableExtra(FILE_URI)
                    vm.openedFileMessage = message
                    if (null != fileUri && null != message?.data) {
                        if (!TAPUtils.openFile(instanceKey, context, fileUri, TAPUtils.getMimeTypeFromMessage(message))) {
                            showDownloadFileDialog()
                        }
                    }
                    else {
                        showDownloadFileDialog()
                    }
                }
            }
        }
    }

    private fun notifyItemChanged(mediaMessage: TAPMessageModel?) {
        activity?.runOnUiThread {
            sharedMediaAdapter?.notifyItemChanged(vm.sharedMediaAdapterItems.indexOf(mediaMessage))
        }
    }

    private fun showSharedMediaLoading() {
        if (!vm.sharedMediaAdapterItems.contains(vm.getLoadingItem())) {
            vm.sharedMediaAdapterItems.add(vm.getLoadingItem())
            sharedMediaAdapter?.notifyItemInserted(vm.sharedMediaAdapterItems.size)
        }
    }

    private fun hideSharedMediaLoading() {
        if (vm.sharedMediaAdapterItems.contains(vm.getLoadingItem())) {
            val index = vm.sharedMediaAdapterItems.indexOf(vm.getLoadingItem())
            vm.sharedMediaAdapterItems.removeAt(index)
            sharedMediaAdapter?.notifyItemRemoved(index)
        }
    }

    private fun showLoading() {
        vb.progressCircular.visibility = View.VISIBLE
    }

    fun hideLoading() {
        vb.progressCircular.visibility = View.GONE
    }

    private fun startFileDownload(message: TAPMessageModel?) {
        if (!TAPUtils.hasPermissions(context, *TAPUtils.getStoragePermissions(true))) {
            // Request storage permission
            activity?.let {
                vm.pendingDownloadMessage = message
                ActivityCompat.requestPermissions(
                    it,
                    TAPUtils.getStoragePermissions(true),
                    PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
                )
            }
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
        if (null != vm.openedFileMessage?.data) {
            val fileId = vm.openedFileMessage?.data?.get(FILE_ID) as String
            var fileUrl = vm.openedFileMessage?.data?.get(FILE_URL) as String
            fileUrl = TAPUtils.getUriKeyFromUrl(fileUrl)
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.room?.roomID, fileId)
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.room?.roomID, fileUrl)
        }
        notifyItemChanged(vm.openedFileMessage)
        TapTalkDialog.Builder(context)
            .setTitle(getString(R.string.tap_error_could_not_find_file))
            .setMessage(getString(R.string.tap_error_redownload_file))
            .setCancelable(true)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setPrimaryButtonListener {
                startFileDownload(
                    vm.openedFileMessage
                )
            }
            .show()
    }
}
