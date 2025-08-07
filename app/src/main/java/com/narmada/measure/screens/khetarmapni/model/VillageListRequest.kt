package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class VillageListRequest(

	@field:SerializedName("zone_id")
	val zoneId: String? = null,

	@field:SerializedName("type")
	val type: String? = ""
)
