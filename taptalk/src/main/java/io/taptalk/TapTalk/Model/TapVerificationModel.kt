package io.taptalk.TapTalk.Model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize data class TapVerificationModel (
    @field:JsonProperty("id") val id: String? = "",
    @field:JsonProperty("waLink") val waLink: String? = "",
    @field:JsonProperty("waMessage") val waMessage: String? = "",
    @field:JsonProperty("qrCode") val qrCode: String? = "",
) : Parcelable
