package com.aleyn.router.plug.task

import com.aleyn.router.plug.visitor.InsertCodeVisitor
import com.aleyn.router.plug.visitor.MODULE_ROUTER_CLASS_SUFFIX
import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.objectweb.asm.ClassVisitor

/**
 * @author: Aleyn
 * @date: 2024/4/1 12:07
 */
internal const val GENERATE_INJECT = "com.router.LRouterGenerateImpl"

abstract class LRouterAsmClassVisitor : AsmClassVisitorFactory<ParametersImpl> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        if (classContext.currentClassData.className == GENERATE_INJECT) {
            val inputFiles = parameters.get().inputFiles.get()
            val genDirName = parameters.get().genDirName.get()

            val allModels = inputFiles.asSequence()
                .flatMap { dir -> dir.asFileTree.matching { it.include("**/**.kt") } }
                .mapNotNull {
                    val className = it.absolutePath
                        .replace("\\", "/")
                        .substringAfter(genDirName)
                        .substringAfter("kotlin/")
                        .removeSuffix(".kt")

                    if (className.endsWith(MODULE_ROUTER_CLASS_SUFFIX)) {
                        return@mapNotNull className
                    }
                    return@mapNotNull null
                }.toList()
            return InsertCodeVisitor(nextClassVisitor, allModels)
        }
        return nextClassVisitor
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className == "com.router.LRouterGenerateImpl"
    }
}


interface ParametersImpl : InstrumentationParameters {

    @get:Internal
    val genDirName: Property<String>

    @get:Internal
    val inputFiles: ListProperty<Directory>
}