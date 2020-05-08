package com.wac.utility_manage.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.queuedemo_kotlin.Retrofit.callApi
import com.example.utility_manage.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.findMeterWaterData
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainFragmentinvoice : AppCompatActivity() {

    private lateinit var pubF: Publicfunction
    private var findMeterData = findMeterWaterData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragmentinvoice)

        initview()

        createFindIdDialog()
    }

    private fun initview() {
        pubF = Publicfunction()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.addinvoice), actionBar)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val adapter = SampleAdapter(supportFragmentManager)

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    fun getFragmentRefreshListener(): FragmentRefreshListener? {
        return fragmentRefreshListener
    }

    fun getFragmentRefreshListener2(): FragmentRefreshListener? {
        return fragmentRefreshListener2
    }

    fun setFragmentRefreshListener(fragmentRefreshListener: FragmentRefreshListener?) {
        this.fragmentRefreshListener = fragmentRefreshListener
    }

    fun setFragmentRefreshListener2(fragmentRefreshListener2: FragmentRefreshListener?) {
        this.fragmentRefreshListener2 = fragmentRefreshListener2
    }

    private var fragmentRefreshListener: FragmentRefreshListener? = null
    private var fragmentRefreshListener2: FragmentRefreshListener? = null

    interface FragmentRefreshListener {
        fun onRefresh()
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

    private fun createFindIdDialog() {
        val builder = AlertDialog.Builder(
            this,
            R.style.CustomDialog
        )
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_savemeter, null)

        builder.setView(view)
        builder.setCancelable(false)
        val alert = builder.create()

        val closebtn = view.findViewById<Button>(R.id.cancel_button)
        closebtn.setOnClickListener {
            (view.parent as ViewGroup).removeView(view) // <- fix
            alert.dismiss()
            onBackPressed()
        }
        val homeidlayout = view.findViewById<TextInputLayout>(R.id.homeid_text_layout)
        homeidlayout!!.helperText = "ตัวอย่าง : 01/001"
        val homeidinput = view.findViewById<TextInputEditText>(R.id.homeid_text_input)
        val meteridlayout = view.findViewById<TextInputLayout>(R.id.meterId_text_layout)
        val meteridinput = view.findViewById<TextInputEditText>(R.id.meterId_text_input)


        val submitbtn = view.findViewById<Button>(R.id.submitbtn)
        submitbtn.setOnClickListener {
            if (meteridinput!!.text.toString().isEmpty() && homeidinput!!.text.toString()
                    .isEmpty()
            ) {
                meteridlayout!!.error = resources.getString(R.string.gettexterror)
                homeidlayout!!.error = resources.getString(R.string.gettexterror)
                pubF.setOntextchange(this, homeidinput!!, homeidlayout!!)
                pubF.setOntextchange(this, meteridinput!!, meteridlayout!!)
                pubF.message(getString(R.string.gettexthint), FancyToast.CONFUSING, this)
            } else {

                findMeterData.address = homeidinput.text.toString().trim()
                findMeterData.meterid = meteridinput.text.toString().trim()
//
                findMeterUser(this, findMeterData, view, alert)
            }


        }

        alert.show()
        val window = alert.window
        if (window != null) {

            val wlp = window.attributes
            wlp.gravity = Gravity.CENTER
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT

            window.attributes = wlp

        }

    }

    fun findMeterUser(
        activity: Activity,
        dataRegister: findMeterWaterData,
        view: View,
        alert: AlertDialog
    ) {
        pubF.builddialogloading(activity)
        val URL: String = activity.getResources().getString(R.string.URL) + activity.getResources()
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Apiname = activity.getResources().getString(R.string.finddatameteruserURL)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<findMeterWaterData> = api.uploadFindmeter(dataRegister, Apiname)
        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call

        call.enqueue(object : Callback<findMeterWaterData?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<findMeterWaterData?>,
                response: Response<findMeterWaterData?>
            ) {

                Log.d("ressss", response.body().toString())
//                Log.d("ressss", response.errorBody().toString())
//                Log.d("ressss", response.headers().toString())

                if (response.isSuccessful) {
                    val js = Gson().toJson(response.body())
                    Log.d("ressss_s", js)

                    var json: JSONObject? = null
                    try {
                        json = JSONObject(js)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.d("ressss_s", json.toString())

                    val jsonmessageArray: JSONArray = json?.get("message") as JSONArray
                    for (i in 0 until jsonmessageArray.length()) {
                        val jsonmessage: JSONObject? = jsonmessageArray.getJSONObject(i)
                        val data = jsonmessage!!.getJSONObject("home").getJSONObject("data")
                        Log.d("ressss_s", data.toString())
                        val user =
                            jsonmessage.getJSONObject("home").getJSONArray("user").getJSONObject(0)

//                        val fragment = FragmentWaterinvoice.newInstance()
                        val fragmentwater = FragmentWaterinvoice.newInstance(data, user)

                        if (getFragmentRefreshListener() != null) {
                            getFragmentRefreshListener()?.onRefresh()
                        }
                        val fragmentother = FragmentOtherinvoice.newInstance(data, user)
                        if (getFragmentRefreshListener2() != null) {
                            getFragmentRefreshListener2()?.onRefresh()
                        }


                    }


//                    homeidinput!!.text = "${getString(R.string.homeid)} : $homeid"
//                    meteridinput!!.text = "${getString(R.string.meterid)} : $meterid"
//                    buildingtypeinput!!.text = "${getString(R.string.buildingtype)} : $buildingtype"
//                    nameinput!!.text = "${getString(R.string.name)} : $name"
//                    telnuminput!!.text = "${getString(R.string.telnum)} : $telnum"
//                    oldwatermeterinput!!.text =
//                        "${getString(R.string.oldwatermeter)} : $oldwatermeter"


                    pubF.loadingDialog!!.dismiss()

                    (view.parent as ViewGroup).removeView(view) // <- fix
                    alert.dismiss()


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        pubF.loadingDialog!!.dismiss()
                        pubF.message(jObjError.getString("message"), FancyToast.WARNING, activity)
//                        (view.parent as ViewGroup).removeView(view) // <- fix
//                        alert.dismiss()

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<findMeterWaterData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")

                pubF.loadingDialog!!.dismiss()
                pubF.message(t.message, FancyToast.ERROR, activity)

            }
        })
    }

}
