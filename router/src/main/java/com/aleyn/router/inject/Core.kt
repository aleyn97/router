package com.aleyn.router.inject

import com.aleyn.router.inject.instance.InstanceData
import com.aleyn.router.inject.instance.InstanceFactory
import com.aleyn.router.inject.instance.InstanceRegistry
import com.aleyn.router.inject.instance.Parameters
import com.aleyn.router.inject.instance.mappingKey
import com.aleyn.router.inject.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/7/27 11:50
 */
object Core {

    val instanceRegistry = InstanceRegistry()

    fun saveFactory(instanceFactory: InstanceFactory<*>) {
        val definition = instanceFactory.definition
        val mapping = mappingKey(definition.primaryType, definition.qualifier)

        instanceRegistry.saveMapping(mapping, instanceFactory)
    }

    inline fun <reified T : Any> get(
        qualifier: Qualifier? = null,
        parameters: Parameters? = null
    ): T {
        return get(T::class, qualifier, parameters)
    }


    fun <T> get(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameterDef: Parameters? = null
    ): T {
        val instanceContext = InstanceData(parameterDef)
        return resolveValue(qualifier, clazz, instanceContext)
    }

    inline fun <reified T : Any> getOrNull(
        qualifier: Qualifier? = null,
        parameters: Parameters? = null
    ): T? {
        return getOrNull(T::class, qualifier, parameters)
    }

    fun <T> getOrNull(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: Parameters? = null
    ): T? {
        return try {
            get(clazz, qualifier, parameters)
        } catch (e: Exception) {
            null
        }
    }


    inline fun <reified T : Any> getAll(): List<T> = getAll(T::class)


    fun <T> getAll(clazz: KClass<*>): List<T> {
        return instanceRegistry.getAll(clazz)
    }


    /**
     * Close all resources from context
     */
    fun close() {
        instanceRegistry.close()
    }

    /**
     * 应用启动时创建单例
     */
    fun createEagerInstances() {
        instanceRegistry.createAllEagerInstances()
    }


    private fun <T> resolveValue(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        instanceContext: InstanceData,
    ): T = instanceRegistry.resolveInstance(qualifier, clazz, instanceContext)
        ?: error("${clazz.qualifiedName} not found ")

}