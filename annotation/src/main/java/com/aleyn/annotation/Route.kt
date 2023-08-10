package com.aleyn.annotation

/**
 * @author: Aleyn
 * @date: 2023/6/13 15:41
 *
 * @Route(path = "/XXX/ZZZ")
 * class xxxActivity: Activity(){
 *      //...
 * }
 *
 * @param path 路由地址，问号后边可跟参数，只能存在一个 '?' 字符
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
annotation class Route(
    /**
     * 路由地址
     */
    val path: String,

    /**
     * 页面说明
     */
    val desc: String = "",

    /**
     * 标识
     */
    val other: Int = 0
)
