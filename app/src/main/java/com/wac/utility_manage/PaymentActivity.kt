package com.wac.utility_manage

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.R
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.Data.addInvoiceData
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import java.text.SimpleDateFormat
import java.util.*


class PaymentActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var prtF: Printfuntion
    private var dataInvoice = addInvoiceData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion

    var setdaterange: LinearLayout? = null
    private var typepaylayout: TextInputLayout? = null
    private var typepayinput: AutoCompleteTextView? = null
    private var homeidlayout: TextInputLayout? = null
    private var homeidinput: TextInputEditText? = null
    private var amountlayout: TextInputLayout? = null
    private var amountinput: TextInputEditText? = null
    var timestartinput: TextView? = null
    var timeendinput: TextView? = null

    private var printsilp: Button? = null
    private var printinvoice: Button? = null

    var dateTimeselectstart: String? = null
    var dateTimeselectend: String? = null
    var dateselectstart: String? = null
    var dateselectend: String? = null
    private var type: String? = ""
    private var homeid: String? = ""
    private var amount: String? = ""
    private var via: String = ""
    private var credit: String = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        setUI()

        val typelist: MutableList<String> = ArrayList()
        typelist.add("ค่าขยะ")
        typelist.add("ค่าส่วนกลาง")
        typelist.add("ค่าภาษีโรงเรือน")
        typelist.add("ค่าภาษีป้าย")
        typelist.add("ค่าภาษีที่ดิน")
        typelist.add("ค่าภาษีธุรกิจเฉพาะ")

        val typeDataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            typelist
        )

        val editTextFilledExposedDropdown: AutoCompleteTextView =
            findViewById(R.id.filled_exposed_dropdown)
        editTextFilledExposedDropdown.setAdapter(typeDataAdapter)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.printpaysilpbtn -> {
                if (checkisempty()) {
                    createdialog("ใบเสร็จค่าบริการ", "", getString(R.string.printslip))

                }
            }
            R.id.printinvoicebtn -> {
                if (pubF.lat.isEmpty() || pubF.long.isEmpty()) {
                    pubF.obtieneLocalizacion(this, pubF.fusedLocationClient)
                }
                if (checkisempty()) {
                    createdialog(
                        "ใบแจ้ง${typepayinput!!.text}",
                        "(ไม่ใช่ใบเสร็จรับเเงิน)",
                        getString(R.string.printslip)
                    )
                }
            }
            R.id.setdaterangelayout -> {
                Setdaterange()
            }
        }

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

    private fun setUI() {
        pubF = Publicfunction()
        prtF = Printfuntion()

        retrofitCallfuntion = retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headerpayment), actionBar)
        pubF.Slideleft(this)

        setdaterange = findViewById(R.id.setdaterangelayout)
        setdaterange!!.setOnClickListener(this)
        timestartinput = findViewById(R.id.txtTimeStart)
        timeendinput = findViewById(R.id.txtTimeEnd)
        typepaylayout = findViewById(R.id.typepay_text_layout)
        typepayinput = findViewById(R.id.filled_exposed_dropdown)
//        pubF.setOntextchange(this, typepayinput!!, typepaylayout!!)
        homeidlayout = findViewById(R.id.homeid_text_layout)
        homeidinput = findViewById(R.id.homeid_text_input)
        pubF.setOntextchange(this, homeidinput!!, homeidlayout!!)
        amountlayout = findViewById(R.id.amount_text_layout)
        amountinput = findViewById(R.id.amount_text_input)
        pubF.setOntextchange(this, amountinput!!, amountlayout!!)

        printsilp = findViewById(R.id.printpaysilpbtn)
        printsilp!!.setOnClickListener(this)
        printinvoice = findViewById(R.id.printinvoicebtn)
        printinvoice!!.setOnClickListener(this)

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


    private fun checkisempty(): Boolean {
        if (typepayinput!!.text.toString().isEmpty()) {
            typepaylayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (homeidinput!!.text.toString().isEmpty()) {
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (amountinput!!.text.toString().isEmpty()) {
            amountlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (timestartinput!!.text.toString().isEmpty() || timeendinput!!.text.toString()
                .isEmpty()
        ) {
            pubF.message(getString(R.string.SelectdateAlert), FancyToast.ERROR, this)
            return false
        } else {
            type = typepayinput!!.text.toString()
            homeid = homeidinput!!.text.toString()
            amount = amountinput!!.text.toString()
            return true
        }
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
        dialogmeterid.visibility = GONE
//        dialogmeterid.text = "${getString(R.string.amount)} : $amount"
        val dialogoldwatermeter = view.findViewById<TextView>(R.id.dialog_oldwatermeter)
        dialogoldwatermeter.text = "${getString(R.string.Startdateinvoice)} : $dateselectstart"
        val dialognewwatermeter = view.findViewById<TextView>(R.id.dialog_newwatermeter)
        dialognewwatermeter.text = "${getString(R.string.Enddateinvoice)} : $dateselectend"
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
                "ใบแจ้ง$type",
                        "\nที่อยู่ : $homeid" +
//                        "\nประเภท : $buildingtype" +
//                        "\nชื่อผู้ใช้น้ำ : $name" +
//                        "\nเบอร์โทร : $telnum" +
                        "\nวันที่แจ้ง : ${pubF.getDatenow()}" +
                        "\nวันครบชำระ : ${timeendinput?.text.toString()}" +
                        "\nรวมเงินครั้งนี้ : $amount" +
                        "\nค้างชำระ : 0 บาท" +
                        "\nรวมเงินที่ต้องชำระทั้งสิ้น : $amount บาท"
            )
            Postinvoice(false, Strprint)

            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
        }


        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }


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

                    val dateTime = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss")
                    val date = SimpleDateFormat("dd/MM/yyyy")

                    //            Log.d("dateee", datestart.hours.toString())
                    //            Log.d("dateee", datestart.minutes.toString())
                    //            Log.d("dateee", datestart.seconds.toString())

                    timestartinput!!.setText(date.format(datestart))
                    dateTimeselectstart = dateTime.format(datestart)
                    Log.d("dateee", dateTimeselectstart)
                    dateselectstart = date.format(datestart)

                    timeendinput!!.setText(date.format(dateend))
                    dateTimeselectend = dateTime.format(dateend)
                    Log.d("dateee", dateTimeselectend)
                    dateselectend = date.format(dateend)

                    //            val dateFormat = DateFormat.getDateFormat(this)
                    //            Log.d("dateee", dateFormat.format(datestart))
                    //            Log.d("dateee", dateFormat.format(dateend))
                }
            }
        }).start()


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
//        credit = "100"
        payment.credit = credit
//        via = "เจ้าหน้าที่"
        payment.via = via
        payment.creditdate = ""
        payment.remain = "0"
        payment.receiveName = ""

        dataInvoice.ref_1 = homeid
        dataInvoice.ref_2 = ""
        dataInvoice.setmeterID("")
        dataInvoice.setmeterVal("")
        dataInvoice.category = type
        dataInvoice.setstartDate(dateTimeselectstart)
        dataInvoice.setdueDate(dateTimeselectend)
        dataInvoice.setgps(gpslocation)
        dataInvoice.setpayment(payment)


        val strCSV = "$homeid,,$type,$amount,$dateselectstart,$dateselectend,," +
                ",${gpslocation.latitude},${gpslocation.longitude},ค้างชำระ"

        retrofitCallfuntion.addinvoice(this, dataInvoice, null, finish, strprint, strCSV)
    }




    override fun onDestroy() {
        super.onDestroy()
        pubF.lat = ""
        pubF.long = ""
    }
}
