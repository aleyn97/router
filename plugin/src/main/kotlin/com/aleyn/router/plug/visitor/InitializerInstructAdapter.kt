package com.aleyn.router.plug.visitor

import com.aleyn.router.plug.data.HandleModel
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.InstructionAdapter


/**
 * @author: Aleyn
 * @date: 2023/7/20 15:06
 */
class InitializerInstructAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val initializerClass: List<HandleModel.Initializer>?
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        initializerClass?.forEach {
            iconst(it.priority)
            mv.visitIntInsn(Opcodes.BIPUSH, it.async)
            mv.visitTypeInsn(Opcodes.NEW, it.className)
            dup()
            invokespecial(it.className, "<init>", "()V", false)
            mv.visitTypeInsn(Opcodes.CHECKCAST, "com/aleyn/router/core/LRouterInitializer")
            invokestatic(
                "com/aleyn/router/core/RouterController",
                "registerInitializer",
                "(BZLcom/aleyn/router/core/LRouterInitializer;)V",
                false
            )
        }
        super.visitCode()
    }

}