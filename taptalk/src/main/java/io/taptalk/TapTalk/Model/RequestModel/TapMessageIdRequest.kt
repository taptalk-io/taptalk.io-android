package io.taptalk.TapTalk.Model.RequestModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapMessageIdRequest(

    @field:JsonProperty("messageID")
    val messageID: String? = null
)
