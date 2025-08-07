package com.narmada.measure.screens.khetarmapni.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.room.AppDatabase
import com.narmada.measure.room.entity.OfflineMapni
import com.narmada.measure.screens.khetarmapni.model.AccountMemberRequest
import com.narmada.measure.screens.khetarmapni.model.CheckComputerCodeRequest
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

class KhetarMapniRepository constructor(private val retrofitService: RetrofitService,private val gfgDatabase: AppDatabase) {
    suspend fun zoneOfficerList() = retrofitService.zoneOfficerList()
    suspend fun commonApi(year: String) = retrofitService.commonApi(year)
    suspend fun checkComputerCode(jsonObject: CheckComputerCodeRequest) =
        retrofitService.checkComputerCode(jsonObject)

    suspend fun villageList(jsonObject: VillageListRequest) =
        retrofitService.villageList(jsonObject)

    suspend fun accountMemberDetail(jsonObject: AccountMemberRequest) =
        retrofitService.accountMemberDetail(jsonObject)

    suspend fun addKhetarMapni(
        params: HashMap<String, RequestBody>,
        mapImage: MultipartBody.Part,
        khetarImage: MultipartBody.Part
    ) = retrofitService.addKhetarMapni(params, mapImage, khetarImage)

    suspend fun delete(mapni: OfflineMapni) {
        return gfgDatabase.OfflineMapniDio().delete(mapni)
    }

    suspend fun searchSabhaSad(text : String) = retrofitService.searchSabhaSad(text)
}