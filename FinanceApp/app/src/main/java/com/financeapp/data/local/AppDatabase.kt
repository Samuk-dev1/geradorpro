package com.financeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.financeapp.data.local.dao.TransactionDao
import com.financeapp.data.local.entity.Transaction

@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "finance_app_db"
    }
}
