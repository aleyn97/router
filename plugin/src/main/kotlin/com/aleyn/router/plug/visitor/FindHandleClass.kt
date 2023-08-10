package com.aleyn.router.plug.visitor

import com.aleyn.router.plug.data.HandleModel
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * @author : Aleyn
 * @date : 2023/07/30 : 22:33
 */

/**
 * 页面参数生成类后缀
 */
internal const val AUTOWIRED_CLASS_SUFFIX = "__LRouter\$\$Autowired"

/**
 * 每个 module 要初始化的路由类后缀
 */
internal const val MODULE_ROUTER_CLASS_SUFFIX = "__ModuleRouter__Registered"

class FindHandleClass(private val outModel: ArrayList<HandleModel>) : ClassNode(Opcodes.ASM9) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitEnd() {
        if (name.endsWith(AUTOWIRED_CLASS_SUFFIX)) {
            outModel.add(HandleModel.Autowired(name))
            return
        }
        if (name.endsWith(MODULE_ROUTER_CLASS_SUFFIX)) {
            outModel.add(HandleModel.Module(name))
            return
        }
        val interceptorAnn =
            invisibleAnnotations?.firstOrNull { it.desc == "Lcom/aleyn/annotation/Interceptor;" }

        val routerIntercept =
            interfaces?.firstOrNull { it == "com/aleyn/router/core/LRouterInterceptor" }

        if (!interceptorAnn?.desc.isNullOrBlank() && !routerIntercept.isNullOrBlank()) {
            val priority = (interceptorAnn?.values?.getOrNull(1) as? Byte) ?: 0
            outModel.add(HandleModel.Intercept(priority, name))
            return
        }

        val initAnn = invisibleAnnotations?.firstOrNull {
            it.desc == "Lcom/aleyn/annotation/Initializer;"
        }
        val routerInitializer = interfaces?.firstOrNull {
            it == "com/aleyn/router/core/LRouterInitializer"
        }

        if (!initAnn?.desc.isNullOrBlank() && !routerInitializer.isNullOrBlank()) {
            var priority = 0
            var async = false
            initAnn!!.values?.forEachIndexed { index, any ->
                if (index % 2 == 1) return@forEachIndexed
                if (any == "priority") {
                    priority = (initAnn.values.getOrNull(index + 1) as? Int) ?: 0
                }
                if (any == "async") {
                    async = (initAnn.values.getOrNull(index + 1) as? Boolean) ?: false
                }
            }
            outModel.add(HandleModel.Initializer(priority, if (async) 1 else 0, name))
        }
    }

}