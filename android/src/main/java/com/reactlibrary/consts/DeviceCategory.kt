package com.reactlibrary.consts

import com.bxl.config.editor.BXLConfigLoader

enum class DeviceCategory(val value: Int) {
    CASH_DRAWER(BXLConfigLoader.DEVICE_CATEGORY_CASH_DRAWER),
    MSR(BXLConfigLoader.DEVICE_CATEGORY_MSR),
    POS_PRINTER(BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER),
    SMART_CARD_RW(BXLConfigLoader.DEVICE_CATEGORY_SMART_CARD_RW),
}