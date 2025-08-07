package com.narmada.measure.screens.pani_bandh_yaadi.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class PaniBandhYaadiListResponse(

    @SerializedName("response")
    var response: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("data")
    var data: PaniBandhYaadiList? = null

)

data class PaniBandhYaadiList(

    @SerializedName("pagination")
    var pagination: Pagination?,

    @SerializedName("Data")
    var villageData: VillageData?,

    @SerializedName("PaniBandhDetails")
    var paniBandhData: ArrayList<PaniBandhData>?

)

data class VillageData(

    @SerializedName("pilan_season")
    var pilanSeason: String? = null,
    @SerializedName("ropan_season")
    var ropanSeason: String? = null

)

data class PaniBandhData(

    @SerializedName("Computer_Code")
    var computerCode: String? = null,

    @SerializedName("Approx_Ropan_Date")
    var approxRopanDate: String? = null,

    @SerializedName("Pani_Bandh_Date")
    var paniBandhDate: String? = null,

    @SerializedName("Lam_Ropan")
    var lamRopan: String? = null,

    @SerializedName("Ropan_Area")
    var ropanArea: String? = null,

    @SerializedName("Account_id")
    var accountId: String? = null,

    @SerializedName("Item_Name")
    var itemName: String? = null,

    @SerializedName("Account_Name")
    var accountName: String? = null,

    @SerializedName("Village_Id")
    var villageId: String? = null,

    @SerializedName("Village_Name")
    var villageName: String? = null,

    @SerializedName("Phone_No")
    var phoneNumber: String? = null

)

data class Pagination(

    @SerializedName("last_page")
    var lastPage: Int? = null

)