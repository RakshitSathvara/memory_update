package com.narmada.measure.screens.login.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Serializable

data class Data(

	@field:SerializedName("zone_id")
	val zoneId: String? = null,

	@field:SerializedName("zone_supervisor_id")
	val zoneSupervisorId: String? = null,

	@field:SerializedName("user_type")
	val userType: Int? = null,

	@field:SerializedName("zone_name")
	val zoneName: String? = null,

	@field:SerializedName("officer_name")
	val officerName: String? = null,

	@field:SerializedName("access_token")
	val token: String? = null,

	@field:SerializedName("working_year")
	val workingYear: String? = null,

) : Serializable
