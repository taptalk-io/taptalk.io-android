package io.taptalk.TapTalk.Model.RequestModel

import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.TapTalk.Model.TAPMessageModel

data class TapCreateScheduledMessageRequest(

	@field:JsonProperty("scheduledTime")
	val scheduledTime: Long? = null,

	@field:JsonProperty("message")
	val message: TAPMessageModel? = null
)