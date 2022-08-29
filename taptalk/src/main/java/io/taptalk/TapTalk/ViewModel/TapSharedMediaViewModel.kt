package io.taptalk.TapTalk.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel

class TapSharedMediaViewModel(application: Application): AndroidViewModel(application) {

    class TapSharedMediaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TapSharedMediaViewModel(application) as T
        }
    }

    var isFinishedLoading = false
    var isLoading = false
    var room: TAPRoomModel? = TAPRoomModel()
    var lastTimestamp = 0L
    var sharedMediasMap: HashMap<String, TAPMessageModel> = HashMap()
    var pendingDownloadMessage: TAPMessageModel? = null
    var sharedMedias: MutableList<TAPMessageModel> = arrayListOf()
    var sharedMediaAdapterItems: MutableList<TAPMessageModel> = arrayListOf()
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

    fun addSharedMedia(sharedMedia : TAPMessageModel) {
        sharedMedias.add(sharedMedia)
        sharedMediasMap[sharedMedia.localID] = sharedMedia
    }

    fun getSharedMedia(localId : String): TAPMessageModel? {
        return sharedMediasMap[localId]
    }
}