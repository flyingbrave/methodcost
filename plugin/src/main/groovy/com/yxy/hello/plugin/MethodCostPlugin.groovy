package com.yxy.hello.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodCostPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("method cost plugin")
//        project.extensions.create("methodCostConfig", MethodCostConfigExtension)
//        project.afterEvaluate{
//            def methodCostConfigExtension = project.methodCostConfig
//            def methodCostConfig = new MethodCostConfig()
//            methodCostConfig.enable = methodCostConfigExtension.enable
//            methodCostConfig.methodMonitorPkgs = methodCostConfigExtension.methodMonitorPkgs
//            methodCostConfig.exceptPkgList = methodCostConfigExtension.exceptPkgList
//            methodCostConfig.printLog = methodCostConfigExtension.printLog
////            Log.d("MethodCostPlugin 1end!"+methodCostConfig.toString())
//
//            GlobalConfig.pluginConfig = methodCostConfig
//
//        }
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new MethodCostTransform(project))
    }
}