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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.telpo.tps550.api.printer.UsbThermalPrinter


class PaymentActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private var typepaylayout: TextInputLayout? = null
    private var typepayinput: TextInputEditText? = null
    private var homeidlayout: TextInputLayout? = null
    private var homeidinput: TextInputEditText? = null
    private var amountlayout: TextInputLayout? = null
    private var amountinput: TextInputEditText? = null

    private var printsilp: Button? = null
    private var printinvoice: Button? = null



    private var type: String? = ""
    private var homeid: String? = ""
    private var amount: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        setUI()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.printpaysilpbtn -> {
                if (checkisempty()) {
                    createdialog("ใบเสร็จค่าบริการ","",getString(R.string.printslip))

                }
            }
            R.id.printinvoicebtn -> {
                if (checkisempty()) {
                    createdialog("ใบแจ้งค่าบริการ","(ไม่ใช่ใบเสร็จรับเงิน)",getString(R.string.printinvoices))

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


        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headerpayment), actionBar)

        typepaylayout = findViewById(R.id.typepay_text_layout)
        typepayinput = findViewById(R.id.typepay_text_input)
        pubF.setOntextchange(this, typepayinput!!, typepaylayout!!)
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
        } else {
            type = typepayinput!!.text.toString()
            homeid = homeidinput!!.text.toString()
            amount = amountinput!!.text.toString()
            return true
        }
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
        val closebtn = view.findViewById<ImageButton>(R.id.dialog_closebtn)
        closebtn.setOnClickListener {
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
        }
        val printbtn = view.findViewById<Button>(R.id.printbtn)
        printbtn.setText(print)
        printbtn.setOnClickListener {
            pubF.mUsbThermalPrinter = UsbThermalPrinter(this)

            pubF.Printheader = "ใบแจ้งค่าบริการ"
            pubF.Printheader2 = ""
            pubF.Printcontent = "\nประเภท : $type" +
                    "\nรวมค่าชำระ : $amount บาท"

            pubF.handler!!.sendMessage(
                pubF.handler!!.obtainMessage(
                    PublicValues().PRINTCONTENT,
                    1,
                    0,
                    null
                )
            )
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
        }

        alert.show()
        val window = alert.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }



}
