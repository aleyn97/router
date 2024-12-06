package com.aleyn.processor.generator

import com.aleyn.annotation.IRouterModule
import com.aleyn.processor.data.RouterMeta
import com.aleyn.processor.scanner.SINGLETON
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
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
    moduleName: String?,
    routerAutowired: List<RouterMeta.RouterAutowired>
) {
    logger.logging("start generator${moduleName}Module")
    val pkgName = "com.module.router"
    if (this.router.isEmpty()
        && this.definitions.isEmpty()
        && this.interceptors.isEmpty()
        && this.initializers.isEmpty()
        && routerAutowired.isEmpty()
        && this.childModule.isEmpty()
    ) return

    val className = moduleName.orEmpty() + MODULE_ROUTER_CLASS_SUFFIX

    val fileBuilder = FileSpec.builder(pkgName, className)
        .jvmName(className)
        .addImport("com.aleyn.router", "LRouter")
        .addImport("com.aleyn.router.core", "RouteMeta")
        .addImport("com.aleyn.annotation", "IRouterModule")

    val routerFunSpec = FunSpec.builder("registerRouter")
        .addModifiers(KModifier.OVERRIDE)

    router.forEach {
        routerFunSpec.addCode(
            "${LINE_START}RouteMeta(\"%1L\", \"%2L\", %3L, \"%4L\").let(LRouter::registerRoute)\n",
            it.path,
            it.desc,
            it.other,
            it.className,
        )
    }

    val instanceProperty = PropertySpec.builder("INSTANCE", ClassName(pkgName, className))
        .jvmStatic()
        .initializer("$pkgName.$className()")
        .build()
    val companion = TypeSpec.companionObjectBuilder()
        .addProperty(instanceProperty)
        .build()

    val classType = TypeSpec.classBuilder(className)
        .addAnnotation(ClassName.bestGuess("androidx.annotation.Keep"))
        .addSuperinterface(ClassName.bestGuess("com.aleyn.annotation.IRouterModule"))
        .addType(companion)
        .addFunction(routerFunSpec.build())
        .addFunction(genDefinition(fileBuilder))
        .addFunction(genInterceptor(fileBuilder))
        .addFunction(genInitializer(fileBuilder))
        .addFunction(routerAutowired.genRouterAutowired(fileBuilder))
        .addFunction(genChildModules(fileBuilder))
        .build()

    val fileSpec = fileBuilder
        .addType(classType)
        .build()

    codeGenerator.getFile(pkgName, className)
        .bufferedWriter()
        .use { fileSpec.writeTo(it) }

    codeGenerator.getFile(
        "META-INF/services",
        "com.aleyn.annotation.IRouterModule",
        ""
    ).use {
        val name = "$pkgName.$className"
        it.write(name.toByteArray())
    }
}

/**
 * 生成注入类注册代码
 */
private fun RouterMeta.Module.genDefinition(fileBuilder: FileSpec.Builder): FunSpec {
    val definitionFun = FunSpec.builder("initDefinition")
        .addModifiers(KModifier.OVERRIDE)

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

/**
 * 生成拦截器注册代码
 */
private fun RouterMeta.Module.genInterceptor(fileBuilder: FileSpec.Builder): FunSpec {
    return FunSpec.builder("addInterceptor")
        .addModifiers(KModifier.OVERRIDE)
        .apply {
            fileBuilder.addImport("com.aleyn.router", "LRouter")
            interceptors.forEach {
                addCode(
                    "${LINE_START}LRouter.addInterceptor(%1L, %2L)\n",
                    it.priority,
                    "${it.pkgName}.${it.simpleName}()"
                )
            }
        }
        .build()
}

/**
 * 生成初始化器集合
 */
private fun RouterMeta.Module.genInitializer(fileBuilder: FileSpec.Builder): FunSpec {

    return FunSpec.builder("registerInitializer")
        .addModifiers(KModifier.OVERRIDE)
        .apply {
            fileBuilder.addImport("com.aleyn.router", "LRouter")
            initializers.forEach {
                addCode(
                    "${LINE_START}LRouter.registerInitializer(%1L, %2L, %3L)\n",
                    it.priority,
                    it.async,
                    "${it.className}()"
                )
            }
        }
        .build()
}

/**
 * 页面参数生成
 */
private fun List<RouterMeta.RouterAutowired>.genRouterAutowired(fileBuilder: FileSpec.Builder): FunSpec {

    val params = ParameterSpec.builder("target", Any::class.asTypeName().copy(true))
        .build()

    return FunSpec.builder("injectAutowired")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter(params)
        .apply {
            fileBuilder.addImport("com.aleyn.router", "LRouter")
            addCode("target ?: return\n")
            this@genRouterAutowired.forEach {
                val pkgName = it.pkgName
                val simpleName = it.simpleName

                val className = simpleName + AUTOWIRED_SUFFIX
                fileBuilder.addImport(pkgName, className)
                addCode(
                    """
                        try {
                         `${className}`.autowiredInject(target)
                        } catch(e:Exception){}
                        
                    """.trimIndent()
                )
            }
        }
        .build()
}


/**
 * 生成初始化器集合
 */
private fun RouterMeta.Module.genChildModules(fileBuilder: FileSpec.Builder): FunSpec {
    return FunSpec.builder("childModule")
        .addModifiers(KModifier.OVERRIDE)
        .returns(List::class.parameterizedBy(IRouterModule::class))
        .apply {
            fileBuilder.addImport("com.aleyn.router", "LRouter")
            addCode("val list = arrayListOf<IRouterModule>()\n")
            childModule.flatMap { it.classNames }.forEach { className ->
                addCode("list.add(${className}.INSTANCE)\n")
            }
            addCode("return list")
        }
        .build()
}
