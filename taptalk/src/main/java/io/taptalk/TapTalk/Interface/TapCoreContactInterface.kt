package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.TAPUserModel

interface TapCoreContactInterface {

    fun onContactBlocked(user : TAPUserModel)

    fun onContactUnblocked(user : TAPUserModel)
}