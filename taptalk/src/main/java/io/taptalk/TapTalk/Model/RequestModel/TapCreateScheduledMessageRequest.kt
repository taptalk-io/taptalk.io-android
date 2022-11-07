package io.taptalk.TapTalk.Model.RequestModel

import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.TapTalk.Model.TAPMessageModel

data class TapCreateScheduledMessageRequest(
	@field:JsonProperty("message")
	val message: HashMap<String, Any>? = null
) {

	@field:JsonProperty("scheduledTime")
	var scheduledTime: Long? = null

	@field:JsonProperty("id")
	var id: Int? = null

}