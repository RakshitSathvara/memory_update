package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class AccountMemberResponse(

	@field:SerializedName("data")
	val accountData: AccountData? = null,

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class AccountData(

	@field:SerializedName("village_id")
	val villageId: String? = null,

	@field:SerializedName("farmer_id")
	val accountId: String? = null,

	@field:SerializedName("farmer_name")
	val accountName: String? = null,

	@field:SerializedName("balance_share")
	val balanceShare: String? = null,

	@field:SerializedName("village_name")
	val villageName: String? = null
)
