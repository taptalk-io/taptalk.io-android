package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetMessageDetailsInterface
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel

@Keep
abstract class TapCoreGetMessageDetailsListener : TapCoreGetMessageDetailsInterface{
    override fun onSuccess(
        message: TAPMessageModel,
        deliveredTo: List<TapMessageRecipientModel>?,
        readBy: List<TapMessageRecipientModel>?
    ) {

    }

    override fun onError(errorCode: String?, errorMessage: String?) {

    }
}