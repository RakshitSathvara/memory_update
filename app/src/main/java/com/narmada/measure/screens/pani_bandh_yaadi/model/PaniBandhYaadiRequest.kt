package com.narmada.measure.screens.pani_bandh_yaadi.model

import com.google.gson.annotations.SerializedName

data class PaniBandhYaadiRequest(
    @SerializedName("working_year")
    var working_year: String,
//    @SerializedName("supervisor_id")
//    var supervisor_id: String? = null,
    @SerializedName("zone_id")
    var zone_id: String,
    @SerializedName("date")
    var date: String,
    @SerializedName("village_id")
    var village_id: String? = null,
    @SerializedName("computer_code")
    var computer_code: String,
)
