package io.taptalk.TapTalk.View.Adapter

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.taptalk.TapTalk.Const.TAPDefaultConstant.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.*
import io.taptalk.TapTalk.Helper.*
import io.taptalk.TapTalk.Interface.TapSharedMediaInterface
import io.taptalk.TapTalk.Manager.TAPCacheManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DATE_SECTION
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DOCUMENT
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LINK
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LOADING
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_MEDIA
import io.taptalk.TapTalk.R
import java.lang.Exception

class TapSharedMediaAdapter(private val instanceKey: String, private val mediaItems: List<TAPMessageModel>, private val glide: RequestManager, private val listener: TapSharedMediaInterface) : TAPBaseAdapter<TAPMessageModel, TAPBaseViewHolder<TAPMessageModel>>() {

    private var gridWidth : Int = (TAPUtils.getScreenWidth() - TAPUtils.dpToPx(34)) / 3
    private var dateSeparators : HashMap<String, TAPMessageModel> = linkedMapOf()

    init {
        setItems(mediaItems, true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TAPBaseViewHolder<TAPMessageModel> {
        return when (viewType) {
            TYPE_MEDIA -> MediaViewHolder(parent, R.layout.tap_cell_media_thumbnail_grid)
            TYPE_LINK -> LinkViewHolder(parent, R.layout.tap_cell_media_link)
            TYPE_DOCUMENT -> DocumentViewHolder(parent, R.layout.tap_cell_media_document)
            TYPE_LOADING -> LoadingViewHolder(parent, R.layout.tap_cell_shared_media_loading)
            else -> {
                DateSeparatorViewHolder(parent, R.layout.tap_cell_shared_media_title)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItemAt(position).type) {
            TYPE_VIDEO, TYPE_IMAGE -> TYPE_MEDIA
            MessageType.TYPE_LINK -> TYPE_LINK
            TYPE_FILE -> TYPE_DOCUMENT
            TYPE_LOADING_MESSAGE_IDENTIFIER -> TYPE_LOADING
            else -> TYPE_DATE_SECTION
        }
    }

    override fun getItemCount(): Int = mediaItems.size

    inner class MediaViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPMessageModel>(parent, itemLayoutId) {
        private val activity: Activity = itemView.context as Activity
        private val clContainer: ConstraintLayout = itemView.findViewById(R.id.cl_container)
        private val flProgress: FrameLayout = itemView.findViewById(R.id.fl_progress)
        private val rcivThumbnail: TAPRoundedCornerImageView = itemView.findViewById(R.id.rciv_thumbnail)
        private val ivButtonProgress: ImageView = itemView.findViewById(R.id.iv_button_progress)
        private val ivVideoIcon: ImageView = itemView.findViewById(R.id.iv_video_icon)
        private val tvMediaInfo: TextView = itemView.findViewById(R.id.tv_media_info)
        private val pbProgress: ProgressBar = itemView.findViewById(R.id.pb_progress)
        private val vThumbnailOverlay: View = itemView.findViewById(R.id.v_thumbnail_overlay)
        private var thumbnail: Drawable? = null
        private var isMediaReady = false
        override fun onBind(message: TAPMessageModel?, position: Int) {
            if (null == message || null == message.data) {
                return
            }
            val downloadProgressValue = TAPFileDownloadManager.getInstance(instanceKey)
                .getDownloadProgressPercent(message.localID)
            clContainer.layoutParams.width = gridWidth
            if (clContainer.layoutParams is MarginLayoutParams) {
                // Update left/right margin per item
                val currentDate = TAPTimeFormatter.formatMonth(message.created)
                val params = clContainer.layoutParams as MarginLayoutParams
                val itemPosition = bindingAdapterPosition - (items.indexOf(dateSeparators[currentDate]) + 1)
                when (itemPosition % 3) {
                    0 -> params.leftMargin = TAPUtils.dpToPx(14)
                    1 -> params.leftMargin = TAPUtils.dpToPx(5)
                    2 -> params.leftMargin = TAPUtils.dpToPx(-4)
                }
                clContainer.layoutParams = params
                clContainer.requestLayout()
            }
            rcivThumbnail.setImageDrawable(null)

            thumbnail = BitmapDrawable(
                itemView.context.resources,
                TAPFileUtils.decodeBase64(
                    (if (null == message.data!![MessageData.THUMBNAIL]) "" else message.data!![MessageData.THUMBNAIL]) as String?
                )
            )
            if ((thumbnail as BitmapDrawable).intrinsicHeight <= 0) {
                // Set placeholder image if thumbnail fails to load
                thumbnail = ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_grey_e4)
            }

            // Load thumbnail when download is not in progress
            if (null == downloadProgressValue) {
                rcivThumbnail.setImageDrawable(thumbnail)
                tvMediaInfo.visibility = View.GONE
                flProgress.visibility = View.GONE
                vThumbnailOverlay.visibility = View.GONE
            } else {
                tvMediaInfo.visibility = View.VISIBLE
                flProgress.visibility = View.VISIBLE
                vThumbnailOverlay.visibility = View.VISIBLE
            }

            // Show/hide video icon
            if (message.type == TYPE_VIDEO) {
                ivVideoIcon.visibility = View.VISIBLE
            } else {
                ivVideoIcon.visibility = View.GONE
            }
            val fileID = message.data!![MessageData.FILE_ID] as String?
            if (TAPCacheManager.getInstance(itemView.context)
                    .containsCache(fileID) || TAPFileDownloadManager.getInstance(instanceKey)
                    .checkPhysicalFileExists(message)
            ) {
                // Image exists in cache / file exists in storage
                if (message.type == TYPE_VIDEO && null != message.data) {
                    val duration = message.data!![MessageData.DURATION] as Number?
                    if (null != duration) {
                        tvMediaInfo.text =
                            TAPUtils.getMediaDurationString(duration.toInt(), duration.toInt())
                        tvMediaInfo.visibility = View.VISIBLE
                        vThumbnailOverlay.visibility = View.VISIBLE
                    } else {
                        tvMediaInfo.visibility = View.GONE
                        vThumbnailOverlay.visibility = View.VISIBLE
                    }
                } else {
                    tvMediaInfo.visibility = View.GONE
                    vThumbnailOverlay.visibility = View.GONE
                }
                pbProgress.progress = 0
                flProgress.visibility = View.GONE
                Thread {
                    var mediaThumbnail =
                        TAPCacheManager.getInstance(itemView.context).getBitmapDrawable(fileID)
                    if (position == bindingAdapterPosition) {
                        // Load image only if view has not been recycled
                        if (message.type == TYPE_VIDEO && null == mediaThumbnail) {
                            // Get full-size thumbnail from Uri
                            val retriever = MediaMetadataRetriever()
                            val dataUri =
                                message.data!![MessageData.FILE_URI] as String?
                            val videoUri =
                                if (null != dataUri) Uri.parse(dataUri) else TAPFileDownloadManager.getInstance(
                                    instanceKey
                                ).getFileMessageUri(message)
                            try {
//                                Uri parsedUri = TAPFileUtils.parseFileUri(videoUri);
//                                retriever.setDataSource(itemView.getContext(), parsedUri);
                                retriever.setDataSource(itemView.context, videoUri)
                                mediaThumbnail = BitmapDrawable(
                                    itemView.context.resources,
                                    retriever.frameAtTime
                                )
                                TAPCacheManager.getInstance(itemView.context)
                                    .addBitmapDrawableToCache(fileID, mediaThumbnail)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                mediaThumbnail = thumbnail as BitmapDrawable?
                            }
                        }
                        if (null != mediaThumbnail) {
                            val finalMediaThumbnail = mediaThumbnail
                            activity.runOnUiThread {
                                // Load media thumbnail
                                glide.load(finalMediaThumbnail)
                                    .apply(RequestOptions().placeholder(thumbnail))
                                    .listener(object :
                                        RequestListener<Drawable?> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any,
                                            target: Target<Drawable?>,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any,
                                            target: Target<Drawable?>,
                                            dataSource: DataSource,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            return if (position == bindingAdapterPosition) {
                                                // Load image only if view has not been recycled
                                                isMediaReady = true
                                                clContainer.setOnClickListener {
                                                    listener.onMediaClicked(message, rcivThumbnail, isMediaReady)
                                                }
                                                false
                                            } else {
                                                true
                                            }
                                        }
                                    }).into(rcivThumbnail)
                            }
                        }
                    }
                }.start()
            } else {
                // Show small thumbnail
                val size = message.data!![MessageData.SIZE] as Number?
                val videoSize =
                    if (null == size) "" else TAPUtils.getStringSizeLengthFile(size.toLong())
                rcivThumbnail.setImageDrawable(thumbnail)
                if (null == downloadProgressValue) {
                    // Show media requires download
                    isMediaReady = false
                    pbProgress.progress = 0
                    clContainer.setOnClickListener {
                        listener.onMediaClicked(message, rcivThumbnail, isMediaReady)
                    }
                    tvMediaInfo.text = videoSize
                    ivButtonProgress.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.tap_ic_download_orange
                        )
                    )
                    ImageViewCompat.setImageTintList(
                        ivButtonProgress,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.tapIconFileUploadDownloadWhite
                            )
                        )
                    )
                } else {
                    // Media is downloading
                    isMediaReady = false
                    pbProgress.max = 100
                    pbProgress.progress = downloadProgressValue
                    clContainer.setOnClickListener {
                        listener.onCancelDownloadClicked(message)
                    }
                    //Long downloadProgressBytes = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressBytes(item.getLocalID());
                    //if (null != downloadProgressBytes) {
                    //    tvMediaInfo.setText(TAPUtils.getFileDisplayProgress(item, downloadProgressBytes));
                    //}
                    tvMediaInfo.text = videoSize
                    ivButtonProgress.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.tap_ic_cancel_white
                        )
                    )
                    ImageViewCompat.setImageTintList(
                        ivButtonProgress,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.tapIconFileCancelUploadDownloadWhite
                            )
                        )
                    )
                    clContainer.setOnClickListener {
                        listener.onCancelDownloadClicked(message)
                    }
                }
                tvMediaInfo.visibility = View.VISIBLE
                flProgress.visibility = View.VISIBLE
                vThumbnailOverlay.visibility = View.VISIBLE
            }
            itemView.setOnLongClickListener {
                listener.onItemLongClicked(message)
                true
            }
        }
    }

    inner class LinkViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPMessageModel>(parent, itemLayoutId) {
        private val tvLink: TextView = itemView.findViewById(R.id.tv_link)
        override fun onBind(item: TAPMessageModel?, position: Int) {
            if (null == item?.data) {
                return
            }
            tvLink.text = item.data?.get(URL) as String
            itemView.setOnClickListener {
                listener.onLinkClicked(item)
            }
            itemView.setOnLongClickListener {
                listener.onItemLongClicked(item)
                true
            }
        }

    }

    inner class DocumentViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPMessageModel>(parent, itemLayoutId) {
        private val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
        private val tvFileInfo: TextView = itemView.findViewById(R.id.tv_file_info)
        private val rcivImage: TAPRoundedCornerImageView = itemView.findViewById(R.id.rciv_image)
        private val pbProgress: ProgressBar = itemView.findViewById(R.id.pb_progress)
        private var fileUri: Uri? = null
        override fun onBind(item: TAPMessageModel?, position: Int) {
            if (null == item?.data) {
                return
            }
            val localID = item.localID
            val downloadProgressPercent = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(localID)
            //            String key = TAPUtils.getUriKeyFromMessage(item);
            fileUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(item)
//            String space = isMessageFromMySelf(item) ? RIGHT_BUBBLE_SPACE_APPEND : LEFT_BUBBLE_SPACE_APPEND;
            tvFileName.text = TAPUtils.getFileDisplayName(item)
            //            tvFileInfoDummy.setText(String.format("%s%s", TAPUtils.getFileDisplayDummyInfo(itemView.getContext(), item), space));
            if (null != item.isFailedSend && item.isFailedSend!!) {
                // Message failed to send
                tvFileInfo.text = TAPUtils.getFileDisplaySizeAndDate(itemView.context, item)
                rcivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.tap_ic_retry_white
                    )
                )
                pbProgress.visibility = View.GONE
                itemView.setOnClickListener { downloadFile(item) }
            } else if ((null == downloadProgressPercent) && (null != fileUri ||
                        TAPFileDownloadManager.getInstance(instanceKey)
                            .checkPhysicalFileExists(item))
            ) {
                // File has finished downloading or uploading
                tvFileInfo.text = TAPUtils.getFileDisplaySizeAndDate(itemView.context, item)
                rcivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.tap_ic_documents_white
                    )
                )
                pbProgress.visibility = View.GONE
                itemView.setOnClickListener { openFile(item) }
            } else if (null == downloadProgressPercent) {
                // File is not downloaded
                tvFileInfo.text = TAPUtils.getFileDisplaySizeAndDate(itemView.context, item)
                if (TAPFileDownloadManager.getInstance(instanceKey).failedDownloads.contains(item.localID)) {
                    rcivImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.tap_ic_retry_white
                        )
                    )
                } else {
                    rcivImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.tap_ic_download_orange
                        )
                    )
                }
                pbProgress.visibility = View.GONE
                itemView.setOnClickListener { downloadFile(item) }
            } else {
                // File is downloading or uploading
                rcivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.tap_ic_cancel_white
                    )
                )
                pbProgress.max = 100
                pbProgress.visibility = View.VISIBLE
                val downloadProgressBytes = TAPFileDownloadManager.getInstance(instanceKey)
                    .getDownloadProgressBytes(localID)
                tvFileInfo.text = TAPUtils.getFileDisplayProgress(item, downloadProgressBytes)
                pbProgress.progress = downloadProgressPercent
                itemView.setOnClickListener { cancelDownload(item) }
            }
            itemView.setOnLongClickListener {
                listener.onItemLongClicked(item)
                true
            }
        }

        private fun downloadFile(item: TAPMessageModel) {
            val intent = Intent(DownloadBroadcastEvent.DownloadFile)
            intent.putExtra(Extras.MESSAGE, item)
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent)
        }

        private fun cancelDownload(item: TAPMessageModel) {
            val intent = Intent(DownloadBroadcastEvent.CancelDownload)
            intent.putExtra(DownloadBroadcastEvent.DownloadLocalID, item.localID)
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent)
        }

        private fun openFile(item: TAPMessageModel) {
            val intent = Intent(DownloadBroadcastEvent.OpenFile)
            intent.putExtra(Extras.MESSAGE, item)
            intent.putExtra(MessageData.FILE_URI, fileUri)
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent)
        }

    }

    inner class DateSeparatorViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPMessageModel>(parent, itemLayoutId) {
        private val tvSectionTitle: TextView = itemView.findViewById(R.id.tv_section_title)
        private val vTopSeparator: View = itemView.findViewById(R.id.v_separator_1)
        private val vTopGap: View = itemView.findViewById(R.id.v_separator_2)
        override fun onBind(item: TAPMessageModel?, position: Int) {
            if (bindingAdapterPosition == 0) {
                vTopSeparator.visibility = View.GONE
                vTopGap.visibility = View.GONE
            } else {
                if (items[absoluteAdapterPosition - 1].type == TYPE_IMAGE || items[absoluteAdapterPosition - 1].type == TYPE_VIDEO) {
                    vTopSeparator.visibility = View.VISIBLE
                    vTopGap.visibility = View.GONE
                } else {
                    vTopSeparator.visibility = View.GONE
                    vTopGap.visibility = View.VISIBLE
                }
            }
            tvSectionTitle.text = item?.body
        }
    }

    inner class LoadingViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPMessageModel>(parent, itemLayoutId) {
        override fun onBind(item: TAPMessageModel?, position: Int) {

        }
    }

    fun setDateSeparators(dateSeparators : HashMap<String, TAPMessageModel>) {
        this.dateSeparators.clear()
        this.dateSeparators.putAll(dateSeparators)
    }
}