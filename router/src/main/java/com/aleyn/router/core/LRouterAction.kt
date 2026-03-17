package com.aleyn.router.core

import android.content.Context
import android.os.Bundle
import androidx.annotation.Keep

/**
 * @author: Aleyn
 * @date: 2026/03/17 15:11
 * @description: 执行动作
 */
@Keep
interface LRouterAction {

    fun action(context: Context, arguments: Bundle)

}