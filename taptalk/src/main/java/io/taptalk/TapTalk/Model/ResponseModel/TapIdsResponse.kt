package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class TapIdsResponse(

    @field:JsonProperty("ids")
    @field:JsonAlias("deletedIDs, sentIDs")
    val ids: List<Int>? = null
)
