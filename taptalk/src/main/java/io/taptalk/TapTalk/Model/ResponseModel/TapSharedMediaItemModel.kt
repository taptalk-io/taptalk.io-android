package io.taptalk.TapTalk.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.TapTalk.Model.TAPMessageModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapSharedMediaItemModel(
    @field:JsonProperty("messageType")
    val messageType: Int? = null,

    @field:JsonProperty("data")
    val data: String? = null,

    @field:JsonProperty("created")
    val created: Long? = null,

    @field:JsonProperty("messageID")
    val messageID: String? = null,

    @field:JsonProperty("userFullname")
    val userFullname: String? = null,

    @field:JsonProperty("localID")
    val localID: String? = null,

    @field:JsonProperty("userID")
    val userID: String? = null
) : Parcelable {
    companion object {
        const val TYPE_DATE_SECTION = 1
        const val TYPE_MEDIA = 2
        const val TYPE_LINK = 3
        const val TYPE_DOCUMENT = 4
        const val TYPE_LOADING = 5
    }
}
