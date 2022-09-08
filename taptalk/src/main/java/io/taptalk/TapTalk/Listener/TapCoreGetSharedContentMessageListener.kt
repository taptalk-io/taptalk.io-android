package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetSharedContentMessageInterface
import io.taptalk.TapTalk.Model.TAPMessageModel

@Keep
abstract class TapCoreGetSharedContentMessageListener : TapCoreGetSharedContentMessageInterface {
    override fun onSuccess(
        mediaMessages: List<TAPMessageModel>,
        fileMessages: List<TAPMessageModel>,
        linkMessages: List<TAPMessageModel>
    ) {

    }

    override fun onError(errorCode: String, errorMessage: String) {

    }
}