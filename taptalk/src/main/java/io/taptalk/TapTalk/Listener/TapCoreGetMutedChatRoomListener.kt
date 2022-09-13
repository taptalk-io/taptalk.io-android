package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetMutedChatRoomInterface
import io.taptalk.TapTalk.Model.ResponseModel.TapMutedRoomListModel

@Keep
abstract class TapCoreGetMutedChatRoomListener : TapCoreGetMutedChatRoomInterface {
    override fun onSuccess(mutedRoomList: ArrayList<TapMutedRoomListModel>) {

    }

    override fun onError(errorCode: String?, errorMessage: String?) {

    }
}