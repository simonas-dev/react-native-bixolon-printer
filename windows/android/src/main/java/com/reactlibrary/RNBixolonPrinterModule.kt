package com.reactlibrary

import android.content.Context
import com.facebook.react.bridge.*
import com.reactlibrary.consts.Alignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RNBixolonPrinterModule(
        private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val appContext: Context
        get() = reactContext.applicationContext

    private var activePrinterSession: BixolonPrinterSession? = null

    override fun getName(): String {
        return "RNBixolonPrinter"
    }

    /**
     * Runs a Bluetooth scan looking for the fist device
     * which name includes [BixolonPrinter.TARGET_DEVICE_NAME]
     *
     * @see [BixolonPrinter.getTargetDevice]
     */
    @ReactMethod
    fun isDeviceAvailable(callback: Callback) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = BixolonPrinter().isDeviceAvailable()
            val args = result.toRnArgs().apply {
                result.getOrNull()?.let { putBoolean("isDeviceAvailable", it) }
            }
            callback.invoke(args)
        }
    }

    /**
     * Manually connect to specified printer.
     *
     * @param logicalName Bluetooth device name e.g. SPP-R410
     * @param address Bluetooth device address e.g. 74:F0:7D:E9:8C:B8
     */
    @ReactMethod
    fun connect(logicalName: String, address: String, callback: Callback) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = BixolonPrinter().open(
                    context = appContext,
                    receiver = null,
                    logicalName = logicalName,
                    address = address
            )
            activePrinterSession = result.getOrNull()
            callback(result.toRnArgs())
        }
    }

    /**
     * Auto-connect with compatible device.
     *
     * @see [BixolonPrinter.getTargetDevice]
     */
    @ReactMethod
    fun autoConnect(callback: Callback) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = BixolonPrinter().autoOpen(
                    context = appContext,
                    receiver = null
            )
            activePrinterSession = result.getOrNull()
            callback.invoke(result.toRnArgs())
        }
    }

    @ReactMethod
    fun printPdf(pdfPath: String, brightness: Int, callback: Callback) {
        val session = activePrinterSession
        if (session == null) {
            callback.invoke(Result.failure<Unit>("Not Connected to a Printer!").toRnArgs())
        } else {
            GlobalScope.launch {
                val result = session.printPdf(
                        filePath = pdfPath,
                        page = 0,
                        alignment = Alignment.Left,
                        brightness = brightness
                )
                callback.invoke(result.toRnArgs())
            }
        }
    }

    @ReactMethod
    fun disconnect(callback: Callback) {
        val session = activePrinterSession
        if (session == null) {
            callback.invoke(Result.failure<Unit>("Not Connected to a Printer!").toRnArgs())
        } else {
            GlobalScope.launch {
                val result = session.close()
                callback.invoke(result.toRnArgs())
            }
        }
    }

    private fun <T> Result<T>.toRnArgs(): WritableMap {
        return Arguments.createMap().apply {
            putBoolean("isSuccess", isSuccess)
            putString("error", exceptionOrNull()?.message)
        }
    }
}