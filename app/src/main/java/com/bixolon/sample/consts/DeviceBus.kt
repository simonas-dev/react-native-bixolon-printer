package com.bixolon.sample.consts

import com.bxl.config.editor.BXLConfigLoader

enum class DeviceBus(val value: Int) {
    Bluetooth(BXLConfigLoader.DEVICE_BUS_BLUETOOTH),
    Ethernet(BXLConfigLoader.DEVICE_BUS_ETHERNET),
    Usb(BXLConfigLoader.DEVICE_BUS_USB),
    Wifi(BXLConfigLoader.DEVICE_BUS_WIFI),
    WifiDirect(BXLConfigLoader.DEVICE_BUS_WIFI_DIRECT),
    BluetoothLE(BXLConfigLoader.DEVICE_BUS_BLUETOOTH_LE)
}