package com.narmada.measure.screens.bareliserdi.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CommonDataResponse (

    @SerializedName("response" ) val response : String? = null,
    @SerializedName("message"  ) val message  : String? = null,
    @SerializedName("data"     ) val data     : CommonData?   = CommonData()

) : Serializable

data class CommonData (

    @SerializedName("sankad"        ) val sankad       : ArrayList<Sankad>     = arrayListOf(),
    @SerializedName("mapni_types"   ) val mapniTypes   : ArrayList<MapniTypes>   = arrayListOf(),
    @SerializedName("piyat_sadhans" ) val piyatSadhans : ArrayList<PiyatSadhans> = arrayListOf(),
    @SerializedName("items"         ) val items        : ArrayList<Items>        = arrayListOf(),
    @SerializedName("kapats"        ) val kapats       : ArrayList<Kapats>       = arrayListOf(),
    @SerializedName("biyarans"      ) val biyarans     : ArrayList<Biyarans>     = arrayListOf()

) : Serializable

data class Sankad (

    @SerializedName("id"   ) val id   : Int?    = null,
    @SerializedName("name" ) val name : String? = null

) : Serializable

data class MapniTypes (

    @SerializedName("id"   ) val id   : Int?    = null,
    @SerializedName("name" ) val name : String? = null

) : Serializable {
    override fun toString(): String {
        return "MapniTypes(id=$id)"
    }
}

data class PiyatSadhans (

    @SerializedName("id"   ) val id   : Int?    = null,
    @SerializedName("name" ) val name : String? = null

) : Serializable

data class Items (

    @SerializedName("Item_ID"   ) val ItemID   : String? = null,
    @SerializedName("Item_Name" ) val ItemName : String? = null

) : Serializable

data class Kapats (

    @SerializedName("Kapat_ID"   ) val KapatID   : String? = null,
    @SerializedName("Kapat_Name" ) val KapatName : String? = null

) : Serializable

data class Biyarans (

    @SerializedName("Biyaran_Id"   ) val BiyaranId   : String? = null,
    @SerializedName("Biyaran_Name" ) val BiyaranName : String? = null

) : Serializable
