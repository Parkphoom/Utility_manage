package com.wac.utility_manage.Retrofit.Data

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
        var values: objhome? = null

        class objhome {

            @SerializedName("data")
            var data: objdata? = null

            @SerializedName("user")
            var user: Array<objuser>? = null

            class objdata {
                @SerializedName("address")
                var address: String? = null

                @SerializedName("buildingType")
                var buildingType: String? = null

                @SerializedName("gps")
                @Expose
                private var gps: GpsObj? = null

                @SerializedName("meterVal")
                var meterVal: String? = null

                @SerializedName("meterId")
                var meterId: String? = null
            }

            class objuser {
                @SerializedName("name")
                var name: String? = null

                @SerializedName("tel")
                var tel: String? = null
            }
        }
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