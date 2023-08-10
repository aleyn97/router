package com.aleyn.router.util

/**
 * @author: Aleyn
 * @date: 2023/7/10 17:06
 */
interface ILogger {

    fun debug(tag: String?, message: String?)

    fun info(tag: String?, message: String?)

    fun warning(tag: String?, message: String?)

    fun error(tag: String?, message: String?)

}