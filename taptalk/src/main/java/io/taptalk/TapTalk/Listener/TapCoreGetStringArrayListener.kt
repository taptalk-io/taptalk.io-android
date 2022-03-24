package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetStringArrayInterface

@Keep
abstract class TapCoreGetStringArrayListener: TapCoreGetStringArrayInterface {
    override fun onSuccess(messages: ArrayList<String>) {

    }

    override fun onError(errorCode: String?, errorMessage: String?) {

    }
}