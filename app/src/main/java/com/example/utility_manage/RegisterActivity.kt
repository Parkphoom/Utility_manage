package com.example.utility_manage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.example.utility_manage.Retrofit.Data.addInvoiceData
import com.example.utility_manage.Retrofit.Data.regisMemberData
import com.example.utility_manage.Retrofit.Jsobj
import com.example.utility_manage.Retrofit.retrofitUpload
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pubF: Publicfunction
    private  var dataRegister = regisMemberData()
    private lateinit var retrofitUpload: retrofitUpload

    private var submitbtn: Button? = null

    private var homeidlayout: TextInputLayout? = null
    private var homeidinput: TextInputEditText? = null
    private var meteridlayout: TextInputLayout? = null
    private var meteridinput: TextInputEditText? = null
    private var gpslayout: TextInputLayout? = null
    private var gpsInput: TextInputEditText? = null
    private var startvalueslayout: TextInputLayout? = null
    private var startvaluesInput: TextInputEditText? = null
    private var namelayout: TextInputLayout? = null
    private var nameInput: TextInputEditText? = null
    private var telnumlayout: TextInputLayout? = null
    private var telnumInput: TextInputEditText? = null
    private var buildingtypelayout: TextInputLayout? = null
    private var buildingtypeinput: TextInputEditText? = null

    private var homeid: String = ""
    private var meterid: String = ""
    private var lat: String = ""
    private var long: String = ""
    private var startvalues: String = ""
    private var name: String = ""
    private var telnum: String = ""
    private var typepay: String = ""
    private var buildingtype: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUI()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {
            R.id.gps_text_input -> {
                obtieneLocalizacion(this, fusedLocationClient, gpsInput)
            }
            R.id.submitbtn -> {
                if (checkisempty()) {

                    val gpslocation = Jsobj()
                    gpslocation.latitude = lat
                    gpslocation.longitude = long

                    dataRegister.address = homeid
                    dataRegister.meterid = meterid
                    dataRegister.setgps(gpslocation)
                    dataRegister.setwaterMeter(startvalues)
                    dataRegister.name = name
                    dataRegister.tel = telnum
                    dataRegister.buildingtype = buildingtype

                    retrofitUpload.regismember(this, dataRegister)
                }
            }
        }
    }

    private fun setUI() {
        pubF = Publicfunction()
        retrofitUpload = retrofitUpload()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headerregister), actionBar)

        submitbtn = findViewById(R.id.submitbtn)
        submitbtn!!.setOnClickListener(this)

        gpsInput = findViewById(R.id.gps_text_input)
        gpslayout = findViewById(R.id.gps_text_layout)
        gpsInput!!.setOnClickListener(this)
        homeidlayout = findViewById(R.id.homeid_text_layout)
        homeidinput = findViewById(R.id.homeid_text_input)
        pubF.setOntextchange(this, homeidinput!!, homeidlayout!!)
        meteridlayout = findViewById(R.id.meterId_text_layout)
        meteridinput = findViewById(R.id.meterId_text_input)
        pubF.setOntextchange(this, meteridinput!!, meteridlayout!!)
        startvalueslayout = findViewById(R.id.valuesId_text_layout)
        startvaluesInput = findViewById(R.id.valuesId_text_input)
        pubF.setOntextchange(this, startvaluesInput!!, startvalueslayout!!)
        nameInput = findViewById(R.id.name_text_input)
        namelayout = findViewById(R.id.name_text_layout)
        pubF.setOntextchange(this, nameInput!!, namelayout!!)
        telnumlayout = findViewById(R.id.telnum_text_layout)
        telnumInput = findViewById(R.id.telnum_text_input)
        pubF.setOntextchange(this, telnumInput!!, telnumlayout!!)
        buildingtypelayout = findViewById(R.id.buildingtype_text_layout)
        buildingtypeinput = findViewById(R.id.buildingtype_text_input)
        pubF.setOntextchange(this, buildingtypeinput!!, buildingtypelayout!!)


    }


    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    fun obtieneLocalizacion(
        activity: Activity,
        fusedLocationClient: FusedLocationProviderClient,
        gpsInput: TextInputEditText?
    ) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PublicValues().GPS_LOCATION_REQUEST
            )

        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    gpsInput!!.setText("${location?.latitude} : ${location?.longitude}")
                    lat = location?.latitude.toString()
                    long = location?.longitude.toString()
                } else {
                    pubF.message("Location error", activity)
                }
            }
        }
    }

    private fun checkisempty(): Boolean {
        if (homeidinput!!.text.toString().isEmpty()) {
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (meteridinput!!.text.toString().isEmpty()) {
            meteridlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (gpsInput!!.text.toString().isEmpty()) {
            gpslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (startvaluesInput!!.text.toString().isEmpty()) {
            startvalueslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (telnumInput!!.text.toString().isEmpty()) {
            telnumlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (nameInput!!.text.toString().isEmpty()) {
            namelayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (buildingtypeinput!!.text.toString().isEmpty()) {
            buildingtypelayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        else {
            homeid = homeidinput!!.text.toString()
            meterid = meteridinput!!.text.toString()
            startvalues = startvaluesInput!!.text.toString()
            name = nameInput!!.text.toString()
            telnum = telnumInput!!.text.toString()
            buildingtype = buildingtypeinput!!.text.toString()
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
