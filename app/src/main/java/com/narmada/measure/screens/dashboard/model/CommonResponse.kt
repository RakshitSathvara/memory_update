package com.narmada.measure.screens.dashboard.model

import com.google.gson.annotations.SerializedName

data class CommonResponse(

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
