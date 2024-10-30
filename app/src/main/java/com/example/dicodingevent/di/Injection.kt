package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.local.room.EventDatabase
import com.example.dicodingevent.data.local.room.FavoriteEventDao
import com.example.dicodingevent.data.remote.EventRepository
import com.example.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getDatabase(context)
        val favoriteEventDao = database.favoriteEventDao()

        return EventRepository(apiService, favoriteEventDao)
    }

    fun provideFavoriteEventDao(context: Context): FavoriteEventDao {
        val database = EventDatabase.getDatabase(context)
        return database.favoriteEventDao()
    }
}