package io.taptalk.TapTalk.View.Fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Interface.TapSharedMediaInterface
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DOCUMENT
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LINK
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_MEDIA
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapSharedMediaAdapter
import io.taptalk.TapTalk.ViewModel.TapSharedMediaViewModel
import kotlinx.android.synthetic.main.tap_fragment_shared_media.*

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
            TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading,
            TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish,
            TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed
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
                return if (sharedMediaAdapter!!.getItemAt(position).type == TYPE_IMAGE || sharedMediaAdapter!!.getItemAt(position).type == TYPE_VIDEO) {
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
            showSharedMediaLoading()
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
//            TODO("Not yet implemented")
        }

        override fun onCancelDownloadClicked(item: TAPMessageModel) {
//            TODO("Not yet implemented")
        }

        override fun onDocumentClicked(item: TAPMessageModel) {
//            TODO("Not yet implemented")
        }

        override fun onLinkClicked(item: TAPMessageModel) {
//            TODO("Not yet implemented")
        }

        override fun onItemLongClicked(item: TAPMessageModel) {
//            TODO("Not yet implemented")
        }
    }

    private val sharedMediaListener: TAPDatabaseListener<TAPMessageEntity> =
        object : TAPDatabaseListener<TAPMessageEntity>() {
            override fun onSelectFinished(entities: List<TAPMessageEntity>) {
                Thread {
                    if (entities.isEmpty() && 0 == vm.sharedMedias.size) {
                        // No shared media
                        vm.isFinishedLoading = true
                        requireActivity().runOnUiThread {
                            recycler_view.visibility = View.GONE
                            g_empty_shared_media.visibility = View.VISIBLE
                            recycler_view.post { hideSharedMediaLoading() }
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
                            // No more medias in database
                            // TODO: 29/08/22 load more from API MU
                            vm.isFinishedLoading = true
                            requireActivity().runOnUiThread {
                                recycler_view!!.viewTreeObserver.removeOnScrollChangedListener(
                                    sharedMediaPagingScrollListener
                                )
                            }
                        }
                        for (entity in entities) {
                            val mediaMessage = TAPMessageModel.fromMessageEntity(entity)
                            vm.addSharedMedia(mediaMessage)
                            vm.sharedMediaAdapterItems.add(mediaMessage)
                        }
                        vm.lastTimestamp =
                            vm.sharedMedias[vm.sharedMedias.size - 1].created
                        vm.isLoading = false
                        requireActivity().runOnUiThread {
                            hideSharedMediaLoading()
                            recycler_view.post {
                                sharedMediaAdapter?.notifyItemRangeInserted(previousSize + 1, entities.size)
                            }
                        }
                    }
                }.start()
            }
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
            val localID = intent.getStringExtra(TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID)
            if (null == action || null == localID) {
                return
            }
            when (action) {
                TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading, TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish, TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed -> requireActivity().runOnUiThread {
                    if (vm.getSharedMedia(localID) != null) {
                        notifyItemChanged(
                            vm.getSharedMedia(localID)
                        )
                    }
                }
            }
        }
    }

    private fun notifyItemChanged(mediaMessage: TAPMessageModel?) {
        requireActivity().runOnUiThread {
            sharedMediaAdapter?.notifyItemChanged( vm.sharedMedias.indexOf(mediaMessage) + 1)
        }
    }

    private fun showSharedMediaLoading() {
        if (vm.sharedMediaAdapterItems.contains(vm.getLoadingItem())) {
            return
        }
        vm.sharedMediaAdapterItems.add(vm.getLoadingItem())
        sharedMediaAdapter?.notifyItemInserted(sharedMediaAdapter!!.itemCount - 1)
    }

    private fun hideSharedMediaLoading() {
        if (!vm.sharedMediaAdapterItems.contains(vm.getLoadingItem())) {
            return
        }
        val index = vm.sharedMediaAdapterItems.indexOf(vm.getLoadingItem())
        vm.sharedMediaAdapterItems.removeAt(index)
        sharedMediaAdapter?.notifyItemRemoved(index)
    }

    private fun showLoading() {
        progress_circular.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progress_circular.visibility = View.GONE
    }
}