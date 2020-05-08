package com.wac.utility_manage.PublicAction

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.kloadingspin.KLoadingSpin
import com.example.utility_manage.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Publicfunction {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    public var loadingDialog: Dialog? = null
    public var lat: String = ""
    public var long: String = ""
    var mConfig: SlidrConfig? = null

    fun message(msg: String?,type : Int, context: Activity?) {
        try {
            context!!.runOnUiThread(Runnable {
                FancyToast.makeText(context,msg,FancyToast.LENGTH_SHORT,type,false).show()
            })
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
    fun message(msg: String?,type : Int,duration:Int, context: Activity?) {
        try {
            context!!.runOnUiThread(Runnable {
                FancyToast.makeText(context,msg,duration,type,false).show()
            })
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    fun getDatetimenow(): String {
        val dateTime = SimpleDateFormat("dd/MM/yyyy, HH:mm")
        val dateTimeNow = dateTime.format(Date())
        return dateTimeNow
    }
    fun getDatenow(): String {
        val dateTime = SimpleDateFormat("dd/MM/yyyy")
        val dateTimeNow = dateTime.format(Date())
        return dateTimeNow
    }

    public fun setOntextchange(
        activity: Activity,
        inputEditText: TextInputEditText,
        layout: TextInputLayout
    ) {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("edittext_after", s.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("edittext", s.toString())
                Log.d("edittext", count.toString())
                Log.d("edittext", start.toString())
                Log.d("edittext", before.toString())
                if (start == 0 && count == 0) {
                    layout.error = activity.getString(R.string.gettexterror)
                } else {
                    layout.isErrorEnabled = false
                }

            }
        })

    }


    fun buildAlertMessageNoGps(context: Context?) {
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

    fun builddialogloading(activity: Activity) {
        Thread(Runnable {
            activity.runOnUiThread {
                val view: View =
                    LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null)
                loadingDialog = Dialog(activity, R.style.LoadingDialog)
                loadingDialog!!.setContentView(view)
                val a: KLoadingSpin = view.findViewById(R.id.KLoadingSpin)
                a.startAnimation()
                a.setIsVisible(true)
                loadingDialog!!.show()
                val window = loadingDialog!!.window
                window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                val closebtn = view.findViewById<Button>(R.id.closebtn)

                closebtn.setOnClickListener{
                    loadingDialog!!.dismiss()
                }

            }


        }).start()


    }


    @Throws(WriterException::class)
    fun CreateCode(
        str: String?,
        type: BarcodeFormat?,
        bmpWidth: Int,
        bmpHeight: Int
    ): Bitmap? {
        val mHashtable =
            Hashtable<EncodeHintType, String?>()
        mHashtable[EncodeHintType.CHARACTER_SET] = "UTF-8"
        // 生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        val matrix =
            MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable)
        val width = matrix.width
        val height = matrix.height
        // 二维矩阵转为一维像素数组（一直横着排）
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (matrix[x, y]) {
                    pixels[y * width + x] = -0x1000000
                } else {
                    pixels[y * width + x] = -0x1
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    fun obtieneLocalizacion(
        activity: Activity,
        fusedLocationClient: FusedLocationProviderClient
    ) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                5
            )

        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d("locationn", location?.longitude.toString())
                    Log.d("locationn", location?.latitude.toString())
                    lat = location?.latitude.toString()
                    long = location?.longitude.toString()
                    if(lat.equals("null")){
                        lat = ""
                    }
                    if(long.equals("null")){
                    long = ""
                    }
                }
        }

    }


    fun writeToCsv(header: String, data: String,csvname : String, activity: Activity) {
        ////+++++ get timestamp
        var data = data
        var headercsv = header
        var timeStamp =
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())
        val newTimestamp = timeStamp.split(" ").toTypedArray()
        val tmpDate = newTimestamp[0].split("/").toTypedArray()
        val d = Date()
        val c = Calendar.getInstance()
        c.time = d
        var year = c[Calendar.YEAR]
        if (year < 2500) {
            year = year + 543
        }
        timeStamp = tmpDate[0] + "/" + tmpDate[1] + "/" + year + " " + newTimestamp[1]
        ////-------------------
        data = data.replace("เลขประจำตัวประชาชน", "")
        var strCsv = data.replace("\n", ",")
        strCsv = strCsv


        try {
            val fileCsv = File(PublicValues().sd, csvname)
            if (!fileCsv.exists()) {
            } else {
                headercsv = ""
            }
            val writerCsv = FileWriter(fileCsv, true) //True = Append to file, false = Overwrite
            writerCsv.append(
                """$headercsv$strCsv

                    """.trimIndent()
            )
            writerCsv.close()
            message("Connecting error, Data is saved to CSV",FancyToast.INFO, activity)
        } catch (e1: IOException) {
            e1.printStackTrace()
            message(e1.toString(),FancyToast.ERROR, activity)
        }
    }

    fun Slideleft(activity: Activity) {
        mConfig = SlidrConfig.Builder() //                .primaryColor(primary)
            .position(SlidrPosition.LEFT)
            .velocityThreshold(2400f) //                .distanceThreshold(.25f)
            .build()

        Slidr.attach(activity, mConfig!!)
    }


}
