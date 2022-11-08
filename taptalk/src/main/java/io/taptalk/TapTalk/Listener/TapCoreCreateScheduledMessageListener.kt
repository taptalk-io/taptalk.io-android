package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreCreateScheduledMessageInterface
import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel

@Keep
abstract class TapCoreCreateScheduledMessageListener : TapCoreCreateScheduledMessageInterface {
    override fun onSuccess(scheduledMessage: TapScheduledMessageModel) {

    }

    override fun onError(errorCode: String, errorMessage: String) {

    }
}