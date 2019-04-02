package io.taptalk.TapTalk.View.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_fragment_phone_login.*

class TAPPhoneLoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tap_fragment_phone_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        et_phone_number.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textCount = s?.length?: 0
                when {
                    textCount in 7..15 -> {
                        fl_continue_btn.background = resources.getDrawable(R.drawable.tap_bg_gradient_ff9833_ff7e00_rounded_8dp_stroke_ff7e00_1dp)
                        fl_continue_btn.setOnClickListener { Log.e("><><><", "Test") }
                        fl_continue_btn.isClickable = true
                    }
                    else -> {
                        fl_continue_btn.background = resources.getDrawable(R.drawable.tap_bg_gradient_cecece_9b9b9b_rounded_8dp_stroke_cecece_1dp)
                        fl_continue_btn.setOnClickListener(null)
                        fl_continue_btn.isClickable = false
                    }
                }
            }

        })
    }

    private fun attemptLogin() {

    }
}
