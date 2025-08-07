package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class CheckComputerCodeRequest(

	@field:SerializedName("working_year")
	val workingYear: String? = null,

	@field:SerializedName("computer_code")
	val computerCode: String? = null
)
