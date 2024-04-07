package com.aleyn.router.inject

import androidx.annotation.Keep

/**
 * @author: Aleyn
 * @date: 2024/4/1 14:49
 */
@Keep
interface ILRouterGenerate {

    fun injectAutowired(target: Any?)

    fun initModuleRouter()

    fun registerIntercept()

    fun registerAllInitializer()

}