package com.aleyn.router.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.aleyn.router.data.InitializerData
import com.aleyn.router.data.InterceptorData
import com.aleyn.router.executor.DefaultPoolExecutor
import com.aleyn.router.inject.initModuleRouter
import com.aleyn.router.inject.registerAllInitializer
import com.aleyn.router.inject.registerIntercept
import com.google.gson.Gson
import java.util.TreeSet
import java.util.concurrent.ExecutorService

/**
 * @author: Aleyn
 * @date: 2023/7/10 16:12
 */

val routerGson = Gson()

internal object RouterController {

    private var executor: ExecutorService = DefaultPoolExecutor()

    private val main = Handler(Looper.getMainLooper())

    internal val ROUTER_MAP = LRouterMap()

    internal val routerInterceptors = TreeSet<InterceptorData>()

    private val initializerModule = TreeSet<InitializerData>()

    internal var navCallback: NavCallback? = null


    fun setThreadPoolExecutor(e: ExecutorService?) = e?.let {
        executor = it
    }

    internal fun async(runnable: Runnable) {
        try {
            executor.execute(runnable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal fun main(runnable: Runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            runnable.run()
        } else {
            main.post(runnable)
        }
    }


    internal fun init(context: Context) {
        // 注册所有初始化器
        registerAllInitializer()
        // 执行各模块初始化器
        async {
            initializerModule.forEach {
                if (it.async) {
                    it.routerInitializer.create(context)
                } else {
                    main { it.routerInitializer.create(context) }
                }
            }
        }
        //注册 路由 拦截器 信息
        async {
            initModuleRouter()
            registerIntercept()
        }
    }

    /**
     * 注册初始化器
     */
    @JvmStatic
    fun registerInitializer(
        priority: Byte,
        async: Boolean,
        initializer: LRouterInitializer
    ) {
        initializerModule.add(InitializerData(priority, async, initializer))
    }

}