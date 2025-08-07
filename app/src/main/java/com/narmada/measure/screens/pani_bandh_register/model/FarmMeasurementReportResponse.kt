package com.narmada.measure.screens.pani_bandh_register.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class FarmMeasurementReportResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("url")
    val url: String? = null

)
