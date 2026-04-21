package com.aleyn.router.data

import com.aleyn.router.core.LRouterInitializer
import com.aleyn.router.core.LRouterInterceptor
import com.aleyn.router.core.Navigator
import org.junit.Assert.assertEquals
import org.junit.Test

class RouterPriorityOrderTest {

    @Test
    fun interceptor_data_sorts_by_priority_desc_then_registration_order() {
        val highPriority = InterceptorData(10, NamedInterceptor("high"))
        val firstSamePriority = InterceptorData(1, NamedInterceptor("first"))
        val secondSamePriority = InterceptorData(1, NamedInterceptor("second"))

        val ordered = sortedSetOf(highPriority, firstSamePriority, secondSamePriority).toList()

        assertEquals(listOf(highPriority, firstSamePriority, secondSamePriority), ordered)
    }

    @Test
    fun initializer_data_sorts_by_priority_desc_then_registration_order() {
        val highPriority = InitializerData(10, false, NamedInitializer("high"))
        val firstSamePriority = InitializerData(1, false, NamedInitializer("first"))
        val secondSamePriority = InitializerData(1, true, NamedInitializer("second"))

        val ordered = sortedSetOf(highPriority, firstSamePriority, secondSamePriority).toList()

        assertEquals(listOf(highPriority, firstSamePriority, secondSamePriority), ordered)
    }

    private class NamedInterceptor(private val name: String) : LRouterInterceptor {
        override fun intercept(navigator: Navigator): Navigator? = navigator

        override fun toString(): String = name
    }

    private class NamedInitializer(private val name: String) : LRouterInitializer {
        override fun create(context: android.content.Context) = Unit

        override fun toString(): String = name
    }
}
