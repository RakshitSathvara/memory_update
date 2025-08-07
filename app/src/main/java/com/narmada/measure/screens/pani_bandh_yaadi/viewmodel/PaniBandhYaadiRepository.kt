package com.narmada.measure.screens.pani_bandh_yaadi.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.pani_bandh_register.model.FarmMeasurementReportRequest
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiRequest

class PaniBandhYaadiRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)
    suspend fun villageList(jsonObject: VillageListRequest) = retrofitService.villageList(jsonObject)
    suspend fun generatePaniBandhYaadiReport(jsonObject: PaniBandhYaadiRequest) = retrofitService.generatePaniBandhYaadiReport(jsonObject)

    suspend fun getRopanYearWiseDates(workingYear: String) = retrofitService.getRopanYearWiseDates(workingYear)
    suspend fun getPaniBandhYaadiReportList(jsonObject: HashMap<String, String>) = retrofitService.getPaniBandhYaadiReportList(jsonObject)
    suspend fun generateFarmMeasurementReport(jsonObject: FarmMeasurementReportRequest) = retrofitService.generateFarmMeasurementReport(jsonObject)

}