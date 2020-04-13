package com.example.queuedemo_kotlin.Retrofit

import com.example.utility_manage.Retrofit.Data.addInvoiceData
import com.example.utility_manage.Retrofit.Data.findMeterWaterData
import com.example.utility_manage.Retrofit.Data.regisMemberData
import com.example.utility_manage.Retrofit.Data.saveinfoData
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
}

