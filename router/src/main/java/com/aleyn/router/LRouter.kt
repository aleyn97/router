package com.aleyn.router

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.aleyn.router.core.LRouterInitializer
import com.aleyn.router.core.LRouterInterceptor
import com.aleyn.router.core.NavCallback
import com.aleyn.router.core.Navigator
import com.aleyn.router.core.RouteMeta
import com.aleyn.router.core.RouterController
import com.aleyn.router.core.RouterUrl
import com.aleyn.router.core.getRouterKey
import com.aleyn.router.data.InterceptorData
import com.aleyn.router.inject.Core
import com.aleyn.router.inject.qualifier.Qualifier
import com.aleyn.router.util.ILogger
import com.aleyn.router.util.currentLogger
import com.aleyn.router.util.dLog
import com.aleyn.router.util.getApplicationByReflect
import com.aleyn.router.util.openLog
import java.util.concurrent.ThreadPoolExecutor
import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/6/9 11:02
 */

private var applicationContext: Context? = null

val appContext
    get() = applicationContext ?: getApplicationByReflect().also {
        applicationContext = it
    }

@Keep
object LRouter {

    var enabledAutoInit = true

    /**
     * 初始化标识
     */
    private var isInit = false

    /**
     * 日志开关
     */
    @JvmStatic
    fun setLogSwitch(status: Boolean) {
        openLog = status
    }

    /**
     * 设置线程池
     */
    @JvmStatic
    fun setExecutor(tpe: ThreadPoolExecutor?) {
        RouterController.setThreadPoolExecutor(tpe)
    }


    /**
     * 设置 Log
     */
    @JvmStatic
    fun setLogger(logger: ILogger) {
        currentLogger = logger
    }


    /**
     * 添加路由
     */
    @JvmStatic
    fun registerRoute(routeMeta: RouteMeta) {
        val key = routeMeta.path.getRouterKey()
        "registerRoute : $key".dLog()
        RouterController.ROUTER_MAP[key] = routeMeta
    }

    /**
     * 获取路由 Meta
     */
    @JvmStatic
    fun getRouteMeta(routerUrl: RouterUrl?): RouteMeta? {
        val path = routerUrl?.routerKey.orEmpty()
        return RouterController.ROUTER_MAP[path]
    }

    /**
     * 添加拦截器
     */
    @JvmStatic
    fun addInterceptor(priority: Byte, interceptor: LRouterInterceptor) {
        RouterController.routerInterceptors.add(InterceptorData(priority, interceptor))
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
        RouterController.registerInitializer(priority, async, initializer)
    }

    /**
     * 全局跳转回调处理
     * 某个单独的导航 设置了回调,全局的回调不会执行
     * 优先级低于单独设置
     */
    @JvmStatic
    fun setNavCallback(navCallback: NavCallback) {
        RouterController.navCallback = navCallback
    }

    /**
     * 初始化
     */
    @JvmStatic
    fun init(context: Context?) {
        applicationContext = context?.applicationContext
        if (isInit) return
        "init: start".dLog()
        RouterController.init(applicationContext!!)
        isInit = true
    }

    /**
     * 构建导航器
     */
    @JvmStatic
    fun build(url: String?, intent: Intent? = null) = Navigator.navBuilder(url, intent)

    /**
     * 为 @Autowired 注解的变量赋值
     */
    @JvmStatic
    fun inject(target: Any?) {
        RouterController.modules.forEach {
            it.injectAutowired(target)
        }
    }

    /**
     * 获取注入类
     */
    @JvmStatic
    fun <T> get(clazz: KClass<*>, qualifier: Qualifier? = null): T? {
        return Core.getOrNull(clazz, qualifier)
    }

    inline fun <reified T> getByType(qualifier: Qualifier? = null): T? {
        return Core.getOrNull(T::class, qualifier)
    }

    /**
     * 获取注入类
     */
    @JvmStatic
    fun <T> getByJava(clazz: Class<*>, qualifier: Qualifier?): T? {
        return Core.getOrNull(clazz.kotlin, qualifier)
    }

    @JvmStatic
    fun <T> getByJava(clazz: Class<*>): T? {
        return Core.getOrNull(clazz.kotlin, null)
    }

}