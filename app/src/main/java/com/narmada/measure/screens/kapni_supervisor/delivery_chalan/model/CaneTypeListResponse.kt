package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model

import com.google.gson.annotations.SerializedName


data class CaneTypeListResponse(

    @SerializedName("response")
    var response: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("data")
    var data: ArrayList<CaneType> = arrayListOf()

)

data class CaneType (

    @SerializedName("caneCode" )
    var caneCode : String? = null,

    @SerializedName("caneType" )
    var caneType : String? = null

)