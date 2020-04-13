package com.example.utility_manage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var valuesbtn: Button? = null
    var registerbtn: Button? = null
    var paymentwaterbtn: Button? = null
    var paymentbtn: Button? = null
    var savedatabtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUI()


    }

    fun setUI() {
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
                val intent = Intent(this, PaymentWaterActivity::class.java)
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
        }

    }
}
