package com.narmada.measure.screens.badeli_serdi_report.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.badeli_serdi_report.model.BadeliSerdiReportRequest

class BadeliSerdiReportRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)
    suspend fun badeliSerdiReport(jsonObject: BadeliSerdiReportRequest) = retrofitService.badeliSerdiReport(jsonObject)

}