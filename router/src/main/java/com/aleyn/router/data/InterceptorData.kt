package com.aleyn.router.data

import com.aleyn.router.core.LRouterInterceptor

/**
 * @author : Aleyn
 * @date : 2023/07/30 : 19:16
 */
internal class InterceptorData(
    private val priority: Byte,
    val interceptor: LRouterInterceptor
) : Comparable<InterceptorData> {
    override fun compareTo(other: InterceptorData): Int {
        return when {
            other.interceptor == this.interceptor -> 0
            other.priority >= this.priority -> 1
            else -> -1
        }
    }
}