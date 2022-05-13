package com.anthonyponte.seamrecord

import android.app.Application
import com.anthonyponte.seamrecord.database.SeamRecordDatabase
import com.anthonyponte.seamrecord.database.RecordRepository

class SeamRecordApp : Application() {
    private val database by lazy { SeamRecordDatabase.getDatabase(this) }
    val repository by lazy { RecordRepository(database.inspectionDao()) }
}