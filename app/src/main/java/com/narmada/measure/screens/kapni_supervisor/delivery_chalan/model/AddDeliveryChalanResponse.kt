package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class AddDeliveryChalanResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: AddDeliveryChalan? = null

)

data class AddDeliveryChalan(
    @SerializedName("Doc_No")
    val docNo: String? = null
)
