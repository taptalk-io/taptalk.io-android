package io.taptalk.TapTalk.ViewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel

class TAPGroupMemberViewModel(application: Application) : AndroidViewModel(application) {
    var isSearchActive: Boolean = false
    var participantsList : MutableList<TAPUserModel> = mutableListOf()
    var groupData : TAPRoomModel? = null
}