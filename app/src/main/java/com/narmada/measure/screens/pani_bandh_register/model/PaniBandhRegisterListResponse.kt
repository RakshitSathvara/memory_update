package com.narmada.measure.screens.pani_bandh_register.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class PaniBandhRegisterListResponse(

    @SerializedName("response")
    var response: String?,

    @SerializedName("message")
    var message: String?,

    @SerializedName("data")
    var data: PaniBandhRegisterList?

)

data class PaniBandhRegisterList(

    @SerializedName("pagination")
    var pagination: Pagination?,

    @SerializedName("village_data")
    var villageData: VillageData?,

    @SerializedName("pani_bandh_data")
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

    @SerializedName("Phone_No")
    var phoneNo: String? = null

)

data class Pagination(

    @SerializedName("last_page")
    var lastPage: Int? = null

)