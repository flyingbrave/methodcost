package com.yxy.hello.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodCostPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("method cost plugin")
//        project.android.registerTransform(new AppJointTransform(project))
    }
}