package com.f1ndle.tgwsproxy.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.f1ndle.tgwsproxy.ProxyViewModel
import com.f1ndle.tgwsproxy.R

data class AppColors(
    val background: Color,
    val surface: Color,
    val buttonBackground: Color,
    val powerButtonInactive: Color,
    val powerButtonActive: Color,
    val powerIconInactive: Color,
    val powerIconActive: Color,
    val accent: Color,
    val statusRed: Color,
    val statusGreen: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val updateBannerBackground: Color,
    val switchUncheckedTrack: Color,
    val switchUncheckedThumb: Color,
    val logBackground: Color,
    val logTextColor: Color,
    val isDark: Boolean,
)

val DarkAppColors = AppColors(
    background = Color(0xFF101014),
    surface = Color(0xFF1A1A22),
    buttonBackground = Color(0xFF262632),
    powerButtonInactive = Color(0xFF2B2A34),
    powerButtonActive = Color(0xFF324468),
    powerIconInactive = Color(0xFFC4C4CD),
    powerIconActive = Color(0xFF5E9CFF),
    accent = Color(0xFF5E9CFF),
    statusRed = Color(0xFFE55755),
    statusGreen = Color(0xFF4CAF50),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF90909A),
    textMuted = Color(0xFF70707A),
    updateBannerBackground = Color(0xFF1B2B4A),
    switchUncheckedTrack = Color(0xFF32323D),
    switchUncheckedThumb = Color(0xFF8E8E98),
    logBackground = Color(0xFF101014),
    logTextColor = Color(0xFFC0C0C8),
    isDark = true,
)

