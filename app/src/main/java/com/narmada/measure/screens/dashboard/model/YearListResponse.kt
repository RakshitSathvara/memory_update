package com.narmada.measure.screens.dashboard.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class YearListResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("response")
    val response: String? = null,



    @field:SerializedName("message")
    val message: String? = null
) : Serializable

data class Data(

    @field:SerializedName("isForceUpgrade")
    val isForceUpgrade: Boolean? = null,

    @field:SerializedName("years")
    val years: List<YearsItem?>? = null,

    @field:SerializedName("current_year")
    val currentYear: String? = null
): Serializable

data class YearsItem(

    @field:SerializedName("year_id")
    val yearId: String? = null,

    @field:SerializedName("is_locked")
    val isLocked: Boolean? = null

): Serializable {
    override fun toString(): String {
        return yearId.toString()
    }
}


