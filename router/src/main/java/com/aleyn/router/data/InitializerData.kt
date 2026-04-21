package com.aleyn.router.data

import com.aleyn.router.core.LRouterInitializer
import java.util.concurrent.atomic.AtomicLong

/**
 * @author : Aleyn
 * @date : 2023/07/29 : 23:11
 */
internal class InitializerData(
    private val priority: Byte,
    val async: Boolean,
    val routerInitializer: LRouterInitializer
) : Comparable<InitializerData> {
    private val registerOrder = nextOrder.getAndIncrement()

    override fun compareTo(other: InitializerData): Int {
        if (other.routerInitializer == this.routerInitializer) return 0

        val priorityCompare = other.priority.compareTo(priority)
        if (priorityCompare != 0) return priorityCompare

        return registerOrder.compareTo(other.registerOrder)
    }

    override fun toString(): String {
        return """
            priority=${priority},async=${async}
        """.trimIndent()
    }

    private companion object {
        val nextOrder = AtomicLong(0)
    }
}
