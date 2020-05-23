package com.example.utility_manage.Retrofit

import android.app.Activity
import android.util.Log
import com.example.queuedemo_kotlin.Retrofit.callApi
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

public class retrofitUpload {

    private lateinit var pubF: Publicfunction

    fun upload(activity: Activity,dataInvoice : retrofitData) {
        pubF = Publicfunction()

        val URL: String = activity.getResources().getString(R.string.URL) + activity.getResources()
            .getString(R.string.PORT)
        Log.d("urllll", URL);

        val Upload: String = activity.getResources().getString(R.string.addInvoice)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) //                .client(httpClient)
            .build()
        val api: callApi = retrofit.create<callApi>(callApi::class.java)


        val call: Call<retrofitData> = api.uploadInvoice(dataInvoice,Upload)
        Log.d("urllll", java.lang.String.valueOf(dataInvoice))

        //finally performing the call
        call.enqueue(object : Callback<retrofitData?> {
            override fun onResponse(
                call: Call<retrofitData?>,
                response: Response<retrofitData?>
            ) {

                Log.d("ressss", "$call $response")
                Log.d("ressss", response.message())
                Log.d("ressss", java.lang.String.valueOf(response.body()))
                if (response.isSuccessful) {
                    pubF.message(
                        activity.getResources().getString(R.string.fileuploadSuccess),
                        activity
                    )
//                    dialogupload.dismiss();
//                    val intent =
//                        Intent(this@resultgetinActivity, SelectActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }
            }

            override fun onFailure(
                call: Call<retrofitData?>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")
                pubF.message(
                    t.message,
                    activity
                )
                //                dialogupload.dismiss();
//                val intent = Intent(this@resultgetinActivity, SelectActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        })
    }
}