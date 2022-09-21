package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapMutedRoomListModel(

	@field:JsonProperty("expiredAt")
	var expiredAt: Long? = null,

	@field:JsonProperty("roomID")
	var roomID: String? = null
) : Parcelable
