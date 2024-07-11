package com.example.techquiz.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.techquiz.data.domain.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class UserDataStoreRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val userFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        val uuidStr = preferences[KEY_USER_UUID]
        val userUuid = uuidStr?.let { UUID.fromString(it) } ?: UUID(0, 0)
        val userToken = preferences[KEY_USER_TOKEN]

        UserPreferences(
            userUUID = userUuid,
            userToken = userToken ?: "",
        )
    }

    suspend fun setUserUUID(
        uuid: UUID,
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_UUID] = uuid.toString()
        }
    }

    suspend fun setUserToken(
        token: String,
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_TOKEN] = token
        }
    }

    private companion object {
        val KEY_USER_UUID = stringPreferencesKey("user_uuid")
        val KEY_USER_TOKEN = stringPreferencesKey("user_token")
    }
}
