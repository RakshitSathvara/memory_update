package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model

import com.google.gson.annotations.SerializedName


data class KapniPaniBandhDetailResponse(

    @SerializedName("response")
    var response: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("data")
    var data: KapniPaniBandhDetail? = null

)

data class KapniPaniBandhDetail(

    @SerializedName("Moje_Village_Name")
    var mojeVillageName: String? = null,

    @SerializedName("Moje_Village_English_Name")
    var mojeVillageEnglishName: String? = null,

    @SerializedName("Moje_Village_Id")
    var mojeVillageId: String? = null,

    @SerializedName("Lam_Ropan")
    var lamRopan: String? = null,

    @SerializedName("Item_Id")
    var itemId: String? = null,

    @SerializedName("Item_Name")
    var itemName: String? = null,

    @SerializedName("Account_id")
    var accountId: String? = null,

    @SerializedName("Account_name")
    var accountName: String? = null,

    @SerializedName("Account_English_Name")
    var accountEnglishName: String? = null,

    @SerializedName("Village_id")
    var villageId: String? = null,

    @SerializedName("Village_Name")
    var villageName: String? = null,

    @SerializedName("Village_English_Name")
    var villageEnglishName: String? = null

)