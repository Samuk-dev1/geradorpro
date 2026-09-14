package com.financeapp.data.repository

import com.financeapp.data.local.dao.CategoryTotal
import com.financeapp.data.local.dao.TransactionDao
import com.financeapp.data.local.entity.Transaction
import com.financeapp.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()

    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> =
        transactionDao.getRecentTransactions(limit)

    fun getTotalBalance(): Flow<Double> =
        transactionDao.getTotalBalance()

    fun getMonthlyIncome(): Flow<Double> {
        val (start, end) = getMonthRange()
        return transactionDao.getTotalByTypeAndMonth(TransactionType.INCOME, start, end)
    }

    fun getMonthlyExpense(): Flow<Double> {
        val (start, end) = getMonthRange()
        return transactionDao.getTotalByTypeAndMonth(TransactionType.EXPENSE, start, end)
    }

    fun getExpenseByCategory(): Flow<List<CategoryTotal>> {
        val (start, end) = getMonthRange()
        return transactionDao.getCategoryTotals(TransactionType.EXPENSE, start, end)
    }

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    suspend fun insert(transaction: Transaction): Long =
        transactionDao.insert(transaction)

    suspend fun update(transaction: Transaction) =
        transactionDao.update(transaction)

    suspend fun delete(transaction: Transaction) =
        transactionDao.delete(transaction)

    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)

    suspend fun getTodayTransactionCount(): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return transactionDao.getTodayTransactionCount(calendar.timeInMillis)
    }

    private fun getMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }
}
