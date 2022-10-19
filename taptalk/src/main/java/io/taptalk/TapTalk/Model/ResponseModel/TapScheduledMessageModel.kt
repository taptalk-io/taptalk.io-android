package io.taptalk.TapTalk.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.TapTalk.Model.TAPMessageModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapScheduledMessageModel (
    @field:JsonProperty("updatedTime")
    val updatedTime: Long? = null,

    @field:JsonProperty("scheduledTime")
    val scheduledTime: Long? = null,

    @field:JsonProperty("createdTime")
    val createdTime: Long? = null,

    @field:JsonProperty("id")
    val id: Int? = null,

    @field:JsonProperty("message")
    val message: TAPMessageModel? = null
) : Parcelable {

    constructor(id: Int?, message: TAPMessageModel?) : this(null, null, null, id, message)

    constructor(id: Int?, scheduledTime: Long?) : this(null, scheduledTime, null, id, null)

    constructor(scheduledTime: Long?, message: TAPMessageModel?) : this(null, scheduledTime, null, null, message)
}
