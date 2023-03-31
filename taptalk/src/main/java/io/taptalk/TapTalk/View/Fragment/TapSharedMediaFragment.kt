package io.taptalk.TapTalk.View.Fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
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
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.VIEW_IN_CHAT
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.*
import io.taptalk.TapTalk.Interface.TapLongPressInterface
import io.taptalk.TapTalk.Interface.TapSharedMediaInterface
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPCacheManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DOCUMENT
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LINK
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_MEDIA
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TapLongPressMenuItem
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity
import io.taptalk.TapTalk.View.Activity.TapSharedMediaActivity
import io.taptalk.TapTalk.View.Adapter.TapSharedMediaAdapter
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.SHARED_MEDIA_TYPE
import io.taptalk.TapTalk.ViewModel.TapSharedMediaViewModel
import kotlinx.android.synthetic.main.tap_fragment_shared_media.*
import java.util.*

class TapSharedMediaFragment(private val instanceKey: String, private val type: Int): Fragment() {

    private lateinit var vm : TapSharedMediaViewModel
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tap_fragment_shared_media, container, false)
        glide = Glide.with(requireActivity())
        vm = ViewModelProvider(this, TapSharedMediaViewModel.TapSharedMediaViewModelFactory(requireActivity().application))[TapSharedMediaViewModel::class.java]
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
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        (sharedMediaGlm as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (vm.sharedMediaAdapterItems[position].type == TYPE_IMAGE || vm.sharedMediaAdapterItems[position].type == TYPE_VIDEO) {
                    1
                } else {
                    3
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = sharedMediaGlm
        recycler_view.adapter = sharedMediaAdapter
        if (!vm.isFinishedLoading) {
            if (vm.sharedMedias.isEmpty()) {
                recycler_view.visibility = View.GONE
            }
            Thread {
                when (type) {
                    TYPE_MEDIA -> {
                        TAPDataManager.getInstance(instanceKey)
                            .getRoomMedias(0L, vm.room?.roomID, sharedMediaListener)

                    }
                    TYPE_LINK -> {
                        TAPDataManager.getInstance(instanceKey)
                            .getRoomLinks(0L, vm.room?.roomID, sharedMediaListener)

                    }
                    TYPE_DOCUMENT -> {
                        TAPDataManager.getInstance(instanceKey)
                            .getRoomFiles(0L, vm.room?.roomID, sharedMediaListener)
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
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE -> startVideoDownload(
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
        override fun onMediaClicked(
            item: TAPMessageModel,
            ivThumbnail: ImageView?,
            isMediaReady: Boolean
        ) {
            if (item.type == TYPE_IMAGE && isMediaReady) {
            // Preview image detail
            TAPImageDetailPreviewActivity.start(
                context,
                instanceKey,
                item,
                ivThumbnail
            )
            } else if (item.type == TYPE_IMAGE) {
                // Download image
                TAPFileDownloadManager.getInstance(instanceKey)
                    .downloadImage(context, item)
                notifyItemChanged(item)
            } else if (item.type == TYPE_VIDEO && isMediaReady && null != item.data) {
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
                } else {
                    // Open video player
                    TAPVideoPlayerActivity.start(
                        context,
                        instanceKey,
                        videoUri,
                        item
                    )
                }
            } else if (item.type == TYPE_VIDEO) {
                // Download video
                startVideoDownload(item)
            }
        }

        override fun onCancelDownloadClicked(item: TAPMessageModel) {
            TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(item.localID)
            notifyItemChanged(item)
        }

        override fun onDocumentClicked(item: TAPMessageModel) {
//            TODO("Not yet implemented")
        }

        override fun onLinkClicked(item: TAPMessageModel) {
            var url: String = item.data?.get(URL) as String
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            TAPUtils.openUrl(activity, url)
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

    private val sharedMediaListener: TAPDatabaseListener<TAPMessageEntity> =
        object : TAPDatabaseListener<TAPMessageEntity>() {
            override fun onSelectFinished(entities: List<TAPMessageEntity>) {
                Thread {
                    if (entities.isEmpty() && 0 == vm.sharedMedias.size) {
                        // No shared media in database, load from api
                        vm.isFinishedLoading = true
                        requireActivity().runOnUiThread {
                            vm.isLoading = true
                            showLoading()
                            Thread {
                                (requireActivity() as TapSharedMediaActivity).getMoreSharedMedias(type)
                            }.start()
                        }
                    } else {
                        // Has shared media
                        val previousSize = vm.sharedMedias.size
                        if (0 == previousSize) {
                            // First load
                            requireActivity().runOnUiThread {
                                recycler_view.visibility = View.VISIBLE
                                g_empty_shared_media.visibility = View.GONE
                                if (TAPDefaultConstant.MAX_ITEMS_PER_PAGE <= entities.size) {
                                    sharedMediaPagingScrollListener =
                                        ViewTreeObserver.OnScrollChangedListener {
                                            if (!vm.isFinishedLoading && sharedMediaGlm!!.findLastVisibleItemPosition() > vm.sharedMedias.size - TAPDefaultConstant.MAX_ITEMS_PER_PAGE / 2) {
                                                // Load more if view holder is visible
                                                if (!vm.isLoading) {
                                                    vm.isLoading = true
                                                    showSharedMediaLoading()
                                                    Thread {
                                                        when(type) {
                                                            TYPE_MEDIA -> TAPDataManager.getInstance(instanceKey)
                                                                .getRoomMedias(
                                                                    vm.lastTimestamp,
                                                                    vm.room?.roomID,
                                                                    this
                                                                )
                                                            TYPE_LINK -> TAPDataManager.getInstance(instanceKey)
                                                                .getRoomLinks(
                                                                    vm.lastTimestamp,
                                                                    vm.room?.roomID,
                                                                    this
                                                                )
                                                            TYPE_DOCUMENT -> TAPDataManager.getInstance(instanceKey)
                                                                .getRoomFiles(
                                                                    vm.lastTimestamp,
                                                                    vm.room?.roomID,
                                                                    this
                                                                )
                                                        }
                                                    }.start()
                                                }
                                            }
                                        }
                                    recycler_view!!.viewTreeObserver.addOnScrollChangedListener(
                                        sharedMediaPagingScrollListener
                                    )
                                }
                            }
                        }
                        if (TAPDefaultConstant.MAX_ITEMS_PER_PAGE > entities.size) {
                            // No more medias in database, load from api
                            vm.isFinishedLoading = true
                            requireActivity().runOnUiThread {
                                recycler_view.visibility = View.VISIBLE
                                recycler_view!!.viewTreeObserver.removeOnScrollChangedListener(
                                    sharedMediaPagingScrollListener
                                )
                                sharedMediaPagingScrollListener =
                                    ViewTreeObserver.OnScrollChangedListener {
                                        if (!vm.isRemoteContentFetched && sharedMediaGlm!!.findLastVisibleItemPosition() > vm.sharedMedias.size - TAPDefaultConstant.MAX_ITEMS_PER_PAGE / 2) {
                                            // Load more if view holder is visible
                                            if (!vm.isLoading) {
                                                vm.isLoading = true
                                                showSharedMediaLoading()
                                                Thread {
                                                    (requireActivity() as TapSharedMediaActivity).getMoreSharedMedias(type)
                                                }.start()
                                            }
                                        }
                                    }
                                recycler_view!!.viewTreeObserver.addOnScrollChangedListener(
                                    sharedMediaPagingScrollListener
                                )
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
                        requireActivity().runOnUiThread {
                            hideSharedMediaLoading()
                            hideLoading()
                            recycler_view.post {
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
            } else {
                vm.sharedMediaAdapterItems.add(lastSharedMediaSeparatorIndex, item)
            }
            vm.sharedMediaIndexes[item.localID] = vm.sharedMediaAdapterItems.indexOf(item)
        }
        if (vm.sharedMedias.isEmpty()) {
            requireActivity().runOnUiThread {
                recycler_view.visibility = View.GONE
                g_empty_shared_media.visibility = View.VISIBLE
                hideLoading()
            }
        } else {
            vm.lastTimestamp = vm.sharedMedias[0].created
            sharedMediaAdapter?.setDateSeparators(vm.dateSeparators)
            requireActivity().runOnUiThread {
                recycler_view.visibility = View.VISIBLE
                recycler_view!!.viewTreeObserver.removeOnScrollChangedListener(sharedMediaPagingScrollListener)
                hideSharedMediaLoading()
                hideLoading()
                recycler_view.post {
                    sharedMediaAdapter?.notifyItemRangeInserted(vm.sharedMedias.size, sharedMedias.size)
                }
            }
        }
        vm.isRemoteContentFetched = true
        vm.isLoading = false
    }

    private fun startVideoDownload(message: TAPMessageModel?) {
        if (!TAPUtils.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm.pendingDownloadMessage = message
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            )
        } else {
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
                DownloadBroadcastEvent.DownloadProgressLoading, DownloadBroadcastEvent.DownloadFinish -> requireActivity().runOnUiThread {
                    if (vm.getSharedMedia(localID) != null) {
                        notifyItemChanged(
                            vm.getSharedMedia(localID)
                        )
                    }
                }
                DownloadBroadcastEvent.DownloadFailed -> requireActivity().runOnUiThread {
                    TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(localID)
                    if (vm.getSharedMedia(localID) != null) {
                        notifyItemChanged(
                            vm.getSharedMedia(localID)
                        )
                    }
                }
                DownloadBroadcastEvent.DownloadFile -> requireActivity().runOnUiThread {
                    startFileDownload(intent.getParcelableExtra(MESSAGE))
                }
                DownloadBroadcastEvent.CancelDownload -> requireActivity().runOnUiThread {
                    TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(localID)
                    if (vm.sharedMediasMap.containsKey(localID)) {
                        notifyItemChanged(vm.sharedMediasMap[localID])
                    }
                }
                DownloadBroadcastEvent.OpenFile -> requireActivity().runOnUiThread {
                    val message: TAPMessageModel? = intent.getParcelableExtra(MESSAGE)
                    val fileUri: Uri? = intent.getParcelableExtra(FILE_URI)
                    vm.openedFileMessage = message
                    if (null != fileUri && null != message?.data && null != message.data?.get(MEDIA_TYPE)) {
                        if (!TAPUtils.openFile(instanceKey, context, fileUri, message.data?.get(MEDIA_TYPE) as String)) {
                            showDownloadFileDialog()
                        }
                    } else {
                        showDownloadFileDialog()
                    }
                }
            }
        }
    }

    private fun notifyItemChanged(mediaMessage: TAPMessageModel?) {
        requireActivity().runOnUiThread {
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
        if (progress_circular != null) {
            progress_circular.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        if (progress_circular != null) {
            progress_circular.visibility = View.GONE
        }
    }

    private fun startFileDownload(message: TAPMessageModel?) {
        if (!TAPUtils.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm.pendingDownloadMessage = message
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
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