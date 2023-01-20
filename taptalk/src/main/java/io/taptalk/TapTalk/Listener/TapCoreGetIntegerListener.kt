package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetIntegerInterface

@Keep
abstract class TapCoreGetIntegerListener: TapCoreGetIntegerInterface {

    override fun onSuccess(int: Int) {

    }

    override fun onError(errorCode: String?, errorMessage: String?) {

    }
}