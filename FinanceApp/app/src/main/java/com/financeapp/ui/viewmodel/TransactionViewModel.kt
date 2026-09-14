package com.financeapp.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.financeapp.FinanceApp
import com.financeapp.data.local.dao.CategoryTotal
import com.financeapp.data.local.entity.Transaction
import com.financeapp.data.local.entity.TransactionType
import com.financeapp.service.NotificationForegroundService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FinanceApp
    private val repository = app.transactionRepository
    private val preferences = app.appPreferences
    private val notificationHelper = app.notificationHelper

    val allTransactions: Flow<List<Transaction>> = repository.getAllTransactions()
    val recentTransactions: Flow<List<Transaction>> = repository.getRecentTransactions(10)
    val totalBalance: Flow<Double> = repository.getTotalBalance()
    val monthlyIncome: Flow<Double> = repository.getMonthlyIncome()
    val monthlyExpense: Flow<Double> = repository.getMonthlyExpense()
    val expenseByCategory: Flow<List<CategoryTotal>> = repository.getExpenseByCategory()

    // Preferências
    val dailyReminderEnabled = preferences.dailyReminderEnabled
    val billReminderEnabled = preferences.billReminderEnabled
    val persistentServiceEnabled = preferences.persistentServiceEnabled
    val notificationSound = preferences.notificationSound
    val reminderHour = preferences.reminderHour
    val reminderMinute = preferences.reminderMinute
    val budgetLimit = preferences.budgetLimit

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.insert(transaction)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.update(transaction)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.delete(transaction)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun selectTransaction(transaction: Transaction?) {
        _selectedTransaction.value = transaction
    }

    // Configurações de Notificação
    fun setDailyReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setDailyReminderEnabled(enabled)
            if (enabled) {
                val hour = preferences.reminderHour.first()
                val minute = preferences.reminderMinute.first()
                notificationHelper.scheduleDailyReminder(hour, minute)
            } else {
                notificationHelper.cancelDailyReminder()
            }
        }
    }

    fun setBillReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setBillReminderEnabled(enabled)
        }
    }

    fun setPersistentServiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setPersistentServiceEnabled(enabled)
            val context = getApplication<Application>()
            val intent = Intent(context, NotificationForegroundService::class.java)

            if (enabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } else {
                context.stopService(intent)
            }
        }
    }

    fun setNotificationSound(soundIndex: Int) {
        viewModelScope.launch {
            preferences.setNotificationSound(soundIndex)
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferences.setReminderTime(hour, minute)
            if (preferences.dailyReminderEnabled.first()) {
                notificationHelper.scheduleDailyReminder(hour, minute)
            }
        }
    }

    fun setBudgetLimit(limit: Int) {
        viewModelScope.launch {
            preferences.setBudgetLimit(limit)
        }
    }

    fun testNotification(soundIndex: Int = 0) {
        notificationHelper.showDailyReminderNotification(soundIndex)
    }

    fun clearUiState() {
        _uiState.value = UiState.Idle
    }
}
