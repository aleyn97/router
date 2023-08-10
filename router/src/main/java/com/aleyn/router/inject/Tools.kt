package com.aleyn.router.inject

import android.content.ComponentCallbacks
import com.aleyn.router.inject.instance.Def
import com.aleyn.router.inject.instance.FactoryInstanceFactory
import com.aleyn.router.inject.instance.Kind
import com.aleyn.router.inject.instance.Parameters
import com.aleyn.router.inject.instance.SingleInstanceFactory
import com.aleyn.router.inject.instance.createDefinition
import com.aleyn.router.inject.qualifier.Qualifier
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * @author : Aleyn
 * @date : 2023/07/26 : 22:56
 */
private val classNames: MutableMap<KClass<*>, String> = ConcurrentHashMap()

fun KClass<*>.getFullName(): String {
    return classNames[this] ?: saveCache()
}

private fun KClass<*>.saveCache(): String {
    val name = this.java.name
    classNames[this] = name
    return name
}

/**
 * 参数生成
 */
fun paramOf(vararg params: Any?) = Parameters(params.toMutableList())

/**
 * 添加 T 类型单例工厂
 */
inline fun <reified T> lRouterSingle(
    qualifier: Qualifier? = null,
    lazy: Boolean = false,
    noinline definition: Def<T>
) {
    val def = createDefinition(Kind.SINGLETON, qualifier, definition)
    val factory = SingleInstanceFactory(def)
    Core.saveFactory(factory)
    if (!lazy) {
        Core.instanceRegistry.addEagerInstances(factory)
    }
}

/**
 * 添加 T 类型生成工厂
 */
inline fun <reified T> lRouterFactory(
    qualifier: Qualifier? = null,
    noinline definition: Def<T>
) {
    val def = createDefinition(Kind.FACTORY, qualifier, definition)
    val factory = FactoryInstanceFactory(def)
    Core.saveFactory(factory)
}


/**
 * Lazy 懒加载获取
 *
 * @param qualifier
 * @param parameters
 */
inline fun <reified T : Any> ComponentCallbacks.inject(
    qualifier: Qualifier? = null,
    parameters: Parameters? = null,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
) = lazy(mode) { get<T>(qualifier, parameters) }

/**
 * get 直接获取
 * @param qualifier
 * @param parameters
 */
inline fun <reified T : Any> ComponentCallbacks.get(
    qualifier: Qualifier? = null,
    parameters: Parameters? = null,
): T {
    return Core.get(qualifier, parameters)
}
