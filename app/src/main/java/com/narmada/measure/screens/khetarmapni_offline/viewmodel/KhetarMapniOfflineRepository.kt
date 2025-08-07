package com.narmada.measure.screens.khetarmapni_offline.viewmodel

import com.narmada.measure.room.AppDatabase
import com.narmada.measure.room.entity.OfflineMapni

class KhetarMapniOfflineRepository constructor(
    private val gfgDatabase: AppDatabase
) {

    suspend fun getAllMapni(): List<OfflineMapni> {
        return gfgDatabase.OfflineMapniDio().getAllMapni()
    }

    suspend fun insert(mapni: OfflineMapni) {
        return gfgDatabase.OfflineMapniDio().insert(mapni)
    }

    suspend fun delete(mapni: OfflineMapni) {
        return gfgDatabase.OfflineMapniDio().delete(mapni)
    }
}