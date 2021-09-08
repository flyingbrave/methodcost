package com.yxy.slowmethod.method



object TransformUtils {

    fun print(msg: String) {
        if (!GlobalConfig.pluginConfig.printLog) return
        kotlin.io.print("🐰 -----> $msg \n")
    }

    fun print(tag: String, msg: String) {
        if (!GlobalConfig.pluginConfig.printLog) return
        kotlin.io.print("🐰 -----> TAG : $tag ; $msg \n")
    }

    fun classInPkgList(className: String, pkgList: List<String>, exceptPkgList: List<String>): Boolean {

        if (pkgList.isEmpty()) return false

        exceptPkgList.forEach {
            if (className.startsWith(it)) {
                return false
            }
        }

        pkgList.forEach {
            if (className.startsWith(it)) {
                return true
            }
        }
        return false
    }
}