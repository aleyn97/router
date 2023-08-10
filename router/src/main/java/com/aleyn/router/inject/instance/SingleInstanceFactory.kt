package com.aleyn.router.inject.instance

/**
 * @author: Aleyn
 * @date: 2023/7/27 12:01
 */
class SingleInstanceFactory<T>(definition: Definition<T>) : InstanceFactory<T>(definition) {

    private var value: T? = null

    private fun getValue(): T = value ?: error("Single instance created couldn't return value")

    override fun isCreated() = value != null

    override fun create(context: InstanceData): T {
        return if (value == null) {
            super.create(context)
        } else getValue()
    }

    override fun get(context: InstanceData): T {
        return value ?: synchronized(this) {
            if (!isCreated()) {
                value = create(context)
            }
            return@synchronized getValue()
        }
    }
}