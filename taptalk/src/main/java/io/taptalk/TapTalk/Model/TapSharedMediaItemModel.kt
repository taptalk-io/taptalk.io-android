package io.taptalk.TapTalk.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapSharedMediaItemModel(
    var message : TAPMessageModel? = null,
    var type: Int
) : Parcelable {
    companion object {
        const val TYPE_DATE_SECTION = 1
        const val TYPE_MEDIA = 2
        const val TYPE_LINK = 3
        const val TYPE_DOCUMENT = 4
    }
}
