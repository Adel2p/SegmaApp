package com.noob.apps.mvvmcountries.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.noob.apps.mvvmcountries.utils.Constant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = " setting_pref")

class UserPreferences(
    appContext: Context
) {
    private val applicationContext = appContext.applicationContext

    private val SAVED_USER = booleanPreferencesKey("saved_user")
    private val SAVED_LOGEDIN = booleanPreferencesKey("saved_logedin")
    private val user_uuid = stringPreferencesKey("user_uuid")
    private val token = stringPreferencesKey("refresh_token")
    private val fcmToken = stringPreferencesKey("fcmToken")
    private val appLanguage = stringPreferencesKey("app_Language")


    suspend fun saveUniversityData(isSaved: Boolean) {
        applicationContext.dataStore.edit { settings ->
            settings[SAVED_USER] = isSaved
        }
    }

    val getUniversityData: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SAVED_USER] ?: false
        }

    suspend fun saveUserLogedIn(islogedin: Boolean) {
        applicationContext.dataStore.edit { settings ->
            settings[SAVED_LOGEDIN] = islogedin
        }
    }

    val savedLogginedFlow: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SAVED_LOGEDIN] ?: false
        }

    suspend fun saveUserId(userId: String) {
        applicationContext.dataStore.edit { settings ->
            settings[user_uuid] = userId
        }
    }

    val getUserId: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[user_uuid] ?: ""
        }

    suspend fun saveRefreshToken(mToken: String) {
        applicationContext.dataStore.edit { settings ->
            settings[token] = mToken
        }
    }

    val getRefreshToken: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[token] ?: ""
        }

    suspend fun saveFCMToken(mToken: String) {
        applicationContext.dataStore.edit { settings ->
            settings[fcmToken] = mToken
        }
    }

    val getFCMToken: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[fcmToken] ?: ""
        }

    suspend fun saveLanguage(language: String) {
        applicationContext.dataStore.edit { preferences ->
            preferences[appLanguage] = language
        }
    }

    val getAppLanguage: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[appLanguage] ?: Constant.ENGLISH
        }

    suspend fun clear() {
        applicationContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}