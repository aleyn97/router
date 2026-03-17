package com.aleyn.router.plug.task

import com.aleyn.router.plug.data.RouterTable
import com.google.gson.Gson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @author: Aleyn
 * @date: 2023/8/7 11:28
 */
abstract class GenLRouterDocTask : DefaultTask() {

    init {
        group = "router"
    }

    private val ourDir = "${project.layout.buildDirectory.get().asFile}/router"
    private val ourFilePath = "${ourDir}/routerTable.json"

    @TaskAction
    fun genRouterTable() {
        val tables = ArrayList<RouterTable>()

        project.dependProject()
            .plus(project)
            .forEach { curProject ->
                val genFile =
                    curProject.file("${curProject.layout.buildDirectory.get().asFile}/generated/ksp")
                        .listFiles()
                curProject.files(genFile)
                    .asFileTree
                    .filter { it.name.endsWith("ModuleRouter__Registered.kt") }
                    .map(::readRouterInfo)
                    .forEach(tables::addAll)
            }

        val dirFile = File(ourDir)
        if (!dirFile.exists()) dirFile.mkdirs()
        File(ourFilePath).writeText(Gson().toJson(tables), Charsets.UTF_8)
        println("Generate RouterTable Success.")
    }


    private fun readRouterInfo(file: File): List<RouterTable> {
        val tables = ArrayList<RouterTable>()

        val fileText = file.readText()

        val routeMetaRegex = "(?<=RouteMeta\\().*?(?=\\))".toRegex()
        val routeMeta = routeMetaRegex.findAll(fileText)
        routeMeta.forEach {
            val array = it.value.split(",")
            RouterTable(
                path = array[0].trimColon(),
                desc = array[1].trimColon(),
                other = array[2].trim().toInt(),
                className = array[3].trimColon(),
            ).let(tables::add)
        }

        val routeActionRegex = "(?<=LRouter.addRouterAction\\().+?(?=\\()".toRegex()
        val routeActions = routeActionRegex.findAll(fileText)
        routeActions.forEach {
            val array = it.value.split(",")
            RouterTable(
                path = array[0].trimColon(),
                other = 1,
                className = array[1].trim()
            ).let(tables::add)
        }
        return tables
    }

}


private fun String.trimColon() = trim().removeSuffix("\"").removePrefix("\"")