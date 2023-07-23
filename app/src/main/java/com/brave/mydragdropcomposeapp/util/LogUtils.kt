package com.brave.mydragdropcomposeapp.util

import android.util.Log

object LogUtils {
    private val TAG = "logTag"
    fun e(value:String){
        Log.e(TAG,value)
    }
}
