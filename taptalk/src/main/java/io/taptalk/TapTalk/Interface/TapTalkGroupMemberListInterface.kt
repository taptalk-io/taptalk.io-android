package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.TAPUserModel

interface TapTalkGroupMemberListInterface {
    fun onContactSelected(contact: TAPUserModel?): Boolean {
        return false
    }

    fun onContactDeselected(contact: TAPUserModel?) {

    }

    fun onContactLongPress(contact: TAPUserModel?) {

    }

    fun onGroupMemberClicked(member: TAPUserModel?, isAdmin: Boolean) {

    }
}