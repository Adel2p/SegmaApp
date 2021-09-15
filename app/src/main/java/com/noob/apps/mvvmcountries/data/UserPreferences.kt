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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constant.SECURE_PREF)

class UserPreferences(
    appContext: Context
) {
    private val applicationContext = appContext.applicationContext

    private val saveUser = booleanPreferencesKey(Constant.SAVED_USER)
    private val saveLogin = booleanPreferencesKey(Constant.SAVED_LOGIN)
    private val userId = stringPreferencesKey(Constant.USER_ID)
    private val token = stringPreferencesKey(Constant.REFRESH_TOKEN)
    private val fcmToken = stringPreferencesKey(Constant.FCM_TOKEN)
    private val appLanguage = stringPreferencesKey(Constant.APP_LANGUAGE)
    private val notificationEnabled = booleanPreferencesKey(Constant.NOTIFICATION_ENABLED)


    suspend fun saveUniversityData(isSaved: Boolean) {
        applicationContext.dataStore.edit { settings ->
            settings[saveUser] = isSaved
        }
    }

    val getUniversityData: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[saveUser] ?: false
        }

    suspend fun saveUserLogedIn(islogedin: Boolean) {
        applicationContext.dataStore.edit { settings ->
            settings[saveLogin] = islogedin
        }
    }

    val savedLogginedFlow: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[saveLogin] ?: false
        }

    suspend fun saveUserId(userId: String) {
        applicationContext.dataStore.edit { settings ->
            settings[this.userId] = userId
        }
    }

    val getUserId: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[userId] ?: ""
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

    suspend fun enableFirebase(enable: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[notificationEnabled] = enable
        }
    }

    val getFirebaseEnabled: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[notificationEnabled] ?: true
        }

    suspend fun clear() {
        applicationContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}