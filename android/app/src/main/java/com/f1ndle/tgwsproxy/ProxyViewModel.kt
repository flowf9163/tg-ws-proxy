package com.f1ndle.tgwsproxy

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProxyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProxySettingsRepository(application)
    val config: StateFlow<ProxyConfigData> = repository.config

    val running = ProxyBridge.running
    val tgLink = ProxyBridge.tgLink
    val error = ProxyBridge.error

    private val _logs = mutableStateListOf<LogLine>()
    val logs: List<LogLine> = _logs

    private var nextLogId = 0L
    private var autoOpenPending = false

    private val _showLogsSheet = MutableStateFlow(false)
    val showLogsSheet: StateFlow<Boolean> = _showLogsSheet.asStateFlow()

    private val _showDomainDialog = MutableStateFlow(false)
    val showDomainDialog: StateFlow<Boolean> = _showDomainDialog.asStateFlow()

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog.asStateFlow()

    private val _showPortDialog = MutableStateFlow(false)
    val showPortDialog: StateFlow<Boolean> = _showPortDialog.asStateFlow()

    init {
        ProxyBridge.syncFromNative()
        viewModelScope.launch {
            ProxyBridge.logs.collect { line ->
                _logs.add(LogLine(nextLogId++, line))
                if (_logs.size > MAX_LOG_LINES) {
                    _logs.removeAt(0)
                }
            }
        }
        viewModelScope.launch {
            ProxyBridge.tgLink.collect { link ->
                if (link != null && autoOpenPending) {
                    autoOpenPending = false
                    openLink()
                }
            }
        }
    }

    fun toggleRunning() {
        if (running.value) {
            stop()
        } else {
            start()
        }
    }

    fun start() {
        ProxyBridge.clearError()
        autoOpenPending = false
        val args = config.value.buildCliArgs()
        ProxyService.start(getApplication(), args)
    }

    fun stop() {
        ProxyService.stop(getApplication())
    }

    fun openLink() {
        val link = tgLink.value ?: config.value.expectedTgLink
        val intent = Intent(Intent.ACTION_VIEW, link.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            getApplication<Application>().startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            ProxyBridge.reportMessage(getApplication<Application>().getString(R.string.telegram_missing))
        }
    }

    fun updateAutostart(enabled: Boolean) = repository.updateAutostart(enabled)
    fun updateCfPriority(enabled: Boolean) = repository.updateCfPriority(enabled)
    fun updateCfBalance(enabled: Boolean) = repository.updateCfBalance(enabled)
    fun updateDefaultDomains(enabled: Boolean) = repository.updateDefaultDomains(enabled)
    fun updateCustomDomain(domain: String) = repository.updateCustomDomain(domain)
    fun updatePort(port: Int) {
        repository.updatePort(port)
        if (running.value) {
            ProxyBridge.reportMessage(getApplication<Application>().getString(R.string.port_changed_restart_hint))
        }
    }
    fun updateQuiet(quiet: Boolean) = repository.updateQuiet(quiet)
    fun updateThemeMode(mode: String) = repository.updateThemeMode(mode)

    fun setShowLogsSheet(show: Boolean) {
        _showLogsSheet.value = show
    }

    fun setShowDomainDialog(show: Boolean) {
        _showDomainDialog.value = show
    }

    fun setShowThemeDialog(show: Boolean) {
        _showThemeDialog.value = show
    }

    fun setShowPortDialog(show: Boolean) {
        _showPortDialog.value = show
    }

    fun clearLogs() {
        _logs.clear()
    }

    fun openTelegramChannel() {
        val uri = "https://t.me/F1NDLE_cn".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            getApplication<Application>().startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            ProxyBridge.reportMessage(getApplication<Application>().getString(R.string.telegram_missing))
        }
    }

    fun requestAddQuickSettingsTile() {
        val app = getApplication<Application>()
        ProxyTileService.requestAddTile(app) { supported, added ->
            if (supported) {
                if (added) {
                    ProxyBridge.reportMessage(app.getString(R.string.tile_added_success))
                }
            } else {
                ProxyBridge.reportMessage(app.getString(R.string.tile_manual_add_hint))
            }
        }
    }

    companion object {
        private const val MAX_LOG_LINES = 500
    }
}
