package com.narmada.measure.screens.kapni_complete_report.model

import com.google.gson.annotations.SerializedName

data class KapniCompleteReportRequest(
    @SerializedName("working_year")
    var working_year: String,
    @SerializedName("supervisor_id")
    var supervisor_id: String,
    @SerializedName("zone_id")
    var zone_id: String,
    @SerializedName("account_id")
    var account_id: String,
    @SerializedName("computer_code")
    var computer_code: String,
    @SerializedName("report_no_view_only")
    var report_no_view_only: String,
    @SerializedName("report_no")
    var report_no: String,
)
