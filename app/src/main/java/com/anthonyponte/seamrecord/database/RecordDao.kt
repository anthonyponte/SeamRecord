package com.anthonyponte.seamrecord.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.anthonyponte.seamrecord.viewmodel.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM records ORDER BY DATETIME(fecha_creacion) DESC")
    fun getAll(): Flow<List<Record>>

    @Insert
    fun insert(record: Record)

    @Delete
    fun delete(record: Record)

    @Query("DELETE FROM records")
    fun deleteAll()
}