package com.wac.utility_manage.PublicAction

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kloadingspin.KLoadingSpin
import com.example.utility_manage.R
import com.google.zxing.BarcodeFormat
import com.shashank.sony.fancytoastlib.FancyToast
import com.telpo.tps550.api.printer.UsbThermalPrinter

class Printfuntion {
    var mUsbThermalPrinter: UsbThermalPrinter? = null
    var handler: MyHandler? = null

    public var loadingDialog : Dialog? = null

    var logo: Bitmap? = null
//    var QRCode: Bitmap? = null
    var ref2: String? = null
    private var Result: String = ""
    private var nopaper = false

    var Printheader: String = ""
        get() = field
        set(value) {
            field = value
        }
    var Printheader2: String = ""
        get() = field
        set(value) {
            field = value
        }
    var Printcontent: String = ""
        get() = field
        set(value) {
            field = value
        }

    public inner class contentPrintThread : Thread() {
        override fun run() {
            super.run()
            try {

                mUsbThermalPrinter!!.reset()
                mUsbThermalPrinter!!.setGray(3)
                mUsbThermalPrinter!!.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
                if (logo != null) {
                    mUsbThermalPrinter!!.printLogo(scaleDown(logo!!, 200F, false), false)
                }

                mUsbThermalPrinter!!.reset()
                mUsbThermalPrinter!!.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
                mUsbThermalPrinter!!.setLeftIndent(0)
                mUsbThermalPrinter!!.setLineSpace(1)
//                wordFont = 1
//                if (wordFont == 4) {
//                    mUsbThermalPrinter.setFontSize(55)
//                } else if (wordFont == 3) {
//                    mUsbThermalPrinter.setTextSize(45)
//                } else if (wordFont == 2) {
//                    mUsbThermalPrinter.setTextSize(35)
//                } else if (wordFont == 1) {
//                    mUsbThermalPrinter.setTextSize(25)
//                }
                mUsbThermalPrinter!!.setTextSize(35)
                mUsbThermalPrinter!!.setGray(3)
                mUsbThermalPrinter!!.addString(Printheader)
                mUsbThermalPrinter!!.printString()

                if(!Printheader2.isEmpty() && Printheader2 != null){
                    mUsbThermalPrinter!!.setTextSize(25)
                    mUsbThermalPrinter!!.setGray(3)
                    mUsbThermalPrinter!!.addString(Printheader2)
                    mUsbThermalPrinter!!.printString()
                }

                mUsbThermalPrinter!!.reset()
                mUsbThermalPrinter!!.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
                mUsbThermalPrinter!!.setLeftIndent(0)
                mUsbThermalPrinter!!.setLineSpace(1)
                mUsbThermalPrinter!!.setTextSize(25)
                mUsbThermalPrinter!!.setGray(3)
                mUsbThermalPrinter!!.addString(Printcontent)
                mUsbThermalPrinter!!.printString()

                mUsbThermalPrinter!!.reset()
                mUsbThermalPrinter!!.setGray(3)
                mUsbThermalPrinter!!.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
                if (ref2 != null) {
                    val BitmapQRCode = Publicfunction().CreateCode(ref2, BarcodeFormat.CODE_128, 320, 100)
                    mUsbThermalPrinter!!.printLogo(BitmapQRCode, false)
                    mUsbThermalPrinter!!.setTextSize(25)
                    mUsbThermalPrinter!!.setGray(3)
                    mUsbThermalPrinter!!.addString(ref2)
                    mUsbThermalPrinter!!.printString()
                }

                mUsbThermalPrinter!!.reset()
                mUsbThermalPrinter!!.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
                mUsbThermalPrinter!!.setLeftIndent(0)
                mUsbThermalPrinter!!.setLineSpace(1)

                mUsbThermalPrinter!!.walkPaper(10)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Result = e.toString()
                Log.d("printerror", e.toString())
                if (Result == "com.telpo.tps550.api.printer.NoPaperException") {
                    nopaper = true
                } else if (Result == "com.telpo.tps550.api.printer.OverHeatException") {
                    handler!!.sendMessage(
                        handler!!.obtainMessage(
                            PublicValues().OVERHEAT, 1, 0, null
                        )
                    )
                } else {
                    handler!!.sendMessage(
                        handler!!.obtainMessage(PublicValues().PRINTERR, 1, 0, null)
                    )
                }
            } finally {
                handler!!.sendMessage(
                    handler!!.obtainMessage(PublicValues().CANCELPROMPT, 1, 0, null)
                )
                if (nopaper) {
                    handler!!.sendMessage(
                        handler!!.obtainMessage(PublicValues().NOPAPER, 1, 0, null)
                    )
                    nopaper = false
                    return
                }
            }
        }
    }

    public inner class MyHandler(private val activity: Activity) : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PublicValues().NOPAPER -> {
                    noPaperDlg(activity)
                    loadingDialog!!.dismiss()

                    Log.d("printerror", "1")
                }
                PublicValues().LOWBATTERY -> {
                    loadingDialog!!.dismiss()
                    val alertDialog =
                        AlertDialog.Builder(activity)
                    alertDialog.setTitle(R.string.operation_result)
                    alertDialog.setMessage(R.string.LowBattery)
                    alertDialog.setPositiveButton(
                        R.string.dialog_comfirm,
                        DialogInterface.OnClickListener { dialogInterface, i -> })
                    alertDialog.show()
                    Log.d("printerror", "2")
                }
                PublicValues().PRINTCONTENT -> {
                    logo = BitmapFactory.decodeResource(
                        activity.getResources(),
                        R.drawable.waclogo
                    )

                    builddialogloading(activity)

                    contentPrintThread().start()
                    Log.d("printerror", "3")
                }

                PublicValues().OVERHEAT -> {
                    Log.d("printerror", "4")
                    val overHeatDialog =
                        AlertDialog.Builder(activity)
                    overHeatDialog.setTitle(R.string.operation_result)
                    overHeatDialog.setMessage(R.string.overTemp)
                    overHeatDialog.setPositiveButton(
                        R.string.dialog_comfirm,
                        DialogInterface.OnClickListener { dialogInterface, i -> })
                    overHeatDialog.show()

                }
                PublicValues().CANCELPROMPT -> {
                    Log.d("printerror", "5")
                    loadingDialog!!.dismiss()
                    activity.finish()

                }
                else -> {
                    Publicfunction()
                        .message("Print Error!", FancyToast.ERROR,activity)
                    loadingDialog!!.dismiss()
                    Log.d("printerror", "6")
                }

            }
        }
    }


    fun noPaperDlg(context: Activity?) {
        val dlg =
            AlertDialog.Builder(context)
        dlg.setTitle(R.string.noPaper)
        dlg.setMessage(R.string.noPaperNotice)
        dlg.setCancelable(false)
        dlg.setPositiveButton(
            R.string.sure
        ) { dialogInterface, i -> }
        dlg.show()
    }

    fun scaleDown(
        realImage: Bitmap, maxImageSize: Float,
        filter: Boolean
    ): Bitmap? {
        val ratio = Math.min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height
        )
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)
        return Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
    }

    fun builddialogloading(activity: Activity){
        val view: View = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null)
        loadingDialog = Dialog(activity,R.style.LoadingDialog)
        loadingDialog!!.setContentView(view)
        val a: KLoadingSpin = view.findViewById(R.id.KLoadingSpin)
        a.startAnimation()
        a.setIsVisible(true)
        loadingDialog!!.show()
        val window = loadingDialog!!.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }


}