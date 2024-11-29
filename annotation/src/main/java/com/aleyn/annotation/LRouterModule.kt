package com.aleyn.annotation

import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2024/11/28 16:18
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
annotation class LRouterModule(
    vararg val modules: KClass<out IRouterModule>,
)
