package com.aleyn.router.data

import com.aleyn.router.core.LRouterInitializer

/**
 * @author : Aleyn
 * @date : 2023/07/29 : 23:11
 */
internal class InitializerData(
    private val priority: Byte,
    val async: Boolean,
    val routerInitializer: LRouterInitializer
) : Comparable<InitializerData> {

    override fun compareTo(other: InitializerData): Int {
        return when {
            other.routerInitializer == this.routerInitializer -> 0
            other.priority >= this.priority -> 1
            else -> -1
        }
    }
}