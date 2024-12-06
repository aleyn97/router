package com.aleyn.router.plug.task

import com.aleyn.router.plug.visitor.InsertCodeVisitor
import com.aleyn.router.plug.visitor.MODULE_ROUTER_CLASS_SUFFIX
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @author: Aleyn
 * @date: 2024/12/5 18:47
 */

/**
 * 待插桩类
 */
internal const val ROUTER_INJECT = "com/aleyn/router/inject/LRouterAutoGenerateKt.class"

private val blackList = arrayOf(
    "androidx/",
    "android/",
    "kotlin/",
    "kotlinx/",
    "com/google/",
    "org/",
    "com/aleyn/router/"
)

abstract class LRouterClassTask : DefaultTask() {

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {

        val handleModels = arrayListOf<String>()

        var waitInsertJar: File? = null

        val jarOutput = JarOutputStream(BufferedOutputStream(FileOutputStream(output.get().asFile)))

        handleModels.clear()

        allDirectories.get().onEach { directory ->
            val directoryUri = directory.asFile.toURI()
            directory.asFile
                .walk()
                .filter { it.isFile }
                .forEach { file ->
                    val filePath = directoryUri
                        .relativize(file.toURI())
                        .path
                        .replace(File.separatorChar, '/')

                    if (filePath == ROUTER_INJECT) {
                        waitInsertJar = file
                        return@forEach
                    }

                    jarOutput.putNextEntry(JarEntry(filePath))
                    file.inputStream().use { it.copyTo(jarOutput) }
                    jarOutput.closeEntry()

                    if (file.name.endsWith(".class")) {
                        val className = file.name.removeSuffix(".class")
                        if (className.isModuleClass()) {
                            file.inputStream().use {
                                val classReader = ClassReader(it)
                                handleModels.add(classReader.className)
                                println("LRouter find Module class: ${classReader.className}")
                            }
                        }
                    }
                }
        }

        allJars.get().onEach { file ->
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                try {
                    if (jarEntry.name == ROUTER_INJECT) {
                        waitInsertJar = file.asFile
                        return@forEach
                    }
                    jarOutput.putNextEntry(JarEntry(jarEntry.name))
                    jarFile.getInputStream(jarEntry).use { it.copyTo(jarOutput) }

                    val have = blackList.any { jarEntry.name.startsWith(it) }

                    if (!have && jarEntry.name.endsWith(".class")) {
                        val className = jarEntry.name.removeSuffix(".class")
                        if (className.isModuleClass()) {
                            handleModels.add(className)
                            println("LRouter find Module class: $className")
                        }
                    }
                } catch (_: Exception) {
                } finally {
                    jarOutput.closeEntry()
                }
            }
            jarFile.close()
        }

        if (waitInsertJar == null) throw RuntimeException("The class to insert was not found, please check for references LRouter")
        val jarFile = JarFile(waitInsertJar!!)
        jarOutput.putNextEntry(JarEntry(ROUTER_INJECT))
        jarFile.getInputStream(jarFile.getJarEntry(ROUTER_INJECT)).use {
            val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val insertVisitor = InsertCodeVisitor(writer, handleModels)
            ClassReader(it).accept(insertVisitor, ClassReader.SKIP_DEBUG)
            jarOutput.write(writer.toByteArray())
            jarOutput.closeEntry()
        }
        jarFile.close()
        jarOutput.close()
    }
}

fun String.isModuleClass(): Boolean {
    return this.endsWith(MODULE_ROUTER_CLASS_SUFFIX)
}
