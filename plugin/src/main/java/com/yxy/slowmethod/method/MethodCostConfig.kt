package com.yxy.slowmethod.method

class MethodCostConfig {
    var methodMonitorPkgs: List<String> = ArrayList() // 函数耗时扫描范围

    var exceptPkgList = mutableListOf<String>()

    var enable: Boolean = true // 是否启动整个插件

    var printLog: Boolean = false

}