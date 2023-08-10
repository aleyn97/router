package com.aleyn.router.util

import android.util.Log

/**
 * @author: Aleyn
 * @date: 2023/7/10 17:09
 */
class DefaultLogger : ILogger {

    override fun debug(tag: String?, message: String?) {
        Log.d(tag, message.orEmpty())
    }

    override fun info(tag: String?, message: String?) {
        Log.i(tag, message.orEmpty())
    }

    override fun warning(tag: String?, message: String?) {
        Log.i(tag, message.orEmpty())
    }

    override fun error(tag: String?, message: String?) {
        Log.e(tag, message.orEmpty())
    }

}