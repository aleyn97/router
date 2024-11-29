package com.aleyn.router.plug.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.InstructionAdapter


/**
 * @author: Aleyn
 * @date: 2023/7/20 15:06
 */
class InterceptInstructAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val moduleClass: List<String>?
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        moduleClass?.forEach { className ->
            invokestatic(
                className,
                "addInterceptor",
                "()V",
                false
            )
        }
        super.visitCode()
    }

}