package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.TAPUserModel

class TapBlockedListViewModel : ViewModel() {

    enum class State {
        VIEW, EDIT
    }

    var editState = State.VIEW
    var blockedList = ArrayList<TAPUserModel>()
}