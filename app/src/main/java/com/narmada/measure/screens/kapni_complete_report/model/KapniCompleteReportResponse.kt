package com.narmada.measure.screens.kapni_complete_report.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class KapniCompleteReportResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: KapniCompleteReport? = null

)

data class KapniCompleteReport(
    @SerializedName("Report_No")
    val reportNo: String? = null
)
