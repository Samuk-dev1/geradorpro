package com.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa uma transação financeira
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME, EXPENSE
}
