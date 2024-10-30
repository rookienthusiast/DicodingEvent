package com.example.dicodingevent.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.dicodingevent.data.local.entity.FavoriteEvent
import com.example.dicodingevent.data.local.room.FavoriteEventDao
import com.example.dicodingevent.data.remote.response.DetailResponse
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.data.remote.response.EventResponse
import com.example.dicodingevent.data.remote.response.ListEventsItem
import com.example.dicodingevent.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class EventRepository(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao,
) {

    suspend fun getEvents(active: Int): Result<List<ListEventsItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<EventResponse> = apiService.getEvents(active)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    Result.Success(events)
                } else {
                    Result.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Result.Error("Exception: ${e.message}")
            }
        }
    }

    suspend fun getEventDetail(eventId: Int): Result<DetailResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<DetailResponse> = apiService.getDetailEvent(eventId)
                if (response.isSuccessful) {
                    response.body()?.let { Result.Success(it) } ?: Result.Error("Data event tidak tersedia")
                } else {
                    Result.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("EventRepository", "Error fetching event detail: ${e.message}", e)
                Result.Error("Exception: ${e.message}")
            }
        }
    }

    suspend fun insertFavoriteEvent(eventItem: EventItem) {
        val favoriteEvent = FavoriteEvent(
            id = eventItem.id.toString(),
            name = eventItem.name.toString(),
            mediaCover = eventItem.mediaCover
        )
        favoriteEventDao.insertFavEvent(favoriteEvent)
    }

    suspend fun deleteFavoriteEvent(eventId: String) {
        favoriteEventDao.deleteFavEventById(eventId)
    }

    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavoriteEvents()
    }

    suspend fun isEventFavorite(eventId: String): Boolean {
        return favoriteEventDao.isFavorite(eventId)
    }
}