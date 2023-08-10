package com.aleyn.router.util

import com.aleyn.router.LRouter
import com.aleyn.router.core.NavCallback
import com.aleyn.router.core.Navigator

/**
 * @author: Aleyn
 * @date: 2023/7/17 16:05
 */


/**
 * 简易导航跳转
 */
fun LRouter.navigator(url: String) {
    build(url).navigation()
}

fun LRouter.getFragment(url: String) = build(url).getFragment()

fun Navigator.Builder.navCallback(
    onInterrupt: (navigator: Navigator) -> Unit = {},
    onLost: (navigator: Navigator) -> Unit = {},
    onFound: (navigator: Navigator) -> Unit = {},
    onArrival: (navigator: Navigator) -> Unit = {},
) {
    navigation(null, navBack(onInterrupt, onLost, onFound, onArrival))
}

fun Navigator.Builder.navInterrupt(onInterrupt: (navigator: Navigator) -> Unit) {
    navigation(null, navBack(onInterrupt))
}

fun Navigator.Builder.navLost(onLost: (navigator: Navigator) -> Unit) {
    navigation(null, navBack(onLost = onLost))
}

fun Navigator.Builder.navFound(onFound: (navigator: Navigator) -> Unit) {
    navigation(null, navBack(onFound = onFound))
}

fun Navigator.Builder.navArrival(onArrival: (navigator: Navigator) -> Unit) {
    navigation(null, navBack(onArrival = onArrival))
}

private fun navBack(
    onInterrupt: (navigator: Navigator) -> Unit = {},
    onLost: (navigator: Navigator) -> Unit = {},
    onFound: (navigator: Navigator) -> Unit = {},
    onArrival: (navigator: Navigator) -> Unit = {},
): NavCallback = object : NavCallback {
    override fun onLost(navigator: Navigator) {
        onLost(navigator)
    }

    override fun onFound(navigator: Navigator) {
        onFound(navigator)
    }

    override fun onArrival(navigator: Navigator) {
        onArrival(navigator)
    }

    override fun onInterrupt(navigator: Navigator) {
        onInterrupt(navigator)
    }
}