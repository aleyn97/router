package com.aleyn.processor.generator

import com.aleyn.processor.data.ConstructorParameter
import com.aleyn.processor.data.DependencyKind
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSTypeArgument
import java.io.OutputStream

/**
 * @author: Aleyn
 * @date: 2023/7/24 16:15
 */

internal fun String.isPrimitiveAndString(): Boolean {
    return when (this.lowercase()) {
        "byte",
        "short",
        "int",
        "long",
        "float",
        "double",
        "boolean",
        "char",
        "string" -> true

        else -> false
    }
}

/**
 * 是否是序列化类
 */
internal fun KSClassDeclaration.isParcelable(): Boolean {
    return this.superTypes
        .mapNotNull { it.resolve().declaration.qualifiedName?.asString() }
        .any {
            it == "android.os.Parcelable" || it == "java.io.Serializable"
        }
}

/**
 * 获取类型字符串,包括所有泛型
 */
internal fun List<KSTypeArgument>.getTypeStr(): String {
    var typeArg = ""
    forEachIndexed { index, it ->
        val type = it.type?.resolve() ?: return ""
        typeArg += if (index == 0) "<" else ", "

        typeArg += type.declaration.qualifiedName!!.asString()
        val childArg = it.type!!.element?.typeArguments
        if (!childArg.isNullOrEmpty()) {
            typeArg += childArg.getTypeStr()
        }
        if (index == size - 1) typeArg += ">"
    }
    return typeArg
}

/**
 * 生成参数注入字符串
 */
internal fun generateConstructor(constructorParameters: List<ConstructorParameter>): String {
    return constructorParameters.joinToString(
        prefix = "(",
        separator = ",",
        postfix = ")"
    ) { param ->
        val isNullable = param.nullable
        when (param) {
            is ConstructorParameter.Dependency -> {
                when (param.kind) {
                    DependencyKind.List -> "getAll()"
                    else -> {
                        val qualifier = param.value?.let { "sq(\"$it\")" }.orEmpty()
                        if (isNullable) "getOrNull($qualifier)" else "get($qualifier)"
                    }
                }
            }

            is ConstructorParameter.ParameterInject -> if (!isNullable) "it.get()" else "it.getOrNull()"
        }
    }
}

internal fun CodeGenerator.genKtFile(
    packageName: String,
    fileName: String,
    vararg sources: KSFile
): OutputStream {
    return try {
        createNewFile(
            Dependencies(aggregating = false, sources = sources),
            packageName,
            fileName,
        )
    } catch (ex: FileAlreadyExistsException) {
        ex.file.outputStream()
    }
}

internal fun CodeGenerator.genResFile(
    packageName: String,
    fileName: String,
    vararg sources: KSFile
): OutputStream {
    return try {
        createNewFile(
            Dependencies(aggregating = false, sources = sources),
            packageName,
            fileName,
            ""
        )
    } catch (ex: FileAlreadyExistsException) {
        ex.file.outputStream()
    }
}