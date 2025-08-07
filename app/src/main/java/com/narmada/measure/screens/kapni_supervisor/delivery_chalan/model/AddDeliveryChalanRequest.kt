package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model

import com.google.gson.annotations.SerializedName

data class AddDeliveryChalanRequest(
    @SerializedName("working_year")
    var working_year: String,
    @SerializedName("date")
    var date: String,
    @SerializedName("computer_code")
    var computer_code: String,
    @SerializedName("account_id")
    var account_id: String,
    @SerializedName("lam_ropan")
    var lam_ropan: String,
    @SerializedName("sankal")
    var sankal: String,
    @SerializedName("cane_type")
    var cane_type: String,
    @SerializedName("transport_id")
    var transport_id: String,
    @SerializedName("transport_id_one")
    var transport_id_one: String,
    @SerializedName("driver_name")
    var driver_name: String,
    @SerializedName("mukadam_id")
    var mukadam_id: String,
    @SerializedName("farm_in")
    var farm_in: String,
    @SerializedName("farm_out")
    var farm_out: String,
    @SerializedName("cutting_close_date")
    var cutting_close_date: String,
    @SerializedName("rasid_number")
    var rasid_number: String,
)
