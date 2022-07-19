package com.anthonyponte.seamrecord.database

import androidx.annotation.WorkerThread
import com.anthonyponte.seamrecord.viewmodel.Record
import kotlinx.coroutines.flow.Flow

class RecordRepository(private val recordDao: RecordDao) {
    val getAll: Flow<List<Record>> = recordDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(record: Record) {
        recordDao.insert(record)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(ids: List<Long>) {
        recordDao.delete(ids)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        recordDao.deleteAll()
    }
}