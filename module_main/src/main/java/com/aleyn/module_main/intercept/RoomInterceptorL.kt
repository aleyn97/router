package com.aleyn.module_main.intercept

import android.util.Log
import com.aleyn.annotation.Interceptor
import com.aleyn.router.core.LRouterInterceptor
import com.aleyn.router.core.Navigator

/**
 * @author: Aleyn
 * @date: 2023/7/19 16:13
 */

@Interceptor(3)
class TestInterceptor : LRouterInterceptor {

    override fun intercept(navigator: Navigator): Navigator {
        if (navigator.path == "/Main/Intercept") {
            return navigator.newBuilder()
                .replaceUrl("/First/Home") // 替换成First
                //.withString() // 替换参数
                .build()
        }
        return navigator
    }
}


@Interceptor(1)
class RoomInterceptor2 : LRouterInterceptor {

    override fun intercept(navigator: Navigator): Navigator {
        if (navigator.path == "/Room/Home") {
            return navigator.newBuilder()
                .replaceUrl("/First/Home")
                .build()
        }
        Log.d("RoomInterceptor2", "intercept: 2")
        return navigator
    }
}
