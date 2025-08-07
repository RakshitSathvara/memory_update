package com.narmada.measure.screens.pani_bandh_register.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.pani_bandh_register.model.FarmMeasurementReportRequest
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterRequest

class PaniBandhRegisterRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)
    suspend fun villageList(jsonObject: VillageListRequest) = retrofitService.villageList(jsonObject)
    suspend fun paniBandhRegisterReport(jsonObject: PaniBandhRegisterRequest) = retrofitService.paniBandhRegisterReport(jsonObject)
    suspend fun getCommonData() = retrofitService.getCommonData()

    suspend fun getPaniBandhRegisterReportList(jsonObject: HashMap<String, String>) = retrofitService.getPaniBandhRegisterReportList(jsonObject)
    suspend fun generateFarmMeasurementReport(jsonObject: FarmMeasurementReportRequest) = retrofitService.generateFarmMeasurementReport(jsonObject)
}