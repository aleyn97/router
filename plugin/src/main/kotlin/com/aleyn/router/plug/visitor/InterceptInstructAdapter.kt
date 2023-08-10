package com.aleyn.router.plug.visitor

import com.aleyn.router.plug.data.HandleModel
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.BIPUSH
import org.objectweb.asm.Opcodes.CHECKCAST
import org.objectweb.asm.Opcodes.NEW
import org.objectweb.asm.commons.InstructionAdapter


/**
 * @author: Aleyn
 * @date: 2023/7/20 15:06
 */
class InterceptInstructAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val interceptClass: List<HandleModel.Intercept>?,
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        interceptClass?.forEach {
            mv.visitIntInsn(BIPUSH, it.priority.toInt())
            mv.visitTypeInsn(NEW, it.className)
            dup()
            invokespecial(it.className, "<init>", "()V", false)
            mv.visitTypeInsn(CHECKCAST, "com/aleyn/router/core/LRouterInterceptor")
            invokestatic(
                "com/aleyn/router/LRouter",
                "addInterceptor",
                "(BLcom/aleyn/router/core/LRouterInterceptor;)V",
                false
            )
        }
        super.visitCode()
    }

}