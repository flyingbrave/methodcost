package com.yxy.slowmethod

object Log {
    val TAG = "slowmethod";
    var PRINT_LOG = true

    @JvmStatic
    fun logEnable(enable : Boolean) {
        PRINT_LOG = enable;
    }

    @JvmStatic
    fun d(msg : String) {
        println("slowmethod2222")
        if (PRINT_LOG) {
            println(TAG +" : "+msg)
        }
    }
}