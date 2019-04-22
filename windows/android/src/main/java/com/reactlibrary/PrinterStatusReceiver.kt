package com.reactlibrary

import jpos.events.DirectIOListener
import jpos.events.ErrorListener
import jpos.events.OutputCompleteListener
import jpos.events.StatusUpdateListener

interface PrinterStatusReceiver :
        StatusUpdateListener,
        ErrorListener,
        OutputCompleteListener,
        DirectIOListener