package io.taptalk.TapTalk.Manager

import io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_GROUP_MAX_PARTICIPANTS
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.GROUP_MAX_PARTICIPANTS
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TAPSocketListener
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse
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

    fun getGroupMaxParticipants(): Int {
        val maxParticipants = TapTalk.getCoreConfigs()[GROUP_MAX_PARTICIPANTS]
        return maxParticipants?.toInt() ?: DEFAULT_GROUP_MAX_PARTICIPANTS.toInt()
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

    fun removeGroupData(roomID: String) {
        getGroupDataMap().remove(roomID)
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

    fun updateGroupDataFromResponse(response: TAPCreateRoomResponse): TAPRoomModel? {
        val room = response.room
        if (null != room) {
            if (null != response.participants && response.participants!!.isNotEmpty()) {
                room.groupParticipants = response.participants
            } else {
                room.groupParticipants = getInstance.getGroupData(response.room!!.roomID)?.groupParticipants
            }
            if (null != response.admins && response.admins!!.isNotEmpty()) {
                room.admins = response.admins
            } else {
                room.admins = getInstance.getGroupData(response.room!!.roomID)?.admins
            }
            addGroupData(room)
        }
        return room
    }

    fun updateGroupDataFromResponse(response: TAPUpdateRoomResponse): TAPRoomModel? {
        val room = response.room
        if (null != room) {
            val existingRoom = getInstance.getGroupData(response.room!!.roomID)
            room.groupParticipants = existingRoom?.groupParticipants
            room.admins = existingRoom?.admins
            updateRoomDataNameAndImage(room)
        }
        return room
    }
}
