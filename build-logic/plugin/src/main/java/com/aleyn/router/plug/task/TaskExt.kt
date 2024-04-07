package com.aleyn.router.plug.task

import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import java.io.File

/**
 * @author: Aleyn
 * @date: 2023/8/7 11:41
 */

fun Project.isApp() = plugins.hasPlugin("com.android.application")

fun Project.isAndroid() =
    isApp() || plugins.hasPlugin("com.android.library")


fun Project.dependProject(): List<Project> {
    val projects = ArrayList<Project>()
    arrayOf("api", "implementation").forEach { name ->
        val dependencyProjects = configurations.getByName(name).dependencies
            .filterIsInstance<DefaultProjectDependency>()
            .filter { it.dependencyProject.isAndroid() }
            .map { it.dependencyProject }
        projects.addAll(dependencyProjects)
        dependencyProjects.forEach { projects.addAll(it.dependProject()) }
    }
    return projects
}