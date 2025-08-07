package com.narmada.measure.screens.bareliserdi.model

/**
 * Created by Dipti Agravat on 14,June,2023
 */
import com.google.gson.annotations.SerializedName


data class PaniBandhDetailResponse(

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: PaniBandhDetail? = null

)

data class PaniBandhDetail(

    @SerializedName("Account_id")
    val AccountId: String? = null,

    @SerializedName("Moje_Village_Id")
    val MojeVillageId: String? = null,

    @SerializedName("Ropan_Area")
    val RopanArea: String? = null,

    @SerializedName("Approx_Ropan_Date")
    val ApproxRopanDate: String? = null,

    @SerializedName("Lam_Ropan")
    val LamRopan: String? = null,

    @SerializedName("Item_Id")
    val ItemId: String? = null,

    @SerializedName("Item_Name")
    val ItemName: String? = null,

    @SerializedName("Account_name")
    val AccountName: String? = null,

    @SerializedName("Account_English_Name")
    val AccountEnglishName: String? = null,

    @SerializedName("Village_Id")
    val VillageId: String? = null,

    @SerializedName("Village_Name")
    val VillageName: String? = null,

    @SerializedName("Village_English_Name")
    val VillageEnglishName: String? = null


)
