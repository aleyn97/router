package com.aleyn.router.core

import com.aleyn.router.util.wLog
import java.util.concurrent.ConcurrentHashMap

/**
 * @author: Aleyn
 * @date: 2023/7/17 16:15
 */
class LRouterMap : ConcurrentHashMap<String, RouteMeta?>() {

    override fun put(key: String, value: RouteMeta?): RouteMeta? {
        if (containsKey(key)) {
            "LRouter: A Key cannot correspond to multiple pages, which will overwrite the existing pages".wLog()
        }
        return super.put(key, value)
    }

}