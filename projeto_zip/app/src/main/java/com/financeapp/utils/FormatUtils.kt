package com.financeapp.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utilitários de formatação
 */
object FormatUtils {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
    private val monthFormat = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR"))

    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatCurrentMonth(): String {
        return monthFormat.format(Date()).replaceFirstChar { it.uppercase() }
    }

    fun getMonthRange(): Pair<Long, Long> {
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

    fun getTodayStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun createDate(day: Int, month: Int, year: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

/**
 * Categorias disponíveis
 */
object CategoryUtils {

    data class Category(
        val id: String,
        val name: String,
        val color: Long,
        val icon: String
    )

    val incomeCategories = listOf(
        Category("salary", "Salário", 0xFF2196F3, "💼"),
        Category("freelance", "Freelance", 0xFF9C27B0, "💻"),
        Category("investment", "Investimentos", 0xFFFF9800, "📈"),
        Category("other_income", "Outros", 0xFF9E9E9E, "💰")
    )

    val expenseCategories = listOf(
        Category("food", "Alimentação", 0xFFE91E63, "🍔"),
        Category("transport", "Transporte", 0xFF00BCD4, "🚗"),
        Category("housing", "Moradia", 0xFF795548, "🏠"),
        Category("health", "Saúde", 0xFFF44336, "⚕️"),
        Category("education", "Educação", 0xFF3F51B5, "📚"),
        Category("entertainment", "Lazer", 0xFFFF5722, "🎮"),
        Category("shopping", "Compras", 0xFFE91E63, "🛍️"),
        Category("bills", "Contas", 0xFF607D8B, "📄"),
        Category("other_expense", "Outros", 0xFF9E9E9E, "📦")
    )

    fun getAllCategories(): List<Category> = incomeCategories + expenseCategories

    fun getCategoryById(id: String): Category? {
        return getAllCategories().find { it.id == id }
    }

    fun getCategoryName(id: String): String {
        return getCategoryById(id)?.name ?: id
    }
}
