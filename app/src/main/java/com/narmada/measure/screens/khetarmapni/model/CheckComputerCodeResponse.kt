package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class CheckComputerCodeResponse(

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("is_computer_code_exist")
	val isComputerCodeExist: Int? = null
)
