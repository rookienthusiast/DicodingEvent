package com.example.dicodingevent.main.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dicodingevent.R
import com.example.dicodingevent.data.remote.response.EventResponse
import com.example.dicodingevent.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"
    }

    private var resultStatus: Result? = null

    override fun doWork(): Result {
        return try {
            getNearbyEvent() // Pastikan tidak null
        } catch (e: Exception) {
            Log.e(TAG, "Error in doWork: ${e.message}")
            Result.failure()
        }
    }

    private fun getNearbyEvent(): Result {
        Log.d(TAG, "getNearbyEvent: Mulai.....")
        Looper.prepare()
        val client = ApiConfig.getApiService().getNearbyEvent()

        client.enqueue(object: Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val name: String = response.body()?.listEvents?.get(0)?.name.toString()
                    val beginTime: String = response.body()?.listEvents?.get(0)?.beginTime.toString()
                    val link: String = response.body()?.listEvents?.get(0)?.link.toString()
                    showNotification(name, beginTime, link)
                    Log.d(TAG, "onSuccess: Selesai.....")
                    resultStatus = Result.success()
                } else {
                    showNotification("Get Nearby Event Not Success", "no date", "no link")
                }
            }
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: Gagal.....")
                showNotification("Get Current Weather Failed", "no date", "no link")
                resultStatus = Result.failure()
            }
        })
        return resultStatus as Result
    }

    fun showNotification(eventName: String, beginTime: String, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle(eventName)
            .setContentText(beginTime)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}