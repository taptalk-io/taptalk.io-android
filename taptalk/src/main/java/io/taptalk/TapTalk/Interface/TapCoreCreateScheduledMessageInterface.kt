package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel

interface TapCoreCreateScheduledMessageInterface {
    fun onSuccess(scheduledMessage : TapScheduledMessageModel)

    fun onError(errorCode : String, errorMessage : String)
}