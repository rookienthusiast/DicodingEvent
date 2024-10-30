package com.example.dicodingevent.main.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.remote.EventRepository
import com.example.dicodingevent.data.remote.Result
import com.example.dicodingevent.data.remote.response.DetailResponse
import com.example.dicodingevent.data.remote.response.EventItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: EventRepository) : ViewModel() {

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _eventDetail = MutableLiveData<DetailResponse>()
    val eventDetail: LiveData<DetailResponse> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun insertFavoriteEvent(eventItem: EventItem) {
        viewModelScope.launch {
            repository.insertFavoriteEvent(eventItem)
            _isFavorite.value = true
        }
    }

    fun deleteFavoriteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteFavoriteEvent(eventId)
            _isFavorite.value = false
        }
    }

    fun loadEventsDetail(eventId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getEventDetail(eventId.toInt())) {
                is Result.Success -> {
                    _eventDetail.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.error
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun checkIfFavorite(eventId: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.isEventFavorite(eventId)
        }
    }
}