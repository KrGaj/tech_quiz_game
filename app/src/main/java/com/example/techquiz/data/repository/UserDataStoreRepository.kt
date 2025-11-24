package com.example.techquiz.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.techquiz.data.domain.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

class UserDataStoreRepository(
    private val dataStore: DataStore<Preferences>,
) {
    @OptIn(ExperimentalUuidApi::class)
    val userFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        val uuidStr = preferences[KEY_USER_UUID]
        val userUuid = uuidStr?.let { UUID.fromString(it).toKotlinUuid() }
            ?: UUID(0, 0).toKotlinUuid()
        val userToken = preferences[KEY_USER_TOKEN] ?: ""

        UserPreferences(
            userUuid = userUuid,
            userToken = userToken,
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun setUserUuid(
        uuid: Uuid,
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
