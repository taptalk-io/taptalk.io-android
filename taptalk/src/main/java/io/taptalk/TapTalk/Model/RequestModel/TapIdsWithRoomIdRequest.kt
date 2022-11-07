package io.taptalk.TapTalk.Model.RequestModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapIdsWithRoomIdRequest(

	@field:JsonProperty("ids")
	val ids: List<Int>? = null,

	@field:JsonProperty("roomID")
	val roomID: String? = null
)
