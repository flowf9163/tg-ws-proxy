package io.github.valnesfjord.tgwsproxyrs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.SecureRandom

data class ProxyConfigData(
    val autostart: Boolean = false,
    val cfPriority: Boolean = true,
    val cfBalance: Boolean = false,
    val defaultDomains: Boolean = true,
    val customDomain: String = "",
    val port: Int = 1443,
    val secret: String = "",
    val quiet: Boolean = true,
) {
    fun buildCliArgs(): String {
        val parts = mutableListOf<String>()
        parts.add("--host")
        parts.add("127.0.0.1")
        parts.add("--link-ip")
        parts.add("127.0.0.1")
        parts.add("--port")
        parts.add(port.toString())

        if (secret.isNotBlank()) {
            parts.add("--secret")
            parts.add(secret)
        }

        if (customDomain.isNotBlank()) {
            parts.add("--cf-domain")
            parts.add(customDomain.trim())
        }

        if (defaultDomains) {
            parts.add("--default-domains")
        }

        if (cfPriority) {
            parts.add("--cf-priority")
        }

        if (cfBalance) {
            parts.add("--cf-balance")
        }

        if (quiet) {
            parts.add("--quiet")
        }

        return parts.joinToString(" ")
    }

    val expectedTgLink: String
        get() = "tg://proxy?server=127.0.0.1&port=$port&secret=$secret"
}

class ProxySettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<ProxyConfigData> = _config.asStateFlow()

    private fun loadConfig(): ProxyConfigData {
        var secret = prefs.getString(KEY_SECRET, null)
        if (secret.isNullOrBlank()) {
            secret = generateSecret()
            prefs.edit { putString(KEY_SECRET, secret) }
        }

        return ProxyConfigData(
            autostart = prefs.getBoolean(KEY_AUTOSTART, false),
            cfPriority = prefs.getBoolean(KEY_CF_PRIORITY, true),
            cfBalance = prefs.getBoolean(KEY_CF_BALANCE, false),
            defaultDomains = prefs.getBoolean(KEY_DEFAULT_DOMAINS, true),
            customDomain = prefs.getString(KEY_CUSTOM_DOMAIN, "") ?: "",
            port = prefs.getInt(KEY_PORT, 1443),
            secret = secret,
            quiet = prefs.getBoolean(KEY_QUIET, true),
        )
    }

    fun updateAutostart(value: Boolean) {
        prefs.edit { putBoolean(KEY_AUTOSTART, value) }
        _config.value = _config.value.copy(autostart = value)
    }

    fun updateCfPriority(value: Boolean) {
        prefs.edit { putBoolean(KEY_CF_PRIORITY, value) }
        _config.value = _config.value.copy(cfPriority = value)
    }

    fun updateCfBalance(value: Boolean) {
        prefs.edit { putBoolean(KEY_CF_BALANCE, value) }
        _config.value = _config.value.copy(cfBalance = value)
    }

    fun updateDefaultDomains(value: Boolean) {
        prefs.edit { putBoolean(KEY_DEFAULT_DOMAINS, value) }
        _config.value = _config.value.copy(defaultDomains = value)
    }

    fun updateCustomDomain(value: String) {
        prefs.edit { putString(KEY_CUSTOM_DOMAIN, value) }
        _config.value = _config.value.copy(customDomain = value)
    }

    fun updatePort(value: Int) {
        prefs.edit { putInt(KEY_PORT, value) }
        _config.value = _config.value.copy(port = value)
    }

    fun updateQuiet(value: Boolean) {
        prefs.edit { putBoolean(KEY_QUIET, value) }
        _config.value = _config.value.copy(quiet = value)
    }

    companion object {
        const val PREFS_NAME = "tg_ws_proxy_settings"
        const val KEY_AUTOSTART = "autostart"
        const val KEY_CF_PRIORITY = "cf_priority"
        const val KEY_CF_BALANCE = "cf_balance"
        const val KEY_DEFAULT_DOMAINS = "default_domains"
        const val KEY_CUSTOM_DOMAIN = "custom_domain"
        const val KEY_PORT = "port"
        const val KEY_SECRET = "secret"
        const val KEY_QUIET = "quiet"

        fun generateSecret(): String {
            val bytes = ByteArray(16)
            SecureRandom().nextBytes(bytes)
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun getSavedArgs(context: Context): String {
            val repo = ProxySettingsRepository(context)
            return repo.config.value.buildCliArgs()
        }

        fun isAutostartEnabled(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_AUTOSTART, false)
        }
    }
}
