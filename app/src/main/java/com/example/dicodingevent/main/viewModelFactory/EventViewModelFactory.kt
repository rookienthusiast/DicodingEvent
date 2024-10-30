package com.example.dicodingevent.main.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.data.remote.EventRepository
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.main.ui.detail.DetailViewModel
import com.example.dicodingevent.main.ui.favorite.FavoriteViewModel
import com.example.dicodingevent.main.ui.finished.FinishedViewModel
import com.example.dicodingevent.main.ui.home.HomeViewModel
import com.example.dicodingevent.main.ui.upcoming.UpcomingViewModel

@Suppress("UNCHECKED_CAST")
class EventViewModelFactory(
    private val repository: EventRepository
) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(repository) as T
                }
                modelClass.isAssignableFrom(UpcomingViewModel::class.java) -> {
                    UpcomingViewModel(repository) as T
                }
                modelClass.isAssignableFrom(FinishedViewModel::class.java) -> {
                    FinishedViewModel(repository) as T
                }
                modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                    DetailViewModel(repository) as T
                }
                modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                    FavoriteViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel Class")
            }
        }

        companion object {
            @Volatile
            private var instance: EventViewModelFactory? = null
            fun getInstance(context: Context): EventViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: EventViewModelFactory(Injection.provideRepository(context))
                }.also { instance = it }
        }
    }