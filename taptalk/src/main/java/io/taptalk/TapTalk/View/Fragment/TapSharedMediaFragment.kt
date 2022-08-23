package io.taptalk.TapTalk.View.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapSharedMediaAdapter
import kotlinx.android.synthetic.main.tap_fragment_shared_media.*

class TapSharedMediaFragment(private val instanceKey: String): Fragment() {

    companion object {
        fun newInstance(instanceKey: String): TapSharedMediaFragment {
            val args = Bundle()
            val fragment = TapSharedMediaFragment(instanceKey)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view  = inflater.inflate(R.layout.tap_fragment_shared_media, container, false)
        // TODO: 22/08/22 set shared media adapter MU
//        recycler_view.adapter = TapSharedMediaAdapter(instanceKey, )
        return view
    }
}