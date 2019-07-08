package io.taptalk.TapTalk.Manager

import io.taptalk.TapTalk.Model.TAPRoomModel

class TAPGroupManager {

    private var groupDataMap : LinkedHashMap<String, TAPRoomModel>? = null

    companion object {
        private lateinit var instance : TAPGroupManager

        val getInstance : TAPGroupManager
        get() {
            if (null == instance) {
                instance = TAPGroupManager()
            }
            return instance
        }
    }

    private fun getGroupDataMap() : LinkedHashMap<String, TAPRoomModel> {
        if (null == groupDataMap) groupDataMap = linkedMapOf()
        return groupDataMap!!
    }

    fun addGroupData(roomModel: TAPRoomModel) {
        getGroupDataMap()[roomModel.roomID] = roomModel
    }

    fun getGroupData(roomID: String) : TAPRoomModel? {
        return if (!getGroupDataMap().containsKey(roomID)) null
        else {
            getGroupData(roomID)
        }
    }

    fun checkIsRoomDataAvailable(roomID: String) : Boolean {
        return getGroupDataMap().containsKey(roomID)
    }
    
}