package com.aleyn.module_main.provider

import com.aleyn.annotation.Singleton
import com.aleyn.lib_base.provider.IAppProvider

/**
 * @author : Aleyn
 * @date : 2023/07/23 : 23:05
 */

@Singleton
class AppProviderImpl : IAppProvider {

    private var count = 0

    override fun getAppInfo(): String {
        count++
        return "this is APP Provider count: $count"
    }

}