package com.f1ndle.tgwsproxy

/**
 * JNI boundary proxying to [io.github.valnesfjord.tgwsproxyrs.NativeProxy]
 * matching the symbols in prebuilt libtg_ws_proxy_jni.so.
 */
object NativeProxy {
    fun load() {
        io.github.valnesfjord.tgwsproxyrs.NativeProxy.load()
    }

    @JvmStatic
    fun nativeStart(args: String): String? =
        io.github.valnesfjord.tgwsproxyrs.NativeProxy.nativeStart(args)

    @JvmStatic
    fun nativeStop() {
        io.github.valnesfjord.tgwsproxyrs.NativeProxy.nativeStop()
    }

    @JvmStatic
    fun nativeIsRunning(): Boolean =
        io.github.valnesfjord.tgwsproxyrs.NativeProxy.nativeIsRunning()

    @JvmStatic
    fun nativeTrimMemory() {
        System.gc()
    }

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
