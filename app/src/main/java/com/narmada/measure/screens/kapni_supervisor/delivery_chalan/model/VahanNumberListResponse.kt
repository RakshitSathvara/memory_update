package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class VahanNumberListResponse(

    @SerializedName("response")
    var response: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("data")
    var data: ArrayList<VahanNumber> = arrayListOf()

) : Serializable

data class VahanNumber(

    @SerializedName("Transport_ID")
    var transportID: String? = null,

    @SerializedName("Transport_Name")
    var transportName: String? = null

) : Serializable
