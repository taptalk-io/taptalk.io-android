package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapCreateScheduledMessageResponse(

	@field:JsonProperty("success")
	val success: Boolean? = null,

	@field:JsonProperty("message")
	val message: String? = null,

	@field:JsonProperty("createdItem")
	val createdItem: TapScheduledMessageModelWithMap? = null
)