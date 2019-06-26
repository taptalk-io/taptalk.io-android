package io.taptalk.TapTalk.ViewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel

class TAPGroupMemberViewModel(application: Application) : AndroidViewModel(application) {
    var isSearchActive: Boolean = false
    var isSelectionMode: Boolean = false
    var participantsList : MutableList<TAPUserModel> = mutableListOf()
    var groupData : TAPRoomModel? = null
    var selectedMembers : HashMap<String?, TAPUserModel?> = HashMap()

    fun addSelectedMember(member: TAPUserModel?) {
        selectedMembers[member?.userID] = member
    }

    fun removeSelectedMember(memberID: String) {
        selectedMembers.remove(memberID)
    }

    fun isSelectedMembersEmpty() : Boolean {
        return selectedMembers.size == 0
    }
}