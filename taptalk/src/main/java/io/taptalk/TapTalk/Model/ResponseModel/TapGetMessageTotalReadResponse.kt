package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapGetMessageTotalReadResponse(
	@field:JsonProperty("count")
	val count: Int = 0
)
