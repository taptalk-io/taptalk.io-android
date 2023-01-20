package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.TapTalk.Model.TAPRoomModel

data class TapRoomModelsResponse(

    @field:JsonProperty("rooms")
    val rooms: List<TAPRoomModel>? = null
)
