package io.taptalk.TapTalk.View.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_fragment_login_verification.*

class TAPLoginVerificationFragment : Fragment() {
    companion object {
        fun getInstance(phoneNumber: String): TAPLoginVerificationFragment {
            val instance = TAPLoginVerificationFragment()
            val args = Bundle()
            args.putString("PhoneNumber", phoneNumber)
            instance.arguments = args
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tap_fragment_login_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_phone_number.text = arguments?.getString("PhoneNumber", "") ?: ""
        iv_back_button.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}
