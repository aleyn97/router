package com.aleyn.processor.data

import com.google.devtools.ksp.symbol.KSPropertyDeclaration

/**
 * @author : Aleyn
 * @date : 2023/07/23 : 17:42
 */
sealed class RouterMeta {

    data class ModuleRouter(
        var path: String = "",
        var desc: String = "",
        var other: Int = 0,
        var className: String = ""
    ) : RouterMeta()

    data class Module(
        val router: List<ModuleRouter>,
        val definitions: List<Definition>,
        val interceptors: List<Interceptor>,
        val initializers: List<Initializer>,
        val childModule: List<ChildModule>,
    ) : RouterMeta()

    data class RouterAutowired(
        val pkgName: String,
        val simpleName: String,
        val list: List<Pair<String, KSPropertyDeclaration>>
    ) : RouterMeta()

    data class Interceptor(
        val pkgName: String,
        val simpleName: String,
        val priority: Byte = 0,
    ) : RouterMeta()

    data class Initializer(
        val priority: Byte = 0,
        val className: String,
        val async: Boolean,
    ) : RouterMeta()


    data class ChildModule(val classNames: List<String>) : RouterMeta()

}

