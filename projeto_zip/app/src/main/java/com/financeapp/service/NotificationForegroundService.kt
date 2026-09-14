package com.financeapp.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.financeapp.FinanceApp
import com.financeapp.notification.NotificationHelper
import com.financeapp.notification.NotificationHelper.Companion.NOTIFICATION_FOREGROUND_SERVICE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Serviço em primeiro plano que mantém o aplicativo ativo em segundo plano.
 * Isso garante que as notificações e verificações continuem funcionando
 * mesmo quando o app está fechado ou a tela está desligada.
 */
class NotificationForegroundService : Service() {

    private val TAG = "ForegroundService"
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var monitoringJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Serviço criado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Serviço iniciado")

        val app = application as FinanceApp
        val notificationHelper = app.notificationHelper

        // Criar notificação para o serviço em primeiro plano
        val notification = notificationHelper.createForegroundServiceNotification()
        startForeground(NOTIFICATION_FOREGROUND_SERVICE, notification)

        // Iniciar monitoramento periódico
        startPeriodicMonitoring()

        return START_STICKY
    }

    /**
     * Monitoramento periódico que verifica condições e envia alertas
     */
    private fun startPeriodicMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (true) {
                try {
                    checkBudgetAndSendAlerts()
                } catch (e: Exception) {
                    Log.e(TAG, "Erro no monitoramento", e)
                }
                // Verificar a cada 1 hora
                delay(60 * 60 * 1000)
            }
        }
    }

    private suspend fun checkBudgetAndSendAlerts() {
        val app = application as FinanceApp
        val budgetLimit = app.appPreferences.budgetLimit.first()
        val monthlyExpense = app.transactionRepository.getMonthlyExpense().first()

        if (budgetLimit > 0) {
            val percentage = (monthlyExpense / budgetLimit) * 100
            if (percentage >= 80) {
                app.notificationHelper.showBudgetAlertNotification(percentage)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        monitoringJob?.cancel()
        Log.d(TAG, "Serviço destruído")

        // Tentar reiniciar o serviço se as preferências ainda estiverem habilitadas
        serviceScope.launch {
            try {
                val app = application as FinanceApp
                val enabled = app.appPreferences.persistentServiceEnabled.first()
                if (enabled) {
                    val restartIntent = Intent(applicationContext, NotificationForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(restartIntent)
                    } else {
                        applicationContext.startService(restartIntent)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao reiniciar serviço", e)
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "Task removida, serviço continuará rodando")
    }
}
