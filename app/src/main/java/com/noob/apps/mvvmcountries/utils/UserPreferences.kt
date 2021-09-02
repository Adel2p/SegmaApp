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


class UserPreferences(
    appContext: Context
) {
    private val applicationContext = appContext.applicationContext
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = " settingpref")
    val EXAMPLE_COUNTER = stringPreferencesKey("example_counter")
    val SAVED_USER = booleanPreferencesKey("saved_user")
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

    suspend fun saveUserData(isSaved: Boolean) {
        applicationContext.dataStore.edit { settings ->
            val currentCounterValue = settings[SAVED_USER] ?: isSaved
            settings[SAVED_USER] = currentCounterValue
        }
    }
    val savedUserFlow: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SAVED_USER] ?: false
        }


}