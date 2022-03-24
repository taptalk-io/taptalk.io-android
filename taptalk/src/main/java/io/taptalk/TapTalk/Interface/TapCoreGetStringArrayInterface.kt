package io.taptalk.TapTalk.Interface

interface TapCoreGetStringArrayInterface {
    fun onSuccess(messages: ArrayList<String>)

    fun onError(errorCode: String?, errorMessage: String?)
}