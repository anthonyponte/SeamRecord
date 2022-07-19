package com.anthonyponte.seamrecord.viewmodel

import androidx.lifecycle.*
import com.anthonyponte.seamrecord.database.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.Instant

class RoomViewModel(private val repository: RecordRepository) : ViewModel() {
    val getAll: LiveData<List<Record>> = repository.getAll.asLiveData()

    fun insert(record: Record) = viewModelScope.launch {
        repository.insert(record)
    }

    fun delete(ids: List<Long>) = viewModelScope.launch {
        repository.delete(ids)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}