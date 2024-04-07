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
    private val moduleClass: List<HandleModel.Module>?
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        moduleClass?.forEach {
            invokestatic(
                it.className,
                "registerInitializer",
                "()V",
                false
            )
        }
        super.visitCode()
    }

}