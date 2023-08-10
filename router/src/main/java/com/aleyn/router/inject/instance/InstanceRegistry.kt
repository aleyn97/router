package com.aleyn.router.inject.instance

import com.aleyn.router.inject.qualifier.Qualifier
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/7/27 11:58
 */
@Suppress("UNCHECKED_CAST")
class InstanceRegistry {

    private val _instances = ConcurrentHashMap<String, InstanceFactory<*>>()

    private val eagerInstances = hashMapOf<Int, SingleInstanceFactory<*>>()

    fun addEagerInstances(factory: SingleInstanceFactory<*>) {
        eagerInstances[factory.hashCode()] = factory
    }

    internal fun createAllEagerInstances() {
        createEagerInstances(eagerInstances.values)
        eagerInstances.clear()
    }

    fun saveMapping(
        mapping: String,
        factory: InstanceFactory<*>
    ) {
        if (_instances.containsKey(mapping)) {
            error("A value cannot correspond to multiple types. Please check the following information:\n  ${factory.definition} \n at $mapping")
        }
        _instances[mapping] = factory
    }

    private fun createEagerInstances(eagerInstances: Collection<SingleInstanceFactory<*>>) {
        if (eagerInstances.isNotEmpty()) {
            eagerInstances.forEach { factory ->
                factory.get(InstanceData())
            }
        }
    }

    internal fun <T> resolveInstance(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        instanceContext: InstanceData
    ): T? {
        val mappingKey = mappingKey(clazz, qualifier)
        return _instances[mappingKey]?.get(instanceContext) as? T
    }


    internal fun close() {
        _instances.clear()
    }

    internal fun <T> getAll(clazz: KClass<*>): List<T> {
        return _instances.values
            .filter { factory ->
                factory.definition.primaryType == clazz
            }
            .distinct()
            .map { it.get(InstanceData()) as T }
    }
}