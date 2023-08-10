package com.aleyn.router.inject.instance

import com.aleyn.router.inject.Core

/**
 * @author: Aleyn
 * @date: 2023/7/27 11:05
 */
abstract class InstanceFactory<T>(val definition: Definition<T>) {


    abstract fun get(context: InstanceData): T

    /**
     * Create an instance
     */
    open fun create(context: InstanceData): T {
        try {
            return definition.def.invoke(Core, context.parameters ?: Parameters())
        } catch (e: Exception) {
            throw RuntimeException("Could not create instance for '$definition'", e)
        }
    }

    abstract fun isCreated(): Boolean
}