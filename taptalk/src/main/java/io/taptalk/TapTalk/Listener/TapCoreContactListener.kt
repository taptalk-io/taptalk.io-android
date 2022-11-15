package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreContactInterface
import io.taptalk.TapTalk.Model.TAPUserModel

@Keep
abstract class TapCoreContactListener : TapCoreContactInterface {

    override fun onContactBlocked(user: TAPUserModel) {

    }

    override fun onContactUnblocked(user: TAPUserModel) {

    }
}