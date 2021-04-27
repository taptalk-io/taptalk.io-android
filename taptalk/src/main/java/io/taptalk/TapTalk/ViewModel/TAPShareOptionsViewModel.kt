package io.taptalk.TapTalk.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomListModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPSearchChatModel

class TAPShareOptionsViewModel(application: Application, instanceKey: String?): AndroidViewModel(application) {

    class TAPShareOptionsViewModelFactory(private val application: Application, private val instanceKey: String?) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TAPShareOptionsViewModel(application, instanceKey) as T
        }
    }

    var roomList: List<TAPRoomListModel>? = ArrayList()
    var selectedRoomList: List<TAPRoomListModel>? = ArrayList()
    var roomPointer: MutableMap<String, TAPRoomListModel>? = LinkedHashMap()
//    var selectedRooms: MutableMap<String, TAPRoomListModel>? = LinkedHashMap()
    var isSelecting = false

//    var recentSearchList: LiveData<List<TAPRecentSearchEntity>>? = TAPDataManager.getInstance(instanceKey).recentSearchLive
    var searchRoomResults: List<TAPRoomListModel>? = ArrayList()
    var recentSearches: List<TAPRoomListModel>? = ArrayList()
    var searchRoomPointer: MutableMap<String, TAPRoomModel>? = LinkedHashMap()
//    var selectedMessage: TAPMessageModel? = null
    var searchKeyword: String? = null
    var pendingSearch: String? = null
    var searchState: Int? = 0

    val STATE_RECENT_SEARCHES = 0
    val STATE_IDLE = 1
    val STATE_SEARCHING = 2
    val STATE_PENDING = 3

    fun addRoomList(roomList: List<TAPRoomListModel>?) {
        roomList?.toMutableList()?.addAll(roomList)
    }

    fun clearRoomList() {
        roomList?.toMutableList()?.clear()
    }

    fun addRoomPointer(roomModel: TAPRoomListModel) {
        roomPointer!![roomModel.lastMessage.room.roomID] = roomModel
    }

    fun getSelectedCount(): Int? {
        return selectedRoomList?.size
    }

    fun setSearchResults(searchResults: List<TAPRoomListModel>) {
        this.searchRoomResults = searchResults
        searchRoomPointer?.clear()
        for (result in searchResults) {
            val room = result.lastMessage.room
            if (null != room) {
                searchRoomPointer?.put(room.roomID, room)
            }
        }
    }

    fun addSearchResult(model: TAPRoomListModel) {
        searchRoomResults?.toMutableList()?.add(model)
        val room = model.lastMessage.room
        if (null != room) {
            searchRoomPointer?.toMutableMap()?.put(room.roomID, room)
        }
    }

    fun clearSearchResults() {
        searchRoomResults?.toMutableList()?.clear()
        roomPointer?.toMutableMap()?.clear()
    }

    fun resultContainsRoom(roomID: String?): Boolean {
        return roomPointer!!.containsKey(roomID)
    }

    fun clearRecentSearches() {
        recentSearches?.toMutableList()?.clear()
    }

    fun addRecentSearches(item: TAPRoomListModel) {
        recentSearches?.toMutableList()?.add(item)
    }


}