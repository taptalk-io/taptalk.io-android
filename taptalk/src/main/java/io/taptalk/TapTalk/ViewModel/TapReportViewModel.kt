package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.View.Activity.TapReportActivity

class TapReportViewModel: ViewModel() {

    var reportType: TapReportActivity.Companion.ReportType = TapReportActivity.Companion.ReportType.USER
    var reportOptions = ArrayList<String>()
    var selectedReportOption : String = ""
    var reportReason : String = ""
}