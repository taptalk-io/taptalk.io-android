package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetRoomArrayInterface
import io.taptalk.TapTalk.Model.TAPRoomModel

@Keep
abstract class TapCoreGetRoomArrayListener : TapCoreGetRoomArrayInterface {
    override fun onSuccess(tapRoomModel: ArrayList<TAPRoomModel>) {

    }

    override fun onError(errorCode: String?, errorMessage: String?) {

    }
}