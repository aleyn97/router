package com.aleyn.processor.data

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/7/27 16:17
 */

data class DefinitionAnnotation(
    val keyword: String,
    val annotationType: KClass<*>
) {
    val annotationName = annotationType.simpleName
}

sealed class Definition(
    val label: String,
    val parameters: List<ConstructorParameter>,
    val packageName: String,
    val qualifier: String? = null,
    val lazy: Boolean? = null,
    val keyword: DefinitionAnnotation,
    val binding: KSDeclaration?,
    var targetFile: KSFile? = null,
) {

    fun isType(keyword: DefinitionAnnotation): Boolean = this.keyword == keyword

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Definition

        if (label != other.label) return false
        if (packageName != other.packageName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + packageName.hashCode()
        return result
    }

    override fun toString(): String {
        return "label:$label," +
                "parameters:$parameters," +
                "packageName:$packageName," +
                "qualifier:$qualifier," +
                "isCreatedAtStart:$lazy," +
                "keyword：${keyword}," +
                "bindings:$binding"
    }

    class ClassDefinition(
        packageName: String,
        qualifier: String?,
        lazy: Boolean? = null,
        keyword: DefinitionAnnotation,
        className: String,
        constructorParameters: List<ConstructorParameter> = emptyList(),
        binding: KSDeclaration?,
        targetFile: KSFile? = null,
    ) : Definition(
        className,
        constructorParameters,
        packageName,
        qualifier,
        lazy,
        keyword,
        binding,
        targetFile
    )


}

sealed class ConstructorParameter(val nullable: Boolean = false) {

    data class Dependency(
        val value: String? = null,
        val isNullable: Boolean = false,
        val kind: DependencyKind = DependencyKind.Single
    ) : ConstructorParameter(isNullable)

    data class ParameterInject(val isNullable: Boolean = false) : ConstructorParameter(isNullable)
}

enum class DependencyKind {
    Single, List
}