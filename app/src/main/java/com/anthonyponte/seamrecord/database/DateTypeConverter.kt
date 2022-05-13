package com.anthonyponte.seamrecord.database

import androidx.room.TypeConverter
import java.time.Instant

class DateTypeConverter {
    @TypeConverter
    fun toDate(value: String): Instant {
        return Instant.parse(value)
    }

    @TypeConverter
    fun fromDate(value: Instant): String {
        return value.toString()
    }
}