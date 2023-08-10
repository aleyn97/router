package com.aleyn.router.inject.instance

/**
 * @author : Aleyn
 * @date : 2023/07/27 : 23:58
 */
class FactoryInstanceFactory<T>(definition: Definition<T>) : InstanceFactory<T>(definition) {

    override fun isCreated(): Boolean = false

    override fun get(context: InstanceData) = create(context)
}