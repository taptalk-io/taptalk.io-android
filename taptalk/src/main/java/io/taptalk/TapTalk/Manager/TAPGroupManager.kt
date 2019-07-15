package io.taptalk.TapTalk.Manager

import io.taptalk.TapTalk.Interface.TapTalkSocketInterface
import io.taptalk.TapTalk.Listener.TAPSocketListener
import io.taptalk.TapTalk.Model.TAPRoomModel

class TAPGroupManager {

    var refreshRoomList = false

    private var groupDataMap : HashMap<String, TAPRoomModel>? = null

    companion object {
        private var instance : TAPGroupManager? = null

        val getInstance : TAPGroupManager
        get() {
            if (null == instance) {
                instance = TAPGroupManager()
            }
            return instance!!
        }
    }

    init {
      TAPConnectionManager.getInstance().addSocketListener(object : TAPSocketListener() {
          override fun onSocketDisconnected() {
              saveRoomDataMapToPreference()
          }
      })
    }

    private fun getGroupDataMap() : HashMap<String, TAPRoomModel> {
        if (null == groupDataMap) groupDataMap = linkedMapOf()
        return groupDataMap!!
    }

    fun addGroupData(roomModel: TAPRoomModel) {
        getGroupDataMap()[roomModel.roomID] = roomModel
    }

    fun getGroupData(roomID: String) : TAPRoomModel? {
        return if (!getGroupDataMap().containsKey(roomID)) null
        else {
            getGroupDataMap()[roomID]
        }
    }

    fun checkIsRoomDataAvailable(roomID: String) : Boolean {
        return getGroupDataMap().containsKey(roomID) && null != getGroupData(roomID)
    }

    fun clearGroupData() {
        getGroupDataMap().clear()
    }

    fun updateRoomDataNameAndImage(roomModel: TAPRoomModel) {
        if (checkIsRoomDataAvailable(roomModel.roomID)) {
            getGroupData(roomModel.roomID)?.roomName = roomModel.roomName
            getGroupData(roomModel.roomID)?.roomImage = roomModel.roomImage
        }
    }

    fun loadAllRoomDataFromPreference() {
        groupDataMap = TAPDataManager.getInstance().roomDataMap
    }

    fun saveRoomDataMapToPreference() {
        TAPDataManager.getInstance().saveRoomDataMap(groupDataMap)
        groupDataMap?.clear()
    }
}