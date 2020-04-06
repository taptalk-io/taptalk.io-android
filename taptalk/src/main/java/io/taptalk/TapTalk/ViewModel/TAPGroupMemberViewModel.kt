package io.taptalk.TapTalk.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel

class TAPGroupMemberViewModel(application: Application, var instanceKey: String) : AndroidViewModel(application) {
    var isSearchActive: Boolean = false
    var isSelectionMode: Boolean = false
    var isUpdateMember: Boolean = false
    var isActiveUserIsAdmin: Boolean = false
    var participantsList: MutableList<TAPUserModel>? = mutableListOf()
    var groupData: TAPRoomModel? = null
    var selectedMembers: LinkedHashMap<String?, TAPUserModel?> = linkedMapOf()
    var adminButtonStatus: AdminButtonShowed = AdminButtonShowed.NOT_SHOWED
    var memberCountModel: TAPUserModel? = TAPUserModel("", "")
    var loadingStartText: String = ""
    var loadingEndText: String = ""

    enum class AdminButtonShowed {
        PROMOTE, DEMOTE, NOT_SHOWED
    }

    companion object {
        class TAPGroupMemberViewModelFactory(private val application: Application, private val instanceKey: String) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TAPGroupMemberViewModel(application, instanceKey) as T
            }
        }
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