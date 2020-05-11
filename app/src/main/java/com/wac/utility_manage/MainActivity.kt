package com.wac.utility_manage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.R
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.Fragment.MainFragmentinvoice
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import com.wac.utility_manage.maplocation.MapsActivity
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var backButtonCount: Int = 0
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
        paymentbtn = findViewById(R.id.updatebtn)
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
            R.id.updatebtn -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
            R.id.savedatabtn -> {
                val intent = Intent(this, SavedataActivity::class.java)
                startActivity(intent)
            }
            R.id.logout ->{
                val editor: SharedPreferences.Editor = this.getSharedPreferences(
                    this.getString(R.string.PrefsLogin),
                    Context.MODE_PRIVATE
                ).edit()
                editor.putBoolean(this.getString(R.string.Status_login), false)
                editor.putString(this.getString(R.string.Admin_username), "")
                editor.putString(this.getString(R.string.Admin_password), "")
                editor.putString(this.getString(R.string.Admin_name), "")
                editor.putString(this.getString(R.string.Admin_telnum), "")
                editor.putString(this.getString(R.string.Admin_email), "")
                editor.putString(this.getString(R.string.Admin_id), "")
                editor.apply()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.otherbtn ->{
                val intent = Intent(this, MainFragmentinvoice::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onBackPressed() {
        if (backButtonCount >= 1) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            pubF.message("Press the back button once again to close the application.",FancyToast.INFO,this)

            backButtonCount++
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    backButtonCount = 0
                }
            }, 2000)
        }
    }

}
