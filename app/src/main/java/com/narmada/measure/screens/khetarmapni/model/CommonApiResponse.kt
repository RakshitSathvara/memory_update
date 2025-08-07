package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName

data class CommonApiResponse(

    @field:SerializedName("data")
	val data: Data? = null,

    @field:SerializedName("response")
	val response: String? = null,

    @field:SerializedName("message")
	val message: String? = null
)

data class KapatsItem(

	@field:SerializedName("Kapat_Name")
	val kapatName: String? = null,

	@field:SerializedName("Kapat_ID")
	val kapatID: String? = null
)

data class PiyatSadhansItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ItemsItem(

	@field:SerializedName("NAME")
	val itemName: String? = null,

	@field:SerializedName("CODE")
	val itemID: String? = null
)

data class BiyaransItem(

	@field:SerializedName("Biyaran_Id")
	val biyaranId: String? = null,

	@field:SerializedName("Biyaran_Name")
	val biyaranName: String? = null
)

data class Data(

	@field:SerializedName("kapats")
	val kapats: List<KapatsItem?>? = null,

	@field:SerializedName("mapni_types")
	val mapniTypes: List<MapniTypesItem?>? = null,

	@field:SerializedName("piyat_sadhans")
	val piyatSadhans: List<PiyatSadhansItem?>? = null,

	@field:SerializedName("biyarans")
	val biyarans: List<BiyaransItem?>? = null,

	@field:SerializedName("items")
	val items: List<ItemsItem?>? = null,

	@field:SerializedName("sankad")
	val sankad: List<Sankad?>? = null

)

data class MapniTypesItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Sankad(
	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
