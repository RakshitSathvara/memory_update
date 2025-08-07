package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class ZoneOfficerListResponse(

	@field:SerializedName("data")
	val data: List<ZoneOfficerItem?>? = null,

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ZoneOfficerItem(

	@field:SerializedName("zone_supervisor_id")
	val zoneOfficerId: String? = null,

	@field:SerializedName("zone_supervisor_name")
	val zoneOfficerName: String? = null,

	@field:SerializedName("zone_id")
	val zoneId: String? = null


) {
	override fun toString(): String {
		return zoneOfficerName.toString()
	}
}
