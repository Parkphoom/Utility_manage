package com.example.utility_manage.Retrofit

import android.app.Activity
import android.util.Log
import com.example.queuedemo_kotlin.Retrofit.callApi
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.R
import com.example.utility_manage.Retrofit.Data.addInvoiceData
import com.example.utility_manage.Retrofit.Data.regisMemberData
import com.example.utility_manage.Retrofit.Data.saveinfoData
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


public class retrofitUpload {

    private lateinit var pubF: Publicfunction

    fun addinvoice(activity: Activity, dataInvoice: addInvoiceData,imageFinance: MultipartBody.Part?) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)

        val URL: String = activity.getResources().getString(R.string.URL) + activity.getResources()
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Upload: String = activity.getResources().getString(R.string.addInvoice)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)


        val call: Call<addInvoiceData> = api.uploadInvoice(dataInvoice,imageFinance, Upload)
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
                    pubF.loadingDialog!!.dismiss()
                    pubF.message(
                        activity.getResources().getString(R.string.fileuploadSuccess),
                        activity
                    )
                }
                else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))


                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<addInvoiceData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                pubF.loadingDialog!!.dismiss()
                pubF.message(
                    t.message,
                    activity
                )


            }
        })
    }

    fun regismember(activity: Activity, dataRegister: regisMemberData) {
        pubF = Publicfunction()
        pubF.builddialogloading(activity)
        val URL: String = activity.getResources().getString(R.string.URL) + activity.getResources()
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val Upload: String = activity.getResources().getString(R.string.regismember)

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
                        activity.getResources().getString(R.string.fileuploadSuccess),
                        activity
                    )

                }
                else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))


                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
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
                pubF.message(
                    t.message,
                    activity
                )

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
        val URL: String = activity.getResources().getString(R.string.URL) + activity.getResources()
            .getString(R.string.PORT)
        Log.d("urllll", URL)
        var Apiname=""
        if(apiName.equals(activity.getString(R.string.travel))){
            Apiname = activity.getString(R.string.addtravel)
        }
        if(apiName.equals(activity.getString(R.string.business))){
            Apiname = activity.getString(R.string.addbusiness)
        }
        if(apiName.equals(activity.getString(R.string.service))){
            Apiname = activity.getString(R.string.addservice)
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
                        activity.getResources().getString(R.string.fileuploadSuccess),
                        activity
                    )
                }
                else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))


                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
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
                    activity
                )
            }
        })
    }


}