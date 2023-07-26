package io.taptalk.TapTalk.Model.RequestModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize data class TapCheckWhatsAppVerificationRequest (
    @field:JsonProperty("phoneWithCode") val phoneWithCode: String = "",
    @field:JsonProperty("verificationID") val verificationID: String = "en",
) : Parcelable
