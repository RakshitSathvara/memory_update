package com.narmada.measure.screens.khetar_mapni_report.model

import com.google.gson.annotations.SerializedName

data class KhetarMapniReportRequest(
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
    @SerializedName("village_id")
    var village_id: List<Int> = arrayListOf(),
)
