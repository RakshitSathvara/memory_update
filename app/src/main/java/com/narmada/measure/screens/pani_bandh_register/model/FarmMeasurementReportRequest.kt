package com.narmada.measure.screens.pani_bandh_register.model

import com.google.gson.annotations.SerializedName

data class FarmMeasurementReportRequest(
    @SerializedName("working_year")
    var working_year: String? = null,
    @SerializedName("computer_code")
    var computer_code: String? = null,
)
