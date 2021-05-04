package io.taptalk.TapTalk.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.Model.TAPRoomListModel
import io.taptalk.TapTalk.Model.TAPRoomModel

class TAPShareOptionsViewModel(application: Application): AndroidViewModel(application) {

    class TAPShareOptionsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TAPShareOptionsViewModel(application) as T
        }
    }

    var roomList: List<TAPRoomListModel>? = ArrayList()
    var roomPointer: MutableMap<String, TAPRoomListModel>? = LinkedHashMap()
    var selectedRooms: MutableMap<String, TAPRoomListModel>? = LinkedHashMap()
    var isSelecting = false

    var searchRoomResults: ArrayList<TAPRoomListModel>? = ArrayList()
    var recentSearches: List<TAPRoomListModel>? = ArrayList()
    var personalContacts: ArrayList<TAPRoomListModel>? = ArrayList()
    var groupContacts: ArrayList<TAPRoomListModel>? = ArrayList()
    var searchRoomPointer: MutableMap<String, TAPRoomModel>? = LinkedHashMap()
    var searchKeyword: String? = null
    var pendingSearch: String? = null
    var searchState: Int? = 0

    val STATE_IDLE = 1
    val STATE_SEARCHING = 2
    val STATE_PENDING = 3

    fun addRoomPointer(roomModel: TAPRoomListModel) {
        roomPointer!![roomModel.lastMessage.room.roomID] = roomModel
    }

    fun getSelectedCount(): Int? {
        return selectedRooms?.size
    }

    fun setSearchResults(searchResults: ArrayList<TAPRoomListModel>) {
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

    fun getSearchResults(): List<TAPRoomListModel>? {
        searchRoomResults?.addAll(groupContacts!!)
        searchRoomResults?.addAll(personalContacts!!)
        return searchRoomResults
    }

    fun clearSearchResults() {
        personalContacts?.clear()
        groupContacts?.clear()
        searchRoomResults?.clear()
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