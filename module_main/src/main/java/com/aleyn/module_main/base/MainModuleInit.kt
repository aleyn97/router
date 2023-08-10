package com.aleyn.module_main.base

import android.content.Context
import android.util.Log
import com.aleyn.annotation.Initializer
import com.aleyn.router.LRouter
import com.aleyn.router.core.LRouterInitializer
import com.aleyn.router.core.RouteMeta

/**
 * @author : Aleyn
 * @date : 2023/07/30 : 18:26
 */

@Initializer(priority = 1)
class MainModuleInit : LRouterInitializer {

    override fun create(context: Context) {
        Log.d("MainModelInit", "create: main Init")
        LRouter.registerRoute(
            RouteMeta("/Main/Room", className = "com.aleyn.module_main.ui.RoomActivity")
        )
    }

}