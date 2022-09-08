package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.TAPMessageModel

interface TapCoreGetSharedContentMessageInterface {
   fun onSuccess(mediaMessages : List<TAPMessageModel> , fileMessages: List<TAPMessageModel> , linkMessages : List<TAPMessageModel>)
   fun onError(errorCode : String, errorMessage : String)
}