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
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapSelectableStringListAdapter
import io.taptalk.TapTalk.ViewModel.TapReportViewModel
import kotlinx.android.synthetic.main.activity_tap_report.*

class TapReportActivity : TAPBaseActivity() {
    val vm : TapReportViewModel by lazy {
        ViewModelProvider(this)[TapReportViewModel::class.java]
    }
    private lateinit var optionsAdapter : TapSelectableStringListAdapter
    private var isLoading = false

    enum class ReportType {
        USER, MESSAGE
    }

    companion object {

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
            room: TAPRoomModel
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapReportActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.ROOM, room)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER)
            }
        }

        fun start(
            context: Context,
            instanceKey: String?,
            message: TAPMessageModel,
            reportType: ReportType
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapReportActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.MESSAGE, message)
                intent.putExtra(TAPDefaultConstant.Extras.REPORT_TYPE, reportType)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap_report)

        if (intent.getSerializableExtra(TAPDefaultConstant.Extras.REPORT_TYPE) != null) {
            vm.reportType = intent.getSerializableExtra(TAPDefaultConstant.Extras.REPORT_TYPE) as ReportType
        }
        if (intent.getSerializableExtra(TAPDefaultConstant.Extras.USER) != null) {
            vm.user = intent.getParcelableExtra(TAPDefaultConstant.Extras.USER)
        }
        if (intent.getSerializableExtra(TAPDefaultConstant.Extras.ROOM) != null) {
            vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        }
        if (intent.getSerializableExtra(TAPDefaultConstant.Extras.MESSAGE) != null) {
            vm.message = intent.getParcelableExtra(TAPDefaultConstant.Extras.MESSAGE)
        }
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
        if (vm.reportType == ReportType.MESSAGE) {
            tv_title.text = getString(R.string.tap_report_message)
        }
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
        vm.reportOptions = when (vm.reportType) {
            ReportType.USER -> arrayListOf(
                getString(R.string.tap_report_user_options_1),
                getString(R.string.tap_report_user_options_2),
                getString(R.string.tap_report_user_options_3),
                getString(R.string.tap_report_user_options_4),
                getString(R.string.tap_others)
            )
            ReportType.MESSAGE -> arrayListOf(
                getString(R.string.tap_report_message_options_1),
                getString(R.string.tap_report_message_options_2),
                getString(R.string.tap_report_message_options_3),
                getString(R.string.tap_report_message_options_4),
                getString(R.string.tap_others)
            )
        }

        optionsAdapter = TapSelectableStringListAdapter(vm.reportOptions, onClickListener)
        if (vm.selectedReportOption.isEmpty()) {
            vm.selectedReportOption = vm.reportOptions[0]
        }
        optionsAdapter.setSelectedText(vm.selectedReportOption)
        rv_options.adapter = optionsAdapter
        rv_options.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_options.setHasFixedSize(true)

        btn_submit.setOnClickListener {
            TapTalkDialog.Builder(this)
                .setTitle(getString(R.string.tap_submit_report))
                .setMessage(getString(R.string.tap_wording_submit_report))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setPrimaryButtonTitle(getString(R.string.tap_submit))
                .setPrimaryButtonListener {
                    when (vm.reportType) {
                        ReportType.USER -> TAPDataManager.getInstance(instanceKey).submitUserReport(vm.user?.userID, vm.selectedReportOption, (vm.selectedReportOption == optionsAdapter.items[optionsAdapter.itemCount -1]), et_reason.text.toString(), submitReportView)
                    }
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setSecondaryButtonListener { }
                .show()
        }
        iv_button_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val title: String
        val message: String
        if (isLoading) {
            title = getString(R.string.tap_report_might_not_submitted)
            message = getString(R.string.tap_report_might_not_submitted_wording)
        } else {
            title = getString(R.string.tap_you_havent_submit_report)
            message = getString(R.string.tap_havent_submit_report_wording)
        }
        TapTalkDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setDialogType(TapTalkDialog.DialogType.DEFAULT)
            .setPrimaryButtonTitle(getString(R.string.tap_yes))
            .setPrimaryButtonListener {
                finish()
            }
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setSecondaryButtonListener { }
            .show()
    }

    private fun showLoading() {
        isLoading = true
        pb_loading.visibility = View.VISIBLE
        btn_submit.text = ""
        et_reason.isEnabled = false
        et_other.isEnabled = false
        optionsAdapter.setEnabled(false)
    }

    private fun hideLoading() {
        isLoading = false
        pb_loading.visibility = View.GONE
        btn_submit.text = getString(R.string.tap_submit_report)
        et_reason.isEnabled = true
        et_other.isEnabled = true
        optionsAdapter.setEnabled(true)
    }

    private fun showPopUpError() {
        TapTalkDialog.Builder(this)
            .setTitle(getString(R.string.tap_failed_to_submit_report))
            .setMessage(getString(R.string.tap_failed_submit_report_wording))
            .setDialogType(TapTalkDialog.DialogType.DEFAULT)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setPrimaryButtonListener { }
            .show()
    }

    private val submitReportView = object : TAPDefaultDataView<TAPCommonResponse>() {
        override fun startLoading() {
            super.startLoading()
            showLoading()
        }

        override fun endLoading() {
            super.endLoading()
            hideLoading()
        }

        override fun onSuccess(response: TAPCommonResponse?) {
            super.onSuccess(response)
            TapTalkDialog.Builder(this@TapReportActivity)
                .setTitle(getString(R.string.tap_report_has_been_submitted))
                .setMessage(getString(R.string.tap_thank_you_for_reporting))
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener { }
                .show()
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            endLoading()
            showPopUpError()
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            endLoading()
            showPopUpError()
        }
    }

    private val characterWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            vm.reportReason = text.toString()
            val reasonCount = "${text?.length}/2000"
            tv_character_count.text = reasonCount
        }
    }

    private val onClickListener = object : TAPGeneralListener<String>() {
        override fun onClick(position: Int, item: String?) {
            super.onClick(position, item)
            if (vm.selectedReportOption != item && !isLoading) {
                vm.selectedReportOption = if (item == optionsAdapter.getSelectedText()) {
                    ""
                } else {
                    item.toString()
                }
                if (position == optionsAdapter.itemCount - 1 && item != optionsAdapter.getSelectedText()) {
                    et_other.visibility = View.VISIBLE
                } else {
                    et_other.visibility = View.GONE
                    et_other.setText("")
                    TAPUtils.dismissKeyboard(this@TapReportActivity)
                }
                optionsAdapter.setSelectedText(vm.selectedReportOption)
                optionsAdapter.notifyDataSetChanged()
            }
        }
    }

}