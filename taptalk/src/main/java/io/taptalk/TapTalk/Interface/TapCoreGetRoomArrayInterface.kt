package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.TAPRoomModel

interface TapCoreGetRoomArrayInterface {
    fun onSuccess(tapRoomModel: ArrayList<TAPRoomModel>)

    fun onError(errorCode: String?, errorMessage: String?)
}