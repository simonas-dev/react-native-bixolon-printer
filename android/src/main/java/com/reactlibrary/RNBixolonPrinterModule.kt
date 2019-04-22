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
     * Prequesties:
     * - Pair the device using Android Bluetooth settings.
     *
     * @see [BixolonPrinter.getTargetDevice]
     * @param callback { isSuccess: Boolean, error: String, isDeviceAvailable: Boolean }
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
     * Prequesties:
     * - Pair the device using Android Bluetooth settings.
     *
     * Always close down the connection before reconnecting to the device again.
     *
     * @param logicalName Bluetooth device name e.g. SPP-R410
     * @param address Bluetooth device address e.g. 74:F0:7D:E9:8C:B8
     * @param callback { isSuccess: Boolean, error: String }
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
     * Prequesties:
     * - Pair the device using Android Bluetooth settings.
     *
     * Always close down the connection before reconnecting to the device again.
     *
     * @see [BixolonPrinter.getTargetDevice]
     * @param callback { isSuccess: Boolean, error: String }
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

    /**
     * Prints the first page of the PDF file.
     *
     * @param pdfPath full path to the pdf file on the Android device.
     * @param brightness value from 0 to 100. Default is 50.
     * @param callback { isSuccess: Boolean, error: String }
     */
    @ReactMethod
    fun printPdf(pdfPath: String, brightness: Int, callback: Callback) {
        val session = activePrinterSession
        if (session == null) {
            callback.invoke(Result.failure<Unit>("Not Connected to a Printer!").toRnArgs())
        } else {
            GlobalScope.launch {
                val result = session.printPdf(
                        filePath = pdfPath,
                        // Only the first page will be printed, thus PDF file with more than one page
                        // aren't supported.
                        //
                        // Native library doesn't expose a official way to print all pdf pages, but
                        // it does provide a method with range params(fromPage, toPage). It could be
                        // posible when giving starting page of 0 and end page of Int.MAX it would
                        // print the whole document, but it's equalily posible that it will print
                        // a few bilion pages.
                        page = 0,
                        alignment = Alignment.Left,
                        brightness = brightness
                )
                callback.invoke(result.toRnArgs())
            }
        }
    }

    /**
     * Close down the printer session.
     *
     * @param callback { isSuccess: Boolean, error: String }
     */
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
