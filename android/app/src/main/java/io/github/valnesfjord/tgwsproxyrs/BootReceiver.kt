package io.github.valnesfjord.tgwsproxyrs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (ProxySettingsRepository.isAutostartEnabled(context)) {
                val args = ProxySettingsRepository.getSavedArgs(context)
                ProxyService.start(context, args)
            }
        }
    }
}
