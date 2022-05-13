package com.anthonyponte.seamrecord.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anthonyponte.seamrecord.viewmodel.Record

@Database(entities = arrayOf(Record::class), version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class SeamRecordDatabase : RoomDatabase() {
    abstract fun inspectionDao(): RecordDao

    companion object {
        private var INSTANCE: SeamRecordDatabase? = null
        fun getDatabase(context: Context): SeamRecordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SeamRecordDatabase::class.java,
                    "record_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}