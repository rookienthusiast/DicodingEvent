package com.example.dicodingevent.main.ui.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.dicodingevent.data.remote.EventRepository
import com.example.dicodingevent.data.remote.response.ListEventsItem

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    val favoriteEvents: LiveData<List<ListEventsItem>> = repository.getFavoriteEvents().map { favoriteEvents ->
        Log.d("FavoriteViewModel", "Converting FavoriteEvent: $favoriteEvents")
        favoriteEvents.map { favoriteEvent ->
            ListEventsItem(
                id = favoriteEvent.id,
                name = favoriteEvent.name,
                mediaCover = favoriteEvent.mediaCover
            )
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        loadFavoriteEvents()
    }

    private fun loadFavoriteEvents() {
        _isLoading.value = true
        _isLoading.value = false
    }
}