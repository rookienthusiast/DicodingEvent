package com.example.dicodingevent.data.remote.retrofit

import com.example.dicodingevent.data.remote.response.DetailResponse
import com.example.dicodingevent.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): Response <EventResponse>

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: Int
    ): Response<DetailResponse>

    @GET("events")
    fun getNearbyEvent (
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int = 1
    ): Call<EventResponse>
}