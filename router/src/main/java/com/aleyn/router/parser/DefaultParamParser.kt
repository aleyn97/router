package com.aleyn.router.parser

import android.app.Activity
import com.aleyn.router.LRouter
import com.aleyn.router.core.routerGson
import com.aleyn.router.inject.qualifier.sq

/**
 * @author : Aleyn
 * @date : 2023/07/23 : 23:41
 */
object DefaultParamParser {


    @Suppress("UNCHECKED_CAST")
    fun <T> parseDefault(
        target: Any?,
        key: String,
        type: Class<T>,
    ): T? {
        val obj = getBundleAny(target, key)
        var value = obj as? T
        if (obj is String && type.isPrimitive) {
            value = obj.toPrimitive(type) as? T
        }
        return value
    }


    inline fun <reified T> parseAny(target: Any?, key: String, fieldName: String): T? {
        val currentKey = key.ifBlank { fieldName }
        val json = getBundleAny(target, currentKey) as? String
        if (!json.isNullOrBlank()) {
            return runCatching { routerGson.fromJson(json, T::class.java) }.getOrNull()
        }
        return if (key.isBlank()) {
            LRouter.get(T::class)
        } else {
            LRouter.get(T::class, sq(key))
        }
    }

    @Suppress("DEPRECATION")
    fun getBundleAny(target: Any?, key: String): Any? {
        val bundle = when (target) {
            is Activity -> target.intent?.extras
            is androidx.fragment.app.Fragment -> target.arguments
            else -> null
        }
        return bundle?.get(key)
    }

}

private fun String.toPrimitive(clazz: Class<*>): Any? {
    return try {
        when (clazz.kotlin.qualifiedName) {
            "kotlin.Byte" -> this.toByte()
            "kotlin.Short" -> this.toShort()
            "kotlin.Int" -> this.toInt()
            "kotlin.Long" -> this.toFloat()
            "kotlin.Float" -> this.toFloat()
            "kotlin.Double" -> this.toDouble()
            "kotlin.Boolean" -> this.toBoolean()
            "kotlin.Char" -> this.toCharArray()
            else -> this
        }
    } catch (_: Throwable) {
        null
    }
}