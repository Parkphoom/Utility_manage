package com.wac.utility_manage.Fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bitvale.switcher.SwitcherX
import com.example.utility_manage.R
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.Data.addInvoiceData
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentOtherinvoice : Fragment(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var prtF: Printfuntion
    private var dataInvoice = addInvoiceData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion

    var setdaterange: LinearLayout? = null
    private var typepaylayout: TextInputLayout? = null
    private var typepayinput: AutoCompleteTextView? = null
    private var amountlayout: TextInputLayout? = null
    private var amountinput: TextInputEditText? = null
    var timeendinput: TextView? = null
    private var txthomdid: TextView? = null
    private var txtbuildingtype: TextView? = null
    private var txtname: TextView? = null
    private var txttelnum: TextView? = null
    private var Switcher: SwitcherX? = null
    private var printinvoice: Button? = null

    var dateTimeselectstart: String? = null
    var dateTimeselectend: String? = null
    var dateselectstart: String? = null
    var dateselectend: String? = null
    private var type: String? = ""
    private var amount: String? = ""
    private var via: String = ""
    private var credit: String = "0"
    var dateTimeselect: String? = null
    private var turnonprint: Boolean = true

    companion object {
        private var homeid: String = ""
        private var meterid: String = ""
        private var buildingtype: String = ""
        private var name: String = ""
        private var telnum: String = ""
        private var oldwatermeter: String = ""

        fun newInstance(): FragmentOtherinvoice = FragmentOtherinvoice()

        fun newInstance(data: JSONObject, user: JSONObject): FragmentOtherinvoice =
            FragmentOtherinvoice().apply {
                homeid = data.get("address").toString()
                meterid = data.get("meterId").toString()
                buildingtype = data.get("buildingType").toString()
                name = user.get("name").toString()
                telnum = user.get("tel").toString()
                oldwatermeter = data.get("meterVal").toString()


                Log.d("refresh", homeid)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_otherinvoice, container, false)

        setUI(root)

        val prefs =
            activity?.getSharedPreferences(getString(R.string.PrefsLogin), Context.MODE_PRIVATE)
        val set: MutableSet<String>? = prefs?.getStringSet(getString(R.string.CategorySet), null)

        var typelist: MutableList<String> = ArrayList()
        if (set != null) {
            for (element in set) {
                Log.d("typelist", element)
                if (element != "ค่าน้ำประปา") {
                    typelist.add(element)
                }
            }
        } else {
            typelist.add("ค่าขยะ")
            typelist.add("ค่าส่วนกลาง")
            typelist.add("ค่าภาษีโรงเรือน")
            typelist.add("ค่าภาษีป้าย")
            typelist.add("ค่าภาษีที่ดิน")
            typelist.add("ค่าภาษีธุรกิจเฉพาะ")
        }


        val typeDataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this.activity!!,
            android.R.layout.simple_spinner_item,
            typelist
        )

        val editTextFilledExposedDropdown: AutoCompleteTextView =
            root.findViewById(R.id.filled_exposed_dropdown)
        editTextFilledExposedDropdown.setAdapter(typeDataAdapter)

        (activity as MainFragmentinvoice?)!!.setFragmentRefreshListener2(object :
            MainFragmentinvoice.FragmentRefreshListener {
            override fun onRefresh() {
                // Refresh Your Fragment
                Log.d("refresh_1", "in")
                Log.d("refresh_1", FragmentWaterinvoice.homeid)
                settext()

            }
        })

        Switcher!!.setOnCheckedChangeListener { checked ->
            if (checked) {
                turnonprint = true
                Log.d("checked", turnonprint.toString())
            } else {
                turnonprint = false
                Log.d("checked", turnonprint.toString())
            }
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        // Check should we need to refresh the fragment
        settext()
    }


    private fun setUI(root: View) {
        pubF = Publicfunction()
        prtF = Printfuntion()

        retrofitCallfuntion = retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity!!)

        Switcher = root.findViewById(R.id.switcher)
        txthomdid = root.findViewById(R.id.homeid)
        txtbuildingtype = root.findViewById(R.id.buildingtype)
        txtname = root.findViewById(R.id.name)
        txttelnum = root.findViewById(R.id.telnum)
        setdaterange = root.findViewById(R.id.setdaterangelayout)
        setdaterange!!.setOnClickListener(this)
        timeendinput = root.findViewById(R.id.txtTimeEnd)
        typepaylayout = root.findViewById(R.id.typepay_text_layout)
        typepayinput = root.findViewById(R.id.filled_exposed_dropdown)
//        pubF.setOntextchange(this, typepayinput!!, typepaylayout!!)
        amountlayout = root.findViewById(R.id.amount_text_layout)
        amountinput = root.findViewById(R.id.amount_text_input)
        pubF.setOntextchange(this.activity!!, amountinput!!, amountlayout!!)

        printinvoice = root.findViewById(R.id.printinvoicebtn)
        printinvoice!!.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.setdaterangelayout -> {
                Setdaterange()
            }
            R.id.printinvoicebtn -> {
                if (pubF.lat.isEmpty() || pubF.long.isEmpty()) {
                    pubF.obtieneLocalizacion(this.activity!!, pubF.fusedLocationClient)
                }
                if (checkisempty()) {
                    if (turnonprint) {
                        createdialog(
                            "ใบแจ้ง$type",
                            "(ไม่ใช่ใบเสร็จรับเเงิน)",
                            "พิมพ์ใบแจ้งหนี้"
                        )
                    } else {
                        val Strprint = listOf("", "")
                        Postinvoice(true, Strprint)
                    }
//                    createdialog(
//                        "ใบแจ้ง${typepayinput!!.text}",
//                        "(ไม่ใช่ใบเสร็จรับเเงิน)",
//                        getString(R.string.printslip)
//                    )
                }
            }
        }

    }

    fun settext() {

        txthomdid!!.text = homeid
        txtbuildingtype!!.text = buildingtype
        txtname!!.text = name
        txttelnum!!.text = telnum

    }

    private fun Setdaterange() {
        Thread(Runnable {
            activity!!.runOnUiThread {
                //your code or your request that you want to run on uiThread
                val builder = MaterialDatePicker.Builder.datePicker()
                val constraintsBuilder = CalendarConstraints.Builder()
                builder.setCalendarConstraints(constraintsBuilder.build())
                val picker = builder.build()
                picker.show(activity!!.supportFragmentManager, picker.toString())
                Log.d("dateee", picker.toString())
                picker.addOnPositiveButtonClickListener { selection -> //Do something...
                    Log.d("dateee", picker.headerText)
                    Log.d("dateee", selection.toString())
                    val dateend = Date(selection)

                    val dateTime = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss")
                    val date = SimpleDateFormat("dd/MM/yyyy")

                    timeendinput!!.text = date.format(dateend)
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

    private fun checkisempty(): Boolean {
        if (typepayinput!!.text.toString().isEmpty()) {
            typepaylayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }

        if (amountinput!!.text.toString().isEmpty()) {
            amountlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (timeendinput!!.text.toString().isEmpty()) {
            pubF.message(getString(R.string.SelectdateAlert), FancyToast.ERROR, activity)
            return false
        } else {
            type = typepayinput!!.text.toString()
            homeid = txthomdid!!.text.toString()
            amount = amountinput!!.text.toString()
            dateTimeselectstart = pubF.getDatetimenow()
            dateTimeselectend = dateselectend.toString()

            return true
        }
    }

    private fun createdialog(strheader1: String, strheader2: String, print: String) {
        val builder = AlertDialog.Builder(
            activity,
            R.style.CustomDialog
        )
        val view: View = LayoutInflater.from(activity).inflate(R.layout.dialog_payment, null)

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
        dialogmeterid.visibility = View.GONE
//        dialogmeterid.text = "${getString(R.string.amount)} : $amount"
        val dialogoldwatermeter = view.findViewById<TextView>(R.id.dialog_oldwatermeter)
        dialogoldwatermeter.text = "${getString(R.string.Startdateinvoice)} : ${pubF.getDatenow()}"
        val dialognewwatermeter = view.findViewById<TextView>(R.id.dialog_newwatermeter)
        dialognewwatermeter.text =
            "${getString(R.string.Enddateinvoice)} : ${timeendinput?.text.toString()}"
        val dialogamount = view.findViewById<TextView>(R.id.dialog_amount)
        dialogamount.text = "${getString(R.string.amount)} : $amount บาท"

        val closebtn = view.findViewById<ImageButton>(R.id.dialog_closebtn)
        closebtn.setOnClickListener {
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
        }
        val printbtn = view.findViewById<Button>(R.id.printbtn)
        printbtn.text = print
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

        retrofitCallfuntion.addinvoice(this.activity!!, dataInvoice, null, finish, strprint, strCSV)
    }

    override fun onDestroy() {
        super.onDestroy()
        pubF.lat = ""
        pubF.long = ""
    }

}
