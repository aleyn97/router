package com.aleyn.router.plug.data

/**
 * @author: Aleyn
 * @date: 2023/7/31 11:25
 */

sealed class HandleModel {

    data class Autowired(val className: String) : HandleModel()

    data class Module(val className: String) : HandleModel()
}