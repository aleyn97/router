package com.aleyn.processor.scanner

import com.aleyn.annotation.Factory
import com.aleyn.annotation.InParam
import com.aleyn.annotation.Qualifier
import com.aleyn.annotation.Singleton
import com.aleyn.processor.data.ConstructorParameter
import com.aleyn.processor.data.DefinitionAnnotation
import com.aleyn.processor.data.DependencyKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import java.util.Locale

/**
 * @author : Aleyn
 * @date : 2023/07/27 : 22:01
 */

val SINGLETON = DefinitionAnnotation("lRouterSingle", Singleton::class)
val FACTORY = DefinitionAnnotation("lRouterFactory", Factory::class)

val DEFINITION_ANNOTATION_LIST = listOf(SINGLETON, FACTORY)

val DEFINITION_ANNOTATION_LIST_NAMES =
    DEFINITION_ANNOTATION_LIST.map { it.annotationName?.lowercase(Locale.getDefault()) }


fun Sequence<KSAnnotation>.filterValidAnn() = this.filter {
    it.shortName.asString().lowercase(Locale.getDefault()) in DEFINITION_ANNOTATION_LIST_NAMES
}


@Suppress("UNCHECKED_CAST")
internal fun <T> List<KSValueArgument>.getArgValue(name: String = "value") =
    firstOrNull { it.name?.asString() == name }?.value as? T


fun KSAnnotated.getQualifier(): String? {
    return annotations.firstOrNull {
        it.shortName.asString() == Qualifier::class.simpleName
    }?.run {
        arguments.getArgValue<String>() ?: error("@Qualifier value is null")
    }
}

fun getConstructorParameter(param: KSValueParameter): ConstructorParameter {
    val firstAnnotation = param.annotations.firstOrNull()
    val annotationName = firstAnnotation?.shortName?.asString()
    val value = firstAnnotation?.arguments?.getArgValue<String>()
    val resolvedType = param.type.resolve()
    val isNullable = resolvedType.isMarkedNullable
    val resolvedTypeString = resolvedType.toString()

    val isList = resolvedTypeString.startsWith("List<")

    return when (annotationName) {
        InParam::class.simpleName -> ConstructorParameter.ParameterInject(isNullable)
        Qualifier::class.simpleName -> ConstructorParameter.Dependency(value, isNullable)
        else -> {
            if (isList) ConstructorParameter.Dependency(kind = DependencyKind.List)
            else ConstructorParameter.Dependency(isNullable = isNullable)
        }
    }
}