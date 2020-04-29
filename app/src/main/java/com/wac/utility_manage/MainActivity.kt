package com.wac.utility_manage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.R
import com.wac.utility_manage.Fragment.MainFragmentinvoice
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.Retrofit.retrofitCallfuntion


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var retrofitCallfuntion: retrofitCallfuntion

    var valuesbtn: Button? = null
    var registerbtn: Button? = null
    var paymentwaterbtn: Button? = null
    var paymentbtn: Button? = null
    var savedatabtn: Button? = null
    var otherbtn: Button? = null

    var logoutbtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences(getString(R.string.PrefsLogin), Context.MODE_PRIVATE)
        val name = prefs.getBoolean(getString(R.string.Status_login), false)
        Log.d("ressss", name.toString())
    }

    override fun onStart() {
        super.onStart()
        setUI()
    }

    fun setUI() {
        pubF = Publicfunction()
        retrofitCallfuntion =
            retrofitCallfuntion()

        valuesbtn = findViewById(R.id.savevaluebtn)
        valuesbtn!!.setOnClickListener(this)
        registerbtn = findViewById(R.id.registerbtn)
        registerbtn!!.setOnClickListener(this)
        paymentwaterbtn = findViewById(R.id.paywaterbtn)
        paymentwaterbtn!!.setOnClickListener(this)
        paymentbtn = findViewById(R.id.paymentbtn)
        paymentbtn!!.setOnClickListener(this)
        savedatabtn = findViewById(R.id.savedatabtn)
        savedatabtn!!.setOnClickListener(this)
        otherbtn = findViewById(R.id.otherbtn)
        otherbtn!!.setOnClickListener(this)

        logoutbtn = findViewById(R.id.logout)
        logoutbtn!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {
            R.id.savevaluebtn -> {
                val intent = Intent(this, SavevaluesActivity::class.java)
//                intent.putExtra("key", value)
                startActivity(intent)
            }
            R.id.registerbtn -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.paywaterbtn -> {
                val intent = Intent(this, ScannerActivity::class.java)
                startActivity(intent)
            }
            R.id.paymentbtn -> {
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            }
            R.id.savedatabtn -> {
                val intent = Intent(this, SavedataActivity::class.java)
                startActivity(intent)
            }
            R.id.logout ->{

            }
            R.id.otherbtn ->{
                val intent = Intent(this, MainFragmentinvoice::class.java)
                startActivity(intent)
            }
        }

    }



}
