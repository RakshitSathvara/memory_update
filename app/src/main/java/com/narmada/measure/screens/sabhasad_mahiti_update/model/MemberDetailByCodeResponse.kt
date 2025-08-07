package com.narmada.measure.screens.sabhasad_mahiti_update.model

import com.google.gson.annotations.SerializedName

data class MemberDetailByCodeResponse(

	@field:SerializedName("data")
	val data: MemberDetailByCode? = null,

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class MemberDetailByCode(

	@field:SerializedName("farmer_id")
	val farmerId: String? = null,

	@field:SerializedName("farmer_name")
	val farmerName: String? = null,

	@field:SerializedName("farmer_english_name")
	val farmerEnglishName: String? = null,

	@field:SerializedName("mobile_number")
	val mobileNumber: String? = null,

	@field:SerializedName("members")
	val members: List<LinkedMember>? = null

)

data class LinkedMember(

	@field:SerializedName("farmer_id")
	val farmerId: String? = null,

	@field:SerializedName("farmer_name")
	val farmerName: String? = null,

	@field:SerializedName("farmer_english_name")
	val farmerEnglishName: String? = null,

	var isSelected: Boolean = false

)
