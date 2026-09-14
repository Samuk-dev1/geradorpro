package com.financeapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.financeapp.FinanceApp
import com.financeapp.service.NotificationForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receiver que é acionado após o boot do dispositivo.
 * Restaura todos os alarmes e serviços necessários.
 */
class BootReceiver : BroadcastReceiver() {

    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Boot recebido: ${intent.action}")

        val app = context.applicationContext as FinanceApp
        val notificationHelper = app.notificationHelper

        // Criar canais de notificação novamente
        notificationHelper.createNotificationChannels()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Restaurar lembrete diário
                val dailyReminderEnabled = app.appPreferences.dailyReminderEnabled.first()
                if (dailyReminderEnabled) {
                    val hour = app.appPreferences.reminderHour.first()
                    val minute = app.appPreferences.reminderMinute.first()
                    notificationHelper.scheduleDailyReminder(hour, minute)
                    Log.d(TAG, "Lembrete diário restaurado: $hour:$minute")
                }

                // Iniciar serviço persistente se habilitado
                val persistentServiceEnabled = app.appPreferences.persistentServiceEnabled.first()
                if (persistentServiceEnabled) {
                    val serviceIntent = Intent(context, NotificationForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                    Log.d(TAG, "Serviço persistente iniciado após boot")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao restaurar após boot", e)
            }
        }
    }
}
