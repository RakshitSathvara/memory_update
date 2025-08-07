package com.narmada.measure.screens.sabhasad_mahiti_update.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class SabhasadMahitiUpdateResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

)
