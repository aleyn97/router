package com.aleyn.annotation

/**
 * @author: Aleyn
 * @date: 2023/6/13 15:51
 *
 * @Autowired
 * var nickname: String = ""
 *
 * @param name Key值，默认使用字段名称
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class Autowired(val name: String = "")
