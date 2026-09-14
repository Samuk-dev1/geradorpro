package com.financeapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "finance_preferences")

class AppPreferences(private val context: Context) {

    companion object {
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val BILL_REMINDER_ENABLED = booleanPreferencesKey("bill_reminder_enabled")
        val PERSISTENT_SERVICE_ENABLED = booleanPreferencesKey("persistent_service_enabled")
        val NOTIFICATION_SOUND = intPreferencesKey("notification_sound")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val BUDGET_LIMIT = intPreferencesKey("budget_limit")
    }

    val dailyReminderEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[DAILY_REMINDER_ENABLED] ?: true }

    val billReminderEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[BILL_REMINDER_ENABLED] ?: true }

    val persistentServiceEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[PERSISTENT_SERVICE_ENABLED] ?: true }

    val notificationSound: Flow<Int> = context.dataStore.data
        .map { it[NOTIFICATION_SOUND] ?: 0 }

    val reminderHour: Flow<Int> = context.dataStore.data
        .map { it[REMINDER_HOUR] ?: 20 }

    val reminderMinute: Flow<Int> = context.dataStore.data
        .map { it[REMINDER_MINUTE] ?: 0 }

    val budgetLimit: Flow<Int> = context.dataStore.data
        .map { it[BUDGET_LIMIT] ?: 3000 }

    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[DAILY_REMINDER_ENABLED] = enabled }
    }

    suspend fun setBillReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[BILL_REMINDER_ENABLED] = enabled }
    }

    suspend fun setPersistentServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PERSISTENT_SERVICE_ENABLED] = enabled }
    }

    suspend fun setNotificationSound(soundIndex: Int) {
        context.dataStore.edit { it[NOTIFICATION_SOUND] = soundIndex }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[REMINDER_HOUR] = hour
            it[REMINDER_MINUTE] = minute
        }
    }

    suspend fun setBudgetLimit(limit: Int) {
        context.dataStore.edit { it[BUDGET_LIMIT] = limit }
    }
}
