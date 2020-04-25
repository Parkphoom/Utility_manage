package com.wac.utility_manage

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.R
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.regisMemberData
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.retrofitCallfuntion

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private var dataRegister =
        regisMemberData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion

    private var submitbtn: Button? = null

    private var homeidlayout: TextInputLayout? = null
    private var homeidinput: TextInputEditText? = null
    private var meteridlayout: TextInputLayout? = null
    private var meteridinput: TextInputEditText? = null
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
    private var startvalues: String = ""
    private var name: String = ""
    private var telnum: String = ""
    private var typepay: String = ""
    private var buildingtype: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setUI()

    }

    override fun onStart() {
        super.onStart()
        pubF.obtieneLocalizacion(this, pubF.fusedLocationClient)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {

            R.id.submitbtn -> {

                if (pubF.lat.isEmpty() || pubF.long.isEmpty()) {
                    pubF.Slideleft(this)
                }

                if (checkisempty()) {

                    val gpslocation =
                        GpsObj()
                    gpslocation.latitude = pubF.lat
                    gpslocation.longitude = pubF.long

                    dataRegister.address = homeid
                    dataRegister.meterid = meterid
                    dataRegister.setgps(gpslocation)
                    dataRegister.setmeterVal(startvalues)
                    dataRegister.name = name
                    dataRegister.tel = telnum
                    dataRegister.buildingtype = buildingtype
                    val status = "user"
                    val password = "123456"

                    val strCSV =
                        "$homeid,$buildingtype,${gpslocation.latitude},${gpslocation.longitude}," +
                                "$meterid,$name,$telnum,$startvalues,$status,$password"

                    retrofitCallfuntion.regismember(this, dataRegister, strCSV)
                }
            }
        }
    }


    private fun setUI() {
        pubF = Publicfunction()
        retrofitCallfuntion =
            retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout()
            .setActionBar(this.resources.getString(R.string.headerregister), actionBar)
        pubF.Slideleft(this)

        submitbtn = findViewById(R.id.submitbtn)
        submitbtn!!.setOnClickListener(this)
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


    private fun checkisempty(): Boolean {
        if (homeidinput!!.text.toString().isEmpty()) {
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (meteridinput!!.text.toString().isEmpty()) {
            meteridlayout!!.error = resources.getString(R.string.gettexterror)
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
        } else {
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


    override fun onDestroy() {
        super.onDestroy()
        pubF.lat = ""
        pubF.long = ""

        homeid = ""
        meterid = ""
        startvalues = ""
        name = ""
        telnum = ""
        typepay = ""
        buildingtype = ""

        homeidinput!!.setText("")
        meteridinput!!.setText("")
        startvaluesInput!!.setText("")
        nameInput!!.setText("")
        telnumInput!!.setText("")
        buildingtypeinput!!.setText("")
    }


}
