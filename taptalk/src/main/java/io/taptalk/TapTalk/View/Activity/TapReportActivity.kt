package io.taptalk.TapTalk.View.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import kotlinx.android.synthetic.main.activity_tap_report.*

class TapReportActivity : TAPBaseActivity() {
    var reportType: ReportType = ReportType.USER
    companion object {
        enum class ReportType {
            USER, MESSAGE
        }
        fun start(
            context: Context,
            instanceKey: String?,
            user: TAPUserModel
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapReportActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.USER, user)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER)
            }
        }

        fun start(
            context: Context,
            instanceKey: String?,
            user: TAPUserModel,
            reportType: ReportType
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapReportActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.USER, user)
                intent.putExtra(TAPDefaultConstant.Extras.REPORT_TYPE, reportType)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap_report)

        val spannable = SpannableString(tv_label_optional.text)
        spannable.setSpan(
            TypefaceSpan("sans-serif"),
            37,
            tv_label_optional.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.tapColorTextMedium)),
            37,
            tv_label_optional.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            37,
            tv_label_optional.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        tv_label_optional.text = spannable
        et_reason.addTextChangedListener(characterWatcher)
        et_reason.setOnTouchListener { view, motionEvent ->
            if (et_reason.hasFocus()) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                if (motionEvent.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_SCROLL) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
        if (intent.getSerializableExtra(TAPDefaultConstant.Extras.REPORT_TYPE) != null) {
            reportType = intent.getSerializableExtra(TAPDefaultConstant.Extras.REPORT_TYPE) as ReportType
        }
    }

    private val characterWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            val reasonCount = "${et_reason.text.length}/2000"
            tv_character_count.text = reasonCount
        }
    }

}