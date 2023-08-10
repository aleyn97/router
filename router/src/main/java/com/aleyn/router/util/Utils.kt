package com.aleyn.router.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import java.lang.reflect.InvocationTargetException

/**
 * @author: Aleyn
 * @date: 2023/6/9 11:35
 */

@SuppressLint("PrivateApi")
internal fun getApplicationByReflect(): Context? {
    try {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val thread = getActivityThread()
        val app = activityThreadClass.getMethod("getApplication").invoke(thread) ?: return null
        return (app as Application).applicationContext
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    return null
}

private fun getActivityThread(): Any? {
    return getActivityThreadInActivityThreadStaticField()
        ?: getActivityThreadInActivityThreadStaticMethod()
}

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
private fun getActivityThreadInActivityThreadStaticField(): Any? {
    return try {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val sCurrentActivityThreadField =
            activityThreadClass.getDeclaredField("sCurrentActivityThread")
        sCurrentActivityThreadField.isAccessible = true
        sCurrentActivityThreadField[null]
    } catch (e: Exception) {
        Log.e(
            "UtilsActivityLifecycle",
            "getActivityThreadInActivityThreadStaticField: " + e.message
        )
        null
    }
}

@SuppressLint("PrivateApi")
private fun getActivityThreadInActivityThreadStaticMethod(): Any? {
    return try {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        activityThreadClass.getMethod("currentActivityThread").invoke(null)
    } catch (e: java.lang.Exception) {
        Log.e(
            "UtilsActivityLifecycle",
            "getActivityThreadInActivityThreadStaticMethod: " + e.message
        )
        null
    }
}