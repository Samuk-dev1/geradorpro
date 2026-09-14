package com.financeapp

import android.app.Application
import androidx.room.Room
import com.financeapp.data.local.AppDatabase
import com.financeapp.data.preferences.AppPreferences
import com.financeapp.data.repository.TransactionRepository
import com.financeapp.notification.NotificationHelper

class FinanceApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var transactionRepository: TransactionRepository
        private set

    lateinit var appPreferences: AppPreferences
        private set

    lateinit var notificationHelper: NotificationHelper
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Inicializar banco de dados
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        // Inicializar repositório
        transactionRepository = TransactionRepository(database.transactionDao())

        // Inicializar preferências
        appPreferences = AppPreferences(applicationContext)

        // Inicializar helper de notificações
        notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.createNotificationChannels()
    }

    companion object {
        lateinit var instance: FinanceApp
            private set
    }
}
