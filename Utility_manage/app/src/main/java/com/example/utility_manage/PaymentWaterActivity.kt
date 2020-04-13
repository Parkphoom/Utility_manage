package com.example.utility_manage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.example.utility_manage.Retrofit.retrofitData
import com.example.utility_manage.Retrofit.retrofitUpload
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.telpo.tps550.api.printer.UsbThermalPrinter
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
                    pubF.Printheader = "ใบเสร็จรับเงินค่าน้ำประปา"
                    pubF.Printcontent = "\nที่อยู่ : $homeid" +
                            "\nเลขมาตรครั้งก่อน : $startvalues" +
                            "\nเลขมาตรครั้งนี้ : $values" +
                            "\nเลขมิเตอร์ : $meterid" +
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
                    pubF.Printheader = "ใบแจ้งค่าน้ำประปา"
                    pubF.Printcontent = "\nที่อยู่ : $homeid" +
                            "\nเลขมาตรครั้งก่อน : $startvalues" +
                            "\nเลขมาตรครั้งนี้ : $values" +
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


    private fun checkisempty(): Boolean {
        if (meterIdinput!!.text.toString().isEmpty() && homeidinput!!.text.toString().isEmpty()) {
            meterIdlayout!!.error = resources.getString(R.string.gettexterror)
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            pubF.message(getString(R.string.gettexthint), this)
            return false
        }
        if (startvaluesinput!!.text.toString().isEmpty() ) {
            startvalueslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (valuesinput!!.text.toString().isEmpty() ) {
            valueslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (amountinput!!.text.toString().isEmpty() ) {
            amountlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }

        else {
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


}
