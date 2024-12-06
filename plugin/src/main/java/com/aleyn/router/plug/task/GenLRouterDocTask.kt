package com.aleyn.router.plug.task

import com.aleyn.router.plug.data.RouterTable
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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

    private val ourDir = "${project.buildDir}/router"
    private val ourFilePath = "${ourDir}/routerTable.json"

    @TaskAction
    fun genRouterTable() {
        val tables = ArrayList<RouterTable>()

        project.dependProject()
            .plus(project)
            .forEach { curProject ->
                val genFile = curProject.file("${curProject.buildDir}/generated/ksp").listFiles()
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
        val start = "override fun registerRouter() {"
        var collect = false
        val lines = file.readLines()

        var temp = ""

        lines.forEach { line ->
            if (line.contains(start)) {
                collect = true
                return@forEach
            }
            if (!collect) return@forEach
            if (line.contains("}")) return tables
            temp += line.trim()
            if (!temp.endsWith(")")) return@forEach
            val first = temp.indexOfFirst { it == '(' } + 1
            val last = temp.indexOfLast { it == '.' } - 1
            val array = temp.substring(first, last).split(",")

            RouterTable(
                path = array[0].trimColon(),
                desc = array[1].trimColon(),
                other = array[2].trim().toInt(),
                className = array[3].trimColon(),
            ).let(tables::add)
            temp = ""
        }
        return tables
    }

}


private fun String.trimColon() = trim().removeSuffix("\"").removePrefix("\"")