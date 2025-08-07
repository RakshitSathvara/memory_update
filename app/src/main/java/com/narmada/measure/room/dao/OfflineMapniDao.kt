package com.narmada.measure.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.narmada.measure.room.entity.OfflineMapni

@Dao
interface OfflineMapniDao {

    @Query("SELECT * FROM offlinemapni")
    suspend fun getAllMapni(): List<OfflineMapni>

    @Insert
    suspend fun insert(mapni: OfflineMapni)

    @Delete
    suspend fun delete(mapni: OfflineMapni)
}