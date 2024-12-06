package com.aleyn.router.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.aleyn.annotation.IRouterModule
import com.aleyn.router.data.InitializerData
import com.aleyn.router.data.InterceptorData
import com.aleyn.router.executor.DefaultPoolExecutor
import com.aleyn.router.inject.Core
import com.aleyn.router.inject.initModuleRouter
import com.aleyn.router.util.dLog
import com.aleyn.router.util.iLog
import com.google.gson.Gson
import java.util.ServiceLoader
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

    private val allModules = HashMap<String, IRouterModule>()
    internal val modules get() = allModules.values

    internal val ROUTER_MAP = LRouterMap()

    internal val routerInterceptors = TreeSet<InterceptorData>()

    private val initializerModule = TreeSet<InitializerData>()

    internal var navCallback: NavCallback? = null

    /**
     * 自动注入
     */
    private var autoInject = false

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
        initModuleRouter()
        if (autoInject) {
            "LRouter use ASM init".iLog()
        } else {
            val serviceLoader = ServiceLoader.load(IRouterModule::class.java)
            val iterator = serviceLoader.iterator()
            val startTime = System.currentTimeMillis()
            try {
                while (iterator.hasNext()) {
                    val module = iterator.next()
                    registerModule(module)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            val endTime = System.currentTimeMillis()
            "LRouter use ServiceLoader init -----> time: ${endTime - startTime}ms".iLog()
        }

        // 注册子Module
        registerChildModule()
        //初始化依赖注入，由于初始化器可能会用到 注入类，所以放在最前
        modules.forEach { it.initDefinition() }
        Core.createEagerInstances()
        // 注册所有初始化器
        modules.forEach { it.registerInitializer() }
        // 执行各模块初始化器
        initializerModule.forEach {
            if (it.async) {
                async { it.routerInitializer.create(context) }
            } else {
                it.routerInitializer.create(context)
            }
        }
        initializerModule.clear()
        //注册 路由 拦截器
        async {
            modules.forEach {
                it.registerRouter()
                it.addInterceptor()
            }
        }
    }

    /**
     * 子Module
     */
    private fun registerChildModule() {
        modules.flatMap { it.childModule() }.forEach(::registerModule)
    }

    /**
     * 注册模块 Module
     */
    @JvmStatic
    fun registerModule(module: IRouterModule) {
        module::class.qualifiedName?.let {
            "registerModule : $it".dLog()
            allModules[it] = module
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


    /**
     * 注册初始化器
     */
    @JvmStatic
    fun autoInject() {
        autoInject = true
    }

}