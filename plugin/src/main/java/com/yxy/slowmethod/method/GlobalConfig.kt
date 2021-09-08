package com.yxy.slowmethod.method



object GlobalConfig {
    var pluginConfig: MethodCostConfig = MethodCostConfig()
    val UNNEED_TRACE_CLASS = arrayOf("R.class", "R$", "Manifest", "BuildConfig")
    var excludeInnerClass = false;

    fun isNeedTraceClass(fileName: String): Boolean {
        var isNeed = true
        if (fileName.endsWith(".class")) {
            for (unTraceCls in UNNEED_TRACE_CLASS) {
                if (fileName.contains(unTraceCls)) {
                    return false
                }
            }
            if (excludeInnerClass) {
                if (fileName.contains("$")) {
                    return false;
                }
            }
        } else {
            isNeed = false
        }
        return isNeed
    }
}