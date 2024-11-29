package com.lrouter.app

import com.aleyn.annotation.LRouterModule
import com.module.router.module_first__ModuleRouter__Registered
import com.module.router.module_main__ModuleRouter__Registered
import com.module.router.module_two__ModuleRouter__Registered

/**
 * @author: Aleyn
 * @date: 2024/11/28 16:30
 *
 * Module 如果是远程依赖的方式，可以用以下方式 {@LRouterModule} 注解，把远程包生成的模块类添加进去
 */
@LRouterModule(
    module_first__ModuleRouter__Registered::class,
    module_main__ModuleRouter__Registered::class,
    module_two__ModuleRouter__Registered::class,
)
class RouterModule