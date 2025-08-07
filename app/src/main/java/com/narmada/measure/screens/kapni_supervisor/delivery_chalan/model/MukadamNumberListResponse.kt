package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MukadamNumberListResponse(

    @SerializedName("response")
    var response: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("data")
    var data: ArrayList<MukadamNumber> = arrayListOf()

) : Serializable

data class MukadamNumber(

    @SerializedName("Mukadam_Id")
    var mukadamID: String? = null,

    @SerializedName("Mukadam_Name")
    var mukadamName: String? = null

) : Serializable
