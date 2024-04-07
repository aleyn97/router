package com.aleyn.router.core

import android.content.Context
import androidx.annotation.Keep

/**
 * @author : Aleyn
 * @date : 2023/07/29 : 22:56
 */
@Keep
interface LRouterInitializer {
    fun create(context: Context)
}