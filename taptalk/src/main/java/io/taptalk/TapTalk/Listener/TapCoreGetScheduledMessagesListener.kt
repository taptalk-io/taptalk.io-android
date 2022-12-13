package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetScheduledMessagesInterface
import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel

@Keep
abstract class TapCoreGetScheduledMessagesListener : TapCoreGetScheduledMessagesInterface{

    override fun onSuccess(scheduledMessages: List<TapScheduledMessageModel>) {

    }

    override fun onError(errorCode: String?, errorMessage: String?) {

    }
}