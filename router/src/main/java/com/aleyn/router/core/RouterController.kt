package com.aleyn.router.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.aleyn.annotation.IRouterModule
import com.aleyn.router.LRouter
import com.aleyn.router.data.InitializerData
import com.aleyn.router.data.InterceptorData
import com.aleyn.router.executor.DefaultPoolExecutor
import com.aleyn.router.inject.Core
import com.aleyn.router.inject.ILRouterGenerate
import com.aleyn.router.util.dLog
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

    internal val allModules = HashSet<IRouterModule>()

    internal val ROUTER_MAP = LRouterMap()

    internal val routerInterceptors = TreeSet<InterceptorData>()

    private val initializerModule = TreeSet<InitializerData>()

    internal var navCallback: NavCallback? = null


    internal val routerGenerate: ILRouterGenerate by lazy {
        Class.forName("com.router.LRouterGenerateImpl")
            .newInstance() as ILRouterGenerate
    }

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
        routerGenerate.initModuleRouter()
        // 注册子Module
        registerChildModule()
        //初始化依赖注入，由于初始化器可能会用到 注入类，所以放在最前
        allModules.forEach { it.initDefinition() }
        Core.createEagerInstances()
        // 注册所有初始化器
        allModules.forEach { it.registerInitializer() }
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
            allModules.forEach {
                it.registerRouter()
                it.addInterceptor()
            }
        }
    }

    /**
     * 子Module
     */
    private fun registerChildModule() {
        allModules.flatMap { it.childModule() }.forEach(::registerModule)
    }

    /**
     * 注册模块 Module
     */
    @JvmStatic
    fun registerModule(module: IRouterModule) {
        "registerModule : ${module::class.qualifiedName}".dLog()
        allModules.add(module)
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