package com.aleyn.router.plug.task

import com.aleyn.router.plug.RouterPlugin
import com.aleyn.router.plug.data.HandleModel
import com.aleyn.router.plug.visitor.FindHandleClass
import com.aleyn.router.plug.visitor.InsertCodeVisitor
import com.android.build.gradle.internal.cxx.io.writeTextIfDifferent
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
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
internal const val ROUTER_GENERATE = "com/router/LRouterGenerateImpl.class"

abstract class LRouterClassTask : DefaultTask() {

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Input
    abstract val cachePath: Property<String>

    @TaskAction
    fun taskAction() {

        val handleModels = arrayListOf<HandleModel>()

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

                    if (filePath == ROUTER_GENERATE) {
                        waitInsertJar = file
                        return@forEach
                    }

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
                    jarOutput.putNextEntry(JarEntry(jarEntry.name))
                    jarFile.getInputStream(jarEntry).use { it.copyTo(jarOutput) }

                    if (jarEntry.name.endsWith(".class")) {
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
        val cacheDir = project.layout.buildDirectory.dir(cachePath.get()).get()
        handleModels.write(cacheDir)

        if (project.isApp()) {
            if (waitInsertJar == null) throw RuntimeException("The class to insert was not found, please check for references LRouter")
            val cachePath = cachePath.get().substringBeforeLast("/")
            val allBuildDir = project.rootProject.subprojects
                .filter { it.plugins.hasPlugin(RouterPlugin::class.java) }
                .map { it.layout.buildDirectory.dir(cachePath).get() }

            jarOutput.putNextEntry(JarEntry(ROUTER_GENERATE))
            val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val insertVisitor = InsertCodeVisitor(writer, allBuildDir)
            ClassReader(waitInsertJar!!.inputStream()).accept(insertVisitor, ClassReader.SKIP_DEBUG)
            jarOutput.write(writer.toByteArray())
            jarOutput.closeEntry()
        }
        jarOutput.close()
    }

}

fun InputStream.findClass(outHandle: ArrayList<HandleModel>) {
    use { ClassReader(it).accept(FindHandleClass(outHandle), 0) }
}

private fun List<HandleModel>.write(outDir: Directory) {
    val cacheFileDir = outDir.asFile
    if (!cacheFileDir.exists()) cacheFileDir.mkdirs()
    this.groupBy { it::class.simpleName!! }.onEach { entry ->
        val fileName = "${entry.key.lowercase()}.txt"
        val file = outDir.file(fileName).asFile
        if (!file.exists()) file.createNewFile()
        val addBuffer = StringBuffer()
        entry.value.forEachIndexed { index, model ->
            when (model) {
                is HandleModel.Autowired -> {
                    addBuffer.append(model.className)
                }

                is HandleModel.Module -> {
                    addBuffer.append(model.className)
                }

                is HandleModel.Initializer -> {
                    addBuffer.append("${model.priority} ${model.async} ${model.className}")
                }

                is HandleModel.Intercept -> {
                    addBuffer.append("${model.priority} ${model.className}")
                }
            }
            if (index != entry.value.lastIndex) addBuffer.append("\n")
        }
        file.writeText(addBuffer.toString())
    }

}