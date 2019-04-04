package io.taptalk.TapTalk.View.Fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_fragment_login_verification.*

class TAPLoginVerificationFragment : Fragment() {
    var otpTimer: CountDownTimer? = null
    val waitTime = 30L

    companion object {
        fun getInstance(phoneNumber: String): TAPLoginVerificationFragment {
            val instance = TAPLoginVerificationFragment()
            val args = Bundle()
            args.putString("PhoneNumber", phoneNumber)
            instance.arguments = args
            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tap_fragment_login_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewListener()
    }

    private fun initViewListener() {
        tv_phone_number.text = arguments?.getString("PhoneNumber", "") ?: ""
        iv_back_button.setOnClickListener {
            activity?.onBackPressed()
        }
        et_otp_code.addTextChangedListener(otpTextWatcher)
        et_otp_code.requestFocus()
        TAPUtils.getInstance().showKeyboard(activity, et_otp_code)
        setAndStartTimer()
    }

    private fun setAndStartTimer() {
        tv_otp_timer.visibility = View.VISIBLE
        tv_request_otp_again.visibility = View.GONE
        otpTimer = object : CountDownTimer(waitTime * 1000, 1000) {
            override fun onFinish() {
                tv_otp_timer.visibility = View.GONE
                tv_request_otp_again.visibility = View.VISIBLE
            }

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                val minuteLeft = timeLeft / 60
                val secondLeft = timeLeft - (60 * (timeLeft / 60))
                when (minuteLeft) {
                    0L -> {
                        try {
                            if (10 > secondLeft) tv_otp_timer.text = "Wait 0:0$secondLeft"
                            else tv_otp_timer.text = "Wait 0:$secondLeft"
                        } catch (e: Exception) {
                            cancelTimer()
                            e.printStackTrace()
                        }
                    }
                    else -> {
                        try {
                            tv_otp_timer.text = "Wait $minuteLeft:$secondLeft"
                        } catch (e: Exception) {
                            cancelTimer()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }.start()
    }

    private fun cancelTimer() {
        otpTimer?.cancel()
    }

    val otpTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s?.length) {
                1 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.VISIBLE
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.INVISIBLE
                    iv_otp_filled_3.visibility = View.INVISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                2 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.INVISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                3 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                4 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.INVISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.VISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                5 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.INVISIBLE
                    v_pointer_5.visibility = View.INVISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.VISIBLE
                    iv_otp_filled_5.visibility = View.VISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                6 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.INVISIBLE
                    v_pointer_5.visibility = View.INVISIBLE
                    v_pointer_6.visibility = View.INVISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.VISIBLE
                    iv_otp_filled_5.visibility = View.VISIBLE
                    iv_otp_filled_6.visibility = View.VISIBLE
                }
                else -> {
                    v_pointer_1.visibility = View.VISIBLE
                    v_pointer_2.visibility = View.VISIBLE
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.INVISIBLE
                    iv_otp_filled_2.visibility = View.INVISIBLE
                    iv_otp_filled_3.visibility = View.INVISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
            }
        }
    };
}
