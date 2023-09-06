package com.aleyn.processor.generator

import com.aleyn.processor.data.RouterMeta
import com.aleyn.processor.scanner.SINGLETON
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.jvm.jvmName
import com.squareup.kotlinpoet.jvm.jvmStatic

/**
 * @author : Aleyn
 * @date : 2023/07/23 : 14:47
 */

internal const val MODULE_ROUTER_CLASS_SUFFIX = "__ModuleRouter__Registered"

internal const val AUTOWIRED_SUFFIX = "__LRouter\$\$Autowired"

internal const val FUN_INJECT_NAME = "autowiredInject"

internal const val LINE_START = "  "

/**
 * 生成参数注入类
 */
fun RouterMeta.RouterAutowired.generatorClass(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
) {
    val routerAutowired = this
    val pkgName = routerAutowired.pkgName
    val simpleName = routerAutowired.simpleName
    val list = routerAutowired.list

    val parentDeclaration =
        routerAutowired.list.firstOrNull()?.second?.parent as? KSClassDeclaration

    val className = simpleName + AUTOWIRED_SUFFIX

    val fileBuilder = FileSpec.builder(pkgName, className)
        .jvmName(className)
        .addImport("com.aleyn.router.parser", "DefaultParamParser")
        .addImport(pkgName, simpleName)

    val classSpec = TypeSpec.objectBuilder(className)
        .addAnnotation(ClassName.bestGuess("androidx.annotation.Keep"))

    val typeParams = if (parentDeclaration?.typeParameters.isNullOrEmpty()) "" else
        parentDeclaration!!.typeParameters.joinToString(", ", "<", ">") {
            "*"
        }

    val funSpec = FunSpec.builder(FUN_INJECT_NAME)
        .jvmStatic()
        .addParameter("target", Any::class)
        .beginControlFlow("\nif (target is ${simpleName}$typeParams)")

    list.forEach { item ->
        val declaration = item.second

        val paramKey = item.first
        val fieldName = declaration.simpleName.asString()
        val type = declaration.type.toString()

        val paramType = declaration.type.resolve()
        val typeDeclaration = paramType.declaration as KSClassDeclaration

        if (!type.isPrimitiveAndString()) {
            val typePkgName = paramType.declaration.packageName.asString()
            fileBuilder.addImport(typePkgName, type)
        }

        val typeAllName = declaration.getAllTypeStr()

        if (typeDeclaration.isParcelable()) {
            val key = paramKey.ifBlank { fieldName }
            funSpec.addCode(
                "\nDefaultParamParser.parseDefault<%1L>(target, \"%2L\", %3L)\n?.let{ target.%4L = it }\n",
                typeAllName,
                key,
                "$type::class.java",
                fieldName
            )
        } else {
            funSpec.addCode(
                "\nDefaultParamParser.parseAny<%1L>(target, \"%2L\", \"%3L\")\n?.let{ target.%3L = it }\n",
                typeAllName,
                paramKey,
                fieldName,
            )
        }
    }

    funSpec.endControlFlow()
    classSpec.addFunction(funSpec.build())

    logger.info("gen :$pkgName.$className")

    val fileSpec = fileBuilder.addType(classSpec.build()).build()

    codeGenerator.getFile(pkgName, className)
        .bufferedWriter()
        .use { fileSpec.writeTo(it) }
}

/**
 *  生成 Module 类
 * 包含 路由注册，类注入
 */
fun RouterMeta.Module.generatorModule(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
    moduleName: String?
) {
    logger.logging("start generator${moduleName}Module")
    val pkgName = "com.module.router"

    if (this.router.isEmpty() && this.definitions.isEmpty()) return

    val className = moduleName.orEmpty() + MODULE_ROUTER_CLASS_SUFFIX

    val fileBuilder = FileSpec.builder(pkgName, className)
        .jvmName(className)
        .addImport("com.aleyn.router", "LRouter")
        .addImport("com.aleyn.router.core", "RouteMeta")

    val routerFunSpec = FunSpec.builder("registerRouter")
        .addAnnotation(ClassName.bestGuess("androidx.annotation.Keep"))

    router.forEach {
        routerFunSpec.addCode(
            "${LINE_START}RouteMeta(\"%1L\", \"%2L\", %3L, \"%4L\").let(LRouter::registerRoute)\n",
            it.path,
            it.desc,
            it.other,
            it.className,
        )
    }

    val fileSpec = fileBuilder
        .addFunction(routerFunSpec.build())
        .addFunction(genDefinition(fileBuilder))
        .build()

    codeGenerator.getFile(pkgName, className)
        .bufferedWriter()
        .use { fileSpec.writeTo(it) }
}

/**
 * 生成注入类注册代码
 */
private fun RouterMeta.Module.genDefinition(fileBuilder: FileSpec.Builder): FunSpec {
    val definitionFun = FunSpec.builder("initDefinition")
    if (this.definitions.isNotEmpty()) {
        this.definitions.forEach {
            val paramGet = generateConstructor(it.parameters)
            if (!paramGet.endsWith("()")) {
                fileBuilder.addImport("com.aleyn.router.inject.qualifier", "sq")
            }
            val qualifier = if (it.qualifier.isNullOrBlank()) "" else {
                fileBuilder.addImport("com.aleyn.router.inject.qualifier", "sq")
                "sq(\"${it.qualifier}\")"
            }
            val lazy = if (it.isType(SINGLETON)) {
                (if (qualifier.isBlank()) "" else ", ") + "lazy = ${it.lazy}"
            } else ""

            fileBuilder.addImport(it.packageName, it.label)
            fileBuilder.addImport("com.aleyn.router.inject", it.keyword.keyword)

            val superName = it.binding?.simpleName?.asString()
            var bindCls = ""
            if (it.binding != null && superName != "Any") {
                fileBuilder.addImport(
                    it.binding.packageName.asString(),
                    it.binding.simpleName.asString()
                )
                bindCls = "<${superName}>"
            }

            definitionFun.addCode("$LINE_START${it.keyword.keyword}$bindCls($qualifier$lazy) { ${it.label}$paramGet }\n")
        }
    }
    return definitionFun
        .addAnnotation(ClassName.bestGuess("androidx.annotation.Keep"))
        .build()
}