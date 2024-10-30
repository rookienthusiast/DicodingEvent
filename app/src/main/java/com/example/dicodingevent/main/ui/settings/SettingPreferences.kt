package com.example.dicodingevent.main.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val booleanKey = booleanPreferencesKey("theme_setting")
    private val booleanKey1 = booleanPreferencesKey("notification_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanKey] = isDarkModeActive
        }
    }

    fun getNotificationSettings(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanKey1] ?: false
        }
    }

    suspend fun saveNotificationSetting(isNotificationsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanKey1] = isNotificationsEnabled
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences?= null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}