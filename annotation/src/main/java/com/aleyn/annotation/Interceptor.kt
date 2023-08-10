package com.aleyn.annotation

/**
 * @author : Aleyn
 * @date : 2023/07/23 : 14:32
 * @desc: 路由拦截器
 *
 *
 * @Interceptor
 * class xxxInterceptor : RouterInterceptor {
 *      //....
 * }
 * @param priority 优先级，从大到小执行
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Interceptor(val priority: Byte = 0)
