package com.example.utility_manage.Retrofit.Data

import com.example.utility_manage.Retrofit.Jsobj
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class findMeterWaterData {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var values: Array<objmessage>? = null


    class objmessage {
        @SerializedName("_id")
        var _id: String? = null

        @SerializedName("home")
        var values: Array<objhome>? = null


    }

    class objhome {

        @SerializedName("_id")
        var _id: String? = null

        @SerializedName("address")
        var address: String? = null

        @SerializedName("buildingType")
        var buildingType: String? = null

        @SerializedName("gps")
        @Expose
        private var gps: Jsobj? = null

        @SerializedName("meterId")
        var meterId: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("tel")
        var tel: String? = null

        @SerializedName("meterValues")
        var meterValues: String? = null
    }


    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("meterId")
    @Expose
    var meterid: String? = null


    fun getaddress(): String? {
        return address
    }

    fun setaddress(address: String?) {
        this.address = address
    }

    fun getmeterid(): String? {
        return meterid
    }

    fun setmeterid(meterid: String?) {
        this.meterid = meterid
    }
}