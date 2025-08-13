package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_DATE_SEPARATOR
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER
import io.taptalk.TapTalk.Helper.TAPTimeFormatter
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel

class TapSharedMediaViewModel: ViewModel() {

    var instanceKey = ""
    var type = -1
    var isFinishedLoading = false
    var isLoading = false
    var room: TAPRoomModel? = TAPRoomModel()
    var lastTimestamp = 0L
    var sharedMediasMap: HashMap<String, TAPMessageModel> = HashMap()
    var pendingDownloadMessage: TAPMessageModel? = null
    var openedFileMessage: TAPMessageModel? = null
    var sharedMedias: MutableList<TAPMessageModel> = arrayListOf()
    val sharedMediaIndexes: LinkedHashMap<String, Int> = linkedMapOf()
    var sharedMediaAdapterItems: MutableList<TAPMessageModel> = arrayListOf()
    var oldestCreatedTime : Long? = null
    var isRemoteContentFetched = false
    var remoteMedias: MutableList<TAPMessageModel> = arrayListOf()
    var remoteDocuments: MutableList<TAPMessageModel> = arrayListOf()
    var remoteLinks: MutableList<TAPMessageModel> = arrayListOf()
    val dateSeparators: LinkedHashMap<String, TAPMessageModel> = linkedMapOf()
    val dateSeparatorIndexes: LinkedHashMap<String, Int> = linkedMapOf()
    private var loadingItem : TAPMessageModel? = null

    fun getLoadingItem(): TAPMessageModel {
        return when (loadingItem) {
            null -> {
                loadingItem = TAPMessageModel()
                loadingItem!!.type = TYPE_LOADING_MESSAGE_IDENTIFIER
                loadingItem!!.localID = LOADING_INDICATOR_LOCAL_ID
                loadingItem!!
            }
            else -> loadingItem!!
        }
    }

    fun addSharedMedia(sharedMedia : TAPMessageModel, index : Int = sharedMedias.size - 1) {
        if (!sharedMediasMap.containsKey(sharedMedia.localID)) {
            if (sharedMedias.isNotEmpty()) {
                sharedMedias.add(index, sharedMedia)
            } else {
                sharedMedias.add(sharedMedia)
            }
            sharedMediasMap[sharedMedia.localID] = sharedMedia
        }
    }

    fun getSharedMedia(localId : String): TAPMessageModel? {
        return sharedMediasMap[localId]
    }

    fun generateDateSeparator(message: TAPMessageModel): TAPMessageModel {
        return TAPMessageModel.Builder(
            TAPTimeFormatter.formatMonth(message.created),
            room,
            TYPE_DATE_SEPARATOR,
            message.created - 1,
            message.user,
            "",
            null
        )
    }
}
