package com.narmada.measure.screens.bareliserdi.model

import com.google.gson.annotations.SerializedName

data class AddBareliSerdiResponse(

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
