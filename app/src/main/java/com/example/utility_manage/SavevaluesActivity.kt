package com.example.utility_manage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.queuedemo_kotlin.Retrofit.callApi
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.example.utility_manage.Retrofit.Data.addInvoiceData
import com.example.utility_manage.Retrofit.Data.findMeterWaterData
import com.example.utility_manage.Retrofit.Jsobj
import com.example.utility_manage.Retrofit.retrofitUpload
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.telpo.tps550.api.printer.UsbThermalPrinter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SavevaluesActivity : AppCompatActivity(), View.OnClickListener {
    private var pictureImagePath = ""
    private val Image_Capture_Code2 = 2

    private var homeid: String = ""
    private var meterid: String = ""
    private var buildingtype: String = ""
    private var name: String = ""
    private var telnum: String = ""
    private var oldwatermeter: String = ""
    private var newwatermeter: String = ""
    private var type: String = ""
    private var amount: String = ""
    private var lat: String = ""
    private var long: String = ""
    private var startinvoice: String = ""
    private var endinvoice: String = ""
    var imageFinance: MultipartBody.Part? = null

    private var homeidinput: TextView? = null
    private var meteridinput: TextView? = null
    private var buildingtypeinput: TextView? = null
    private var nameinput: TextView? = null
    private var telnuminput: TextView? = null
    private var oldwatermeterinput: TextView? = null

    private var cambtn: ImageButton? = null
    private var imgcam: ImageView? = null
    private var gpsInput: TextInputEditText? = null
    private var gpslayout: TextInputLayout? = null
    private var valuesidinput: TextInputEditText? = null
    private var valuesidlayout: TextInputLayout? = null
    private var typepayidinput: TextInputEditText? = null
    private var typepayidlayout: TextInputLayout? = null
    private var amountinput: TextInputEditText? = null
    private var amountlayout: TextInputLayout? = null

    var setdaterangeexport: LinearLayout? = null
    private var submitbtn: Button? = null
    private var printinvoice: Button? = null
    var timestartinput: TextView? = null
    var timeendinput: TextView? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 1
    private val IMAGE_DIRECTORY_NAME = "WAC"
    private lateinit var mCurrentPhotoPath: String

    private lateinit var pubF: Publicfunction
    private var dataInvoice = addInvoiceData()
    private var findMeterData = findMeterWaterData()
    private lateinit var retrofitUpload: retrofitUpload
    private var choosePhotoHelper: ChoosePhotoHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savevalues)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createFindIdDialog()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        setUI()
    }


    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {
            R.id.opencambtn -> {
//                openBackCamera()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    captureImage()
                } else {
                    captureImage2()

                }
            }
            R.id.gps_text_input -> {
                obtieneLocalizacion(this, fusedLocationClient, gpsInput)

            }
            R.id.submitbtn -> {
                if (checkisempty()) {

                    Postinvoice()

                }
            }
            R.id.printinvoicebtn -> {
                if (checkisempty()) {
                    createdialog("ใบแจ้งค่าน้ำประปา", "การประปาส่วนภูมิภาค", "พิมพ์ใบแจ้งหนี้")

                }
            }
            R.id.setdaterangeexport -> {
                Setdaterange()
            }
        }
    }

    private fun Postinvoice() {
        val gpslocation = Jsobj()
        gpslocation.latitude = lat
        gpslocation.longitude = long

        dataInvoice.ref_1 = homeid
        dataInvoice.ref_2 = ""
        dataInvoice.setmeterID(meterid)
        dataInvoice.setwaterMeter(newwatermeter)
        dataInvoice.category = type
        dataInvoice.amount = amount
        dataInvoice.setstartDate(startinvoice)
        dataInvoice.setdueDate(endinvoice)
        dataInvoice.setgps(gpslocation)
        dataInvoice.setpayment("ค้างชำระ")

        retrofitUpload.addinvoice(this, dataInvoice,imageFinance)
    }

    private fun createdialog(strheader1:String,strheader2: String,print:String) {
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null)

        builder.setView(view)
        builder.setCancelable(false)
        val alert = builder.create()

        val header = view.findViewById<TextView>(R.id.header)
        header.text = strheader1
        val header2 = view.findViewById<TextView>(R.id.header2)
        header2.text = strheader2
        val dialoghomeid = view.findViewById<TextView>(R.id.dialog_homeid)
        dialoghomeid.text = "${getString(R.string.homeid)} : $homeid"
        val dialogmeterid = view.findViewById<TextView>(R.id.dialog_meter)
        dialogmeterid.text = "${getString(R.string.meterid)} : $meterid"
        val dialogoldwatermeter = view.findViewById<TextView>(R.id.dialog_oldwatermeter)
        dialogoldwatermeter.text = "${getString(R.string.oldwatermeter)} : $oldwatermeter"
        val dialognewwatermeter = view.findViewById<TextView>(R.id.dialog_newwatermeter)
        dialognewwatermeter.text = "${getString(R.string.newwatermeter)} : $newwatermeter"
        val dialogamount = view.findViewById<TextView>(R.id.dialog_amount)
        dialogamount.text = "${getString(R.string.amount)} : $amount บาท"
        val closebtn = view.findViewById<ImageButton>(R.id.dialog_closebtn)
        closebtn.setOnClickListener {
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
        }
        val printbtn = view.findViewById<Button>(R.id.printbtn)
        printbtn.setText(print)
        printbtn.setOnClickListener {

            pubF.handler = pubF.MyHandler(this)
            pubF.mUsbThermalPrinter = UsbThermalPrinter(this)

            pubF.Printheader = "ใบแจ้งค่าน้ำประปา"
            pubF.Printcontent = "\nที่อยู่ : $homeid" +
                    "\nเลขมาตรครั้งก่อน : $oldwatermeter" +
                    "\nเลขมาตรครั้งนี้ : $newwatermeter" +
                    "\nเลขจากมิเตอร์ : $meterid" +
                    "\nรวมค่าชำระ : $amount บาท"


            pubF.handler!!.sendMessage(
                pubF.handler!!.obtainMessage(
                    PublicValues().PRINTCONTENT,
                    1,
                    0,
                    null
                )
            )
            Postinvoice()
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
        }

        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        choosePhotoHelper!!.onActivityResult(requestCode, resultCode, data)
    }


    private fun setUI() {
        pubF = Publicfunction()

        retrofitUpload = retrofitUpload()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headersavevalues), actionBar)


        setdaterangeexport = findViewById(R.id.setdaterangeexport)
        setdaterangeexport!!.setOnClickListener(this)
        timestartinput = findViewById(R.id.txtTimeStart)
        timeendinput = findViewById(R.id.txtTimeEnd)
        cambtn = findViewById(R.id.opencambtn)
        cambtn!!.setOnClickListener(this)
        submitbtn = findViewById(R.id.submitbtn)
        submitbtn!!.setOnClickListener(this)
        printinvoice = findViewById(R.id.printinvoicebtn)
        printinvoice!!.setOnClickListener(this)

        imgcam = findViewById(R.id.imgfromcam)

        gpslayout = findViewById(R.id.gps_text_layout)
        gpsInput = findViewById(R.id.gps_text_input)
        gpsInput!!.setOnClickListener(this)
        pubF.setOntextchange(this, gpsInput!!, gpslayout!!)
        homeidinput = findViewById(R.id.homeid_text_input)
        meteridinput = findViewById(R.id.meterId_text_input)
        buildingtypeinput = findViewById(R.id.buildingtype_text_input)
        nameinput = findViewById(R.id.name_text_input)
        telnuminput = findViewById(R.id.telnum_text_input)
        oldwatermeterinput = findViewById(R.id.oldwatermeter_text_input)
        valuesidlayout = findViewById(R.id.valuesId_text_layout)
        valuesidinput = findViewById(R.id.valuesId_text_input)
        pubF.setOntextchange(this, valuesidinput!!, valuesidlayout!!)
        typepayidlayout = findViewById(R.id.typepay_text_layout)
        typepayidinput = findViewById(R.id.typepay_text_input)
        pubF.setOntextchange(this, typepayidinput!!, typepayidlayout!!)
        amountlayout = findViewById(R.id.amount_text_layout)
        amountinput = findViewById(R.id.amount_text_input)
        pubF.setOntextchange(this, amountinput!!, amountlayout!!)


        Photohelper(this, imgcam!!)

        val floatingActionButton =
            findViewById<View>(R.id.opencambtn) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            choosePhotoHelper!!.showChooser()
        }
    }

    private fun checkisempty(): Boolean {

        if (gpsInput!!.text.toString().isEmpty()) {

            gpslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (valuesidinput!!.text.toString().isEmpty()) {
            valuesidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (typepayidinput!!.text.toString().isEmpty()) {
            typepayidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (amountinput!!.text.toString().isEmpty()) {
            amountlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (timestartinput!!.text.toString().isEmpty() || timeendinput!!.text.toString()
                .isEmpty()
        ) {
            pubF.message("เลือกวันที่แจ้งหนี้และวันครบชำระ", this)
            return false
        } else {
            gpslayout!!.isErrorEnabled = false
            valuesidlayout!!.isErrorEnabled = false
            typepayidlayout!!.isErrorEnabled = false
            amountlayout!!.isErrorEnabled = false

//            homeid = homeidinput!!.text.toString()
//            meterid = meteridinput!!.text.toString()
            newwatermeter = valuesidinput!!.text.toString()
            type = typepayidinput!!.text.toString()
            amount = amountinput!!.text.toString()
            startinvoice = timestartinput!!.text.toString()
            endinvoice = timeendinput!!.text.toString()

            return true
        }
    }


    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private fun captureImage2() {

        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = createImageFile4()
            if (photoFile != null) {
                pubF.message(photoFile!!.getAbsolutePath(), this)
                Log.i("Mayank", photoFile!!.getAbsolutePath())
//                val photoURI = Uri.fromFile(photoFile)
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext().getPackageName().toString() + ".provider",
                    photoFile!!
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
            }
        } catch (e: Exception) {
            pubF.message("Camera is not available." + e.toString(), this)
        }

    }

    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Create the File where the photo should go
            try {

                photoFile = createImageFile()
//                    displayMessage(baseContext, photoFile!!.getAbsolutePath())
                Log.i("Mayank", photoFile!!.getAbsolutePath())

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d("clickkk", "5")

//                        var photoURI = FileProvider.getUriForFile(this,
//                            "com.vlemonn.blog.kotlincaptureimage.fileprovider",
//                            photoFile!!
//                        )
                    val photoURI: Uri = FileProvider
                        .getUriForFile(
                            this,
                            this.getApplicationContext().getPackageName().toString() + ".provider",
                            photoFile!!
                        )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)

                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
//                    displayMessage(baseContext,"Capture Image Bug: "  + ex.message.toString())
            }
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Create the File where the photo should go
            try {

                photoFile = createImageFile()
//                    displayMessage(baseContext, photoFile!!.getAbsolutePath())
                Log.i("Mayank", photoFile!!.getAbsolutePath())

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d("clickkk", "5")

//                        var photoURI = FileProvider.getUriForFile(this,
//                            "com.vlemonn.blog.kotlincaptureimage.fileprovider",
//                            photoFile!!
//                        )
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        this.getApplicationContext().getPackageName().toString() + ".provider",
                        photoFile!!
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)

                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
//                    displayMessage(baseContext,"Capture Image Bug: "  + ex.message.toString())
            }


        }


    }


    private fun createImageFile4(): File? {
        // External sdcard location
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            IMAGE_DIRECTORY_NAME
        )
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                displayMessage(baseContext, "Unable to create directory.")
                return null
            }
        }

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        return File(
            mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".jpg"
        )

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath

        return image
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        choosePhotoHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                captureImage()
            }
        }

    }

    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    fun obtieneLocalizacion(
        activity: Activity,
        fusedLocationClient: FusedLocationProviderClient,
        gpsInput: TextInputEditText?
    ) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PublicValues().GPS_LOCATION_REQUEST
            )

        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    gpsInput!!.setText("${location?.latitude} : ${location?.longitude}")
                    lat = location?.latitude.toString()
                    long = location?.longitude.toString()
                } else {
                    pubF.message("Location error", activity)
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.getAction() === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    var dateTimeselect: String? = null
    var dateselectstart: String? = null
    var dateselectend: String? = null
    private fun Setdaterange() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val constraintsBuilder = CalendarConstraints.Builder()
        builder.setCalendarConstraints(constraintsBuilder.build())
        val picker = builder.build()
        picker.show(this.supportFragmentManager, picker.toString())
        Log.d("dateee", picker.toString())
        picker.addOnPositiveButtonClickListener { selection -> //Do something...
            Log.d("dateee", picker.headerText)
            Log.d("dateee", selection.first.toString())
            Log.d("dateee", selection.second.toString())
            val datestart = Date(selection.first!!)
            val dateend = Date(selection.second!!)

            val dateTime = SimpleDateFormat("dd/MM/yy,HH:mm:ss")
            val date = SimpleDateFormat("yyyy/MM/dd")

//            Log.d("dateee", datestart.hours.toString())
//            Log.d("dateee", datestart.minutes.toString())
//            Log.d("dateee", datestart.seconds.toString())

            timestartinput!!.setText(date.format(datestart))
            dateTimeselect = dateTime.format(datestart)
            Log.d("dateee", dateTimeselect)
            dateselectstart = dateTimeselect

            timeendinput!!.setText(date.format(dateend))
            dateTimeselect = dateTime.format(dateend)
            Log.d("dateee", dateTimeselect)
            dateselectend = dateTimeselect

//            val dateFormat = DateFormat.getDateFormat(this)
//            Log.d("dateee", dateFormat.format(datestart))
//            Log.d("dateee", dateFormat.format(dateend))


        }
    }

    private fun createFindIdDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_savemeter, null)

        builder.setView(view)
        builder.setCancelable(false)
        val alert = builder.create()

        val closebtn = view.findViewById<Button>(R.id.cancel_button)
        closebtn.setOnClickListener {
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
            finish()
        }
        val homeidlayout = view.findViewById<TextInputLayout>(R.id.homeid_text_layout)
        homeidlayout!!.helperText = "ตัวอย่าง : 01/001"
        val homeidinput = view.findViewById<TextInputEditText>(R.id.homeid_text_input)
        val meteridlayout = view.findViewById<TextInputLayout>(R.id.meterId_text_layout)
        val meteridinput = view.findViewById<TextInputEditText>(R.id.meterId_text_input)


        val submitbtn = view.findViewById<Button>(R.id.submitbtn)
        submitbtn.setOnClickListener {
            if (meteridinput!!.text.toString().isEmpty() && homeidinput!!.text.toString()
                    .isEmpty()
            ) {
                meteridlayout!!.error = resources.getString(R.string.gettexterror)
                homeidlayout!!.error = resources.getString(R.string.gettexterror)
                pubF.setOntextchange(this, homeidinput!!, homeidlayout!!)
                pubF.setOntextchange(this, meteridinput!!, meteridlayout!!)
                pubF.message(getString(R.string.gettexthint), this)
            } else {
                findMeterData.address = homeidinput.text.toString().trim()
                findMeterData.meterid = meteridinput.text.toString().trim()

                findMeterUser(this, findMeterData, view, alert)


            }


        }

        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window.setGravity(Gravity.CENTER)

    }


    fun findMeterUser(
        activity: Activity,
        dataRegister: findMeterWaterData,
        view: View,
        alert: AlertDialog
    ) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)
        val URL: String = activity.getResources().getString(R.string.URL) + activity.getResources()
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Apiname = activity.getResources().getString(R.string.finddatameteruser)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<findMeterWaterData> = api.uploadFindmeter(dataRegister, Apiname)
        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call

        call.enqueue(object : Callback<findMeterWaterData?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<findMeterWaterData?>,
                response: Response<findMeterWaterData?>
            ) {

                Log.d("ressss", response.body().toString())
                Log.d("ressss", response.errorBody().toString())
                Log.d("ressss", response.headers().toString())

                if (response.isSuccessful) {
                    val js = Gson().toJson(response.body())
                    Log.d("ressss_s", js)

                    var json: JSONObject? = null
                    try {
                        json = JSONObject(js)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.d("ressss_s", json.toString())

                    val jsonmessageArray: JSONArray = json?.get("message") as JSONArray
                    var jsonmessage: JSONObject? = jsonmessageArray.getJSONObject(0)
                    val jsonhomeArray: JSONArray = jsonmessage!!.get("home") as JSONArray
                    val homelength = jsonhomeArray.length()

                    var x = 0
                    while (x < homelength) {
                        var jsonhome: JSONObject? = jsonhomeArray.getJSONObject(x)
                        homeid = jsonhome!!.get("address").toString()
                        meterid = jsonhome.get("meterId").toString()
                        buildingtype = jsonhome.get("buildingType").toString()
                        name = jsonhome.get("name").toString()
                        telnum = jsonhome.get("tel").toString()
                        oldwatermeter = jsonhome.get("meterValues").toString()
                        x++
                    }
                    homeidinput!!.text = "${getString(R.string.homeid)} : $homeid"
                    meteridinput!!.text = "${getString(R.string.meterid)} : $meterid"
                    buildingtypeinput!!.text = "${getString(R.string.buildingtype)} : $buildingtype"
                    nameinput!!.text = "${getString(R.string.name)} : $name"
                    telnuminput!!.text = "${getString(R.string.telnum)} : $telnum"
                    oldwatermeterinput!!.text = "${getString(R.string.oldwatermeter)} : $oldwatermeter"

                    pubF.loadingDialog!!.dismiss()

                    (view.parent as ViewGroup).removeView(view) // <- fix
                    alert.dismiss()

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        pubF.loadingDialog!!.dismiss()
                        pubF.message(jObjError.getString("message"), activity)
//                        (view.parent as ViewGroup).removeView(view) // <- fix
//                        alert.dismiss()

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<findMeterWaterData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")

                pubF.loadingDialog!!.dismiss()
                pubF.message(t.message, activity)


            }
        })
    }

    public fun Photohelper(activity: Activity, view: ImageView) {
        choosePhotoHelper = ChoosePhotoHelper.with(activity)
            .asFilePath()
            .build(ChoosePhotoCallback {
                Glide.with(activity)
                    .load(it)
                    .apply(RequestOptions.placeholderOf(R.drawable.icons8_image_200px))
                    .into(view!!)
                //        creating request body for file
                if(it != null){
                    val file = File(it)
                    val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                    imageFinance = MultipartBody.Part.createFormData("imageFinance", file.name, requestFile)
                }

            })
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}
