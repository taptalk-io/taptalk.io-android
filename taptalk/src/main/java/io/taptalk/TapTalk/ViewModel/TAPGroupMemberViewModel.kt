package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel

class TAPGroupMemberViewModel: ViewModel() {
    var instanceKey: String = ""
    var isSearchActive: Boolean = false
    var isSelectionMode: Boolean = false
    var isUpdateMember: Boolean = false
    var isActiveUserIsAdmin: Boolean = false
    var participantsList: MutableList<TAPUserModel>? = mutableListOf()
    var groupData: TAPRoomModel? = null
    var selectedMembers: LinkedHashMap<String?, TAPUserModel?> = linkedMapOf()
    var adminButtonStatus: AdminButtonShowed = AdminButtonShowed.NOT_SHOWED
    var memberCountModel: TAPUserModel? = TAPUserModel("", "")
    var searchKeyword: String = ""
    var loadingStartText: String = ""
    var loadingEndText: String = ""

    enum class AdminButtonShowed {
        PROMOTE, DEMOTE, NOT_SHOWED
    }

    fun setGroupDataAndCheckAdmin(groupData: TAPRoomModel?) {
        this.groupData = groupData
        if (groupData?.admins?.contains(TAPChatManager.getInstance(instanceKey).activeUser?.userID) == true) {
            isActiveUserIsAdmin = true
        }
    }

    fun addSelectedMember(member: TAPUserModel?) {
        selectedMembers[member?.userID] = member
    }

    fun removeSelectedMember(memberID: String) {
        selectedMembers.remove(memberID)
    }

    fun isSelectedMembersEmpty(): Boolean {
        return selectedMembers.size == 0
    }

    fun getSelectedUserIDs(): List<String?> {
        return selectedMembers.keys.toList()
    }
}