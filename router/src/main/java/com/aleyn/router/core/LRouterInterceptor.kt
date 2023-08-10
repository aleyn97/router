package com.aleyn.router.core

import androidx.annotation.Keep

/**
 * @author: Aleyn
 * @date: 2023/7/17 12:16
 * 路由拦截器
 */
@Keep
interface LRouterInterceptor {
    fun intercept(navigator: Navigator): Navigator?
}