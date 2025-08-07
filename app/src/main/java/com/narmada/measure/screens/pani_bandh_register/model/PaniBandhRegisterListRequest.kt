package com.narmada.measure.screens.pani_bandh_register.model

import com.google.gson.annotations.SerializedName

data class PaniBandhRegisterListRequest(
    @SerializedName("working_year")
    val working_year: String,
    @SerializedName("from_date")
    val from_date: String,
    @SerializedName("to_date")
    val to_date: String,
    @SerializedName("supervisor_id")
    val supervisor_id: String,
    @SerializedName("zone_id")
    val zone_id: String,
    @SerializedName("village_id")
    val village_id: String,
    @SerializedName("mapni_type")
    val mapni_type: String,
    @SerializedName("computer_code")
    val computer_code: String,
    @SerializedName("sabhasad_code")
    val sabhasad_code: String,
)
