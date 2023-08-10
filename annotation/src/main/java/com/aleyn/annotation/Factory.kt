package com.aleyn.annotation

import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/7/28 11:28
 */


/**
 * 每次创建新的实例
 *
 * @Factory
 * class TestClass(){
 *      //......
 * }
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Factory(val bind: KClass<*> = Unit::class)
