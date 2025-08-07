package com.narmada.measure.screens.pani_bandh_register.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SupervisorZoneListResponse(

	@field:SerializedName("data")
	val data: List<SupervisorZoneItem?>? = null,

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class SupervisorZoneItem(

	@field:SerializedName("zone_english_name")
	val englishZoneName: String? = null,

	@field:SerializedName("zone_name")
	val zoneName: String? = null,

	@field:SerializedName("zone_id")
	val zoneId: String? = null

) : Serializable {
	override fun toString(): String {
		return englishZoneName.toString()
	}
}
