package com.narmada.measure.screens.pani_bandh_yaadi.model

import com.google.gson.annotations.SerializedName

data class PaniBandhYaadiListRequest(
    val working_year: String,
//    @SerializedName("supervisor_id")
//    val supervisor_id: String,
    @SerializedName("zone_id")
    val zone_id: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("village_id")
    val village_id: String? = null,
    @SerializedName("computer_code")
    val computer_code: String? = null,
)
