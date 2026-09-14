package com.financeapp.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeapp.R
import com.financeapp.BuildConfig
import com.financeapp.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: TransactionViewModel) {
    val context = LocalContext.current

    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsState(initial = true)
    val billReminderEnabled by viewModel.billReminderEnabled.collectAsState(initial = true)
    val persistentServiceEnabled by viewModel.persistentServiceEnabled.collectAsState(initial = true)
    val notificationSound by viewModel.notificationSound.collectAsState(initial = 0)
    val reminderHour by viewModel.reminderHour.collectAsState(initial = 20)
    val reminderMinute by viewModel.reminderMinute.collectAsState(initial = 0)
    val budgetLimit by viewModel.budgetLimit.collectAsState(initial = 3000)

    var showTimePicker by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_settings),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
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
            // Seção de Notificações
            item {
                SectionHeader(
                    title = stringResource(R.string.notifications),
                    icon = Icons.Default.Notifications
                )
            }

            item {
                SettingsCard {
                    // Lembrete Diário
                    SwitchSettingItem(
                        title = stringResource(R.string.daily_reminder),
                        description = stringResource(R.string.daily_reminder_desc),
                        icon = Icons.Default.AccessTime,
                        checked = dailyReminderEnabled,
                        onCheckedChange = {
                            viewModel.setDailyReminderEnabled(it)
                            if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )

                    if (dailyReminderEnabled) {
                        ClickableSettingItem(
                            title = stringResource(R.string.reminder_time),
                            description = String.format("%02d:%02d", reminderHour, reminderMinute),
                            icon = Icons.Default.AccessTime,
                            onClick = { showTimePicker = true }
                        )
                    }

                    // Lembrete de Contas
                    SwitchSettingItem(
                        title = stringResource(R.string.bill_reminder),
                        description = stringResource(R.string.bill_reminder_desc),
                        icon = Icons.Default.NotificationsActive,
                        checked = billReminderEnabled,
                        onCheckedChange = { viewModel.setBillReminderEnabled(it) }
                    )

                    // Som de Notificação
                    ClickableSettingItem(
                        title = stringResource(R.string.notification_sound),
                        description = getSoundName(notificationSound),
                        icon = Icons.Default.VolumeUp,
                        onClick = { showSoundDialog = true }
                    )

                    // Serviço Persistente
                    SwitchSettingItem(
                        title = stringResource(R.string.persistent_service),
                        description = stringResource(R.string.persistent_service_desc),
                        icon = Icons.Default.BatteryAlert,
                        checked = persistentServiceEnabled,
                        onCheckedChange = {
                            viewModel.setPersistentServiceEnabled(it)
                        }
                    )
                }
            }

            // Seção de Orçamento
            item {
                SectionHeader(title = "Orçamento", icon = Icons.Default.PlayArrow)
            }

            item {
                SettingsCard {
                    ClickableSettingItem(
                        title = "Limite de Gastos Mensal",
                        description = "R$ $budgetLimit",
                        icon = Icons.Default.PlayArrow,
                        onClick = { showBudgetDialog = true }
                    )
                }
            }

            // Seção de Permissões e Otimização
            item {
                SectionHeader(title = "Permissões", icon = Icons.Default.BatteryAlert)
            }

            item {
                SettingsCard {
                    ClickableSettingItem(
                        title = "Desativar Otimização de Bateria",
                        description = "Garante notificações em segundo plano",
                        icon = Icons.Default.BatteryAlert,
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                intent.data = Uri.parse("package:${context.packageName}")
                                context.startActivity(intent)
                            }
                        }
                    )

                    ClickableSettingItem(
                        title = "Configurações do App",
                        description = "Gerenciar permissões e notificações",
                        icon = Icons.Default.Settings,
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:${context.packageName}")
                            context.startActivity(intent)
                        }
                    )
                }
            }

            // Testar Notificação
            item {
                Button(
                    onClick = { viewModel.testNotification(notificationSound) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Testar Notificação")
                }
            }

            // Sobre
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.app_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Diálogos
    if (showTimePicker) {
        TimePickerDialog(
            initialHour = reminderHour,
            initialMinute = reminderMinute,
            onConfirm = { hour, minute ->
                viewModel.setReminderTime(hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showBudgetDialog) {
        BudgetDialog(
            currentLimit = budgetLimit,
            onConfirm = { viewModel.setBudgetLimit(it) },
            onDismiss = { showBudgetDialog = false }
        )
    }

    if (showSoundDialog) {
        SoundPickerDialog(
            currentSound = notificationSound,
            onSelect = { soundIndex ->
                viewModel.setNotificationSound(soundIndex)
                viewModel.testNotification(soundIndex)
                showSoundDialog = false
            },
            onDismiss = { showSoundDialog = false }
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
fun ClickableSettingItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

fun getSoundName(index: Int): String {
    return when (index) {
        0 -> "Padrão"
        1 -> "Sucesso"
        2 -> "Aviso"
        3 -> "Alerta"
        4 -> "Moeda"
        5 -> "Conta"
        else -> "Padrão"
    }
}

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Horário do Lembrete") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(
                    value = hour,
                    range = 0..23,
                    onValueChange = { hour = it },
                    label = "Hora"
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                NumberPicker(
                    value = minute,
                    range = 0..59,
                    onValueChange = { minute = it },
                    label = "Min"
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(hour, minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = {
            val next = if (value + 1 in range) value + 1 else range.first
            onValueChange(next)
        }) {
            Text("▲", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = {
            val prev = if (value - 1 in range) value - 1 else range.last
            onValueChange(prev)
        }) {
            Text("▼", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BudgetDialog(
    currentLimit: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf(currentLimit.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Limite de Gastos Mensal") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it.filter { c -> c.isDigit() } },
                label = { Text("Valor em R$") },
                prefix = { Text("R$ ") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                val intValue = value.toIntOrNull() ?: 0
                onConfirm(intValue)
                onDismiss()
            }) {
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
fun SoundPickerDialog(
    currentSound: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    val sounds = listOf("Padrão", "Sucesso", "Aviso", "Alerta", "Moeda", "Conta")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.notification_sound)) },
        text = {
            Column {
                sounds.forEachIndexed { index, name ->
                    ListItem(
                        modifier = Modifier.clickable { onSelect(index) },
                        headlineContent = { Text(name) },
                        leadingContent = {
                            Icon(Icons.Default.VolumeUp, contentDescription = null)
                        },
                        trailingContent = if (index == currentSound) {
                            { Text("✓", color = MaterialTheme.colorScheme.primary) }
                        } else null
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}
