package com.narmada.measure.screens.bareliserdi.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class GetFormNumberResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("Burn_Form_No")
    val Burn_Form_No: String? = null

)
