package com.aleyn.router.core

/**
 * @author: Aleyn
 * @date: 2023/7/17 12:16
 * @desc:导航后的回调
 */
interface NavCallback {

    /**
     * 没有找到对应路由地址
     */
    fun onLost(navigator: Navigator) {}

    /**
     * 找到路由地址
     */
    fun onFound(navigator: Navigator) {}

    /**
     * 抵达到目标地址
     */
    fun onArrival(navigator: Navigator) {}

    /**
     * 导航中断 (由拦截器中断)
     */
    fun onInterrupt(navigator: Navigator) {}

}