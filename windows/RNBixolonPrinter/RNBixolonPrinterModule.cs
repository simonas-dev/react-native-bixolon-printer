using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Bixolon.Printer.RNBixolonPrinter
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNBixolonPrinterModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNBixolonPrinterModule"/>.
        /// </summary>
        internal RNBixolonPrinterModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNBixolonPrinter";
            }
        }
    }
}
