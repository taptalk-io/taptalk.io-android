package io.taptalk.TapTalk.Interface

interface TapCoreGetIntegerInterface {
    fun onSuccess(int: Int)

    fun onError(errorCode: String?, errorMessage: String?)
}
