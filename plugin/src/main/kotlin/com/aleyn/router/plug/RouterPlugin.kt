package com.aleyn.router.plug

import com.aleyn.plugin.ROUTER_CORE
import com.aleyn.plugin.ROUTER_PROCESSOR
import com.aleyn.plugin.ROUTER_VERSION
import com.aleyn.router.plug.task.GenLRouterDocTask
import com.aleyn.router.plug.task.LRouterClassTask
import com.aleyn.router.plug.task.RouterStubClassTask
import com.android.build.api.AndroidPluginVersion
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.DynamicFeaturePlugin
import com.android.build.gradle.LibraryPlugin
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author: Aleyn
 * @date: 2023/7/7 17:23
 */

class RouterPlugin : Plugin<Project> {

    private val kspPlugin = "com.google.devtools.ksp"

    override fun apply(project: Project) {

        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)

        val isAndroid = (isApp
                || project.plugins.hasPlugin(LibraryPlugin::class.java)
                || project.plugins.hasPlugin(DynamicFeaturePlugin::class.java))

        require(isAndroid) { "LRouter cannot be applied to non Android modules" }

        val isAuto = project.properties["LRouter.auto"] ?: "true"
        if (isAuto == "true") addDependencies(project)

        project.extensions.findByType(KspExtension::class.java)?.apply {
            arg("L_ROUTER_MODULE_NAME", project.name)
        }

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        if (isApp) {
            val gradleVersion = project.gradle.gradleVersion
            println("-------- LRouter Current environment --------")
            println("Gradle Version $gradleVersion")
            println("${androidComponents.pluginVersion}")
            println("JDK Version ${System.getProperty("java.version")}")
            println("LRouter Version $ROUTER_VERSION")
            println("LRouter Auto $isAuto")
        }

        require(androidComponents.pluginVersion >= AndroidPluginVersion(7, 4, 0)) {
            "AGP version must be at least 7.4 or higher. current version ${androidComponents.pluginVersion}"
        }

        androidComponents.onVariants { variant ->
            if (isApp) {
                val addSourceTaskProvider = project.tasks.register(
                    "${variant.name}RouterStubClass",
                    RouterStubClassTask::class.java
                )
                variant.sources.java?.addGeneratedSourceDirectory(
                    addSourceTaskProvider,
                    RouterStubClassTask::outputFolder
                )
            }


            val routerDir = "intermediates/router_scanning_data/${variant.name}"
            val taskProvider = project.tasks.register(
                "${variant.name}LRouterHandleClasses",
                LRouterClassTask::class.java
            ) {
                if (isApp) it.outputs.upToDateWhen { false }
                it.cachePath.set(routerDir)
            }
            variant.artifacts
                .forScope(ScopedArtifacts.Scope.PROJECT)
                .use(taskProvider)
                .toTransform(
                    ScopedArtifact.CLASSES,
                    LRouterClassTask::allJars,
                    LRouterClassTask::allDirectories,
                    LRouterClassTask::output
                )
        }
        project.tasks.register("generateLRouterDoc", GenLRouterDocTask::class.java)
    }

    private fun addDependencies(project: Project) {
        if (!project.plugins.hasPlugin(kspPlugin)) {
            project.plugins.apply(kspPlugin)
        }

        var dependVer = project.rootProject.properties["routerVersion"] as? String
        if (dependVer.isNullOrBlank()) dependVer = ROUTER_VERSION


        val router = project.rootProject.findProject("router")
        if (router == null) {
            project.dependencies.add("implementation", ROUTER_CORE + dependVer)
        } else {
            project.dependencies.add("implementation", router)
        }
        val processor = project.rootProject.findProject("processor")
        if (processor == null) {
            project.dependencies.add("ksp", ROUTER_PROCESSOR + dependVer)
        } else {
            project.dependencies.add("ksp", processor)
        }
    }

}