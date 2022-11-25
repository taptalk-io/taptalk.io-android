package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapGetMessageDetailResponse(

	@field:JsonProperty("message")
	val message: HashMap<String, Any>? = null,

	@field:JsonProperty("deliveredTo")
	val deliveredTo: List<TapMessageRecipientModel>? = null,

	@field:JsonProperty("readBy")
	val readBy: List<TapMessageRecipientModel>? = null
)