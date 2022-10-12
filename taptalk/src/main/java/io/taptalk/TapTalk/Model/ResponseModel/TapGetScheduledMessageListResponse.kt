package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapGetScheduledMessageListResponse(

	@field:JsonProperty("items")
	val items: List<TapScheduledMessageModel>? = null
)