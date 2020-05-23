package com.example.utility_manage.PublicAction

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.utility_manage.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.telpo.tps550.api.printer.UsbThermalPrinter
import java.text.SimpleDateFormat
import java.util.*


class Publicfunction {

    var mUsbThermalPrinter: UsbThermalPrinter? = null
    var handler: MyHandler? = null

    var logo: Bitmap? = null
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



    fun message(msg: String?, context: Activity?) {
        try {
            context!!.runOnUiThread(Runnable {
                Toast.makeText(
                    context, msg,
                    Toast.LENGTH_SHORT
                ).show()
            })
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    fun getDatetimenow(): String {
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateTimeNow = dateTime.format(Date())
        return dateTimeNow
    }

    public fun setOntextchange(
        activity: Activity,
        inputEditText: TextInputEditText,
        layout: TextInputLayout
    ) {
        inputEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("edittext", s.toString())
                Log.d("edittext", count.toString())
                Log.d("edittext", start.toString())
                Log.d("edittext", before.toString())
                if (start == 0 && count == 0) {
                    layout!!.error = activity.getString(R.string.gettexterror)
                } else {
                    layout!!.isErrorEnabled = false
                }

            }
        })

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

                mUsbThermalPrinter!!.setTextSize(25)
                mUsbThermalPrinter!!.setGray(3)
                mUsbThermalPrinter!!.addString(Printheader2)
                mUsbThermalPrinter!!.printString()

                mUsbThermalPrinter!!.reset()
                mUsbThermalPrinter!!.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
                mUsbThermalPrinter!!.setLeftIndent(0)
                mUsbThermalPrinter!!.setLineSpace(1)
                mUsbThermalPrinter!!.setTextSize(25)
                mUsbThermalPrinter!!.setGray(3)
                mUsbThermalPrinter!!.addString(Printcontent)
                mUsbThermalPrinter!!.printString()
                mUsbThermalPrinter!!.walkPaper(10)
//                val intent = Intent(this@resultgetinActivity, SelectActivity::class.java)
//                startActivity(intent)
//                finish()
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

    public inner class MyHandler(activity: Activity) : Handler() {
        val activity = activity

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PublicValues().NOPAPER -> {
                    noPaperDlg(activity)
                    logo = BitmapFactory.decodeResource(activity.getResources(), R.drawable.waclogo)

                    Log.d("printerror", "1")
                }
                PublicValues().LOWBATTERY -> {
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
                    contentPrintThread().start()
                    Log.d("printerror", "3")
                }

                PublicValues().OVERHEAT -> {
                    val overHeatDialog =
                        AlertDialog.Builder(activity)
                    overHeatDialog.setTitle(R.string.operation_result)
                    overHeatDialog.setMessage(R.string.overTemp)
                    overHeatDialog.setPositiveButton(
                        R.string.dialog_comfirm,
                        DialogInterface.OnClickListener { dialogInterface, i -> })
                    overHeatDialog.show()
                    Log.d("printerror", "4")
                }
                PublicValues().CANCELPROMPT -> {
                    Publicfunction().message(
                        "Finished!",
                        activity
                    )
                    Log.d("printerror", "5")
                }
                else -> {
                    Publicfunction().message("Print Error!", activity)
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

    fun statusCheck(context: Context?) {
        val manager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context)
        }
    }

    private fun buildAlertMessageNoGps(context: Context?) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") {
//                    dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    dialog, id ->
                startActivity(context!!, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }
}
