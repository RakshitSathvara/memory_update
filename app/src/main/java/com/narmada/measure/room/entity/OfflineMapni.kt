package com.narmada.measure.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "offlinemapni")
data class OfflineMapni(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "khetarCode") val khetarCode: String?,
    @ColumnInfo(name = "sabhaCode") val sabhaCode: String?,
    @ColumnInfo(name = "polygon") val polygon: String?,
    @ColumnInfo(name = "polygonImage") val polygonImage: String?,
    @ColumnInfo(name = "khetarImage") val khetarImage: String?,
    @ColumnInfo(name = "totalAcre") val totalAcre: String?,
    @ColumnInfo(name = "northKhetarName") val northKhetarName: String?,
    @ColumnInfo(name = "southKhetarName") val southKhetarName: String?,
    @ColumnInfo(name = "eastKhetarName") val eastKhetarName: String?,
    @ColumnInfo(name = "westKhetarName") val westKhetarName: String?,
    @ColumnInfo(name = "chasNumber") val chasNumber: String?,
    @ColumnInfo(name = "chasDirection") val chasDirection: String?,

) : Serializable