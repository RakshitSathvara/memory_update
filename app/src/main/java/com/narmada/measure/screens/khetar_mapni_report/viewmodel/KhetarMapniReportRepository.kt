package com.narmada.measure.screens.khetar_mapni_report.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetar_mapni_report.model.KhetarMapniReportRequest
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest

class KhetarMapniReportRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)
    suspend fun villageList(jsonObject: VillageListRequest) = retrofitService.villageList(jsonObject)
    suspend fun khetarMapniReport(jsonObject: KhetarMapniReportRequest) = retrofitService.khetarMapniReport(jsonObject)

}