package com.wac.utility_manage.Retrofit.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GpsObj {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: objmessage? = null

    class objmessage {
        @SerializedName("operate")
        @Expose
        var operate: String? = null

        @SerializedName("data")
        @Expose
        var data: String? = null
    }

    @SerializedName("latitude")
    var latitude: String? = null

    @SerializedName("longitude")
    var longitude: String? = null


}