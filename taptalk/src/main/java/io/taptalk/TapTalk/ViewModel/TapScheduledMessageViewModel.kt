package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import java.io.File

class TapScheduledMessageViewModel: ViewModel() {
    var pendingScheduledMessageType = 0
    var pendingScheduledMessageTime = 0L
    var pendingScheduledMessageText = ""
    var pendingScheduledMessageMedias = ArrayList<TAPMediaPreviewModel>()
    var pendingScheduledMessageLatitude = 0.0
    var pendingScheduledMessageLongitude = 0.0
    var pendingScheduledMessageFile: File? = null
    var pendingRescheduledMessage: TAPMessageModel? = null
}