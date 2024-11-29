package com.aleyn.annotation


/**
 * @author: Aleyn
 * @date: 2024/11/28 11:14
 */
interface IRouterModule {

    fun registerInitializer()

    fun initDefinition()

    fun registerRouter()

    fun injectAutowired(target: Any?)

    fun addInterceptor()

    fun childModule(): List<IRouterModule>

}