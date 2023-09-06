package com.aleyn.processor.scanner

import com.aleyn.annotation.Factory
import com.aleyn.annotation.Singleton
import com.aleyn.processor.data.Definition
import com.aleyn.processor.data.DefinitionAnnotation
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

/**
 * @author : Aleyn
 * @date : 2023/07/27 : 21:33
 */

class ClassInjectScanner(private val logger: KSPLogger) {

    fun createClassDefinition(element: KSAnnotated): Definition {
        val ksClassDeclaration = (element as KSClassDeclaration)
        val packageName = ksClassDeclaration.packageName.asString()
        val className = ksClassDeclaration.simpleName.asString()
        val qualifier = ksClassDeclaration.getQualifier()
        val annotations = element.annotations.filterValidAnn()

        return annotations.firstNotNullOf {
            createClassDefinition(
                it,
                ksClassDeclaration,
                packageName,
                qualifier,
                className,
            )
        }

    }

    private fun createClassDefinition(
        annotation: KSAnnotation,
        declaration: KSClassDeclaration,
        packageName: String,
        qualifier: String?,
        className: String,
    ): Definition.ClassDefinition {

        //默认绑定父类
        val binding = declaration.superTypes.firstOrNull()?.resolve()?.declaration

        // 构造函数参数
        val constructorParams = declaration.primaryConstructor
            ?.parameters?.map(::getConstructorParameter)

        val keyword: DefinitionAnnotation
        var lazy: Boolean? = null

        var declarationBind: KSType?
        when (annotation.shortName.asString()) {
            Singleton::class.simpleName -> {
                keyword = SINGLETON
                lazy = annotation.arguments.getArgValue("lazy") ?: true
                declarationBind = annotation.arguments.getArgValue<KSType>("bind")
            }

            Factory::class.simpleName -> {
                keyword = FACTORY
                declarationBind = annotation.arguments.getArgValue<KSType>("bind")
            }

            else -> {
                logger.error("Unknown annotation type: ${annotation.shortName.asString()}\"")
                error("Unknown annotation type: ${annotation.shortName.asString()}")
            }
        }
        var currentBind = binding
        declarationBind?.declaration?.let {
            if (it.qualifiedName?.asString() != "kotlin.Unit") {
                currentBind = it
            }
        }
        return Definition.ClassDefinition(
            packageName,
            qualifier,
            lazy,
            keyword,
            className,
            constructorParams ?: emptyList(),
            currentBind,
        )
    }
}