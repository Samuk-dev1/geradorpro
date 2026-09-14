package com.financeapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.financeapp.R
import com.financeapp.ui.screens.HomeScreen
import com.financeapp.ui.screens.ReportsScreen
import com.financeapp.ui.screens.SettingsScreen
import com.financeapp.ui.screens.TransactionsScreen
import com.financeapp.ui.viewmodel.TransactionViewModel

sealed class Screen(val route: String, val title: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Transactions : Screen("transactions", R.string.nav_transactions, Icons.Default.List)
    object Reports : Screen("reports", R.string.nav_reports, Icons.Default.PieChart)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAppNavigation(viewModel: TransactionViewModel) {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val screens = listOf(
        Screen.Home,
        Screen.Transactions,
        Screen.Reports,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.title)) },
                        selected = selectedScreen.route == screen.route,
                        onClick = { selectedScreen = screen }
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.animation.AnimatedContent(
            targetState = selectedScreen,
            modifier = Modifier.padding(innerPadding),
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                is Screen.Home -> HomeScreen(viewModel)
                is Screen.Transactions -> TransactionsScreen(viewModel)
                is Screen.Reports -> ReportsScreen(viewModel)
                is Screen.Settings -> SettingsScreen(viewModel)
            }
        }
    }
}
