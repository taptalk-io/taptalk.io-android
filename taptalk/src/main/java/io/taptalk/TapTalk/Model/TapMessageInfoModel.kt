package io.taptalk.TapTalk.Model

import com.fasterxml.jackson.annotation.JsonProperty

// TODO: update model when API ready MU
data class TapMessageInfoModel(

    @JsonProperty("user")
    val user : TAPUserModel?,

    @JsonProperty("read")
    val readTime : Long,

    @JsonProperty("delivered")
    val deliveredTime : Long
)
