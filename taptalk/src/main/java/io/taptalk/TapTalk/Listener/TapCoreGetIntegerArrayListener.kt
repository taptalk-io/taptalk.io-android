package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreGetIntegerArrayInterface

@Keep
abstract class TapCoreGetIntegerArrayListener: TapCoreGetIntegerArrayInterface {

    override fun onSuccess(arrayList: ArrayList<Int>) {

    }

    override fun onError(errorCode: String?, errorMessage: String?) {

    }
}