package com.wac.utility_manage.Retrofit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.queuedemo_kotlin.Retrofit.callApi
import com.example.utility_manage.R
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import com.telpo.tps550.api.printer.UsbThermalPrinter
import com.wac.utility_manage.MainActivity
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.PublicValues
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.Retrofit.Data.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException


class retrofitCallfuntion {

    private lateinit var pubF: Publicfunction

    fun addinvoice(
        activity: Activity,
        dataInvoice: addInvoiceData,
        imageFinance: MultipartBody.Part?,
        finish: Boolean,
        strprint: List<String>,
        strCSV: String
    ) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)

        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Upload: String = activity.resources.getString(R.string.addInvoiceURL)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<addInvoiceData> = api.uploadInvoice(dataInvoice, imageFinance, Upload)
        Log.d("urllll", java.lang.String.valueOf(dataInvoice))

        //finally performing the call
        call.enqueue(object : Callback<addInvoiceData?> {
            override fun onResponse(
                call: Call<addInvoiceData?>,
                response: Response<addInvoiceData?>
            ) {

                Log.d("ressss", "$call $response")
                Log.d("ressss", response.message())
                Log.d("ressss", java.lang.String.valueOf(response.body()))
                if (response.isSuccessful) {
                    val js = Gson().toJson(response.body())
                    Log.d("ressss_s", js)

                    var json: JSONObject? = null
                    try {
                        json = JSONObject(js)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    var ref2: String? = null
                    if (json != null) {
                        try {
                            ref2 = json.getJSONObject("message").getString("generation")

                            Log.d("ressss_s", ref2.toString())

                            pubF.loadingDialog!!.dismiss()
                            pubF.message(
                                activity.resources.getString(R.string.fileuploadSuccess),
                                FancyToast.SUCCESS,
                                activity
                            )
                            if (finish) {
                                activity.onBackPressed()
                            } else {
                                val prtF = Printfuntion()
                                prtF.handler = prtF.MyHandler(activity)
                                prtF.mUsbThermalPrinter = UsbThermalPrinter(activity)

                                prtF.Printheader = strprint[0]
                                prtF.Printcontent = strprint[1]
                                prtF.ref2 = ref2


                                prtF.handler!!.sendMessage(
                                    prtF.handler!!.obtainMessage(
                                        PublicValues().PRINTCONTENT,
                                        1,
                                        0,
                                        null
                                    )
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            Log.d(activity.getString(R.string.LogError), e.toString())

                            pubF.loadingDialog!!.dismiss()

                            if (finish) {
                                activity.onBackPressed()
                            }
                        }

                    }


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))


                    } catch (e: Exception) {
                        Log.d(activity.getString(R.string.LogError), e.message.toString())
                    }
                }
            }

            override fun onFailure(
                call: Call<addInvoiceData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                pubF.loadingDialog!!.dismiss()


                if (finish) {
                    activity.onBackPressed()
                }

            }
        })
    }

    fun regismember(
        activity: Activity,
        dataRegister: regisMemberData,
        strCSV: String
    ) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Upload: String = activity.resources.getString(R.string.regismemberURL)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)


        val call: Call<regisMemberData> = api.uploaddataRegister(dataRegister, Upload)
        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call
        call.enqueue(object : Callback<regisMemberData?> {
            override fun onResponse(
                call: Call<regisMemberData?>,
                response: Response<regisMemberData?>
            ) {

                Log.d("ressss", "$call $response")
                Log.d("ressss", response.message())
                if (response.isSuccessful) {
                    pubF.loadingDialog!!.dismiss()

                    Log.d("ressss", response.message())
                    Log.d("ressss", response.body().toString())
                    Log.d("ressss", response.errorBody().toString())


                    pubF.message(
                        activity.resources.getString(R.string.fileuploadSuccess),
                        FancyToast.SUCCESS,
                        activity
                    )
                    activity.onBackPressed()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))


                    } catch (e: Exception) {
                        Log.d(activity.getString(R.string.LogError), e.message.toString())
                    }
                }
            }

            override fun onFailure(
                call: Call<regisMemberData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", "${t.message}")
                pubF.loadingDialog!!.dismiss()
//                pubF.message(
//                    t.message,
//                    activity
//                )
                pubF.writeToCsv(
                    PublicValues().headercsvRegister,
                    strCSV,
                    PublicValues().CSVnameRegister,
                    activity
                )
                activity.onBackPressed()
            }
        })
    }

    fun saveInfo(
        activity: Activity,
        datainfo: saveinfoData,
        apiName: String,
        imgOfficial: MultipartBody.Part?
    ) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)
        var Apiname = ""
        if (apiName.equals(activity.getString(R.string.travel))) {
            Apiname = activity.getString(R.string.addtravelURL)
        }
        if (apiName.equals(activity.getString(R.string.business))) {
            Apiname = activity.getString(R.string.addbusinessURL)
        }
        if (apiName.equals(activity.getString(R.string.service))) {
            Apiname = activity.getString(R.string.addserviceURL)
        }
        if (apiName.equals(activity.getString(R.string.estate))) {
            Apiname = activity.getString(R.string.addassetURL)
        }


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)


        val call: Call<saveinfoData> = api.uploadInfo(datainfo, imgOfficial, Apiname)
        Log.d("urllll", java.lang.String.valueOf(datainfo))

        //finally performing the call
        call.enqueue(object : Callback<saveinfoData?> {
            override fun onResponse(
                call: Call<saveinfoData?>,
                response: Response<saveinfoData?>
            ) {

                Log.d("ressss", "$call $response")
                Log.d("ressss", response.message())
                Log.d("ressss", java.lang.String.valueOf(response.body()))
                if (response.isSuccessful) {
                    pubF.loadingDialog!!.dismiss()
                    pubF.message(
                        activity.resources.getString(R.string.fileuploadSuccess),
                        FancyToast.SUCCESS,
                        activity
                    )
                    activity.onBackPressed()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))


                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message.toString())
                    }
                }
            }

            override fun onFailure(
                call: Call<saveinfoData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                pubF.loadingDialog!!.dismiss()
                pubF.message(
                    t.message,
                    FancyToast.ERROR,
                    activity
                )
            }
        })
    }

    fun Login(
        activity: Activity,
        datalogin: loginData
    ) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)
        var Apiname = activity.resources.getString(R.string.loginURL)


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create(callApi::class.java)


        val call: Call<loginData> = api.uploadLogin(datalogin, Apiname)
        Log.d("urllll", java.lang.String.valueOf(datalogin))

        //finally performing the call
        call.enqueue(object : Callback<loginData?> {
            override fun onResponse(
                call: Call<loginData?>,
                response: Response<loginData?>
            ) {

                Log.d("ressss", "$call $response")
                Log.d("ressss", response.message())
                Log.d("ressss", java.lang.String.valueOf(response.body()))
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

                    val jsonmessageArray: JSONArray
                    try {
                        jsonmessageArray = json?.get("message") as JSONArray
                        Log.d("ressss_s", jsonmessageArray.toString())

                        var id: String? = ""
                        var name: String? = ""
                        var telnum: String? = ""
                        var email: String? = ""
                        for (i in 0 until jsonmessageArray.length()) {
                            val jsonmessage: JSONObject = jsonmessageArray.getJSONObject(i)
                            id = jsonmessage.getString("_id")
                            name = jsonmessage.getString("name")
                            telnum = jsonmessage.getString("tel")
                            email = jsonmessage.getString("email")
                            Log.d("ressss_s", name)

//                        homeid = data.get("address").toString()
//                        meterid = data.get("meterId").toString()
//                        buildingtype = data.get("buildingType").toString()
//                        name = user.get("name").toString()
//                        telnum = user.get("tel").toString()
//                        oldwatermeter = data.get("meterVal").toString()
                        }

                        val editor: SharedPreferences.Editor = activity.getSharedPreferences(
                            activity.getString(R.string.PrefsLogin),
                            Context.MODE_PRIVATE
                        ).edit()
                        editor.putBoolean(activity.getString(R.string.Status_login), true)
                        editor.putString(
                            activity.getString(R.string.Admin_username),
                            datalogin.username
                        )
                        editor.putString(
                            activity.getString(R.string.Admin_password),
                            datalogin.password
                        )
                        editor.putString(activity.getString(R.string.Admin_name), name)
                        editor.putString(activity.getString(R.string.Admin_telnum), telnum)
                        editor.putString(activity.getString(R.string.Admin_email), email)
                        editor.putString(activity.getString(R.string.Admin_id), id)
                        editor.apply()

                        pubF.loadingDialog!!.dismiss()
                        pubF.message(
                            activity.resources
                                .getString(R.string.login) + " " + activity.resources
                                .getString(R.string.success),
                            FancyToast.SUCCESS,
                            activity
                        )

                        val intent = Intent(activity, MainActivity::class.java)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                        activity.finish()
//                    activity.onBackPressed()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.d(activity.getString(R.string.LogError), e.message.toString())
                    }


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))
                        pubF.loadingDialog!!.dismiss()
                        pubF.message(
                            jObjError.getString("message"),
                            FancyToast.CONFUSING,
                            activity
                        )

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message.toString())
                        pubF.loadingDialog!!.dismiss()
                        pubF.message(
                            e.message,
                            FancyToast.CONFUSING,
                            activity
                        )
                    }
                }
            }

            override fun onFailure(
                call: Call<loginData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                pubF.loadingDialog!!.dismiss()
                pubF.message(
                    "???????????????????????????????????????????????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????",
                    FancyToast.ERROR,
                    activity
                )
            }
        })
    }

    fun getPayment(
        activity: Activity,
        ref2: String,
        retrofitcallback: retrofitCallback
    ): String? {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Apiname = activity.getString(R.string.Payment)


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<PaymentData.GET> = api.getPayment(Apiname + ref2)
//        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call

        var meterId: String? = ""
        call.enqueue(object : Callback<PaymentData.GET?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<PaymentData.GET?>,
                response: Response<PaymentData.GET?>
            ) {

                Log.d("ressss", response.body().toString())
                Log.d("ressss", response.errorBody().toString())
                Log.d("ressss", response.headers().toString())

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
                        retrofitcallback.onSucess(jsonmessage!!)
                    }
                    pubF.loadingDialog!!.dismiss()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        pubF.loadingDialog!!.dismiss()
                        pubF.message(
                            jObjError.getString("message"),
                            FancyToast.ERROR,
                            FancyToast.LENGTH_SHORT,
                            activity
                        )
                        retrofitcallback.onFailure()
//                        (view.parent as ViewGroup).removeView(view) // <- fix
//                        alert.dismiss()

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                        retrofitcallback.onFailure()
                    }
                }
            }

            override fun onFailure(
                call: Call<PaymentData.GET?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")
                retrofitcallback.onFailure()
                pubF.loadingDialog!!.dismiss()
                pubF.message(t.message, FancyToast.ERROR, activity)

            }

        })
        return meterId
    }


    fun findMeterUser(
        activity: Activity,
        dataRegister: findMeterWaterData,
        retrofitcallback: retrofitCallback
    ) {
        val publicfunction = Publicfunction()
        publicfunction.builddialogloading(activity)
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Apiname = activity.resources.getString(R.string.finddatameteruserURL)

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

                        if (jsonmessage != null) {
                            retrofitcallback.onSucess(jsonmessage)
                            publicfunction.loadingDialog?.dismiss()
                        }

                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        publicfunction.loadingDialog?.dismiss()
                        publicfunction.message(
                            jObjError.getString("message"),
                            FancyToast.WARNING,
                            activity
                        )
//                        (view.parent as ViewGroup).removeView(view) // <- fix
//                        alert.dismiss()

                    } catch (e: Exception) {
                        publicfunction.loadingDialog?.dismiss()
                        Log.d("ressss_2", e.message)
                        publicfunction.message(e.message, FancyToast.WARNING, activity)
                    }
                }
            }

            override fun onFailure(
                call: Call<findMeterWaterData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")
                publicfunction.loadingDialog?.dismiss()
                publicfunction.message(t.message, FancyToast.ERROR, activity)


            }
        })
    }


    fun postPayment(
        activity: Activity,
        datapayment: PaymentData.POST,
        retrofitcallback: retrofitCallback
    ) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Apiname = activity.resources.getString(R.string.Payment)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<PaymentData.POST> = api.postPayment(datapayment, Apiname)
