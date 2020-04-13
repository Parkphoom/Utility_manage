package com.example.utility_manage.Retrofit

import org.json.JSONObject


interface retrofitCallback {
    fun onSuccess(value: JSONObject?)

    fun onError(throwable: Throwable)
}