package com.narmada.measure.screens.kapni_complete_report.model

import com.google.gson.annotations.SerializedName


data class KhetarCodeResponse(

    @SerializedName("response")
    var response: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("data")
    var data: ArrayList<KhetarCode> = arrayListOf()

)


data class KhetarCode(

    @SerializedName("Computer_Code")
    var computerCode: String? = null

)