package com.f1ndle.tgwsproxy

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class ProxyTileService : TileService() {
    private var scope: CoroutineScope? = null
    private var stateJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        NativeProxy.load()
    }

    override fun onStartListening() {
        super.onStartListening()
        scope?.cancel()
        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        scope = newScope

        ProxyBridge.syncFromNative()
        updateTileState(ProxyBridge.running.value)

        stateJob = newScope.launch {
            ProxyBridge.running.collectLatest { running ->
                updateTileState(running)
            }
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        scope?.cancel()
        scope = null
    }

    override fun onClick() {
        super.onClick()
        ProxyBridge.syncFromNative()
        val isRunning = ProxyBridge.running.value
        if (isRunning) {
            ProxyService.stop(this)
            updateTileState(false)
        } else {
            val args = ProxySettingsRepository.getSavedArgs(this)
            ProxyService.start(this, args)
            updateTileState(true)
        }
    }

    private fun updateTileState(running: Boolean) {
        val tile = qsTile ?: return
        tile.state = if (running) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.ic_notification)
        tile.label = getString(R.string.tile_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (running) {
                getString(R.string.tile_active)
            } else {
                getString(R.string.tile_inactive)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tile.stateDescription = if (running) {
                getString(R.string.tile_active)
            } else {
                getString(R.string.tile_inactive)
            }
        }
        tile.contentDescription = if (running) {
            getString(R.string.press_to_stop)
        } else {
            getString(R.string.press_to_start)
        }
        tile.updateTile()
    }

    companion object {
        fun requestUpdate(context: Context) {
            try {
                requestListeningState(
                    context,
                    ComponentName(context, ProxyTileService::class.java),
                )
            } catch (_: Exception) {
            }
        }

        fun requestAddTile(context: Context, onResult: (Boolean, Boolean) -> Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                try {
                    val sbm = context.getSystemService(StatusBarManager::class.java)
                    sbm.requestAddTileService(
                        ComponentName(context, ProxyTileService::class.java),
                        context.getString(R.string.tile_name),
                        Icon.createWithResource(context, R.drawable.ic_notification),
                        Executor { it.run() },
                    ) { resultCode ->
                        val success = resultCode == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ||
                                resultCode == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED
                        onResult(true, success)
                    }
                } catch (_: Exception) {
                    onResult(false, false)
                }
            } else {
                onResult(false, false)
            }
        }
    }
}