val LightAppColors = AppColors(
    background = Color(0xFFF2F3F7),
    surface = Color(0xFFFFFFFF),
    buttonBackground = Color(0xFFE6E9F2),
    powerButtonInactive = Color(0xFFE4E7F0),
    powerButtonActive = Color(0xFFD6E4FF),
    powerIconInactive = Color(0xFF7E8494),
    powerIconActive = Color(0xFF1D72F2),
    accent = Color(0xFF1D72F2),
    statusRed = Color(0xFFE53935),
    statusGreen = Color(0xFF2E7D32),
    textPrimary = Color(0xFF141419),
    textSecondary = Color(0xFF63636E),
    textMuted = Color(0xFF8E8E98),
    updateBannerBackground = Color(0xFFE2EDFE),
    switchUncheckedTrack = Color(0xFFDCDCE2),
    switchUncheckedThumb = Color(0xFF8E8E98),
    logBackground = Color(0xFFEBECEF),
    logTextColor = Color(0xFF282830),
    isDark = false,
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

@Composable
fun TgWsTheme(
    themeMode: String = "dark",
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemDark
    }
    val appColors = if (isDark) DarkAppColors else LightAppColors
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = appColors.accent,
            background = appColors.background,
            surface = appColors.surface,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary,
        )
    } else {
        lightColorScheme(
            primary = appColors.accent,
            background = appColors.background,
            surface = appColors.surface,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary,
        )
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProxyScreen(viewModel: ProxyViewModel) {
    val colors = LocalAppColors.current

    val config by viewModel.config.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val showLogsSheet by viewModel.showLogsSheet.collectAsStateWithLifecycle()
    val showDomainDialog by viewModel.showDomainDialog.collectAsStateWithLifecycle()
    val showThemeDialog by viewModel.showThemeDialog.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = viewModel::openTelegramChannel) {
                        Icon(
                            painter = painterResource(R.drawable.ic_telegram),
                            contentDescription = stringResource(R.string.view_telegram_channel),
                            tint = colors.textSecondary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 30.sp,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.app_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                    ),
                )
            }

            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {
                    // Status Row with Dot
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (running) colors.statusGreen else colors.statusRed),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(if (running) R.string.status_running else R.string.status_stopped),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                                fontSize = 21.sp,
                            ),
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // MTProto row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "MTProto",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colors.textMuted,
                                fontSize = 14.sp,
                            ),
                            modifier = Modifier.width(90.dp),
                        )
                        Text(
                            text = "127.0.0.1:${config.port}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                            ),
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Cloudflare row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Cloudflare",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colors.textMuted,
                                fontSize = 14.sp,
                            ),
                            modifier = Modifier.width(90.dp),
                        )
                        val cfDisplay = if (config.customDomain.isNotBlank()) {
                            config.customDomain
                        } else {
                            "f1ndle.biz"
                        }
                        Text(
                            text = cfDisplay,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                            ),
                        )
                    }
                }
            }

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(36.dp))

            // Big Central Power Button
            val animatedColor by animateColorAsState(
                targetValue = if (running) colors.powerButtonActive else colors.powerButtonInactive,
                label = "powerColor",
            )
            val buttonScale by animateFloatAsState(
                targetValue = if (running) 1.05f else 1.0f,
                label = "buttonScale",
            )

            Box(
                modifier = Modifier
                    .size(108.dp)
                    .scale(buttonScale)
                    .clip(CircleShape)
                    .background(animatedColor)
                    .clickable { viewModel.toggleRunning() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_power),
                    contentDescription = stringResource(if (running) R.string.stop else R.string.start),
                    tint = if (running) colors.powerIconActive else colors.powerIconInactive,
                    modifier = Modifier.size(52.dp),
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = stringResource(if (running) R.string.press_to_stop else R.string.press_to_start),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                ),
            )

            Spacer(Modifier.height(28.dp))

            // Apply in Telegram Button
            Button(
                onClick = viewModel::openLink,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonBackground,
                    contentColor = colors.textPrimary,
                ),
            ) {
                Text(
                    text = stringResource(R.string.apply_in_telegram),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    ),
                )
            }

            Spacer(Modifier.height(32.dp))

            // Settings Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.section_settings),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        letterSpacing = 1.sp,
                        fontSize = 12.sp,
                    ),
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp),
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                    ) {
                        SettingSwitchItem(
                            title = stringResource(R.string.setting_autostart_title),
                            subtitle = stringResource(R.string.setting_autostart_desc),
                            checked = config.autostart,
                            onCheckedChange = viewModel::updateAutostart,
                        )

                        SettingSwitchItem(
                            title = stringResource(R.string.setting_cf_priority_title),
                            subtitle = stringResource(R.string.setting_cf_priority_desc),
                            checked = config.cfPriority,
                            onCheckedChange = viewModel::updateCfPriority,
                        )

                        SettingSwitchItem(
                            title = stringResource(R.string.setting_cf_balance_title),
                            subtitle = stringResource(R.string.setting_cf_balance_desc),
                            checked = config.cfBalance,
                            onCheckedChange = viewModel::updateCfBalance,
                        )

                        SettingSwitchItem(
                            title = stringResource(R.string.setting_default_domains_title),
                            subtitle = stringResource(R.string.setting_default_domains_desc),
                            checked = config.defaultDomains,
                            onCheckedChange = viewModel::updateDefaultDomains,
                        )

                        // Custom Domain Row
                        SettingClickableItem(
                            title = stringResource(R.string.setting_custom_domain_title),
                            subtitle = if (config.customDomain.isNotBlank()) {
                                config.customDomain
                            } else {
                                "По умолчанию (f1ndle.biz)"
                            },
                            onClick = { viewModel.setShowDomainDialog(true) },
                        )

                        // Logs Viewer Row
                        SettingClickableItem(
                            title = stringResource(R.string.setting_logs_title),
                            subtitle = stringResource(R.string.setting_logs_desc),
                            onClick = { viewModel.setShowLogsSheet(true) },
                        )

                        // Theme Selection Row
                        val themeSubtitle = when (config.themeMode) {
                            "light" -> stringResource(R.string.theme_light)
                            "system" -> stringResource(R.string.theme_system)
                            else -> stringResource(R.string.theme_dark)
                        }
                        SettingClickableItem(
                            title = stringResource(R.string.setting_theme_title),
                            subtitle = themeSubtitle,
                            onClick = { viewModel.setShowThemeDialog(true) },
                        )

                        // Quick Settings Tile Row
                        SettingClickableItem(
                            title = stringResource(R.string.setting_quick_settings_tile),
                            subtitle = stringResource(R.string.setting_quick_settings_tile_desc),
                            onClick = viewModel::requestAddQuickSettingsTile,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Community & Updates Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.section_community),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        letterSpacing = 1.sp,
                        fontSize = 12.sp,
                    ),
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp),
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                    ) {
                        // Telegram Channel Row
                        SettingClickableItem(
                            title = stringResource(R.string.setting_telegram_channel),
                            subtitle = stringResource(R.string.setting_telegram_channel_desc),
                            onClick = viewModel::openTelegramChannel,
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // Custom Domain Dialog
    if (showDomainDialog) {
        var domainInput by remember { mutableStateOf(config.customDomain) }
        AlertDialog(
            onDismissRequest = { viewModel.setShowDomainDialog(false) },
            containerColor = colors.surface,
            title = {
                Text(
                    text = stringResource(R.string.setting_custom_domain_title),
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.custom_domain_hint),
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = domainInput,
                        onValueChange = { domainInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("f1ndle.biz", color = colors.textMuted) },
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCustomDomain(domainInput.trim())
                        viewModel.setShowDomainDialog(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                ) {
                    Text(stringResource(R.string.save), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDomainDialog(false) }) {
                    Text(stringResource(R.string.cancel), color = colors.textSecondary)
                }
            },
        )
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        val options = listOf(
            "system" to stringResource(R.string.theme_system),
            "dark" to stringResource(R.string.theme_dark),
            "light" to stringResource(R.string.theme_light),
        )
        AlertDialog(
            onDismissRequest = { viewModel.setShowThemeDialog(false) },
            containerColor = colors.surface,
            title = {
                Text(
                    text = stringResource(R.string.theme_dialog_title),
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    options.forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateThemeMode(mode)
                                    viewModel.setShowThemeDialog(false)
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = config.themeMode == mode,
                                onClick = {
                                    viewModel.updateThemeMode(mode)
                                    viewModel.setShowThemeDialog(false)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colors.accent,
                                    unselectedColor = colors.textSecondary,
                                ),
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = label,
                                color = colors.textPrimary,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowThemeDialog(false) }) {
                    Text(stringResource(R.string.cancel), color = colors.textSecondary)
                }
            },
        )
    }

    // Logs Bottom Sheet
    if (showLogsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.setShowLogsSheet(false) },
            sheetState = sheetState,
            containerColor = colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.setting_logs_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Row {
                        TextButton(onClick = viewModel::clearLogs) {
                            Text(stringResource(R.string.clear_logs), color = colors.accent)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                val logs = viewModel.logs
                val listState = rememberLazyListState()

                LaunchedEffect(logs.size) {
                    if (logs.isNotEmpty()) {
                        listState.scrollToItem(logs.lastIndex)
                    }
                }

                SelectionContainer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.logBackground, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                ) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        items(logs, key = { it.id }) { line ->
                            Text(
                                text = line.text,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = colors.logTextColor,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                ),
            )
        }
        Spacer(Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = if (colors.isDark) Color(0xFF1B2A4A) else Color.White,
                checkedTrackColor = colors.accent,
                uncheckedThumbColor = colors.switchUncheckedThumb,
                uncheckedTrackColor = colors.switchUncheckedTrack,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun SettingClickableItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                ),
            )
        }
    }
}
