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

    // OTA Updates
    val currentVersion: String = UpdateManager.getCurrentVersion(application)

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

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
        // Check for updates on startup (silently)
        checkForUpdates(manual = false)
    }

    fun checkForUpdates(manual: Boolean) {
        viewModelScope.launch {
            if (manual) {
                _isCheckingUpdate.value = true
                _updateMessage.value = null
            }
            val info = UpdateManager.checkUpdate(getApplication())
            _updateInfo.value = info
            if (manual) {
                _isCheckingUpdate.value = false
                if (info != null) {
                    _showUpdateDialog.value = true
                } else {
                    _updateMessage.value = getApplication<Application>().getString(R.string.up_to_date)
                }
            }
        }
    }

    fun dismissUpdateDialog() {
        _showUpdateDialog.value = false
    }

    fun openUpdateUrl() {
        val info = _updateInfo.value ?: return
        UpdateManager.openUrl(getApplication(), info.downloadUrl)
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
    fun updatePort(port: Int) = repository.updatePort(port)
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

    fun clearLogs() {
        _logs.clear()
    }

    fun openRepo() {
        val uri = "https://github.com/f1ndles/tg-ws-proxy/".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            getApplication<Application>().startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            ProxyBridge.reportMessage(getApplication<Application>().getString(R.string.browser_missing))
        }
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

    companion object {
        private const val MAX_LOG_LINES = 500
    }
}
