package com.financeapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.financeapp.FinanceApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receiver que responde aos alarmes agendados.
 * Funciona mesmo com o aplicativo fechado.
 */
class AlarmReceiver : BroadcastReceiver() {

    private val TAG = "AlarmReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarme recebido: ${intent.action}")

        val app = context.applicationContext as FinanceApp
        val notificationHelper = app.notificationHelper

        when (intent.action) {
            "com.financeapp.DAILY_REMINDER" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val soundIndex = app.appPreferences.notificationSound.first()
                        val enabled = app.appPreferences.dailyReminderEnabled.first()

                        if (enabled) {
                            // Verificar se já tem transações hoje
                            val todayCount = app.transactionRepository.getTodayTransactionCount()
                            if (todayCount == 0) {
                                notificationHelper.showDailyReminderNotification(soundIndex)
                            }
                        }

                        // Reagendar para o próximo dia
                        val hour = app.appPreferences.reminderHour.first()
                        val minute = app.appPreferences.reminderMinute.first()
                        notificationHelper.scheduleDailyReminder(hour, minute)
                    } catch (e: Exception) {
                        Log.e(TAG, "Erro no processamento do alarme", e)
                    }
                }
            }
        }
    }
}
