package com.aleyn.processor.scanner

import com.aleyn.annotation.Autowired
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