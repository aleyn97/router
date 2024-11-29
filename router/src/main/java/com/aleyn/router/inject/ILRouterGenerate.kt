package com.aleyn.router.inject

import androidx.annotation.Keep
import com.aleyn.router.LRouter
import com.aleyn.annotation.IRouterModule

/**
 * @author: Aleyn
 * @date: 2024/4/1 14:49
 */
@Keep
interface ILRouterGenerate {
    fun initModuleRouter()
}