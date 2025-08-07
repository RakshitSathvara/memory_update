package com.narmada.measure.screens.badeli_serdi_report.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class BadeliSerdiReportResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("url")
    val url: String? = null

)
