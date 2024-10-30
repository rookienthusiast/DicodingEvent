package com.example.dicodingevent.main.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.remote.EventRepository
import com.example.dicodingevent.data.remote.Result
import com.example.dicodingevent.data.remote.response.ListEventsItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents : LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents : LiveData<List<ListEventsItem>> = _finishedEvents

    private val _upcomingEventsLoading = MutableLiveData<Boolean>()
    val upcomingEventsLoading: LiveData<Boolean> = _upcomingEventsLoading

    private val _finishedEventsLoading = MutableLiveData<Boolean>()
    val finishedEventsLoading: LiveData<Boolean> = _finishedEventsLoading


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        loadEventsUpcoming()
        loadEventsFinished()
    }

    private fun loadEventsUpcoming() {
        _upcomingEventsLoading.value  = true
        viewModelScope.launch {
            when (val result = repository.getEvents(1)) {
                is Result.Success -> {
                    _upcomingEvents.value = result.data
                    _upcomingEventsLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.error
                    _upcomingEventsLoading.value  = false
                }
                is Result.Loading -> {
                    _upcomingEventsLoading.value  = true
                }
            }
        }
    }

    private fun loadEventsFinished() {
        _finishedEventsLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getEvents(0)) {
                is Result.Success -> {
                    _finishedEvents.value = result.data
                    _finishedEventsLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.error
                    _finishedEventsLoading.value = false
                }
                is Result.Loading -> {
                    _finishedEventsLoading.value = true
                }
            }
        }
    }
}