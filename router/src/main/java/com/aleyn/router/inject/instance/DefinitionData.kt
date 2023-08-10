package com.aleyn.router.inject.instance

import com.aleyn.router.inject.Core
import com.aleyn.router.inject.getFullName
import com.aleyn.router.inject.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/7/27 11:21
 */

enum class Kind {
    SINGLETON,
    FACTORY
}

data class Definition<T>(
    val primaryType: KClass<*>,
    var qualifier: Qualifier? = null,
    val def: Def<T>,
    val kind: Kind,
)


fun mappingKey(clazz: KClass<*>, typeQualifier: Qualifier?): String {
    val tq = typeQualifier?.value ?: ""
    return "${clazz.getFullName()}:$tq"
}

typealias Def<T> = Core.(Parameters) -> T

inline fun <reified T> createDefinition(
    kind: Kind = Kind.SINGLETON,
    qualifier: Qualifier? = null,
    noinline definition: Def<T>,
) = Definition(
    T::class,
    qualifier,
    definition,
    kind
)