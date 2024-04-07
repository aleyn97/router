package com.aleyn.processor.scanner

import com.aleyn.annotation.Autowired
import com.aleyn.annotation.Initializer
import com.aleyn.annotation.Interceptor
import com.aleyn.annotation.Route
import com.aleyn.processor.data.RouterMeta
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

/**
 * @author : Aleyn
 * @date : 2023/07/25 : 21:35
 */
fun KSClassDeclaration.createAutowiredClass(): RouterMeta.RouterAutowired? {
    val list = arrayListOf<Pair<String, KSPropertyDeclaration>>()
    val pkgName = this.packageName.asString()
    val className = this.simpleName.asString()

    this.declarations
        .filterIsInstance<KSPropertyDeclaration>()
        .forEach { property ->
            property.annotations.firstOrNull {
                it.shortName.asString() == Autowired::class.simpleName
            }?.let { ann ->
                val paramKey = ann.arguments.firstOrNull()?.value.toString()
                list.add(paramKey to property)
            }
        }
    if (list.isNotEmpty()) {
        return RouterMeta.RouterAutowired(pkgName, className, list)
    }
    return null
}


fun KSClassDeclaration.createModuleRouter(): RouterMeta.ModuleRouter? {

    this.annotations.forEach { annotation ->
        if (annotation.shortName.asString() == Route::class.simpleName) {
            val router = RouterMeta.ModuleRouter()
            router.className = this.qualifiedName?.asString().orEmpty()
            annotation.arguments.forEach {
                when (it.name?.asString()) {
                    Route::path.name -> router.path = it.value.toString()
                    Route::desc.name -> router.desc = it.value.toString()
                    Route::other.name -> router.other = it.value as Int
                }
            }
            return router
        }
    }
    return null
}

fun KSClassDeclaration.createInterceptor(): RouterMeta.Interceptor? {

    this.annotations.forEach { annotation ->
        if (annotation.shortName.asString() == Interceptor::class.simpleName) {
            val pkgName = this.packageName.asString()
            val className = this.simpleName.asString()
            var priority: Byte = 0
            annotation.arguments.forEach {
                when (it.name?.asString()) {
                    Interceptor::priority.name -> {
                        priority = (it.value as? Byte) ?: 0
                    }
                }
            }
            return RouterMeta.Interceptor(pkgName, className, priority)
        }
    }
    return null
}

fun KSClassDeclaration.createInitializer(): RouterMeta.Initializer? {
    this.annotations.forEach { annotation ->
        if (annotation.shortName.asString() == Initializer::class.simpleName) {
            val className = this.qualifiedName?.asString().orEmpty()
            var priority: Byte = 0
            var async = false
            annotation.arguments.forEach {
                when (it.name?.asString()) {
                    Initializer::priority.name -> priority = (it.value as? Byte) ?: 0

                    Initializer::async.name -> async = (it.value as? Boolean) ?: false
                }
            }
            return RouterMeta.Initializer(priority, className, async)
        }
    }
    return null
}