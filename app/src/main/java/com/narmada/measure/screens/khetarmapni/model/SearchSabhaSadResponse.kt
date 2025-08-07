package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class SearchSabhaSadResponse(

    @field:SerializedName("data")
    val data: List<SabhaSadItem?>? = null,

    @field:SerializedName("response")
    val response: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class SabhaSadItem(

    @field:SerializedName("farmer_id")
    val farmerId: String? = null,

    @field:SerializedName("farmer_name")
    val farmerName: String? = null,

    @field:SerializedName("farmer_english_name")
    val farmerEnglishName: String? = null


) {
    override fun toString(): String {
        return "$farmerEnglishName-$farmerId"
    }
}
