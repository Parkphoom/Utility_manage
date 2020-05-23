package com.wac.utility_manage.Retrofit.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class allinvoiceData {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: Array<objmessage>? = null

    class objmessage {
        @SerializedName("_id")
        var _id: String? = null

        @SerializedName("finance")
        var finance: Array<objfinance>? = null

        class objfinance {

            @SerializedName("_id")
            var _id: String? = null

            @SerializedName("category")
            var category: String? = null

            @SerializedName("dueDate")
            @Expose
            var dueDate: String? = null

            @SerializedName("gps")
            @Expose
            private var gps: GpsObj? = null

            @SerializedName("meterId")
            var meterId: String? = null

            @SerializedName("meterVal")
            var meterVal: String? = null


            @SerializedName("payment")
            @Expose
            private var payment: paymentObj? = null

            inner class paymentObj {
                @SerializedName("amount")
                @Expose
                var amount: String? = null

                @SerializedName("credit")
                @Expose
                var credit: String? = null

                @SerializedName("creditDate")
                @Expose
                var creditdate: String? = null

                @SerializedName("receiveName")
                @Expose
                var receiveName: String? = null

                @SerializedName("remain")
                @Expose
                var remain: String? = null

                @SerializedName("via")
                @Expose
                var via: String? = null

                @SerializedName("status")
                @Expose
                var status: String? = null
            }


            @SerializedName("ref1")
            @Expose
            var ref1: String? = null

            @SerializedName("ref2")
            @Expose
            var ref2: String? = null

            @SerializedName("startDate")
            @Expose
            var startDate: String? = null

            @SerializedName("images")
            @Expose
            var images: String? = null

            @SerializedName("timeStamp")
            @Expose
            var timeStamp: String? = null
        }
    }


}