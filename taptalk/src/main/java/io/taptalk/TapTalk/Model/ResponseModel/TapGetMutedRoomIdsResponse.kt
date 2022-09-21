package io.taptalk.TapTalk.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapGetMutedRoomIdsResponse(

	@field:JsonProperty("mutedRooms")
	var mutedRooms: List<TapMutedRoomListModel>? = null
): Parcelable
