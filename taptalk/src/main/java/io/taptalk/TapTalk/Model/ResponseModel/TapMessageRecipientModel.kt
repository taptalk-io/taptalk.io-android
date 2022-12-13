package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.TapTalk.Model.TAPImageURL

data class TapMessageRecipientModel(

    @field:JsonProperty("userImageURL")
    val userImageURL: TAPImageURL? = null,

    @field:JsonProperty("xcUserID")
    val xcUserID: String? = null,

    @field:JsonProperty("readTime")
    val readTime: Long? = null,

    @field:JsonProperty("fullname")
    val fullname: String? = null,

    @field:JsonProperty("deliveredTime")
    val deliveredTime: Long? = null,

    @field:JsonProperty("userID")
    val userID: String? = null
) {
    constructor(readTime: Long?, deliveredTime: Long?) : this(null, null, readTime, null, deliveredTime, null)
}
