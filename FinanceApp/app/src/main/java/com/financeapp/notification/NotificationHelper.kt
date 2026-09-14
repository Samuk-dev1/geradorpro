package com.financeapp.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.financeapp.R
import com.financeapp.ui.MainActivity

/**
 * Helper responsável por gerenciar todas as notificações do aplicativo.
 * Suporta notificações mesmo com app fechado e tela desligada.
 */
class NotificationHelper(private val context: Context) {

    companion object {
        // IDs dos Canais de Notificação
        const val CHANNEL_REMINDERS = "channel_reminders"
        const val CHANNEL_SERVICE = "channel_service"
        const val CHANNEL_ALERTS = "channel_alerts"

        // IDs das Notificações
        const val NOTIFICATION_DAILY_REMINDER = 1001
        const val NOTIFICATION_FOREGROUND_SERVICE = 2001
        const val NOTIFICATION_BUDGET_ALERT = 3001
        const val NOTIFICATION_BILL_REMINDER = 4001

        // IDs de Som
        val SOUND_RES_IDS = listOf(
            R.raw.notification_default,
            R.raw.notification_success,
            R.raw.notification_warning,
            R.raw.notification_alert,
            R.raw.notification_coin,
            R.raw.notification_bill
        )
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Cria os canais de notificação necessários para Android O+
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal de Lembretes
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.notification_channel_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_reminders_desc)
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 100, 200)
                setSound(getSoundUri(0), getAudioAttributes())
            }

            // Canal de Serviço em Segundo Plano
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE,
                context.getString(R.string.notification_channel_service),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_service_desc)
                enableVibration(false)
                setSound(null, null)
            }

            // Canal de Alertas
            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                context.getString(R.string.notification_channel_alerts),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_alerts_desc)
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(getSoundUri(2), getAudioAttributes())
            }

            notificationManager.createNotificationChannels(
                listOf(remindersChannel, serviceChannel, alertsChannel)
            )
        }
    }

    /**
     * Obtém URI do som de notificação personalizado
     */
    fun getSoundUri(soundIndex: Int = 0): Uri {
        val resId = SOUND_RES_IDS.getOrElse(soundIndex) { SOUND_RES_IDS[0] }
        return Uri.parse("android.resource://${context.packageName}/$resId")
    }

    private fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    }

    /**
     * Cria notificação de lembrete diário
     */
    fun showDailyReminderNotification(soundIndex: Int = 0) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_money)
            .setContentTitle(context.getString(R.string.daily_reminder_title))
            .setContentText(context.getString(R.string.daily_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(getSoundUri(soundIndex))
            .setVibrate(longArrayOf(100, 200, 100, 200))
            .setLights(android.graphics.Color.BLUE, 1000, 500)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_DAILY_REMINDER, notification)
        } catch (e: SecurityException) {
            // Permissão de notificação não concedida
        }
    }

    /**
     * Cria notificação para o serviço em primeiro plano
     */
    fun createForegroundServiceNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_SERVICE)
            .setSmallIcon(R.drawable.ic_money)
            .setContentTitle(context.getString(R.string.service_running_title))
            .setContentText(context.getString(R.string.service_running_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSound(null)
            .setVibrate(null)
            .build()
    }

    /**
     * Mostra alerta de orçamento
     */
    fun showBudgetAlertNotification(percentage: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_money)
            .setContentTitle(context.getString(R.string.budget_alert_title))
            .setContentText("Você já utilizou ${String.format("%.0f", percentage)}% do seu orçamento mensal!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(getSoundUri(2))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setLights(android.graphics.Color.RED, 500, 500)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_BUDGET_ALERT, notification)
        } catch (e: SecurityException) {
            // Permissão não concedida
        }
    }

    /**
     * Agenda alarme diário para lembrete
     */
    fun scheduleDailyReminder(hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.financeapp.DAILY_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_DAILY_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Usar setAlarmClock para garantir que dispara mesmo com tela desligada
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    /**
     * Cancela o lembrete diário
     */
    fun cancelDailyReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.financeapp.DAILY_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_DAILY_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
