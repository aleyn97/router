package com.aleyn.router.plug.task

import com.aleyn.router.plug.data.HandleModel
import com.aleyn.router.plug.visitor.FindHandleClass
import com.aleyn.router.plug.visitor.InsertCodeVisitor
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
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @author: Aleyn
 * @date: 2023/7/14 11:03
 */

/**
 * 待插桩类
 */
internal const val ROUTER_INJECT = "com/aleyn/router/inject/LRouterGenerateKt.class"

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

        val handleModels = arrayListOf<HandleModel>()

        var waitInsertJar: File? = null

        val jarOutput = JarOutputStream(BufferedOutputStream(FileOutputStream(output.get().asFile)))

        handleModels.clear()

        allDirectories.get().forEach { directory ->
            val directoryUri = directory.asFile.toURI()
            directory.asFile
                .walk()
                .filter { it.isFile }
                .forEach { file ->
                    val filePath = directoryUri
                        .relativize(file.toURI())
                        .path
                        .replace(File.separatorChar, '/')
                    jarOutput.putNextEntry(JarEntry(filePath))
                    file.inputStream().use { it.copyTo(jarOutput) }
                    jarOutput.closeEntry()

                    if (file.name.endsWith(".class")) {
                        file.inputStream().findClass(handleModels)
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
                        runCatching {
                            jarFile.getInputStream(jarEntry).findClass(handleModels)
                        }.onFailure {
                            println("LRouter handle " + jarEntry.name + " error:${it.message}")
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

fun InputStream.findClass(outHandle: ArrayList<HandleModel>) {
    use { ClassReader(it).accept(FindHandleClass(outHandle), 0) }
}