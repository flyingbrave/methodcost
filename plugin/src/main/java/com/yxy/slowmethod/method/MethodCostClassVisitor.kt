package com.yxy.slowmethod.method
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type


class MethodCostClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

    private val notTraceMethods = listOf("<init>", "<clinit>")
    private var className: String = ""
    private var isInConfigPkgList = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name?.replace("/", ".") ?: ""
        isInConfigPkgList = TransformUtils.classInPkgList(
            className,
            GlobalConfig.pluginConfig.methodMonitorPkgs,
            GlobalConfig.pluginConfig.exceptPkgList
        )

    }

    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        val isUnImplMethod = access and Opcodes.ACC_ABSTRACT > 0 || access and Opcodes.ACC_INTERFACE > 0   //未实现的方法

        return if (notTraceMethods.contains(name) || isUnImplMethod || !isInConfigPkgList) {
            super.visitMethod(access, name, desc, signature, exceptions)
        } else {
            val argTypes: Array<Type> = Type.getArgumentTypes(desc)

            val methodName = "$className&$name&parameterSize:${argTypes.size}"
            TransformUtils.print("MethodCostClassVisitor -> className : $className  methodName-> $methodName")
            val mv = cv.visitMethod(access, name, desc, signature, exceptions)
            MethodCostMethodVisitor(
                api,
                mv,
                access,
                name,
                desc,
                methodName
            )
        }
    }

}