package io.moselo.SampleApps.`interface`

import android.view.View
import io.taptalk.TapTalk.Model.TAPRoomListModel

interface TAPShareOptionsInterface {
    fun onRoomSelected(v: View?, item: TAPRoomListModel?, position: Int)

    fun onMultipleRoomSelected(v: View?, item: TAPRoomListModel?, position: Int): Boolean
}