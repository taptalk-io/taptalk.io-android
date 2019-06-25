package io.taptalk.TapTalk.Interface

import io.taptalk.TapTalk.Model.TAPUserModel

interface TapTalkGroupMemberListInterface : TapTalkContactListInterface {
    override fun onContactSelected(contact: TAPUserModel?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onContactDeselected(contact: TAPUserModel?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onContactLongPress(contact: TAPUserModel?) {

    }
}