//        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call

        call.enqueue(object : Callback<PaymentData.POST?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<PaymentData.POST?>,
                response: Response<PaymentData.POST?>
            ) {

                Log.d("ressss", response.body().toString())
                Log.d("ressss", response.errorBody().toString())
                Log.d("ressss", response.headers().toString())

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
                    try {
                        val jsonmessage: JSONObject = json?.get("message") as JSONObject
                        retrofitcallback.onSucess(jsonmessage)

//                        val jsonhomeArray: JSONArray = jsonmessage!!.get("home") as JSONArray
//                        var jsonhome: JSONObject? = jsonhomeArray.getJSONObject(0)
//                        val jsondata : JSONObject? = jsonhome!!.getJSONObject("data")

                        pubF.loadingDialog!!.dismiss()
                    } catch (e: JSONException) {
                        Log.d(activity.getString(R.string.LogError), e.message.toString())
                    }


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        pubF.loadingDialog!!.dismiss()
                        pubF.message(jObjError.getString("message"), FancyToast.ERROR, activity)
//                        (view.parent as ViewGroup).removeView(view) // <- fix
//                        alert.dismiss()

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<PaymentData.POST?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")

                pubF.loadingDialog!!.dismiss()
                pubF.message(t.message, FancyToast.ERROR, activity)


            }
        })
    }

    fun postGPSUpdate(
        activity: Activity,
        datagps: GpsObj,
        homeid: String,
        retrofitcallback: retrofitCallback
    ) {
        val publicfunction = Publicfunction()
        publicfunction.builddialogloading(activity)
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT) + activity.resources.getString(R.string.GPSUpdate)
        Log.d("urllll", URL)


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<GpsObj> = api.postGPSupdate(datagps, homeid)
//        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call

        call.enqueue(object : Callback<GpsObj> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<GpsObj>,
                response: Response<GpsObj>
            ) {

                Log.d("ressss", response.body().toString())
                Log.d("ressss", response.errorBody().toString())
                Log.d("ressss", response.headers().toString())

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
                    try {
                        val jsonmessage: JSONObject = json?.get("message") as JSONObject
                        retrofitcallback.onSucess(jsonmessage)

//                        val jsonhomeArray: JSONArray = jsonmessage!!.get("home") as JSONArray
//                        var jsonhome: JSONObject? = jsonhomeArray.getJSONObject(0)
//                        val jsondata : JSONObject? = jsonhome!!.getJSONObject("data")

                        publicfunction.loadingDialog!!.dismiss()
                    } catch (e: JSONException) {
                        Log.d(activity.getString(R.string.LogError), e.message.toString())
                    }


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        publicfunction.loadingDialog!!.dismiss()
                        publicfunction.message(
                            jObjError.getString("message"),
                            FancyToast.ERROR,
                            activity
                        )
//                        (view.parent as ViewGroup).removeView(view) // <- fix
//                        alert.dismiss()

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<GpsObj?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")

                publicfunction.loadingDialog!!.dismiss()
                publicfunction.message(t.message, FancyToast.ERROR, activity)


            }
        })
    }

    fun getAllinvoice(
        activity: Activity,
        retrofitcallback: retrofitCallback
    ) {
        pubF = Publicfunction()
        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Apiname = activity.getString(R.string.Allinvoice)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<Map<String, Any>> = api.getResponse(Apiname)
//        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call

        call.enqueue(object : Callback<Map<String, Any>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {

                Log.d("ressss", response.body().toString())

                if (response.isSuccessful) {
                    val js = Gson().toJson(response.body())
                    var json: JSONObject? = JSONObject(js)
                    val iter = json!!.keys()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        try {
                            val value = json[key]
                            Log.d("datadata", value.toString())
                            if (key == "message") {
                                Log.d("datadata", (value as JSONArray).toString())
                                retrofitcallback.onSucess(value)
                            }
                        } catch (e: JSONException) {
                        }
                    }

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        pubF.message(
                            jObjError.getString("message"),
                            FancyToast.ERROR,
                            FancyToast.LENGTH_SHORT,
                            activity
                        )
                        retrofitcallback.onFailure()
//                        (view.parent as ViewGroup).removeView(view) // <- fix
//                        alert.dismiss()

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                        retrofitcallback.onFailure()
                    }
                }
            }

            override fun onFailure(
                call: Call<Map<String, Any>>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")
                retrofitcallback.onFailure()
                pubF.message(t.message, FancyToast.ERROR, activity)

            }

        })
    }

    fun getCategory(
        activity: Activity,
        retrofitcallback: retrofitCallback
    ) {
        pubF = Publicfunction()
        val URL: String = activity.resources.getString(R.string.WACURL)
        Log.d("urllll", URL)

        val Apiname = activity.getString(R.string.Category)


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getUnsafeOkHttpClient())
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)

        val call: Call<Array<Any>> = api.getCategory(Apiname)
