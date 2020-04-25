package com.wac.utility_manage.Retrofit.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentData {
    class GET {
        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("message")
        @Expose
        var message: Array<objmessage>? = null

        class objmessage {

            @SerializedName("_id")
            var _id: String? = null

            @SerializedName("ref1")
            @Expose
            var ref_1: String? = null

            @SerializedName("ref2")
            @Expose
            var ref_2: String? = null

            @SerializedName("category")
            @Expose
            var category: String? = null


            @SerializedName("startDate")
            @Expose
            private var startDate: String? = null

            @SerializedName("dueDate")
            @Expose
            private var dueDate: String? = null

            @SerializedName("meterVal")
            @Expose
            private var meterVal: String? = null

            @SerializedName("meterId")
            @Expose
            private var meterID: String? = null

            @SerializedName("gps")
            @Expose
            private var gps: GpsObj? = null

            @SerializedName("payment")
            @Expose
            private var payment: paymentObj? = null

            inner class paymentObj {
                @SerializedName("status")
                @Expose
                var status: String? = null

                @SerializedName("amount")
                @Expose
                var amount: String? = null

                @SerializedName("credit")
                @Expose
                var credit: String? = null

                @SerializedName("remain")
                @Expose
                var remain: String? = null

                @SerializedName("via")
                @Expose
                var via: String? = null

                @SerializedName("creditDate")
                @Expose
                var creditdate: String? = null

                @SerializedName("receiveName")
                @Expose
                var receivname: String? = null

            }

        }
    }


    class POST {
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

        @SerializedName("_idInvoice")
        @Expose
        var _idInvoice: String? = null

        @SerializedName("amount")
        @Expose
        var amount: String? = null

        @SerializedName("credit")
        @Expose
        var credit: String? = null

        @SerializedName("creditDate")
        @Expose
        var creditDate: String? = null

        @SerializedName("receiveName")
        @Expose
        var receiveName: String? = null

        @SerializedName("remain")
        @Expose
        var remain: String? = null

        @SerializedName("via")
        @Expose
        var via: String? = null
    }


}