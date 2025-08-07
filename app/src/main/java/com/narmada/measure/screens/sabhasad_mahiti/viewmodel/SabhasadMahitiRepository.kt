package com.narmada.measure.screens.sabhasad_mahiti.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest

class SabhasadMahitiRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun supervisorZoneList(supervisorId: String) = retrofitService.supervisorZoneList(supervisorId)
    suspend fun villageList(jsonObject: VillageListRequest) = retrofitService.villageList(jsonObject)
    suspend fun sabhasadMahitiReport(villageId: String) = retrofitService.sabhasadMahitiReport(villageId)

}