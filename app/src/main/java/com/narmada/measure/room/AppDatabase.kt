package com.narmada.measure.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.narmada.measure.room.dao.OfflineMapniDao
import com.narmada.measure.room.entity.OfflineMapni

@Database(
    entities = [OfflineMapni::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
//        AutoMigration(from = 2, to = 3),
//        AutoMigration(from = 3, to = 4)
    ],
    version = 2, exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun OfflineMapniDio(): OfflineMapniDao

    companion object DatabaseBuilder {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "narmada-offline-mapni"
            )
                .build()
    }
}