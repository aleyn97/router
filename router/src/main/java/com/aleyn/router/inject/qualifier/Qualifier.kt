package com.aleyn.router.inject.qualifier

/**
 * @author: Aleyn
 * @date: 2023/7/27 11:22
 */

interface Qualifier {
    val value: String
}

data class StringQualifier(override val value: String) : Qualifier {
    override fun toString() = value
}

fun sq(value: String) = StringQualifier(value)