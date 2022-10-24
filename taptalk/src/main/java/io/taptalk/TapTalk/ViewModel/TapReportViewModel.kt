package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Activity.TapReportActivity

class TapReportViewModel: ViewModel() {

    var reportType: TapReportActivity.ReportType = TapReportActivity.ReportType.USER
    var reportOptions = ArrayList<String>()
    var selectedReportOption : String = ""
    var reportReason : String = ""
    var user : TAPUserModel? = null
    var message : TAPMessageModel? = null
}