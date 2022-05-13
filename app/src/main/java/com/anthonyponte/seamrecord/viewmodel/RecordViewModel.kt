package com.anthonyponte.seamrecord.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {
    private val _record = MutableLiveData<Record>()
    val record: LiveData<Record> get() = _record

    fun postRecord(record: Record) {
        _record.postValue(record)
    }
}