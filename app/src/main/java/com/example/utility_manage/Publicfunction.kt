package com.example.utility_manage

import android.app.Activity
import android.content.Context
import android.widget.Toast

class Publicfunction {
    fun message(msg: String?,context: Activity?) {
        try {
            context!!.runOnUiThread(Runnable {
                Toast.makeText(
                    context, msg,
                    Toast.LENGTH_SHORT
                ).show()
            })
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}