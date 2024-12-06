package com.aleyn.router.plug.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author: Aleyn
 * @date: 2023/7/11 11:15
 */

/**
 * 每个 module 要初始化的路由类后缀
 */
internal const val MODULE_ROUTER_CLASS_SUFFIX = "__ModuleRouter__Registered"


class InsertCodeVisitor(
    nextVisitor: ClassVisitor,
    private val allModuleClass: List<String>
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
            "initModuleRouter" -> ModuleRouterInstructAdapter(
                Opcodes.ASM9,
                mv,
                allModuleClass
            )

            else -> mv
        }
    }
}