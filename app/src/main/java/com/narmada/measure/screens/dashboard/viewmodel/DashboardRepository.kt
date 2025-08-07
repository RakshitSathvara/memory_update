package com.narmada.measure.screens.dashboard.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.room.AppDatabase
import com.narmada.measure.room.entity.OfflineMapni

class DashboardRepository constructor(
    private val retrofitService: RetrofitService
) {
    suspend fun yearList(appVersion: String) = retrofitService.yearList(appVersion)
    suspend fun logout() = retrofitService.logout()

}