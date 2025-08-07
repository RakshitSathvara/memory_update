package com.narmada.measure.screens.kapni_complete_report_view.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class KapniPuriMahitiReportDownloadResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("url")
    val url: List<String>? = null

)
