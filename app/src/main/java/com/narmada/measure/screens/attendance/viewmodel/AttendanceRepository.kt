package com.narmada.measure.screens.attendance.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AttendanceRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun attendanceHistory(fromDate: String, toDate: String) =
        retrofitService.getAttendanceHistory(fromDate, toDate)

    suspend fun addAttendance(
        params: HashMap<String, RequestBody>,
        profileImage: MultipartBody.Part,
    ) = retrofitService.addAttendance(params, profileImage)

    suspend fun villageList(jsonObject: VillageListRequest) =
        retrofitService.villageList(jsonObject)
}
