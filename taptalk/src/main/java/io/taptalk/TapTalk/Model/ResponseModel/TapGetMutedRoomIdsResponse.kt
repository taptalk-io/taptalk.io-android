package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapGetMutedRoomIdsResponse(

	@field:JsonProperty("mutedRooms")
	val mutedRooms: List<TapMutedRoomListModel>? = null
)
