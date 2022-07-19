package com.anthonyponte.seamrecord.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.anthonyponte.seamrecord.viewmodel.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM records ORDER BY DATETIME(fecha_creacion) DESC")
    fun getAll(): Flow<List<Record>>

    @Insert
    suspend fun insert(record: Record)

    @Query("DELETE FROM records WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)

    @Query("DELETE FROM records")
    suspend fun deleteAll()
}