package com.geby.saldo.data.pref

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property untuk context
val Context.dataStore by preferencesDataStore(name = "user_prefs")
class UserPreference private constructor(private val context: Context) {

    companion object {
        private val NAME_KEY = stringPreferencesKey("user_name")
        private val SALDO_AWAL_KEY = doublePreferencesKey("user_saldo")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    val name: Flow<String> = context.dataStore.data.map {
        it[NAME_KEY] ?: ""
    }

    val saldoAwal: Flow<Double> = context.dataStore.data.map {
        it[SALDO_AWAL_KEY] ?: 0.0
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map {
        it[DARK_MODE_KEY] ?: false
    }

    suspend fun saveUser(name: String, saldoAwal: Double) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[SALDO_AWAL_KEY] = saldoAwal
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}
