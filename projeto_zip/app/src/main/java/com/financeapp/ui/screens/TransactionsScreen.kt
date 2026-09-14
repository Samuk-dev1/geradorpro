package com.financeapp.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financeapp.R
import com.financeapp.data.local.entity.Transaction
import com.financeapp.data.local.entity.TransactionType
import com.financeapp.ui.theme.ExpenseColor
import com.financeapp.ui.theme.IncomeColor
import com.financeapp.ui.viewmodel.TransactionViewModel
import com.financeapp.utils.CategoryUtils
import com.financeapp.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    var filter by remember { mutableStateOf<TransactionType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    val filteredTransactions = when (filter) {
        TransactionType.INCOME -> transactions.filter { it.type == TransactionType.INCOME }
        TransactionType.EXPENSE -> transactions.filter { it.type == TransactionType.EXPENSE }
        null -> transactions
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_transactions),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == null,
                    onClick = { filter = null },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = filter == TransactionType.INCOME,
                    onClick = { filter = TransactionType.INCOME },
                    label = { Text(stringResource(R.string.income)) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeColor.copy(alpha = 0.15f),
                        selectedLabelColor = IncomeColor
                    )
                )
                FilterChip(
                    selected = filter == TransactionType.EXPENSE,
                    onClick = { filter = TransactionType.EXPENSE },
                    label = { Text(stringResource(R.string.expense)) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseColor.copy(alpha = 0.15f),
                        selectedLabelColor = ExpenseColor
                    )
                )
            }

            if (filteredTransactions.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTransactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = {
                                selectedTransaction = transaction
                                viewModel.selectTransaction(transaction)
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showAddDialog) {
        TransactionFormDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }

    if (selectedTransaction != null) {
        TransactionDetailDialog(
            transaction = selectedTransaction!!,
            viewModel = viewModel,
            onDismiss = { selectedTransaction = null }
        )
    }
}
