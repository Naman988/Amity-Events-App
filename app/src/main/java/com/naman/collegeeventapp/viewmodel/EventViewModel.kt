package com.naman.collegeeventapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naman.collegeeventapp.data.FirestoreRepository
import com.naman.collegeeventapp.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val repo = FirestoreRepository()
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _events.value = repo.getEvents()
        }
    }
    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            event.id?.let { eventId ->
                repo.deleteEvent(eventId)
                // Refresh the events list after deletion
                _events.value = repo.getEvents()
            }
        }
    }

}
