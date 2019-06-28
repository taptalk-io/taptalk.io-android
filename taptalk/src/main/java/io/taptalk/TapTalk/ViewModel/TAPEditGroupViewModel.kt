package io.taptalk.TapTalk.ViewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel

class TAPEditGroupViewModel(application: Application) : AndroidViewModel(application) {
    var groupData: TAPRoomModel? = null
        get() = field
        set(value) {
            field = value
        }

    var isGroupPicChanged: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    var isGroupPicStartEmpty: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    var isGroupNameChanged: Boolean = false
        get() = field
        set(value) {
            field = value
        }
}