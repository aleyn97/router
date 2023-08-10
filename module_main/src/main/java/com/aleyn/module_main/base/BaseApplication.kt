package com.aleyn.module_main.base

import android.app.Application
import android.util.Log
import com.aleyn.router.LRouter
import com.aleyn.router.core.NavCallback
import com.aleyn.router.core.Navigator

/**
 * @author : Aleyn
 * @date : 2023/07/28 : 0:23
 */
class BaseApplication : Application() {

    companion object {
        private const val TAG = "BaseApplication"
    }

    override fun onCreate() {
        super.onCreate()

        LRouter.setNavCallback(object : NavCallback {
            override fun onLost(navigator: Navigator) {
                Log.d(TAG, "onLost: ${navigator.path}")
            }

            override fun onFound(navigator: Navigator) {
                Log.d(TAG, "onFound: ${navigator.path}")
            }

            override fun onArrival(navigator: Navigator) {
                Log.d(TAG, "onArrival: ${navigator.path}")
            }

            override fun onInterrupt(navigator: Navigator) {
                Log.d(TAG, "onInterrupt: ${navigator.path}")
            }
        })
    }
}