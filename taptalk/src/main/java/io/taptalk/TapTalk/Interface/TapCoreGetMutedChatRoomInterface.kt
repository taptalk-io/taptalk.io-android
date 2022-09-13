package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.ResponseModel.TapMutedRoomListModel

interface TapCoreGetMutedChatRoomInterface {
    fun onSuccess(mutedRoomList: ArrayList<TapMutedRoomListModel>)

    fun onError(errorCode: String?, errorMessage: String?)
}