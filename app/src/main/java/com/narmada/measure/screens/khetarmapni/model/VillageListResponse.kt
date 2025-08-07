package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VillageListResponse(

	@field:SerializedName("data")
	val data: List<VillageItem?>? = null,

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class VillageItem(

	@field:SerializedName("Village_Id")
	val villageId: String? = null,

	@field:SerializedName("Zone_id")
	val zoneId: String? = null,

	@field:SerializedName("Village_Name")
	val villageName: String? = null
) : Serializable
