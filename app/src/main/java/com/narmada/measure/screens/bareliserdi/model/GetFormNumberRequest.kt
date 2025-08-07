package com.narmada.measure.screens.bareliserdi.model

import com.google.gson.annotations.SerializedName

data class GetFormNumberRequest(
	@SerializedName("working_year" )
	val working_year: String
)

