package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class SubmitKhetarMapniResponse(

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
