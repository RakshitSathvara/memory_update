package com.narmada.measure.screens.sabhasad_mahiti_update.model

import com.google.gson.annotations.SerializedName

data class SabhasadMahitiUpdateRequest(

    @SerializedName("farmer_id")
    var farmerId: String? = null,

    @SerializedName("mobile_number")
    var mobileNumber: String? = null,

    @SerializedName("members")
    var members: ArrayList<String>,

)
