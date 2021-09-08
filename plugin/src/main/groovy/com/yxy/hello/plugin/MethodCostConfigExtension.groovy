package com.yxy.hello.plugin

class MethodCostConfigExtension {
    boolean enable
    boolean printLog
    def methodMonitorPkgs = []
    def exceptPkgList = []
    MethodCostConfigExtension() {
    }

//    var methodMonitorPkgs: List<String> = ArrayList() // 函数耗时扫描范围
//
//    var enable: Boolean = true // 是否启动整个插件
//
//    var printLog: Boolean = true
}