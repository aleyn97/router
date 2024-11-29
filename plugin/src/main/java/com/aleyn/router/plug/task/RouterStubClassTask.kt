package com.aleyn.router.plug.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @author: Aleyn
 * @date: 2024/4/1 15:27
 */
abstract class RouterStubClassTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val outputFile = File(outputFolder.asFile.get(), "com/router/LRouterGenerateImpl.kt")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
                package com.router

                import androidx.annotation.Keep
                import com.aleyn.router.inject.ILRouterGenerate

                /**
                 * 插桩类，自动生成
                 */
                @Keep
                class LRouterGenerateImpl : ILRouterGenerate {
                    @Keep
                    override fun initModuleRouter() {
                    }
                }
            """.trimIndent()
        )
    }
}