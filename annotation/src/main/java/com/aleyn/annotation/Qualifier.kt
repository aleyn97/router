package com.aleyn.annotation

/**
 * @author: Aleyn
 * @date: 2023/7/28 11:28
 * @desc: 限定符
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class Qualifier(val value: String)
