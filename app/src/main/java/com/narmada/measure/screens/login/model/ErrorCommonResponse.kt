package com.narmada.measure.screens.login.model

import com.google.gson.annotations.SerializedName

data class ErrorCommonResponse(

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
