package com.noob.apps.mvvmcountries.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = " setting_pref")

class UserPreferences(
    appContext: Context
) {
    private val applicationContext = appContext.applicationContext

    val EXAMPLE_COUNTER = stringPreferencesKey("example_counter")
    val SAVED_USER = booleanPreferencesKey("saved_user")
    val SAVED_LOGEDIN = booleanPreferencesKey("saved_logedin")
    val user_uuid = stringPreferencesKey("user_uuid")
    val token = stringPreferencesKey("refresh_token")
    val fcmToken = stringPreferencesKey("fcmToken")


    val exampleCounterFlow: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[EXAMPLE_COUNTER] ?: ""
        }

    suspend fun incrementCounter(bookmark: String) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: bookmark
            settings[EXAMPLE_COUNTER] = currentCounterValue
        }
    }

    suspend fun saveUniversityData(isSaved: Boolean) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[SAVED_USER] ?: isSaved
            settings[SAVED_USER] = currentCounterValue
        }
    }

    val getUniversityData: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SAVED_USER] ?: false
        }

    suspend fun saveUserLogedIn(islogedin: Boolean) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[SAVED_LOGEDIN] ?: islogedin
            settings[SAVED_LOGEDIN] = currentCounterValue
        }
    }

    val savedLogginedFlow: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SAVED_LOGEDIN] ?: false
        }

    suspend fun saveUserId(userId: String) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[user_uuid] ?: userId
            settings[user_uuid] = currentCounterValue
        }
    }

    val getUserId: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[user_uuid] ?: ""
        }

    suspend fun saveRefreshToken(mToken: String) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[token] ?: mToken
            settings[token] = currentCounterValue
        }
    }

    val getRefreshToken: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[token] ?: ""
        }

    suspend fun saveFCMToken(mToken: String) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[fcmToken] ?: mToken
            settings[fcmToken] = currentCounterValue
        }
    }

    val getFCMToken: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[fcmToken] ?: ""
        }

    suspend fun clear() {
        applicationContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}