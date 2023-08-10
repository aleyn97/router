package com.aleyn.annotation

import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/7/28 11:28
 *
 * 单例 全局只有一个实例
 *
 * @Singleton
 * class TestClass(){
 *      //......
 * }
 *
 * @param bind: 声明绑定类型，默认绑定父类
 * @param lazy: 第一次使用到再创建
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Singleton(
    val bind: KClass<*> = Unit::class,
    val lazy: Boolean = false
)

