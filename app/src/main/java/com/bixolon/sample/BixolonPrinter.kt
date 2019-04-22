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

    /**
     * Runs a Bluetooth scan looking for a device which name includes string [TARGET_DEVICE_NAME].
     *
     * If multiple devices are found the first in the list will be selected for the process.
     *
     */
    private fun getTargetDevice(): BluetoothDevice? {
        return BluetoothAdapter.getDefaultAdapter()
                .bondedDevices
                .find { it.name.contains(TARGET_DEVICE_NAME.value) }
    }


    suspend fun isDeviceAvailable(): Boolean {
        return getTargetDevice() != null
    }

    /**
     * Tries to open a session with [getTargetDevice].
     *
     * @param receiver Logs various information about [POSPrinter] status. Isn't required!
     */
    fun autoOpen(
            context: Context,
            receiver: PrinterStatusReceiver?,
            callback: (Result<BixolonPrinterSession>) -> Unit
    ) {
       GlobalScope.launch(Dispatchers.IO) {
           callback(autoOpen(context, receiver))
       }
    }


    suspend fun autoOpen(
            context: Context,
            receiver: PrinterStatusReceiver?
    ): Result<BixolonPrinterSession> {
        val device = getTargetDevice()
        return if (device != null) {
            open(context, receiver, device.name, device.address)
        } else {
            Result.failure("Couldn't find paired device with the name: ${TARGET_DEVICE_NAME.value}")
        }
    }

    /**
     * Open Printer Session
     *
     * It Initiates the use of printer class and includes initialization operations such as memory
     * allocation. It must be first performed to call a Method above Claim. Devices not saved via
     * the BXLConfigLoader Class will not be opened.
     *
     * @param receiver Logs various information about [POSPrinter] status. Isn't required!
     * @param logicalName Bluetooth device name e.g. SPP-R410
     * @param address Bluetooth device address e.g. 74:F0:7D:E9:8C:B8
     */
    fun open(
            context: Context,
            receiver: PrinterStatusReceiver?,
            logicalName: String,
            address: String,
            callback: (Result<BixolonPrinterSession>) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            callback(open(context, receiver, logicalName, address))
        }
    }

    suspend fun open(
            context: Context,
            receiver: PrinterStatusReceiver?,
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

    /**
     * This is a class for POS printer control. Using this class, operations can be performed such
     * as connecting/disconnecting printer and executing print jobs. It generates a JposException
     * when an error occurs while performing a specific function.
     */
    private fun createPosPrinter(
            context: Context,
            printerStatusReceiver: PrinterStatusReceiver?
    ): Result<POSPrinter> {
        return try {
            Result.success(POSPrinter(context).apply {
                printerStatusReceiver?.let {
                    addStatusUpdateListener(it)
                    addErrorListener(it)
                    addOutputCompleteListener(it)
                    addDirectIOListener(it)
                }
            })
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    /**
     * This is a class to save device setting information to be connected. The setting information
     * manages device information through the BXLConfigLoader Class. The setting information
     * includes the device name, product name, interface, etc., and if the information is not saved
     * normally, the device cannot be connected. Before calling the Open function, this class must
     * be called to save the setting information.
     */
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