//        Log.d("urllll", java.lang.String.valueOf(dataRegister))

        //finally performing the call

        call.enqueue(object : Callback<Array<Any>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<Array<Any>>,
                response: Response<Array<Any>>
            ) {

                Log.d("ressss", response.body().toString())

                if (response.isSuccessful) {
                    val js = Gson().toJson(response.body())

                    val json: JSONArray? = JSONArray(js)
                    Log.d("datadata", json.toString())
                    if (json != null) {
                        retrofitcallback.onSucess(json)
                    }
//                    val iter = json!!.keys()
//                    while (iter.hasNext()) {
//                        val key = iter.next()
//                        try {
//                            val value = json[key]
//                            Log.d("datadata", value.toString())
////
//                        } catch (e: JSONException) {
//                        }
//                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))

                        pubF.message(
                            jObjError.getString("message"),
                            FancyToast.ERROR,
                            FancyToast.LENGTH_SHORT,
                            activity
                        )
                        retrofitcallback.onFailure()

                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                        retrofitcallback.onFailure()
                    }
                }
            }

            override fun onFailure(
                call: Call<Array<Any>>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                Log.d("ressss", " ${t.message}")
                retrofitcallback.onFailure()
                pubF.message(t.message, FancyToast.ERROR, activity)

            }

        })
    }


    private fun getUnsafeOkHttpClient(): OkHttpClient? {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }

                }
            )

            // Install the all-trusting trust manager

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                (trustAllCerts[0] as X509TrustManager)
            )
            builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })

            return builder.build()
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }

}