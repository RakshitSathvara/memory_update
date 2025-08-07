package com.narmada.measure.screens.khetarmapni.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KhetarMapniRequest(

	@field:SerializedName("zone_officer_id")
	var zoneOfficerId: String? = null,

	@field:SerializedName("zone_officer_name")
	var zoneOfficerName: String? = null,

	@field:SerializedName("account_id")
	var accountId: String? = null,

	@field:SerializedName("computer_code")
	var computerCode: String? = null,

	@field:SerializedName("moje_village_id")
	var mojeVillageId: String? = null,

	@field:SerializedName("moje_village_name")
	var mojeVillageName: String? = null,

	@field:SerializedName("nondh_village_id")
	var nondhVillageId: String? = null,

	@field:SerializedName("nondh_village_name")
	var nondhVillageName: String? = null,

	@field:SerializedName("sabha_code")
	var sabhaCode: String? = null,

	@field:SerializedName("sabha_name")
	var sabhaName: String? = null,

	@field:SerializedName("gaam_code")
	var gaamCode: String? = null,

	@field:SerializedName("sabha_gaam")
	var sabhaGaam: String? = null,

	@field:SerializedName("sher_total")
	var sherTotal: String? = null,

	@field:SerializedName("approx_ropan_date")
    var approxRopanDate: String? = null,

	@field:SerializedName("mapni_type")
	var mapniType: String? = null,

	@field:SerializedName("mapni_name")
	var mapniName: String? = null,

	@field:SerializedName("item_id")
	var itemId: String? = null,

	@field:SerializedName("item_name")
	var itemName: String? = null,

	@field:SerializedName("piyat_sadhan")
	var piyatSadhan: String? = null,

	@field:SerializedName("piyat_sadhan_name")
	var piyatSadhanName: String? = null,

	@field:SerializedName("serial_number")
	var serialNumber: String? = null,

	@field:SerializedName("biyaran_id")
	var biyaranId: String? = null,

	@field:SerializedName("biyaran_name")
	var biyaranName: String? = null,

	@field:SerializedName("khetar_name")
	var khetarName: String? = null,

	@field:SerializedName("working_year")
	var workingYear: String? = null,

	@field:SerializedName("pilan_season")
	var pilanSeason: String? = null,

	@field:SerializedName("ropan_area")
	var ropanArea: String? = null,

	@field:SerializedName("polygon_json")
	var polygonJson: String? = null,

	@field:SerializedName("not_organic")
	var notOrganic: String? = null,

	@field:SerializedName("not_organic_name")
	var notOrganicName: String? = null,

	@field:SerializedName("farm_owner_name_for_north_direction")
	var northKhetarName: String? = null,

	@field:SerializedName("farm_owner_name_for_south_direction")
	var southKhetarName: String? = null,

	@field:SerializedName("farm_owner_name_for_west_direction")
	var westKhetarName: String? = null,

	@field:SerializedName("farm_owner_name_for_east_direction")
	var eastKhetarName: String? = null,

	@field:SerializedName("trench_count")
	var chasNumber: String? = null,

	@field:SerializedName("trench_direction")
	var chasDirection: String? = null,

	@field:SerializedName("trench_direction_name")
	var chasDirectionName: String? = null,

	) : Serializable
