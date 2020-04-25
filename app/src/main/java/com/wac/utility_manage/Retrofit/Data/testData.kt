package com.wac.utility_manage.Retrofit.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class testData {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: Array<objmessage>? = null

    class objmessage {
        @SerializedName("_id")
        var _id: String? = null

        @SerializedName("home")
        var home: objhome? = null

    }

    class objhome {
        @SerializedName("data")
        var data: objdata? = null

        @SerializedName("user")
        var user: Array<objuser>? = null

    }

    class objdata {

        @SerializedName("address")
        var address: String? = null

        @SerializedName("buildingType")
        var buildingType: String? = null

        @SerializedName("meterId")
        var meterId: String? = null

        @SerializedName("meterVal")
        var meterVal: String? = null

        @SerializedName("gps")
        @Expose
        private var gps: GpsObj? = null


    }

    class objuser {
        @SerializedName("_idUser")
        var _idUser: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("tel")
        var tel: String? = null

    }
}