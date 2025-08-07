package com.narmada.measure.screens.kapni_complete_report_view.model

import com.google.gson.annotations.SerializedName

data class KapniPuriThayaniMahitiListResponse(
    @SerializedName("response")
    val response: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: List<CaneReceiptMainItem>?,
)

data class CaneReceiptMainItem(
    @SerializedName("Report_No")
    val reportNo: String,
    @SerializedName("Report_Date")
    val reportDate: String,
    @SerializedName("Computer_Code")
    val computerCode: String,
    @SerializedName("Account_id")
    val accountId: String,
    @SerializedName("Account_name")
    val accountName: String,
    @SerializedName("Cane_Receipt_Data")
    val caneReceiptData: ArrayList<CaneReceiptChildData>?,
)

data class CaneReceiptChildData(
    @SerializedName("doc_date")
    val docDate: String,
    @SerializedName("doc_no")
    val docNo: String,
    @SerializedName("Computer_Code")
    val computerCode: String,
    @SerializedName("Account_name")
    val accountName: String,
    @SerializedName("Account_Id")
    val accountId: String,
    @SerializedName("Lam_Ropan")
    val lamRopan: String,
    @SerializedName("Sankal")
    val sankal: String,
    @SerializedName("Cane_Type")
    val caneType: String,
    @SerializedName("Cane_Type_En")
    val caneTypeEn: String,
    @SerializedName("Vehicle_number")
    val vehicleNumber: String,
    @SerializedName("Transport_Sadhan")
    val transportSadhan: String,
    @SerializedName("Driver_Name")
    val driverName: String,
    @SerializedName("Mukadam_code")
    val mukadamCode: String,
    @SerializedName("Farm_In")
    val farmIn: String,
    @SerializedName("Farm_Out")
    val farmOut: String,
    @SerializedName("Cutting_Close_Date")
    val cuttingCloseDate: String,
    @SerializedName("Net_Weight")
    val netWeight: String,
    @SerializedName("Commulative_Weight")
    val commulativeWeight: String,
)

