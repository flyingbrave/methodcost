package com.yxy.slowmethod.method

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter



class MethodCostMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?,
    private val methodNameParams: String
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    override fun onMethodEnter() {
        super.onMethodEnter()
        mv.visitLdcInsn(methodNameParams)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC, MethodTracer.CLASS_PATH,
            MethodTracer.METHOD_RECORD_METHOD_START,
            MethodTracer.METHOD_RECORD_METHOD_END_PARAMS,
            false
        )
    }

    override fun onMethodExit(opcode: Int) {
        mv.visitLdcInsn(methodNameParams)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC, MethodTracer.CLASS_PATH,
            MethodTracer.METHOD_RECORD_METHOD_END,
            MethodTracer.METHOD_RECORD_METHOD_END_PARAMS,
            false
        )
    }



}