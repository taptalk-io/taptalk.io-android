package io.taptalk.TapTalk.View.Adapter.PagerAdapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DOCUMENT
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LINK
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_MEDIA
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Fragment.TapSharedMediaFragment

class TapSharedMediaPagerAdapter(private val context : Context, private val instanceKey: String, fm: FragmentManager, private val room : TAPRoomModel?) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.tap_media)
            1 -> context.getString(R.string.tap_links)
            else -> context.getString(R.string.tap_documents)
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TapSharedMediaFragment.newInstance(instanceKey, TYPE_MEDIA, room)
            1 -> TapSharedMediaFragment.newInstance(instanceKey, TYPE_LINK, room)
            else -> TapSharedMediaFragment.newInstance(instanceKey, TYPE_DOCUMENT, room)
        }
    }

    override fun getCount(): Int = 3
}