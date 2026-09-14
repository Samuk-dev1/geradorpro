package com.financeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeapp.R
import com.financeapp.data.local.entity.Transaction
import com.financeapp.data.local.entity.TransactionType
import com.financeapp.ui.theme.ExpenseColor
import com.financeapp.ui.theme.IncomeColor
import com.financeapp.ui.viewmodel.TransactionViewModel
import com.financeapp.utils.CategoryUtils
import com.financeapp.utils.FormatUtils
import java.util.Calendar

@Composable
fun TransactionFormDialog(
    viewModel: TransactionViewModel,
    transaction: Transaction? = null,
    onDismiss: () -> Unit
) {
    var type by remember { mutableStateOf(transaction?.type ?: TransactionType.EXPENSE) }
    var title by remember { mutableStateOf(transaction?.title ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.toString()?.replace(".", ",") ?: "") }
    var category by remember { mutableStateOf(transaction?.category ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = if (type == TransactionType.INCOME)
        CategoryUtils.incomeCategories else CategoryUtils.expenseCategories

    // Inicializar categoria padrão
    if (category.isEmpty() && categories.isNotEmpty()) {
        category = categories[0].id
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (transaction == null) stringResource(R.string.add_transaction)
                else stringResource(R.string.edit),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tipo de transação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = {
                            type = TransactionType.INCOME
                            category = CategoryUtils.incomeCategories[0].id
                        },
                        label = { Text(stringResource(R.string.income)) },
                        leadingIcon = { Text("+") },
                        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeColor.copy(alpha = 0.15f),
                            selectedLabelColor = IncomeColor
                        )
                    )
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = {
                            type = TransactionType.EXPENSE
                            category = CategoryUtils.expenseCategories[0].id
                        },
                        label = { Text(stringResource(R.string.expense)) },
                        leadingIcon = { Text("-") },
                        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseColor.copy(alpha = 0.15f),
                            selectedLabelColor = ExpenseColor
                        )
                    )
                }

                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = it.isBlank()
                    },
                    label = { Text(stringResource(R.string.transaction_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError,
                    singleLine = true
                )

                // Valor
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.filter { c -> c.isDigit() || c == ',' }
                        amountError = amount.isBlank() || amount.toDoubleOrNull() == null
                    },
                    label = { Text(stringResource(R.string.transaction_amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = amountError,
                    prefix = { Text("R$ ") },
                    singleLine = true
                )

                // Categoria
                CategoryDropdown(
                    categories = categories,
                    selectedCategory = category,
                    onCategorySelected = { category = it }
                )

                // Descrição
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.transaction_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    titleError = title.isBlank()
                    val amountValue = amount.replace(",", ".").toDoubleOrNull()
                    amountError = amountValue == null || amountValue <= 0

                    if (!titleError && !amountError && amountValue != null) {
                        val newTransaction = Transaction(
                            id = transaction?.id ?: 0,
                            title = title,
                            amount = amountValue,
                            type = type,
                            category = category,
                            date = transaction?.date ?: System.currentTimeMillis(),
                            description = description
                        )
                        if (transaction == null) {
                            viewModel.insertTransaction(newTransaction)
                        } else {
                            viewModel.updateTransaction(newTransaction)
                        }
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<CategoryUtils.Category>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = categories.find { it.id == selectedCategory }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.transaction_category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = selected?.let {
                { Text(text = it.icon) }
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = category.icon)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = category.name)
                        }
                    },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TransactionDetailDialog(
    transaction: Transaction,
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    val category = CategoryUtils.getCategoryById(transaction.category)
    val isIncome = transaction.type == TransactionType.INCOME
    var showEditDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {
                    viewModel.deleteTransaction(transaction)
                    onDismiss()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = ExpenseColor)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow(
                    label = stringResource(R.string.transaction_amount),
                    value = FormatUtils.formatCurrency(transaction.amount),
                    valueColor = if (isIncome) IncomeColor else ExpenseColor
                )
                DetailRow(
                    label = stringResource(R.string.transaction_category),
                    value = "${category?.icon ?: ""} ${category?.name ?: transaction.category}"
                )
                DetailRow(
                    label = stringResource(R.string.transaction_date),
                    value = FormatUtils.formatDate(transaction.date)
                )
                if (transaction.description.isNotBlank()) {
                    DetailRow(
                        label = stringResource(R.string.transaction_description),
                        value = transaction.description
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { showEditDialog = true }) {
                Text(stringResource(R.string.edit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showEditDialog) {
        TransactionFormDialog(
            viewModel = viewModel,
            transaction = transaction,
            onDismiss = {
                showEditDialog = false
                onDismiss()
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}
