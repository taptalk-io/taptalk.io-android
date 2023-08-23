package io.taptalk.TapTalk.ViewModel

import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel

class TapMessageInfoViewModel : ViewModel() {
    var message : TAPMessageModel? = null
    var pendingDownloadMessage : TAPMessageModel? = null
    var openedFileMessage : TAPMessageModel? = null
    var room : TAPRoomModel? = null
    var isStarred = false
    var isPinned = false
    var isFirstLoadFinished = false
    var isMediaPlaying = false
    var isSeeking = false
    var voiceDuration: Int = 0
    var pausedPosition: Int = 0
    var voiceUri: Uri? = null
    var readList = ArrayList<TapMessageRecipientModel>()
    var deliveredList = ArrayList<TapMessageRecipientModel>()
    var mediaPlayer: MediaPlayer? = null
}
