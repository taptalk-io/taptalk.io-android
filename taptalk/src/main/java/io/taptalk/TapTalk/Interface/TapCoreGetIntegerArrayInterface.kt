package io.taptalk.TapTalk.Interface

interface TapCoreGetIntegerArrayInterface {
    fun onSuccess(arrayList: ArrayList<Int>)

    fun onError(errorCode: String?, errorMessage: String?)
}