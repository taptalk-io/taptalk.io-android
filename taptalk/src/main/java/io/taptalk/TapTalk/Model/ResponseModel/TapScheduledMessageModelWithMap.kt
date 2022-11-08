package io.taptalk.TapTalk.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapScheduledMessageModelWithMap (
    @field:JsonProperty("updatedTime")
    val updatedTime: Long? = null,

    @field:JsonProperty("scheduledTime")
    val scheduledTime: Long? = null,

    @field:JsonProperty("createdTime")
    val createdTime: Long? = null,

    @field:JsonProperty("id")
    val id: Int? = null,

    @field:JsonProperty("message")
    val message: HashMap<String, Any>? = null
) : Parcelable

