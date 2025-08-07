package com.narmada.measure.screens.khetar_mapni_report.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class KhetarMapniReportResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: List<KhetarMapniReport>? = null

)

data class KhetarMapniReport(

    @field:SerializedName("Village_Name")
    val villageName: String? = null,

    @field:SerializedName("Ropan_Area")
    val ropanArea: String? = null,

    @field:SerializedName("Count")
    val count: String? = null

)
