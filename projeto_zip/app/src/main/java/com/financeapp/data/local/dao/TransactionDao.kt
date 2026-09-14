package com.financeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.financeapp.data.local.entity.Transaction
import com.financeapp.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0) FROM transactions")
    fun getTotalBalance(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalByTypeAndMonth(type: TransactionType, startDate: Long, endDate: Long): Flow<Double>

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate GROUP BY category ORDER BY total DESC")
    fun getCategoryTotals(type: TransactionType, startDate: Long, endDate: Long): Flow<List<CategoryTotal>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    @Query("SELECT COUNT(*) FROM transactions WHERE date >= :today")
    suspend fun getTodayTransactionCount(today: Long): Int
}

data class CategoryTotal(
    val category: String,
    val total: Double
)
