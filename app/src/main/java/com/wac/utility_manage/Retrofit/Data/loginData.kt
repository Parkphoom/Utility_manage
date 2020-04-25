package com.wac.utility_manage.Retrofit.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class loginData {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: Array<objmessage>? = null

    class objmessage {
        @SerializedName("_id")
        @Expose
        var _id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("tel")
        @Expose
        var tel: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null


    }

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null




    fun getusername(): String? { return username }
    fun setusername(username: String?) { this.username = username }

    fun getpassword(): String? { return password }
    fun setpassword(password: String?) { this.password = password }
}