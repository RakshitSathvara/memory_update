package com.narmada.measure.screens.badeli_serdi_report.model

import com.google.gson.annotations.SerializedName

data class BadeliSerdiReportRequest(
    @SerializedName("working_year")
    var working_year: String? = null,
    @SerializedName("from_date")
    var from_date: String? = null,
    @SerializedName("to_date")
    var to_date: String? = null,
    @SerializedName("supervisor_id")
    var supervisor_id: String? = null,
    @SerializedName("zone_id")
    var zone_id: String? = null,
)
