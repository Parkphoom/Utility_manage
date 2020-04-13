package com.example.utility_manage

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
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
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.telpo.tps550.api.printer.UsbThermalPrinter


class PaymentWaterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pubF: Publicfunction
    private lateinit var mCurrentPhotoPath: String

    private var homeidinput: TextInputEditText? = null
    private var homeidlayout: TextInputLayout? = null
    private var meterIdinput: TextInputEditText? = null
    private var meterIdlayout: TextInputLayout? = null
    private var startvaluesinput: TextInputEditText? = null
    private var startvalueslayout: TextInputLayout? = null
    private var valuesinput: TextInputEditText? = null
    private var valueslayout: TextInputLayout? = null
    private var amountinput: TextInputEditText? = null
    private var amountlayout: TextInputLayout? = null


    private var printsilp: Button? = null
    private var printinvoice: Button? = null

    private var homeid: String? = ""
    private var meterid: String? = ""
    private var startvalues: String? = ""
    private var values: String? = ""
    private var amount: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_water)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUI()

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.printpaysilpbtn -> {
                if (checkisempty()) {
                    if (checkisempty()) {
                        if (startvalues!!.toInt() < values!!.toInt()) {
                            createdialog("ใบเสร็จค่าน้ำประปา","(ไม่ใช่ใบแจ้งหนี้)",getString(R.string.printslip))

                        } else {
                            pubF.message("เลขมาตรผิดพลาด", this)
                        }

                    }
                }
            }
            R.id.printinvoicebtn -> {
                if (checkisempty()) {
                    if (startvalues!!.toInt() < values!!.toInt()) {
                        createdialog("ใบแจ้งค่าน้ำประปา","(ไม่ใช่ใบเสร็จรับเงิน)",getString(R.string.printinvoices))

                    } else {
                        pubF.message("เลขมาตรผิดพลาด", this)
                    }

                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUI() {
        pubF = Publicfunction()
        pubF.handler = pubF.MyHandler(this)
        pubF.mUsbThermalPrinter = UsbThermalPrinter(this)

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(
            this.resources.getString(R.string.headerpaymentwater),
            actionBar
        )

        homeidinput = findViewById(R.id.homeid_text_input)
        homeidlayout = findViewById(R.id.homeid_text_layout)
        pubF.setOntextchange(this, homeidinput!!, homeidlayout!!)
        meterIdinput = findViewById(R.id.meterId_text_input)
        meterIdlayout = findViewById(R.id.meterId_text_layout)
        pubF.setOntextchange(this, meterIdinput!!, meterIdlayout!!)
        startvaluesinput = findViewById(R.id.startvaluesId_text_input)
        startvalueslayout = findViewById(R.id.startvaluesId_text_layout)
        pubF.setOntextchange(this, startvaluesinput!!, startvalueslayout!!)
        valuesinput = findViewById(R.id.valuesId_text_input)
        valueslayout = findViewById(R.id.valuesId_text_layout)
        pubF.setOntextchange(this, valuesinput!!, valueslayout!!)
        amountinput = findViewById(R.id.amount_text_input)
        amountlayout = findViewById(R.id.amount_text_layout)
        pubF.setOntextchange(this, amountinput!!, amountlayout!!)

        printsilp = findViewById(R.id.printpaysilpbtn)
        printsilp!!.setOnClickListener(this)
        printinvoice = findViewById(R.id.printinvoicebtn)
        printinvoice!!.setOnClickListener(this)

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
            pubF.Printheader = "ใบแจ้งค่าน้ำประปา"
            pubF.Printcontent = "\nที่อยู่ : $homeid" +
                    "\nเลขมาตรครั้งก่อน : $startvalues" +
                    "\nเลขมาตรครั้งนี้ : $values" +
                    "\nเลขจากมิเตอร์ : $meterid" +
                    "\nรวมค่าชำระ : $amount บาท"

            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
            pubF.handler!!.sendMessage(
                pubF.handler!!.obtainMessage(
                    PublicValues().PRINTCONTENT,
                    1,
                    0,
                    null
                )
            )
        }

        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }


    private fun checkisempty(): Boolean {
        if (meterIdinput!!.text.toString().isEmpty() && homeidinput!!.text.toString().isEmpty()) {
            meterIdlayout!!.error = resources.getString(R.string.gettexterror)
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            pubF.message(getString(R.string.gettexthint), this)
            return false
        }
        if (startvaluesinput!!.text.toString().isEmpty()) {
            startvalueslayout!!.error = resources.getString(R.string.gettexterror)
            homeidlayout!!.isErrorEnabled = false
            meterIdlayout!!.isErrorEnabled = false
            return false
        }
        if (valuesinput!!.text.toString().isEmpty()) {
            valueslayout!!.error = resources.getString(R.string.gettexterror)
            homeidlayout!!.isErrorEnabled = false
            meterIdlayout!!.isErrorEnabled = false
            return false
        }
        if (amountinput!!.text.toString().isEmpty()) {
            amountlayout!!.error = resources.getString(R.string.gettexterror)
            homeidlayout!!.isErrorEnabled = false
            meterIdlayout!!.isErrorEnabled = false
            return false
        } else {
            homeidlayout!!.isErrorEnabled = false
            meterIdlayout!!.isErrorEnabled = false
            homeid = homeidinput!!.text.toString()
            meterid = meterIdinput!!.text.toString()
            startvalues = startvaluesinput!!.text.toString()
            values = valuesinput!!.text.toString()
            amount = amountinput!!.text.toString()

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

}
