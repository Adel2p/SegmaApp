package com.noob.apps.mvvmcountries.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = " settingpref")

class UserPreferences(
    appContext: Context
) {
    private val applicationContext = appContext.applicationContext

    val EXAMPLE_COUNTER = stringPreferencesKey("example_counter")
    val SAVED_USER = booleanPreferencesKey("saved_user")
    val SAVED_LOGEDIN = booleanPreferencesKey("saved_logedin")
    val user_uuid = stringPreferencesKey("user_uuid")
    val token = stringPreferencesKey("AuthorizationToken")


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

    suspend fun saveUserToken(mtoken: String) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[token] ?: mtoken
            settings[token] = currentCounterValue
        }
    }

    val getUserToken: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[token] ?: ""
        }
}