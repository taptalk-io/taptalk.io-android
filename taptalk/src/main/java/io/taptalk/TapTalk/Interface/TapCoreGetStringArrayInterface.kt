package io.taptalk.TapTalk.Interface

interface TapCoreGetStringArrayInterface {
    fun onSuccess(arrayList: ArrayList<String>)

    fun onError(errorCode: String?, errorMessage: String?)
}