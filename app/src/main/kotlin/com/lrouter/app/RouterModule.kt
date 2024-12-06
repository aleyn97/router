package com.lrouter.app

import com.aleyn.annotation.LRouterModule
import com.module.router.module_first__ModuleRouter__Registered
import com.module.router.module_main__ModuleRouter__Registered
import com.module.router.module_two__ModuleRouter__Registered

/**
 * @author: Aleyn
 * @date: 2024/11/28 16:30
 *
 * Module 添加子 Module (1.0.8 之后基本用不到)
 */
@LRouterModule(
    module_first__ModuleRouter__Registered::class,
    module_main__ModuleRouter__Registered::class,
    module_two__ModuleRouter__Registered::class,
)
class RouterModule