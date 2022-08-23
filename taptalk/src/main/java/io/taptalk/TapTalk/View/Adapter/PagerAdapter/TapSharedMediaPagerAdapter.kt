package io.taptalk.TapTalk.View.Adapter.PagerAdapter

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Fragment.TapSharedMediaFragment

class TapSharedMediaPagerAdapter(private val context : Context, private val instanceKey: String, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.tap_m_e_d_i_a)
            1 -> context.getString(R.string.tap_l_i_n_k_s)
            else -> context.getString(R.string.tap_d_o_c_u_m_e_n_t_s)
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            // TODO: 22/08/22 differ media type MU
            0 -> TapSharedMediaFragment.newInstance(instanceKey)
            1 -> TapSharedMediaFragment.newInstance(instanceKey)
            else -> TapSharedMediaFragment.newInstance(instanceKey)
        }
    }

    override fun getCount(): Int = 3

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

}