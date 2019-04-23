package com.reactlibrary

import com.reactlibrary.consts.Alignment
import jpos.POSPrinter
import jpos.POSPrinterConst

class BixolonPrinterSession(
        private val posPrinter: POSPrinter
) {

    /**
     * Close Printer Session
     */
    suspend fun close(): Result<Unit> {
        try {
            if (posPrinter.claimed) {
                posPrinter.deviceEnabled = false
                posPrinter.close()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.failure(e)
        }

        return Result.success(Unit)
    }

    /**
     * @param filePath the absolute path to the pdf file.
     * @param page the page number of the PDF to be printed.
     * @param alignment select an enum value.
     * @param brightness the brightness value(0..100). Note that 100 will print the whole page black.
     */
    suspend fun printPdf(
            filePath: String,
            page: Int = 0,
            alignment: Alignment = Alignment.Left,
            brightness: Int = 50
    ): Result<Unit> {
        try {
            if (!posPrinter.deviceEnabled) {
                return Result.failure("Device is Disabled!")
            }
            val width: Int = posPrinter.recLineWidth
            val station: Int = POSPrinterConst.PTR_S_RECEIPT
            posPrinter.printPDF(station, filePath, width, alignment.value, page, brightness)
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.failure(e)
        }
        return Result.success(Unit)
    }
}