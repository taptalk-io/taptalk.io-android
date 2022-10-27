package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.TAPUserModel

class TapBlockedListViewModel : ViewModel() {
    var blockedList = ArrayList<TAPUserModel>()
}