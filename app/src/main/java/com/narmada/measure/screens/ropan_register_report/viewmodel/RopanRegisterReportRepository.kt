package com.narmada.measure.screens.ropan_register_report.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.ropan_register_report.model.RopanRegisterReportRequest

class RopanRegisterReportRepository constructor(private val retrofitService: RetrofitService) {
    suspend fun getCommonData() = retrofitService.getCommonData()
    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)
    suspend fun villageList(jsonObject: VillageListRequest) = retrofitService.villageList(jsonObject)
    suspend fun ropanRegisterReport(jsonObject: RopanRegisterReportRequest) = retrofitService.ropanRegisterReport(jsonObject)

}