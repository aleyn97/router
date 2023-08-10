package com.aleyn.router.plug.visitor

import com.aleyn.router.plug.data.HandleModel
import com.aleyn.router.plug.data.getTarget
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author: Aleyn
 * @date: 2023/7/11 11:15
 */
class InsertCodeVisitor(
    nextVisitor: ClassVisitor,
    private val handleModels: List<HandleModel>,
) : ClassVisitor(Opcodes.ASM9, nextVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return when (name) {
            "injectAutowired" -> AutowiredInstructAdapter(
                Opcodes.ASM9,
                mv,
                handleModels.getTarget()
            )

            "initModuleRouter" -> ModuleRouterInstructAdapter(
                Opcodes.ASM9,
                mv,
                handleModels.getTarget()
            )

            "registerIntercept" -> InterceptInstructAdapter(
                Opcodes.ASM9,
                mv,
                handleModels.getTarget()
            )

            "registerAllInitializer" -> InitializerInstructAdapter(
                Opcodes.ASM9,
                mv,
                handleModels.getTarget()
            )

            else -> mv
        }
    }

}