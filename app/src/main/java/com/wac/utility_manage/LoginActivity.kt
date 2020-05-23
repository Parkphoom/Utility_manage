package com.wac.utility_manage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.Retrofit.Data.loginData
import com.wac.utility_manage.Retrofit.retrofitCallback
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var retrofitCallfuntion: retrofitCallfuntion
    private var logindata = loginData()
    private var pubF = Publicfunction()

    private var usernamelayout: TextInputLayout? = null
    private var usernameinput: TextInputEditText? = null
    private var passwordlayout: TextInputLayout? = null
    private var passwordinput: TextInputEditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initview()
        CallgetCategory()

    }

    fun initview() {
        retrofitCallfuntion = retrofitCallfuntion()

        val prefs = getSharedPreferences(
            getString(R.string.PrefsLogin),
            Context.MODE_PRIVATE
        )

        usernamelayout = findViewById(R.id.username_text_layout)
        usernameinput = findViewById(R.id.username_text_input)
        usernameinput?.setText(prefs.getString(getString(R.string.Admin_username), ""))
        pubF.setOntextchange(this, usernameinput!!, usernamelayout!!)
        passwordlayout = findViewById(R.id.password_text_layout)
        passwordinput = findViewById(R.id.password_text_input)
        passwordinput?.setText(prefs.getString(getString(R.string.Admin_password), ""))
        pubF.setOntextchange(this, passwordinput!!, passwordlayout!!)

        val loginbtn = findViewById<Button>(R.id.Loginbtn)
        loginbtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.Loginbtn -> {
                logindata.username = usernameinput!!.text.toString().trim()
                logindata.password = passwordinput!!.text.toString().trim()

                retrofitCallfuntion.Login(this, logindata)

            }

        }
    }

    fun CallgetCategory() {
        retrofitCallfuntion.getCategory(this@LoginActivity,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {

                }

                @SuppressLint("CommitPrefEdits")
                override fun onSucess(value: JSONArray) {
                    val CategorySet = arrayListOf<String>()
                    for (i in 0 until value.length()) {
                        val jsonmessage: JSONObject? = value.getJSONObject(i)
                        var id = ""
                        var category = ""
                        try {
                            if (jsonmessage != null) {
                                id = jsonmessage.getString("_id")
                            }
                        } catch (e: Exception) {
                            Log.d("respone_CallgetCategory", e.toString())
                        }
                        try {
                            if (jsonmessage != null) {
                                category = jsonmessage.getString("category")
                                CategorySet.add(category)
                            }
                        } catch (e: Exception) {
                            Log.d("respone_CallgetCategory", e.toString())
                        }

                    }

                    val editor: SharedPreferences.Editor = this@LoginActivity.getSharedPreferences(
                        this@LoginActivity.getString(R.string.PrefsLogin),
                        Context.MODE_PRIVATE
                    ).edit()
                    val set: MutableSet<String> = HashSet()
                    set.addAll(CategorySet)
                    editor.putStringSet(this@LoginActivity.getString(R.string.CategorySet), set)
                    editor.apply()
                }

                override fun onFailure() {}
            })
    }





}