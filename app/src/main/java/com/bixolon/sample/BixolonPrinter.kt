package com.bixolon.sample

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.bixolon.sample.consts.DeviceBus
import com.bixolon.sample.consts.DeviceCategory
import com.bixolon.sample.consts.DeviceName
import com.bxl.config.editor.BXLConfigLoader
import jpos.JposException
import jpos.POSPrinter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BixolonPrinter {

    fun autoOpen(
            context: Context,
            receiver: PrinterStatusReceiver,
            callback: (Result<BixolonPrinterSession>) -> Unit
    ) {
       GlobalScope.launch(Dispatchers.IO) {
           callback(autoOpen(context, receiver))
       }
    }

    suspend fun autoOpen(
            context: Context,
            receiver: PrinterStatusReceiver
    ): Result<BixolonPrinterSession> {

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val device: BluetoothDevice? = bluetoothAdapter.bondedDevices
                .find { it.name.contains(TARGET_DEVICE_NAME.value) }
        return if (device != null) {
            open(context, receiver, device.name, device.address)
        } else {
            Result.failure("Couldn't find paired device with the name: ${TARGET_DEVICE_NAME.value}")
        }
    }

    fun open(
            context: Context,
            receiver: PrinterStatusReceiver,
            logicalName: String,
            address: String,
            callback: (Result<BixolonPrinterSession>) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            callback(open(context, receiver, logicalName, address))
        }
    }

    /**
     * Open Printer Session
     *
     * @param logicalName Bluetooth Device Name e.g. SPP-R410
     * @param address Bluetooth Device Address e.g. 74:F0:7D:E9:8C:B8
     */
    suspend fun open(
            context: Context,
            receiver: PrinterStatusReceiver,
            logicalName: String,
            address: String
    ): Result<BixolonPrinterSession> {
        val posPrinter = createPosPrinter(
                context,
                receiver
        ).getOrElse {
            return Result.failure(it)
        }

        createBxlConfigLoader(
                context,
                TARGET_DEVICE_NAME,
                TARGET_DEVICE_CATEGORY,
                TARGET_DEVICE_BUS,
                logicalName,
                address
        ).getOrElse {
            return Result.failure(it)
        }

        try {
            posPrinter.open(logicalName)
            posPrinter.claim(5000)
            posPrinter.deviceEnabled = true
            posPrinter.asyncMode = true
        } catch (e: JposException) {
            e.printStackTrace()
            try {
                posPrinter.close()
            } catch (e1: JposException) {
                e1.printStackTrace()
            }

            return Result.failure(e)
        }
        return Result.success(BixolonPrinterSession(posPrinter))
    }

    private fun createPosPrinter(
            context: Context,
            printerStatusReceiver: PrinterStatusReceiver
    ): Result<POSPrinter> {
        return try {
            Result.success(POSPrinter(context).apply {
                addStatusUpdateListener(printerStatusReceiver)
                addErrorListener(printerStatusReceiver)
                addOutputCompleteListener(printerStatusReceiver)
                addDirectIOListener(printerStatusReceiver)
            })
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private fun createBxlConfigLoader(
            context: Context,
            deviceName: DeviceName,
            deviceCategory: DeviceCategory,
            deviceBus: DeviceBus,
            logicalName: String,
            address: String
    ): Result<BXLConfigLoader> {
        return try {
            val configLoader = BXLConfigLoader(context)
            configLoader.entries
                    .filter { it.logicalName == logicalName }
                    .forEach {
                        configLoader.removeEntry(it.logicalName)
                    }

            configLoader.addEntry(
                    logicalName,
                    deviceCategory.value,
                    deviceName.value,
                    deviceBus.value,
                    address
            )

            configLoader.saveFile()
            Result.success(configLoader)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    companion object {
        private val TARGET_DEVICE_NAME = DeviceName.SPP_R410
        private val TARGET_DEVICE_CATEGORY = DeviceCategory.POS_PRINTER
        private val TARGET_DEVICE_BUS = DeviceBus.Bluetooth
    }
}