package com.narmada.measure.screens.kapni_supervisor.chalan_history.model

import com.google.gson.annotations.SerializedName
import com.narmada.measure.screens.pani_bandh_yaadi.model.Pagination

data class DeliveryChalanHistoryResponse(

    @SerializedName("response")
    var response: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("data")
    var data: DeliveryChalanHistory? = null

)

data class DeliveryChalanHistory(

    @SerializedName("pagination")
    var pagination: Pagination?,

    @SerializedName("deliveryChallan")
    var deliveryChallan: ArrayList<DeliveryChalan>?

)

data class DeliveryChalan(

    @SerializedName("Working_Year")
    var workingYear: String? = null,

    @SerializedName("Doc_No")
    var docNo: String? = null,

    @SerializedName("rasid_number")
    var rasidNumber: String? = null,

    @SerializedName("Doc_Date")
    var docDate: String? = null,

    @SerializedName("Computer_Code")
    var computerCode: String? = null,

    @SerializedName("Account_Id")
    var accountId: String? = null,

    @SerializedName("Account_name")
    var accountName: String? = null,

    @SerializedName("Lam_Ropan")
    var lamRopan: String? = null,

    @SerializedName("Sankal")
    var sankal: String? = null,

    @SerializedName("Cane_Type")
    var caneType: String? = null,

    @SerializedName("Transport_Name")
    var transportName: String? = null,

    @SerializedName("Transport_Sadhan")
    var transportSadhan: String? = null,

    @SerializedName("Transport_Name_One")
    var transportNameOne: String? = null,

    @SerializedName("Transport_Sadhan_One")
    var transportSadhanOne: String? = null,

    @SerializedName("Driver_Name")
    var driverName: String? = null,

    @SerializedName("Mukadam_Id")
    var mukadamId: String? = null,

    @SerializedName("Mukadam_Name")
    var mukadamName: String? = null,

    @SerializedName("Farm_In")
    var farmIn: String? = null,

    @SerializedName("Farm_Out")
    var farmOut: String? = null,

    @SerializedName("Cutting_Close_Date")
    var cuttingCloseDate: String? = null

)