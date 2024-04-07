package com.aleyn.router.plug.visitor

import com.aleyn.router.plug.data.HandleModel
import org.gradle.api.file.Directory
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author: Aleyn
 * @date: 2023/7/11 11:15
 */
class InsertCodeVisitor(
    nextVisitor: ClassVisitor,
    private val allRouterDir: List<Directory>,
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
                allRouterDir.getTarget()
            )

            "initModuleRouter" -> ModuleRouterInstructAdapter(
                Opcodes.ASM9,
                mv,
                allRouterDir.getTarget()
            )

            "registerIntercept" -> InterceptInstructAdapter(
                Opcodes.ASM9,
                mv,
                allRouterDir.getTarget()
            )

            "registerAllInitializer" -> InitializerInstructAdapter(
                Opcodes.ASM9,
                mv,
                allRouterDir.getTarget()
            )

            else -> mv
        }
    }

    private inline fun <reified T : HandleModel> List<Directory>.getTarget(): List<T> {
        val fileName = "${T::class.simpleName!!.lowercase()}.txt"
        return asSequence()
            .filter { it.asFile.exists() }
            .flatMap { dir -> dir.asFileTree.matching { it.include("**/${fileName}") } }
            .flatMap { it.readLines() }
            .filter { it.isNotBlank() }
            .mapNotNull {
                when (T::class) {
                    HandleModel.Autowired::class -> {
                        HandleModel.Autowired(it)
                    }

                    HandleModel.Module::class -> {
                        HandleModel.Module(it)
                    }

                    HandleModel.Intercept::class -> {
                        val params = it.split(" ")
                        HandleModel.Intercept(params[0].toByte(), params[1])
                    }

                    HandleModel.Initializer::class -> {
                        val params = it.split(" ")
                        HandleModel.Initializer(params[0].toInt(), params[1].toInt(), params[2])
                    }

                    else -> null
                } as? T
            }.toList()
    }
}