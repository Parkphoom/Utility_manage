package com.wac.utility_manage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.queuedemo_kotlin.Retrofit.callApi
import com.example.utility_manage.R
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import com.shivtechs.maplocationpicker.MapUtility
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.Data.addInvoiceData
import com.wac.utility_manage.Retrofit.Data.findMeterWaterData
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
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
    private var credit: String = "0"
    private var startinvoice: String = ""
    private var endinvoice: String = ""
    private var via: String = ""
    var imageFinance: MultipartBody.Part? = null

    private var homeidinput: TextView? = null
    private var meteridinput: TextView? = null
    private var buildingtypeinput: TextView? = null
    private var nameinput: TextView? = null
    private var telnuminput: TextView? = null
    private var oldwatermeterinput: TextView? = null

    private var cambtn: ImageButton? = null
    private var imgcam: ImageView? = null
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

    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 1
    private val IMAGE_DIRECTORY_NAME = "WAC"
    private lateinit var mCurrentPhotoPath: String

    private lateinit var pubF: Publicfunction
    private lateinit var prtF: Printfuntion
    private var dataInvoice = addInvoiceData()
    private var findMeterData = findMeterWaterData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion
    private var choosePhotoHelper: ChoosePhotoHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savevalues)
        MapUtility.apiKey = getString(R.string.google_maps_key)
        setUI()

        createFindIdDialog()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }

    override fun onStart() {
        super.onStart()
        pubF.obtieneLocalizacion(this, pubF.fusedLocationClient)

    }


    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {
            R.id.opencambtn -> {

            }

            R.id.submitbtn -> {
                if (checkisempty()) {
                    val Strprint = listOf("","")
                    Postinvoice(true, Strprint)
                }
            }
            R.id.printinvoicebtn -> {
                if (pubF.lat.isEmpty() || pubF.long.isEmpty()) {
                    pubF.obtieneLocalizacion(this, pubF.fusedLocationClient)
                }
                if (checkisempty()) {
                    createdialog("ใบแจ้งค่าน้ำประปา", "(ไม่ใช่ใบเสร็จรับเเงิน)", "พิมพ์ใบแจ้งหนี้")

                }
            }
            R.id.setdaterangeexport -> {
                Setdaterange()
            }
        }
    }


    private fun Postinvoice(
        finish: Boolean,
        strprint: List<String>
    ) {
        val gpslocation = GpsObj()
        gpslocation.latitude = pubF.lat
        gpslocation.longitude = pubF.long

        val payment = dataInvoice.paymentObj()
        payment.status = "ค้างชำระ"
        payment.amount = amount
        credit = "100"
        payment.credit = "0"
        payment.via = via
        payment.creditdate = ""
        payment.remain = "0"
        payment.receiveName = ""

        dataInvoice.ref_1 = homeid
        dataInvoice.ref_2 = ""
        dataInvoice.setmeterID(meterid)
        dataInvoice.setmeterVal(newwatermeter)
        dataInvoice.category = type
        dataInvoice.setstartDate(startinvoice)
        dataInvoice.setdueDate(endinvoice)
        dataInvoice.setgps(gpslocation)
        dataInvoice.setpayment(payment)


        val strCSV = "$homeid,,$type,$amount,$startinvoice,$endinvoice,$newwatermeter,$meterid" +
                ",${gpslocation.latitude},${gpslocation.longitude},ค้างชำระ"

        retrofitCallfuntion.addinvoice(this, dataInvoice, imageFinance, finish,strprint, strCSV)
    }

    private fun createdialog(strheader1: String, strheader2: String, print: String) {
        val builder = AlertDialog.Builder(
            this,
            R.style.CustomDialog
        )
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


            val Strprint = listOf(
                "ใบแจ้งค่าน้ำประปา",
                "\nเลขมิเตอร์ : $meterid" +
                        "\nเลขมาตรครั้งก่อน : $oldwatermeter" +
                        "\nเลขมาตรครั้งนี้ : $newwatermeter" +
                        "\nหน่วยน้ำที่ใช้ : ${(newwatermeter.toInt() - oldwatermeter.toInt())} หน่วย" +
                        "\nที่อยู่ : $homeid" +
                        "\nประเภท : $buildingtype" +
                        "\nชื่อผู้ใช้น้ำ : $name" +
                        "\nเบอร์โทร : $telnum" +
                        "\nวันที่แจ้งค่าน้ำ : $startinvoice" +
                        "\nวันครบชำระ : $endinvoice" +
                        "\nรวมเงินครั้งนี้ : $amount" +
                        "\nค่าน้ำค้างชำระ : 0 บาท" +
                        "\nรวมเงินที่ต้องชำระทั้งสิ้น : $amount บาท"
            )

            Postinvoice(false,Strprint)

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
        prtF = Printfuntion()
        retrofitCallfuntion =
            retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headersavevalues), actionBar)
        pubF.Slideleft(this)


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
            pubF.message(getString(R.string.SelectdateAlert), FancyToast.CONFUSING, this)
            return false
        } else {
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        choosePhotoHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)


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
        Thread(Runnable {
            runOnUiThread {
                //your code or your request that you want to run on uiThread
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
                    val date = SimpleDateFormat("dd/MM/yyyy")

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
        }).start()


    }

    private fun createFindIdDialog() {
        val builder = AlertDialog.Builder(
            this,
            R.style.CustomDialog
        )
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_savemeter, null)

        builder.setView(view)
        builder.setCancelable(false)
        val alert = builder.create()

        val closebtn = view.findViewById<Button>(R.id.cancel_button)
        closebtn.setOnClickListener {
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
            onBackPressed()
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
                pubF.message(getString(R.string.gettexthint), FancyToast.CONFUSING, this)
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
        pubF.builddialogloading(activity)
        val URL: String = activity.getResources().getString(R.string.URL) + activity.getResources()
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Apiname = activity.getResources().getString(R.string.finddatameteruserURL)

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
//                Log.d("ressss", response.errorBody().toString())
//                Log.d("ressss", response.headers().toString())

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
                    for (i in 0 until jsonmessageArray.length()) {
                        val jsonmessage: JSONObject? = jsonmessageArray.getJSONObject(i)
                        val data = jsonmessage!!.getJSONObject("home").getJSONObject("data")
                        Log.d("ressss_s", data.toString())
                        val user =
                            jsonmessage.getJSONObject("home").getJSONArray("user").getJSONObject(0)

                        homeid = data.get("address").toString()
                        meterid = data.get("meterId").toString()
                        buildingtype = data.get("buildingType").toString()
                        name = user.get("name").toString()
                        telnum = user.get("tel").toString()
                        oldwatermeter = data.get("meterVal").toString()
                    }

//
                    homeidinput!!.text = "${getString(R.string.homeid)} : $homeid"
                    meteridinput!!.text = "${getString(R.string.meterid)} : $meterid"
                    buildingtypeinput!!.text = "${getString(R.string.buildingtype)} : $buildingtype"
                    nameinput!!.text = "${getString(R.string.name)} : $name"
                    telnuminput!!.text = "${getString(R.string.telnum)} : $telnum"
                    oldwatermeterinput!!.text =
                        "${getString(R.string.oldwatermeter)} : $oldwatermeter"

                    pubF.loadingDialog!!.dismiss()

                    (view.parent as ViewGroup).removeView(view) // <- fix
                    alert.dismiss()


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        pubF.loadingDialog!!.dismiss()
                        pubF.message(jObjError.getString("message"), FancyToast.WARNING, activity)
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
                pubF.message(t.message, FancyToast.ERROR, activity)


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
                if (it != null) {
                    val file = File(it)
                    val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                    imageFinance =
                        MultipartBody.Part.createFormData("imageFinance", file.name, requestFile)
                }

            })
    }

    override fun onDestroy() {
        super.onDestroy()
        pubF.lat = ""
        pubF.long = ""
    }

}
