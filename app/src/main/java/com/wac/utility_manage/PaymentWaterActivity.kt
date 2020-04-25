package com.wac.utility_manage

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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

    private var startvaluesinput: TextInputEditText? = null
    private var startvalueslayout: TextInputLayout? = null
    private var valuesinput: TextInputEditText? = null
    private var valueslayout: TextInputLayout? = null
    private var amountinput: TextInputEditText? = null
    private var amountlayout: TextInputLayout? = null

    private var txthomdid: TextView? = null
    private var txtmeterid: TextView? = null
    private var txtbuildingtype: TextView? = null
    private var txtname: TextView? = null
    private var txttelnum: TextView? = null
    private var txtstartdateinvocie: TextView? = null
    private var txtenddateinvoice: TextView? = null
    private var txtinvoice: TextView? = null

    private var nextbutton: Button? = null

    private var homeid: String? = ""
    private var meterid: String? = ""
    private var startvalues: String? = ""
    private var values: String? = ""
    private var amount: String? = ""
    private var ref2 = ""
    private var credit: String? = "0"
    private var invoice_id: String? = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_water)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUI()
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.nextbtn -> {
                if (checkisempty()) {
                    if (credit!!.toInt() > amount!!.toInt()) {
                        amountlayout?.error = "จำนวนเงินเกินยอดค้างชำระ"
                    } else {
                        if (credit!!.toInt() < amount!!.toInt()) {
                            pubF.message(
                                "จำนวนเงินที่จ่ายไม่ครบยอดค้างชำระ",
                                FancyToast.WARNING,
                                this
                            )
                        }
                        createdialog(
                            "ใบเสร็จค่าน้ำประปา",
                            "(ไม่ใช่ใบแจ้งหนี้)",
                            getString(R.string.printinvoices)
                        )

                    }

                }
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

    private fun setUI() {
        pubF = Publicfunction()
        prtF = Printfuntion()
        retrofitCallfuntion = retrofitCallfuntion()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(
            this.resources.getString(R.string.headerpaymentwater),
            actionBar
        )
        ref2 = intent.getStringExtra("Ref2")
        if (ref2.isEmpty()) {
            createref2input()
        } else {
            Callgetpayment(ref2)
        }


        pubF.Slideleft(this)
        startvaluesinput = findViewById(R.id.startvaluesId_text_input)
        startvalueslayout = findViewById(R.id.startvaluesId_text_layout)
        pubF.setOntextchange(this, startvaluesinput!!, startvalueslayout!!)
        valuesinput = findViewById(R.id.valuesId_text_input)
        valueslayout = findViewById(R.id.valuesId_text_layout)
        pubF.setOntextchange(this, valuesinput!!, valueslayout!!)
        amountinput = findViewById(R.id.amount_text_input)
        amountlayout = findViewById(R.id.amount_text_layout)
        pubF.setOntextchange(this, amountinput!!, amountlayout!!)

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
            val name = prefs.getString(getString(R.string.key_login), "")
            Log.d("ressss", name)

            postpaymentData._idInvoice = invoice_id
            postpaymentData.amount = amount
            postpaymentData.credit = credit
            postpaymentData.creditDate = pubF.getDatetimenow()
            postpaymentData.receiveName = name
            postpaymentData.remain = "0"
            postpaymentData.via = "เจ้าหน้าที่"

            PostPayment(postpaymentData)


        }

        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }

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

            ref2input.text.toString()

            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
        }

        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }


    private fun checkisempty(): Boolean {

        if (startvaluesinput!!.text.toString().isEmpty()) {
            startvalueslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (valuesinput!!.text.toString().isEmpty()) {
            valueslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (amountinput!!.text.toString().isEmpty()) {
            amountlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        } else {
            startvalues = startvaluesinput!!.text.toString()
            values = valuesinput!!.text.toString()
            credit = amountinput!!.text.toString()

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

                        invoice_id = value.getString("_id")
                        txtstartdateinvocie?.setText(value.getString("startDate"))
                        txtenddateinvoice?.setText(value.getString("dueDate"))
                        valuesinput?.setText(value.getString("meterVal"))
                        amount = value.getJSONObject("payment").getString("amount")
                        txtinvoice?.setText(amount)

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
                            findMeterData.address = value.getString("ref1")
                            findMeterData.meterid = value.getString("meterId")
                            Callfinduser(findMeterData)
                        }

                    }

                    override fun onFailure() {}
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
                    txtname?.setText(user.getString("name"))
                    txttelnum?.setText(user.getString("tel"))
                }

                override fun onFailure() {}
            })
    }

    fun PostPayment(datapayment: PaymentData.POST) {
        retrofitCallfuntion.postPayment(this@PaymentWaterActivity, datapayment,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val data = value.getString("operate")
                    Log.d("res_PostPayment", data.toString())
                    val user = value.getString("data")
                    Log.d("res_PostPayment", user.toString())


                    prtF.handler = prtF.MyHandler(this@PaymentWaterActivity)
                    prtF.mUsbThermalPrinter = UsbThermalPrinter(this@PaymentWaterActivity)
                    prtF.Printheader = "ใบแจ้งค่าน้ำประปา"
                    prtF.Printcontent = "\nที่อยู่ : $homeid" +
                            "\nเลขมาตรครั้งก่อน : $startvalues" +
                            "\nเลขมาตรครั้งนี้ : $values" +
                            "\nเลขจากมิเตอร์ : $meterid" +
                            "\nรวมค่าชำระ : $amount บาท"


                    prtF.handler!!.sendMessage(
                        prtF.handler!!.obtainMessage(
                            PublicValues().PRINTCONTENT,
                            1,
                            0,
                            null
                        )
                    )
                }

                override fun onFailure() {}
            })
    }
}
