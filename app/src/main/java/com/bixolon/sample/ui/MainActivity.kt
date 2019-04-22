package com.bixolon.sample.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.bixolon.sample.BixolonPrinter
import com.bixolon.sample.BixolonPrinterSession
import com.bixolon.sample.PrinterStatusReceiver
import com.bixolon.sample.R
import jpos.events.DirectIOEvent
import jpos.events.ErrorEvent
import jpos.events.OutputCompleteEvent
import jpos.events.StatusUpdateEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity(), PrinterStatusReceiver {

    var session: BixolonPrinterSession? = null
    val searcher = BixolonPrinter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout.addView(Button(this).apply {
            text = "Connect"
            setOnClickListener {
                searcher.autoOpen(context, this@MainActivity) {
                    session = it.getOrElse {
                        log("ERROR: $it")
                        null
                    }
                    log("Connect Done!")
                }
            }
        })

        val filePathList = listOf(
                "/storage/self/primary/Download/sample_pdf.pdf",
                "/storage/self/primary/Download/nr2-A-v0.1.pdf",
                "/storage/self/primary/Download/nr2-B-v0.1.pdf",
                "/storage/self/primary/Download/nr2-R-v0.1.pdf"
        )

        filePathList.map { File(it) }.forEach { file ->
            mainLayout.addView(Button(this).apply {
                text = "Print Sample PDF ${file.name}"
                setOnClickListener {
                    session?.printPdf(file.absolutePath) {
                        it.getOrElse { log("ERROR: $it") }
                        log("Print Done!")
                    }
                }
            })
        }

        mainLayout.addView(Button(this).apply {
            text = "Disconnect"
            setOnClickListener {
                session!!.close {
                    it.getOrElse { log("ERROR: $it") }
                    log("Disconnect Done!")
                }
            }
        })

    }

    override fun errorOccurred(p0: ErrorEvent?) {
        log(p0.toString())
    }

    override fun outputCompleteOccurred(p0: OutputCompleteEvent?) {
        log(p0.toString())
    }

    override fun directIOOccurred(p0: DirectIOEvent?) {
        log(p0.toString())
    }

    override fun statusUpdateOccurred(p0: StatusUpdateEvent?) {
        log(p0.toString())
    }

    private fun log(msg: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
