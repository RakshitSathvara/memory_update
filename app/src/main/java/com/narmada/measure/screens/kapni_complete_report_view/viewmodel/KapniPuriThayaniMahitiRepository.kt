package com.narmada.measure.screens.kapni_complete_report_view.viewmodel

import com.narmada.measure.network.RetrofitService

class KapniPuriThayaniMahitiRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)
    suspend fun getKhetarCodeList(jsonObject: HashMap<String, String>) = retrofitService.khetarCodeList(jsonObject)
    suspend fun kapniCompleteReportList(jsonObject: HashMap<String, String>) = retrofitService.kapniCompleteReportList(jsonObject)
    suspend fun kapniCompleteReportDownload(jsonObject: HashMap<String, String>) = retrofitService.kapniCompleteReportDownload(jsonObject)
}