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
        val definitions: List<Definition>
    ) : RouterMeta()

    data class RouterAutowired(
        val pkgName: String,
        val simpleName: String,
        val list: List<Pair<String, KSPropertyDeclaration>>
    ) : RouterMeta()

}

