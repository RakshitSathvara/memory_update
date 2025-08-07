package com.narmada.measure.screens.bareliserdi.model

import com.google.gson.annotations.SerializedName

data class AddBareliSerdiRequest(
    @SerializedName("account_id")
    val account_id: String? = null,
    @SerializedName("computer_code")
    val computer_code: String? = null,
    @SerializedName("moje_village_id")
    val moje_village_id: String? = null,
    @SerializedName("ropan_or_laam_date")
    val ropan_or_laam_date: String? = null,
    @SerializedName("sherdi_badeli_date")
    val sherdi_badeli_date: String? = null,
    @SerializedName("mapni_type")
    val mapni_type: String? = null,
    @SerializedName("item_id")
    val item_id: String? = null,
    @SerializedName("kapat_id")
    val kapat_id: String? = null,
    @SerializedName("total_area")
    val total_area: String? = null,
    @SerializedName("burned_area")
    val burned_area: String? = null,
    @SerializedName("working_year")
    val working_year: String? = null,
    @SerializedName("burn_form_no")
    val burn_form_no: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("time")
    val time: String? = null
)
