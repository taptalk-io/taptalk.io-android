package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel

interface TapCoreGetScheduledMessagesInterface {
    fun onSuccess(scheduledMessages : List<TapScheduledMessageModel>)

    fun onError(errorCode: String?, errorMessage: String?)
}