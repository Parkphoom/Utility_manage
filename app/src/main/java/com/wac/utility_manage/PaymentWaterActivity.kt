package com.wac.utility_manage

import android.annotation.SuppressLint
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
import com.example.utility_manage.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shashank.sony.fancytoastlib.FancyToast
import com.telpo.tps550.api.printer.UsbThermalPrinter
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.PublicValues
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.PaymentData
import com.wac.utility_manage.Retrofit.Data.findMeterWaterData
import com.wac.utility_manage.Retrofit.retrofitCallback
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import kotlinx.android.synthetic.main.activity_payment_water.*
import org.json.JSONObject


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PaymentWaterActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var retrofitCallfuntion: retrofitCallfuntion
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pubF: Publicfunction
    private lateinit var prtF: Printfuntion
    private lateinit var mCurrentPhotoPath: String
    private var findMeterData = findMeterWaterData()
    private var postpaymentData = PaymentData.POST()

    private var watermeterlayout: LinearLayout? = null

    private var startvaluesinput: TextInputEditText? = null
    private var startvalueslayout: TextInputLayout? = null
    private var valuesinput: TextInputEditText? = null
    private var valueslayout: TextInputLayout? = null

    private var txthomdid: TextView? = null
    private var txtmeterid: TextView? = null
    private var txtbuildingtype: TextView? = null
    private var txtname: TextView? = null
    private var txttelnum: TextView? = null
    private var txtstartdateinvocie: TextView? = null
    private var txtenddateinvoice: TextView? = null
    private var txtinvoice: TextView? = null
    private var txtaddress: TextView? = null
    private var txtcategory: TextView? = null

    private var nextbutton: Button? = null

    private var homeid: String? = ""
    private var meterid: String? = ""
    private var startvalues: String? = ""
    private var values: String? = ""
    private var amount: String? = ""
    private var ref2 = ""
    private var invoice_id: String? = "0"
    private var category: String? = ""

    private var iswater: Boolean = false
    private var Strprint: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_water)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUI()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nextbtn -> {
                if (iswater) {
                    homeid = txthomdid?.text.toString()
                    meterid = txtmeterid?.text.toString()
                    startvalues = startvaluesinput?.text.toString()
                    values = valuesinput?.text.toString()

                    Strprint = listOf(
                        "ใบเสร็จ${txtcategory?.text.toString()}",
                        "\nเลขมิเตอร์ : $meterid" +
                                "\nเลขมาตรครั้งก่อน : $startvalues" +
                                "\nเลขมาตรครั้งนี้ : $values" +
                                "\nหน่วยน้ำที่ใช้ : ${(startvalues!!.toInt() - values!!.toInt())} หน่วย" +
                                "\nที่อยู่ : $homeid" +
                                "\nประเภท : ${buildingtype.text.toString()}" +
                                "\nชื่อผู้ใช้น้ำ : ${txtname?.text.toString()}" +
                                "\nเบอร์โทร : ${txttelnum?.text.toString()}" +
                                "\nวันที่แจ้งค่าน้ำ : ${pubF.getDatenow()}" +
                                "\nวันครบชำระ : ${txtenddateinvoice?.text.toString()}" +
                                "\n" +
                                "\n" +
                                "\nค่าน้ำค้างชำระ : 0 บาท" +
                                "\nชำระแล้วทั้งสิ้น : $amount บาท"+
                                "\nวันที่ชำระ : ${pubF.getDatenow()}"
                    )

                } else {
                    homeid = txthomdid?.text.toString()
                    txtmeterid!!.visibility = View.GONE
                    startvaluesinput!!.visibility = View.GONE
                    valuesinput!!.visibility = View.GONE
//                    meterid = txtmeterid?.text.toString()
//                    startvalues = startvaluesinput?.text.toString()
//                    values = valuesinput?.text.toString()

                    Strprint = listOf(
                        "ใบเสร็จ${txtcategory?.text.toString()}",
                        "\nที่อยู่ : $homeid" +
                                "\nประเภท : ${buildingtype.text.toString()}" +
                                "\nชื่อผู้ใช้ : ${txtname?.text.toString()}" +
                                "\nเบอร์โทร : ${txttelnum?.text.toString()}" +
                                "\nวันที่แจ้ง : ${pubF.getDatenow()}" +
                                "\nวันครบชำระ : ${txtenddateinvoice?.text.toString()}" +
                                "\n" +
                                "\n" +
                                "\nค้างชำระ : 0 บาท" +
                                "\nชำระแล้วทั้งสิ้น : $amount บาท"+
                                "\nวันที่ชำระ : ${pubF.getDatenow()}"
                    )
                }


                createdialog(
                    "ใบเสร็จ${txtcategory?.text.toString()}",
                    "(ไม่ใช่ใบแจ้งหนี้)",
                    getString(R.string.printslip),
                    Strprint
                )
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        overridePendingTransition(
//            R.anim.slide_in_left,
//            R.anim.slide_out_right
//        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onStart() {
        super.onStart()
        ref2 = intent.getStringExtra("Ref2")
        if (ref2.isEmpty()) {
            createref2input()
        } else {
            Callgetpayment(ref2)
        }
    }

    private fun setUI() {
        pubF = Publicfunction()
        prtF = Printfuntion()
        retrofitCallfuntion = retrofitCallfuntion()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(
            this.resources.getString(R.string.headerpayment),
            actionBar
        )



        pubF.Slideleft(this)
        startvaluesinput = findViewById(R.id.startvaluesId_text_input)
        startvalueslayout = findViewById(R.id.startvaluesId_text_layout)
        pubF.setOntextchange(this, startvaluesinput!!, startvalueslayout!!)
        valuesinput = findViewById(R.id.valuesId_text_input)
        valueslayout = findViewById(R.id.valuesId_text_layout)
        pubF.setOntextchange(this, valuesinput!!, valueslayout!!)
        watermeterlayout = findViewById(R.id.watermeterlayout)

        nextbutton = findViewById(R.id.nextbtn)
        nextbutton!!.setOnClickListener(this)


        txthomdid = findViewById(R.id.homeid)
        txtmeterid = findViewById(R.id.meterId)
        txtbuildingtype = findViewById(R.id.buildingtype)
        txtname = findViewById(R.id.name)
        txttelnum = findViewById(R.id.telnum)
        txtstartdateinvocie = findViewById(R.id.TimeStart)
        txtenddateinvoice = findViewById(R.id.TimeEnd)
        txtinvoice = findViewById(R.id.invoice)
        txtaddress = findViewById(R.id.address)
        txtcategory = findViewById(R.id.category)
    }

    private fun createdialog(
        strheader1: String,
        strheader2: String,
        print: String,
        strprint: List<String>
    ) {
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
        dialogoldwatermeter.text = "${getString(R.string.oldwatermeter)} : $startvalues"
        val dialognewwatermeter = view.findViewById<TextView>(R.id.dialog_newwatermeter)
        dialognewwatermeter.text = "${getString(R.string.newwatermeter)} : $values"
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
            val prefs = getSharedPreferences(getString(R.string.PrefsLogin), Context.MODE_PRIVATE)
            val name = prefs.getString(getString(R.string.Admin_name), "")
            Log.d("ressss", name)

            postpaymentData._idInvoice = invoice_id
            postpaymentData.amount = amount
            postpaymentData.credit = amount
            postpaymentData.creditDate = pubF.getDatetimenow()
            postpaymentData.receiveName = name
            postpaymentData.remain = "0"
            postpaymentData.via = "เจ้าหน้าที่"


            PostPayment(postpaymentData, strprint)

        }

        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }

    @SuppressLint("InflateParams")
    private fun createref2input() {
        val builder = AlertDialog.Builder(
            this,
            R.style.CustomDialog
        )
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_ref2, null)

        builder.setView(view)
        builder.setCancelable(false)
        val alert = builder.create()

        val ref2input = view.findViewById<TextInputEditText>(R.id.ref2_text_input)
        val submitbtn = view.findViewById<Button>(R.id.submitbtn)
        submitbtn.setOnClickListener {
            if (!ref2input.text?.isEmpty()!!) {
                ref2 = ref2input.text.toString()
                Callgetpayment(ref2, view, alert)
            } else {
                pubF.message(getString(R.string.Inputref_2), FancyToast.ERROR, this)
            }

        }
        val closebtn = view.findViewById<ImageButton>(R.id.dialog_closebtn)
        closebtn.setOnClickListener {
            this.onBackPressed()
        }

        alert.show()
        val window = alert.window
        if (window != null) {

            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT

            window.attributes = wlp

        }


    }


    private fun checkisempty(): Boolean {

        if (startvaluesinput!!.text.toString().isEmpty()) {
            startvalueslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (valuesinput!!.text.toString().isEmpty()) {
            valueslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        } else {
            startvalues = startvaluesinput!!.text.toString()
            values = valuesinput!!.text.toString()

            return true
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


    fun Callgetpayment(ref2: String?) {
        //            retrofitCallfuntion.getPayment(this@PaymentWaterActivity,ref2)
        if (ref2 != null) {
            retrofitCallfuntion.getPayment(this@PaymentWaterActivity, ref2,
                object : retrofitCallback {
                    override fun onSucess(value: JSONObject) {
                        Log.d("res_Callgetpayment", value.getString("_id"))
                        val status: String = value.getJSONObject("payment").getString("status")
                        if (status.equals("ชำระแล้ว")) {
                            pubF.message(
                                "รายการนี้ถูกชำระแล้ว",
                                FancyToast.INFO,
                                Toast.LENGTH_LONG,
                                this@PaymentWaterActivity
                            )
                            finish()
                        } else {

                            val category = value.getString("category")
                            if (!category.equals("ค่าน้ำประปา")) {
                                watermeterlayout!!.visibility = View.GONE
                            } else {
                                iswater = true
                            }
                            txtcategory?.setText(category)
                            invoice_id = value.getString("_id")

                            var strstartdate = value.getString("startDate")
                            val startdate = strstartdate.substring(0, strstartdate.indexOf(", "))
                            txtstartdateinvocie?.setText(startdate)

                            var strenddate = value.getString("dueDate")
                            val enddate = strenddate.substring(0, strenddate.indexOf(", "))
                            txtenddateinvoice?.setText(enddate)

                            valuesinput?.setText(value.getString("meterVal"))
                            amount = value.getJSONObject("payment").getString("amount")
                            val credit: String = value.getJSONObject("payment").getString("credit")
                            val remain = (amount?.toInt()!! - credit.toInt())
                            txtinvoice?.setText(remain.toString())

                            findMeterData.address = value.getString("ref1")
                            findMeterData.meterid = value.getString("meterId")
                            Callfinduser(findMeterData)
                        }


                    }

                    override fun onFailure() {
                        this@PaymentWaterActivity.onBackPressed()
                    }
                })
        }
    }

    fun Callgetpayment(ref2: String?, view: View, alert: AlertDialog) {
        //            retrofitCallfuntion.getPayment(this@PaymentWaterActivity,ref2)
        if (ref2 != null) {
            retrofitCallfuntion.getPayment(this@PaymentWaterActivity, ref2,
                object : retrofitCallback {
                    override fun onSucess(value: JSONObject) {
                        (view.parent as ViewGroup).removeView(view) // <- fix
                        alert.dismiss()
                        Log.d("res_Callgetpayment", value.getString("_id"))
                        val status: String = value.getJSONObject("payment").getString("status")
                        if (status.equals("ชำระแล้ว")) {
                            pubF.message(
                                "รายการนี้ถูกชำระแล้ว",
                                FancyToast.INFO,
                                FancyToast.LENGTH_LONG,
                                this@PaymentWaterActivity
                            )
                            finish()
                        } else {

                            category = value.getString("category")
                            if (!category.equals("ค่าน้ำประปา")) {
                                watermeterlayout!!.visibility = View.GONE
                            }
                            txtcategory?.setText(category)
                            invoice_id = value.getString("_id")
                            var strstartdate = value.getString("startDate")
                            val startdate = strstartdate.substring(0, strstartdate.indexOf(", "))
                            txtstartdateinvocie?.setText(startdate)

                            var strenddate = value.getString("dueDate")
                            val enddate = strenddate.substring(0, strenddate.indexOf(", "))
                            txtenddateinvoice?.setText(enddate)
                            valuesinput?.setText(value.getString("meterVal"))
                            amount = value.getJSONObject("payment").getString("amount")
                            val credit: String = value.getJSONObject("payment").getString("credit")
                            val remain = (amount?.toInt()!! - credit.toInt())
                            txtinvoice?.setText(remain.toString())

                            findMeterData.address = value.getString("ref1")
                            findMeterData.meterid = value.getString("meterId")
                            Callfinduser(findMeterData)

                        }


                    }

                    override fun onFailure() {

                    }
                })
        }
    }

    fun Callfinduser(findMeterData: findMeterWaterData) {
        retrofitCallfuntion.findMeterUser(this@PaymentWaterActivity, findMeterData,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val data = value.getJSONObject("home").getJSONObject("data")
                    Log.d("res_Callfinduser", data.toString())
                    val user = value.getJSONObject("home").getJSONArray("user").getJSONObject(0)
                    Log.d("res_Callfinduser", user.toString())

                    txthomdid?.setText(data.getString("address"))
                    txtmeterid?.setText(data.getString("meterId"))
                    txtbuildingtype?.setText(data.getString("buildingType"))
                    startvaluesinput?.setText(data.getString("meterVal"))
                    txtaddress?.setText(data.getString("address"))
                    txtname?.setText(user.getString("name"))
                    txttelnum?.setText(user.getString("tel"))
                }

                override fun onFailure() {}
            })
    }

    fun PostPayment(
        datapayment: PaymentData.POST,
        strprint: List<String>
    ) {
        retrofitCallfuntion.postPayment(this@PaymentWaterActivity, datapayment,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val data = value.getString("operate")
                    Log.d("res_PostPayment", data.toString())
                    val user = value.getString("data")
                    Log.d("res_PostPayment", user.toString())


                    prtF.handler = prtF.MyHandler(this@PaymentWaterActivity)
                    prtF.mUsbThermalPrinter = UsbThermalPrinter(this@PaymentWaterActivity)
                    prtF.Printheader = strprint[0]
                    prtF.Printcontent = strprint[1]
                    prtF.ref2 = ref2

                    prtF.handler!!.sendMessage(
                        prtF.handler!!.obtainMessage(
                            PublicValues().PRINTCONTENT,
                            1,
                            0,
                            null
                        )
                    )
                }

                override fun onFailure() {
//                    Log.d(getString(R.string.LogError), e.message.toString())
                }
            })
    }
}
