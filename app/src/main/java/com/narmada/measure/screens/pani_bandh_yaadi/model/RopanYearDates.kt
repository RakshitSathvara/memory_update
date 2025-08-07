package com.narmada.measure.screens.pani_bandh_yaadi.model

import com.google.gson.annotations.SerializedName


data class RopanYearDates(

    @SerializedName("response")
    var response: String?,

    @SerializedName("message")
    var message: String?,

    @SerializedName("data")
    var data: ArrayList<String>?

)
