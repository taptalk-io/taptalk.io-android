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
import io.taptalk.TapTalk.databinding.ActivityTapReportBinding

class TapReportActivity : TAPBaseActivity() {

    private lateinit var vb : ActivityTapReportBinding
    private lateinit var optionsAdapter : TapSelectableStringListAdapter
    val vm : TapReportViewModel by lazy {
        ViewModelProvider(this)[TapReportViewModel::class.java]
    }
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
                context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
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
                context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
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
                context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityTapReportBinding.inflate(layoutInflater)
        setContentView(vb.root)
        if (finishIfNotLoggedIn()) {
            return
        }

        if (intent.getSerializableExtra(TAPDefaultConstant.Extras.REPORT_TYPE) != null) {
            vm.reportType = intent.getSerializableExtra(TAPDefaultConstant.Extras.REPORT_TYPE) as ReportType
        }
        if (intent.getParcelableExtra<TAPUserModel>(TAPDefaultConstant.Extras.USER) != null) {
            vm.user = intent.getParcelableExtra(TAPDefaultConstant.Extras.USER)
        }
        if (intent.getParcelableExtra<TAPRoomModel>(TAPDefaultConstant.Extras.ROOM) != null) {
            vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        }
        if (intent.getParcelableExtra<TAPMessageModel>(TAPDefaultConstant.Extras.MESSAGE) != null) {
            vm.message = intent.getParcelableExtra(TAPDefaultConstant.Extras.MESSAGE)
        }
        val spannable = SpannableString(vb.tvLabelOptional.text)
        spannable.setSpan(
            TypefaceSpan("sans-serif"),
            37,
            vb.tvLabelOptional.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.tapColorTextMedium)),
            37,
            vb.tvLabelOptional.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            37,
            vb.tvLabelOptional.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        vb.tvLabelOptional.text = spannable
        if (vm.reportType == ReportType.MESSAGE) {
            vb.tvTitle.text = getString(R.string.tap_report_message)
        }
        vb.etOther.addTextChangedListener(otherTextWatcher)
        vb.etReason.addTextChangedListener(characterWatcher)
        vb.etReason.setOnTouchListener { view, motionEvent ->
            if (vb.etReason.hasFocus()) {
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

        optionsAdapter = TapSelectableStringListAdapter(vm.reportOptions, onClickListener, vm.selectedReportOption)
        vb.rvOptions.adapter = optionsAdapter
        vb.rvOptions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        vb.rvOptions.setHasFixedSize(true)

        if (vm.selectedReportOption == vm.reportOptions[vm.reportOptions.size - 1]) {
            vb.etOther.visibility = View.VISIBLE
        }

        vb.btnSubmit.setOnClickListener {
            if (vm.selectedReportOption.isEmpty()) {
                showOtherError(R.string.tap_please_select_a_category)
            }
            else {
                val isOther = vm.selectedReportOption == optionsAdapter.items[optionsAdapter.itemCount - 1]
                if (isOther && vb.etOther.text.isEmpty()) {
                    showOtherError(R.string.tap_this_field_is_required)
                }
                else {
                    TapTalkDialog.Builder(this)
                        .setTitle(getString(R.string.tap_submit_report))
                        .setMessage(getString(R.string.tap_wording_submit_report))
                        .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                        .setPrimaryButtonTitle(getString(R.string.tap_submit))
                        .setPrimaryButtonListener {
                            val category = if (isOther) vb.etOther.text.toString() else vm.selectedReportOption
                            when (vm.reportType) {
                                ReportType.USER -> TAPDataManager.getInstance(instanceKey)
                                    .submitUserReport(
                                        vm.user?.userID,
                                        category,
                                        isOther,
                                        vb.etReason.text.toString(),
                                        submitReportView
                                    )
                                ReportType.MESSAGE -> TAPDataManager.getInstance(instanceKey)
                                    .submitMessageReport(
                                        vm.message?.messageID,
                                        vm.message?.room?.roomID,
                                        category,
                                        isOther,
                                        vb.etReason.text.toString(),
                                        submitReportView
                                    )
                            }
                        }
                        .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                        .setSecondaryButtonListener { }
                        .show()
                }
            }
        }
        vb.ivButtonBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val title: String
        val message: String
        if (isLoading) {
            title = getString(R.string.tap_report_might_not_submitted)
            message = getString(R.string.tap_report_might_not_submitted_wording)
        }
        else if (vm.selectedReportOption.isNotEmpty() || vm.reportReason.isNotEmpty()) {
            title = getString(R.string.tap_you_havent_submit_report)
            message = getString(R.string.tap_havent_submit_report_wording)
        }
        else {
            title = ""
            message = ""
        }
        if (title.isNotEmpty()) {
            TapTalkDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setPrimaryButtonTitle(getString(R.string.tap_yes))
                .setPrimaryButtonListener {
                    finish()
                    overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setSecondaryButtonListener { }
                .show()
        }
        else {
            try {
                super.onBackPressed()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showLoading() {
        isLoading = true
        vb.pbLoading.visibility = View.VISIBLE
        vb.btnSubmit.text = ""
        vb.etReason.isEnabled = false
        vb.etOther.isEnabled = false
        optionsAdapter.setEnabled(false)
    }

    private fun hideLoading() {
        isLoading = false
        vb.pbLoading.visibility = View.GONE
        vb.btnSubmit.text = getString(R.string.tap_submit_report)
        vb.etReason.isEnabled = true
        vb.etOther.isEnabled = true
        optionsAdapter.setEnabled(true)
    }

    private fun showOtherError(stringRes: Int) {
        if (vb.tvErrorOther.visibility == View.GONE) {
            vb.tvErrorOther.text = getString(stringRes)
            vb.tvErrorOther.visibility = View.VISIBLE
            vb.etOther.setBackgroundResource(R.drawable.tap_bg_text_field_error)
        }
    }

    private fun hideOtherError() {
        if (vb.tvErrorOther.visibility == View.VISIBLE) {
            vb.tvErrorOther.visibility = View.GONE
            vb.etOther.setBackgroundResource(R.drawable.tap_bg_area_text_field)
        }
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
                .setPrimaryButtonListener {
                    finish()
                }
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

    private val otherTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            hideOtherError()
        }
    }

    private val characterWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            vm.reportReason = text.toString()
            val reasonCount = "${text?.length}/2000"
            vb.tvCharacterCount.text = reasonCount
        }
    }

    private val onClickListener = object : TAPGeneralListener<String>() {
        override fun onClick(position: Int, item: String?) {
            super.onClick(position, item)
            if (vm.selectedReportOption != item && !isLoading) {
                vm.selectedReportOption = if (item == optionsAdapter.getSelectedText()) {
                    ""
                }
                else {
                    item.toString()
                }
                if (position == optionsAdapter.itemCount - 1 && item != optionsAdapter.getSelectedText()) {
                    vb.etOther.visibility = View.VISIBLE
                }
                else {
                    vb.etOther.visibility = View.GONE
                    vb.etOther.setText("")
                    TAPUtils.dismissKeyboard(this@TapReportActivity)
                }
                optionsAdapter.setSelectedText(vm.selectedReportOption)
                optionsAdapter.notifyDataSetChanged()
                hideOtherError()
            }
        }
    }

}