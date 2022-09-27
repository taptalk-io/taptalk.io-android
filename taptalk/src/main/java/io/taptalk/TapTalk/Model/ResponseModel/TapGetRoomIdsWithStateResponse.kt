package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapGetRoomIdsWithStateResponse(

	@field:JsonProperty("clearedRooms")
	val clearedRooms: List<TapMutedRoomListModel>? = null,

	@field:JsonProperty("pinnedRoomIDs")
	val pinnedRoomIDs: List<String>? = null,

	@field:JsonProperty("mutedRooms")
	val mutedRooms: List<TapMutedRoomListModel>? = null,

	@field:JsonProperty("unreadRoomIDs")
	val unreadRoomIDs: List<String>? = null
)