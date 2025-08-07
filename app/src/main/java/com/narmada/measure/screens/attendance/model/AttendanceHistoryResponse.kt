package com.narmada.measure.screens.attendance.model

import com.google.gson.annotations.SerializedName

data class AttendanceHistoryResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("in_out_time_status")
	val inOutTimeStatus: Int? = null,

	@field:SerializedName("attendanceList")
	val attendanceList: List<AttendanceListItem?>? = null
)

data class AttendanceListItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("out_time")
	val outTime: String? = null,

	@field:SerializedName("in_time")
	val inTime: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
