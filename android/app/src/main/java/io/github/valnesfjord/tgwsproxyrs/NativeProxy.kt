package io.github.valnesfjord.tgwsproxyrs

import com.f1ndle.tgwsproxy.ProxyBridge

/**
 * JNI boundary matching prebuilt libtg_ws_proxy_jni.so symbol table.
 *
 * The native side calls [onNativeLog], [onNativeListening], [onNativeError]
 * and [onNativeStopped] on whichever thread the Tokio runtime happens to be
 * on; [ProxyBridge] hops to the main thread for UI observers.
 */
object NativeProxy {
    private var loaded = false

    fun load() {
        if (!loaded) {
            System.loadLibrary("tg_ws_proxy_jni")
            loaded = true
        }
    }

    @JvmStatic
    external fun nativeStart(args: String): String?

    @JvmStatic
    external fun nativeStop()

    @JvmStatic
    external fun nativeIsRunning(): Boolean

    @JvmStatic
    fun onNativeLog(line: String) {
        ProxyBridge.onLog(line)
    }

    @JvmStatic
    fun onNativeListening(link: String) {
        ProxyBridge.onListening(link)
    }

    @JvmStatic
    fun onNativeError(message: String) {
        ProxyBridge.reportError(message)
    }

    @JvmStatic
    fun onNativeStopped() {
        ProxyBridge.setRunning(false)
    }
}
