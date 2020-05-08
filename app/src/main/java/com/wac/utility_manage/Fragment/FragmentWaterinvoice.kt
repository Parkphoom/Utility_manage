package com.wac.utility_manage.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.bitvale.switcher.SwitcherX
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.utility_manage.R
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.Data.addInvoiceData
import com.wac.utility_manage.Retrofit.Data.findMeterWaterData
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class FragmentWaterinvoice : Fragment(), View.OnClickListener {

    private var pictureImagePath = ""
    private val Image_Capture_Code2 = 2


    private var newwatermeter: String = ""
    private var type: String = ""
    private var amount: String = ""
    private var credit: String = "0"
    private var startinvoice: String = ""
    private var endinvoice: String = ""
    private var via: String = ""
    var imageFinance: MultipartBody.Part? = null

    var homeidinput: TextView? = null
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
    var timeendinput: TextView? = null

    private var turnonprint: Boolean = true
    private var Switcher: SwitcherX? = null
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


    companion object {
        public var homeid: String = ""
        private var meterid: String = ""
        private var buildingtype: String = ""
        private var name: String = ""
        private var telnum: String = ""
        private var oldwatermeter: String = ""

        fun newInstance(): FragmentWaterinvoice = FragmentWaterinvoice()

        fun newInstance(data: JSONObject, user: JSONObject): FragmentWaterinvoice =
            FragmentWaterinvoice().apply {
                homeid = data.get("address").toString()
                meterid = data.get("meterId").toString()
                buildingtype = data.get("buildingType").toString()
                name = user.get("name").toString()
                telnum = user.get("tel").toString()
                oldwatermeter = data.get("meterVal").toString()

                Log.d("refresh-1", homeid)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_waterinvoice, container, false)
        setUI(root)

        (activity as MainFragmentinvoice?)!!.setFragmentRefreshListener(object :
            MainFragmentinvoice.FragmentRefreshListener {
            override fun onRefresh() {
                // Refresh Your Fragment
                Log.d("refresh_1", "in")
                Log.d("refresh_1", homeid)
                settext()

            }
        })


        Switcher!!.setOnCheckedChangeListener { checked ->
            if (checked) {
                turnonprint = true
                Log.d("checked", turnonprint.toString())
            }
            else{
                turnonprint = false
                Log.d("checked", turnonprint.toString())
            }
        }
        return root
    }


    fun settext() {

        homeidinput!!.text = homeid
        meteridinput!!.text = meterid
        buildingtypeinput!!.text = buildingtype
        nameinput!!.text = name
        telnuminput!!.text = telnum
        oldwatermeterinput!!.text = oldwatermeter

    }

    override fun onStart() {
        super.onStart()
        pubF.obtieneLocalizacion(activity as Activity, pubF.fusedLocationClient)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        choosePhotoHelper!!.onActivityResult(requestCode, resultCode, data)

    }


    private fun setUI(root: View) {
        pubF = Publicfunction()
        prtF = Printfuntion()
        retrofitCallfuntion =
            retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(root.context)

        setdaterangeexport = root.findViewById(R.id.setdaterangeexport)
        setdaterangeexport!!.setOnClickListener(this)
        timeendinput = root.findViewById(R.id.txtTimeEnd)
        cambtn = root.findViewById(R.id.opencambtn)
        cambtn!!.setOnClickListener(this)
        submitbtn = root.findViewById(R.id.submitbtn)
        submitbtn!!.setOnClickListener(this)
        printinvoice = root.findViewById(R.id.printinvoicebtn)
        printinvoice!!.setOnClickListener(this)

        imgcam = root.findViewById(R.id.imgfromcam)
        Switcher = root.findViewById(R.id.switcher)
        homeidinput = root.findViewById(R.id.homeid_text_input)
        meteridinput = root.findViewById(R.id.meterId_text_input)
        buildingtypeinput = root.findViewById(R.id.buildingtype_text_input)
        nameinput = root.findViewById(R.id.name_text_input)
        telnuminput = root.findViewById(R.id.telnum_text_input)
        oldwatermeterinput = root.findViewById(R.id.oldwatermeter_text_input)
        valuesidlayout = root.findViewById(R.id.valuesId_text_layout)
        valuesidinput = root.findViewById(R.id.valuesId_text_input)
        pubF.setOntextchange(this!!.activity!!, valuesidinput!!, valuesidlayout!!)
        typepayidlayout = root.findViewById(R.id.typepay_text_layout)
        typepayidinput = root.findViewById(R.id.typepay_text_input)
        pubF.setOntextchange(this!!.activity!!, typepayidinput!!, typepayidlayout!!)
        amountlayout = root.findViewById(R.id.amount_text_layout)
        amountinput = root.findViewById(R.id.amount_text_input)
        pubF.setOntextchange(this!!.activity!!, amountinput!!, amountlayout!!)

        Photohelper(this, imgcam!!)

        val floatingActionButton =
            root.findViewById<View>(R.id.opencambtn) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            choosePhotoHelper!!.showChooser()
        }

    }

    public fun Photohelper(fragment: Fragment, view: ImageView) {
        choosePhotoHelper = ChoosePhotoHelper.with(fragment)
            .asFilePath()
            .build(ChoosePhotoCallback {
                Glide.with(fragment)
                    .load(it)
                    .apply(RequestOptions.placeholderOf(R.drawable.icons8_image_200px))
                    .into(view)
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

    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {


            R.id.submitbtn -> {
                if (checkisempty()) {
                    val Strprint = listOf("", "")
                    Postinvoice(true, Strprint)
                }
            }
            R.id.printinvoicebtn -> {
                if (pubF.lat.isEmpty() || pubF.long.isEmpty()) {
                    activity?.let { pubF.obtieneLocalizacion(it, pubF.fusedLocationClient) }
                }
                if (checkisempty()) {
                    if (turnonprint) {
                        createdialog(
                            "ใบแจ้งค่าน้ำประปา",
                            "(ไม่ใช่ใบเสร็จรับเเงิน)",
                            "พิมพ์ใบแจ้งหนี้"
                        )
                    } else {
                        val Strprint = listOf("", "")
                        Postinvoice(true, Strprint)
                    }


                }
            }
            R.id.setdaterangeexport -> {
                Setdaterange()
            }
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
        if (timeendinput!!.text.toString()
                .isEmpty()
        ) {
            pubF.message(getString(R.string.SelectdateAlert), FancyToast.CONFUSING, activity)
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
            startinvoice = pubF.getDatetimenow()
            endinvoice = dateselectend.toString()

            return true
        }
    }

    var dateTimeselect: String? = null
    var dateselectstart: String? = null
    var dateselectend: String? = null
    private fun Setdaterange() {
        Thread(Runnable {
            activity?.runOnUiThread {
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

        activity?.let {
            retrofitCallfuntion.addinvoice(
                it,
                dataInvoice,
                imageFinance,
                finish,
                strprint,
                strCSV
            )
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
                        "\nวันที่แจ้งค่าน้ำ : ${pubF.getDatenow()}" +
                        "\nวันครบชำระ : ${timeendinput?.text.toString()}" +
                        "\nรวมเงินครั้งนี้ : $amount" +
                        "\nค่าน้ำค้างชำระ : 0 บาท" +
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


}
