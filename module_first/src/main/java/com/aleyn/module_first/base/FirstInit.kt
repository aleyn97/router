package com.aleyn.module_first.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.aleyn.annotation.Initializer
import com.aleyn.router.core.LRouterInitializer

/**
 * @author : Aleyn
 * @date : 2023/07/29 : 22:53
 */
@Initializer(5)
class FirstInit : LRouterInitializer {

    override fun create(context: Context) {
        Log.d("FirstInit", "First Module Init ${context is Application}")
    }

}