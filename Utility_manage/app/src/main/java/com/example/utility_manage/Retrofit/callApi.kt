package com.example.queuedemo_kotlin.Retrofit

import com.example.utility_manage.Retrofit.retrofitData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by Belal on 10/5/2017.
 */
interface callApi {
    //the base URL for our API
//make sure you are not using localhost
//find the ip usinc ipconfig command
//this is our multipart request
//we have two parameters on is name and other one is description
    @POST("{port}")
    fun uploadInvoice(
        @Body data: retrofitData
        , @Path(value = "port", encoded = true) id: String
    )
            : Call<retrofitData>


}

