package com.financeapp.worker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.financeapp.FinanceApp
import com.financeapp.service.NotificationForegroundService

/**
 * Worker que garante que o serviço de notificações esteja rodando.
 * Executado periodicamente pelo WorkManager.
 */
class NotificationMaintenanceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "MaintenanceWorker"

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker de manutenção executado")

        return try {
            val app = applicationContext as FinanceApp
            val persistentServiceEnabled = app.appPreferences.persistentServiceEnabled.first()

            if (persistentServiceEnabled) {
                // Verificar e reiniciar o serviço se necessário
                val serviceIntent = android.content.Intent(applicationContext, NotificationForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    applicationContext.startForegroundService(serviceIntent)
                } else {
                    applicationContext.startService(serviceIntent)
                }
                Log.d(TAG, "Serviço verificado/reiniciado com sucesso")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Erro no worker de manutenção", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "notification_maintenance_worker"
    }
}
