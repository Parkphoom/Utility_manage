package com.example.utility_manage

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.telpo.tps550.api.printer.ThermalPrinter
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
                    pubF.Printheader = "ใบเสร็จรับเงินค่าบริการ"
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
                }
            }
            R.id.printinvoicebtn -> {
                if (checkisempty()) {
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


}
