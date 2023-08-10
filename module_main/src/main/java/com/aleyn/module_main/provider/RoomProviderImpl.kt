package com.aleyn.module_main.provider

import com.aleyn.annotation.Factory
import com.aleyn.annotation.InParam
import com.aleyn.annotation.Qualifier
import com.aleyn.annotation.Singleton
import com.aleyn.lib_base.provider.IMainProvider

/**
 * @author : Aleyn
 * @date : 2023/07/23 : 23:05
 */

@Factory
class MainProviderImpl : IMainProvider {

    override fun getName(): String {
        return "this is Main Provider"
    }
}

@Qualifier(value = "main1")
@Factory
class MainProviderImpl1 : IMainProvider {

    override fun getName(): String {
        return "this is Main Provider 1"
    }
}

@Qualifier("main2")
@Factory
class MainProviderImpl2(@Qualifier("car") val car: Car) : IMainProvider {

    override fun getName(): String {
        return "this is Main Provider2:${car.name}"
    }
}

@Qualifier("main3")
@Factory
class MainProviderImpl3(@InParam val param: String) : IMainProvider {

    override fun getName(): String {
        return "this is Main Provider2:${param}"
    }
}

abstract class ICar {
    open val name = "ICar"
}

@Qualifier("car2")
@Singleton(lazy = true)
class Car2 : ICar() {
    override val name = "Car2"
}

@Qualifier("car")
@Singleton(bind = Car::class)
class Car : ICar() {
    override val name: String = "Car"
}