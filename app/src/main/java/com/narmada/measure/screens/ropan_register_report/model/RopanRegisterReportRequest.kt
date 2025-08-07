package com.narmada.measure.screens.ropan_register_report.model

import com.google.gson.annotations.SerializedName

data class RopanRegisterReportRequest(
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
    @SerializedName("report_type")
    var report_type: String? = null, // "1" for ItemId, "2" for VillageId
    @SerializedName("item_id")
    var item_id: List<Int> = arrayListOf(),
    @SerializedName("village_id")
    var village_id: List<Int> = arrayListOf(),
    @SerializedName("mapni_type")
    var mapni_type: String? = null,
)
