package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapMutedRoomListModel(

	@field:JsonProperty("expiredAt")
	val expiredAt: Long? = null,

	@field:JsonProperty("roomID")
	val roomID: String? = null
) : Parcelable
