package io.taptalk.TapTalk.Model.RequestModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapReportUserRequest(
    @field: JsonProperty("category")
    val category: String? = null,

    @field: JsonProperty("isOtherCategory")
    val isOtherCategory: Boolean? = null,

    @field: JsonProperty("reason")
    val reason: String? = null
) {

    @field: JsonProperty("userID")
    var userID: String? = null

    @field: JsonProperty("messageID")
    var messageID: String? = null

    @field: JsonProperty("roomID")
    var roomID: String? = null

}