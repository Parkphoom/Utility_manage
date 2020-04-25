package com.wac.utility_manage.Retrofit

import org.json.JSONObject


interface retrofitCallback {
    fun onSucess(value: JSONObject)
    fun onFailure()
}