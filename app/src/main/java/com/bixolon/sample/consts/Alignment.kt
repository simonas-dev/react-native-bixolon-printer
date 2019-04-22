package com.bixolon.sample.consts

import jpos.POSPrinterConst

enum class Alignment(val value: Int) {
    Left(POSPrinterConst.PTR_PDF_LEFT),
    Center(POSPrinterConst.PTR_PDF_CENTER),
    Right(POSPrinterConst.PTR_PDF_RIGHT)
}