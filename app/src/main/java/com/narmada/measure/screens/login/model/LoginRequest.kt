package com.narmada.measure.screens.login.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
	@SerializedName("password" )
	val password: String? = null,
	@SerializedName("username" )
	val username: String? = null
)

