package com.financeapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financeapp.R
import com.financeapp.data.local.dao.CategoryTotal
import com.financeapp.ui.theme.ExpenseColor
import com.financeapp.ui.theme.IncomeColor
import com.financeapp.ui.viewmodel.TransactionViewModel
import com.financeapp.utils.CategoryUtils
import com.financeapp.utils.FormatUtils
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: TransactionViewModel) {
    val income by viewModel.monthlyIncome.collectAsState(initial = 0.0)
    val expense by viewModel.monthlyExpense.collectAsState(initial = 0.0)
    val categoryTotals by viewModel.expenseByCategory.collectAsState(initial = emptyList())
    val balance by viewModel.totalBalance.collectAsState(initial = 0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.nav_reports),
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = FormatUtils.formatCurrentMonth(),
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumo
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryMiniCard(
                        title = stringResource(R.string.monthly_income),
                        value = income,
                        color = IncomeColor,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMiniCard(
                        title = stringResource(R.string.monthly_expense),
                        value = expense,
                        color = ExpenseColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Saldo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Economia do Mês",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FormatUtils.formatCurrency(income - expense),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (income - expense >= 0) IncomeColor else ExpenseColor
                        )
                    }
                }
            }

            // Gráfico de Pizza
            item {
                Text(
                    text = stringResource(R.string.expense_by_category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                if (categoryTotals.isEmpty() || expense == 0.0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sem dados para exibir",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    PieChart(
                        categoryTotals = categoryTotals,
                        total = expense
                    )
                }
            }

            // Legenda / Detalhes
            if (categoryTotals.isNotEmpty()) {
                items(categoryTotals) { item ->
                    CategoryLegendItem(item = item, total = expense)
                }
            }
        }
    }
}

@Composable
fun SummaryMiniCard(
    title: String,
    value: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = FormatUtils.formatCurrency(value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun PieChart(
    categoryTotals: List<CategoryTotal>,
    total: Double
) {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF00BCD4), Color(0xFF795548),
        Color(0xFFF44336), Color(0xFF3F51B5), Color(0xFFFF5722),
        Color(0xFF9C27B0), Color(0xFF607D8B), Color(0xFF9E9E9E)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val diameter = min(size.width, size.height)
                val radius = diameter / 2
                val strokeWidth = 60.dp.toPx()
                var startAngle = -90f

                categoryTotals.forEachIndexed { index, item ->
                    val sweepAngle = ((item.total / total) * 360).toFloat()
                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = FormatUtils.formatCurrency(total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryLegendItem(item: CategoryTotal, total: Double) {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF00BCD4), Color(0xFF795548),
        Color(0xFFF44336), Color(0xFF3F51B5), Color(0xFFFF5722),
        Color(0xFF9C27B0), Color(0xFF607D8B), Color(0xFF9E9E9E)
    )
    val category = CategoryUtils.getCategoryById(item.category)
    val percentage = (item.total / total) * 100
    val colorIndex = CategoryUtils.expenseCategories.indexOfFirst { it.id == item.category }
    val color = colors.getOrElse(colorIndex) { Color.Gray }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${category?.icon ?: ""} ${category?.name ?: item.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${String.format("%.1f", percentage)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = FormatUtils.formatCurrency(item.total),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
