package io.taptalk.TapTalk.Model.RequestModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize data class TapWhatsAppVerificationRequest (
    @field:JsonProperty("countryID") val countryID: Int = 0,
    @field:JsonProperty("phone") val phone: String = "",
    @field:JsonProperty("languageCode") val languageCode: String = "en",
    @field:JsonProperty("appLink") val appLink: String? = null,
) : Parcelable
