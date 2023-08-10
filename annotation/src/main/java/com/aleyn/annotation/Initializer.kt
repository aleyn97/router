package com.aleyn.annotation

/**
 * @author : Aleyn
 * @date : 2023/07/29 : 22:35
 */

/**
 * 初始化注解
 * 与 Google 的 [androidx.startup.Initializer] 功能类似。
 * 此注解通过编译期收集所有有此注解的方法，通过 ASM 注册进去，APP启动时递归调用
 *
 * @param async 是否异步
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Initializer(val priority: Byte = 0, val async: Boolean = false)
