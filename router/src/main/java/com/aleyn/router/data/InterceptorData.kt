package com.aleyn.router.data

import com.aleyn.router.core.LRouterInterceptor
import java.util.concurrent.atomic.AtomicLong

/**
 * @author : Aleyn
 * @date : 2023/07/30 : 19:16
 */
internal class InterceptorData(
    private val priority: Byte,
    val interceptor: LRouterInterceptor
) : Comparable<InterceptorData> {
    private val registerOrder = nextOrder.getAndIncrement()

    override fun compareTo(other: InterceptorData): Int {
        if (other.interceptor == this.interceptor) return 0

        val priorityCompare = other.priority.compareTo(priority)
        if (priorityCompare != 0) return priorityCompare

        return registerOrder.compareTo(other.registerOrder)
    }

    private companion object {
        val nextOrder = AtomicLong(0)
    }
}
