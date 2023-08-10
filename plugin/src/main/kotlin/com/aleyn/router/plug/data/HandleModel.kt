package com.aleyn.router.plug.data

/**
 * @author: Aleyn
 * @date: 2023/7/31 11:25
 */

sealed class HandleModel {

    data class Autowired(val className: String) : HandleModel()

    data class Module(val className: String) : HandleModel()

    data class Intercept(
        val priority: Byte = 0,
        val className: String
    ) : HandleModel()

    data class Initializer(
        val priority: Int = 0,
        val async: Int = 0,
        val className: String
    ) : HandleModel()
}

inline fun <reified T> List<HandleModel>.getTarget(): List<T> {
    return this.filterIsInstance<T>()
}