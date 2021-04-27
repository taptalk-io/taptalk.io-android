package io.moselo.SampleApps.listener

import android.view.View
import io.taptalk.TapTalk.Model.TAPRoomListModel

interface TAPShareOptionsInterface {
    fun onRoomSelected(item: TAPRoomListModel?)

    fun onRoomDeselected(item: TAPRoomListModel?, position: Int)

    fun onMultipleRoomSelected(v: View?, item: TAPRoomListModel?, position: Int): Boolean
}