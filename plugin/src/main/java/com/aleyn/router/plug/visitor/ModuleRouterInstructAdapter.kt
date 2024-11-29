package com.aleyn.router.plug.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.CHECKCAST
import org.objectweb.asm.commons.InstructionAdapter


/**
 * @author: Aleyn
 * @date: 2023/7/20 15:06
 */
class ModuleRouterInstructAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val moduleClass: List<String>?
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        moduleClass?.forEach { className ->
            getstatic(
                className,
                "INSTANCE",
                "L${className};"
            )

            visitTypeInsn(CHECKCAST, "com/aleyn/annotation/IRouterModule")

            invokestatic(
                "com/aleyn/router/core/RouterController",
                "registerModule",
                "(Lcom/aleyn/annotation/IRouterModule;)V",
                false
            )
        }
        super.visitCode()
    }

}