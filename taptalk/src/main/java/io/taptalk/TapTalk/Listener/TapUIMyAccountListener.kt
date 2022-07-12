package io.taptalk.TapTalk.Listener

import android.app.Activity
import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapUIMyAccountInterface

@Keep
abstract class TapUIMyAccountListener() : TapUIMyAccountInterface {

    override fun onDeleteButtonInMyAccountPageTapped(activity: Activity) {

    }
}