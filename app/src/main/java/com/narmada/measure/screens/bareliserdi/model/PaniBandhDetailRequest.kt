package com.narmada.measure.screens.bareliserdi.model

import com.google.gson.annotations.SerializedName

data class PaniBandhDetailRequest(
	@SerializedName("working_year" )
	val working_year: String,
	@SerializedName("computer_code" )
	val computer_code: String
)

