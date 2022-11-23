package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel

interface TapCoreGetMessageDetailsInterface {

    fun onSuccess(message : TAPMessageModel, deliveredTo : List<TapMessageRecipientModel>?, readBy : List<TapMessageRecipientModel>?)

    fun onError(errorCode: String?, errorMessage: String?)
}