package com.narmada.measure.screens.kapni_complete_report.viewmodel

import com.narmada.measure.network.RetrofitService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class KapniCompleteReportRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)

    suspend fun getKhetarCodeList(jsonObject: HashMap<String, String>) = retrofitService.khetarCodeList(jsonObject)
    suspend fun getWeightList(jsonObject: HashMap<String, String>) = retrofitService.supervisorWeightList(jsonObject)

    suspend fun addKapniCompleteReport(params: HashMap<String, RequestBody>, faceImage: MultipartBody.Part) = retrofitService.addKapniCompleteReport(params, faceImage)

}