package io.taptalk.TapTalk.Interface

import android.widget.ImageView
import io.taptalk.TapTalk.Model.TAPMessageModel

interface TapSharedMediaInterface {
    fun onMediaClicked(item: TAPMessageModel, ivThumbnail: ImageView?, isMediaReady: Boolean)
    fun onCancelDownloadClicked(item: TAPMessageModel)

    fun onDocumentClicked(item: TAPMessageModel)

    fun onLinkClicked(item: TAPMessageModel)

    fun onItemLongClicked(item: TAPMessageModel)
}