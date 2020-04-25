package com.example.queuedemo_kotlin.Retrofit

import com.wac.utility_manage.Retrofit.Data.*
import okhttp3.MultipartBody
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
    @Multipart
    @POST("{port}")
    fun uploadInvoice(
        @Part("dataFinance") data: addInvoiceData,
        @Part imageFinance: MultipartBody.Part?,
        @Path(value = "port", encoded = true) id: String
    ): Call<addInvoiceData>

    @POST("{port}")
    fun uploaddataRegister(
        @Body data: regisMemberData
        , @Path(value = "port", encoded = true) id: String
    ): Call<regisMemberData>

    @Multipart
    @POST("{port}")
    fun uploadInfo(
        @Part("dataOfficial") data: saveinfoData,
        @Part imageOfficial: MultipartBody.Part?,
        @Path(value = "port", encoded = true) id: String
    ): Call<saveinfoData>

    @POST("{port}")
    fun uploadFindmeter(
        @Body data: findMeterWaterData
        , @Path(value = "port", encoded = true) id: String
    ): Call<findMeterWaterData>

    @POST("{port}")
    fun uploadLogin(
        @Body data: loginData
        , @Path(value = "port", encoded = true) id: String
    ): Call<loginData>


    @GET("{Ref2}")
    fun getPayment(@Path(value = "Ref2", encoded = true) id: String)
            : Call<PaymentData.GET>

    @POST("{port}")
    fun postPayment(
        @Body data: PaymentData.POST
        , @Path(value = "port", encoded = true) id: String
    ): Call<PaymentData.POST>
}

