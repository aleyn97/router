package com.aleyn.router.core

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.aleyn.router.LRouter
import com.aleyn.router.appContext
import com.aleyn.router.core.RouterController.async
import com.aleyn.router.core.RouterController.main
import com.aleyn.router.core.RouterUrl.Companion.toRouterUrl
import com.aleyn.router.util.dLog
import com.aleyn.router.util.iLog
import java.io.Serializable

/**
 * @author: Aleyn
 * @date: 2023/7/17 12:13
 */
class Navigator private constructor(
    builder: Builder,
) {
    val path: String = builder.path

    val intent: Intent? = builder.intent

    val routerUrl = builder.routerUrl

    val bundle: Bundle = builder.mBundle

    val flags = builder.flags // Flags of route

    val enterAnim = builder.enterAnim

    val exitAnim = builder.exitAnim

    val action: String? = builder.action

    val optionsCompat: ActivityOptionsCompat? = builder.optionsCompat

    val routeMeta = LRouter.getRouteMeta(routerUrl)

    /**
     * ActivityResultLauncher
     */
    private val resultLauncher: ActivityResultLauncher<Intent>? = builder.resultLauncher

    fun newBuilder(): Builder = Builder(this)


    private fun createFragment(navCallback: NavCallback? = null): Fragment? {
        "Navigator to $path".iLog()
        val route = routeMeta
        if (route == null) {
            "$path on found".iLog()
            navCallback?.onLost(this)
            return null
        }
        navCallback?.onFound(this)
        try {
            val targetClass = route.targetClass
            if (targetClass != null && Fragment::class.java.isAssignableFrom(targetClass)) {
                val fragment = targetClass.getConstructor().newInstance() as Fragment
                routerUrl.queryAllParameter()?.also {
                    bundle.putAll(it)
                }
                fragment.arguments = bundle
                "Navigator: create fragment is ${route.className}".dLog()
                navCallback?.onArrival(this)
                return fragment
            } else {
                "Navigator: ${route.className} not Fragment".dLog()
            }
        } catch (e: Exception) {
            "Navigator create fragment ${route.className} error:${e.message}".iLog()
        }
        return null
    }

    /**
     * 开始导航到路由地址
     */
    private fun goNavigator(
        currentContext: Context,
        navCallback: NavCallback? = null
    ) {
        "Navigator to $path".iLog()
        val route = routeMeta
        if (route == null) {
            "$path Lost".iLog()
            navCallback?.onLost(this)
            return
        }
        navCallback?.onFound(this)
        val intent = intent ?: Intent()
        if (!this.action.isNullOrBlank()) {
            intent.action = this.action
        }

        routerUrl.queryAllParameter()?.also {
            bundle.putAll(it)
        }

        intent.putExtras(bundle)
        if (flags != 0) {
            intent.addFlags(flags)
        }
        intent.component = ComponentName(currentContext.packageName, route.className)

        if (currentContext !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (resultLauncher == null) {
            "Navigator startActivity ${route.className}".dLog()
            currentContext.startActivity(intent, optionsCompat?.toBundle())
        } else {
            "Navigator startActivityForResult ${route.className}".dLog()
            resultLauncher.launch(intent, optionsCompat)
        }
        navCallback?.onArrival(this)
        if ((enterAnim != 0 || exitAnim != 0) && currentContext is Activity) {
            currentContext.overridePendingTransition(enterAnim, exitAnim)
        }
    }

    class Builder {
        internal var path: String = ""
        internal var intent: Intent? = null
        internal val routerUrl get() = path.toRouterUrl()
        internal val mBundle = Bundle()
        internal var flags = 0 // Flags of route

        internal var enterAnim = -1
        internal var exitAnim = -1
        internal var action: String? = null

        internal var optionsCompat: ActivityOptionsCompat? = null

        internal var resultLauncher: ActivityResultLauncher<Intent>? = null

        internal constructor(
            path: String,
            intent: Intent?
        ) {
            this.path = path
            this.intent = intent
        }

        internal constructor(navigator: Navigator) {
            this.path = navigator.path
            this.intent = navigator.intent
            this.mBundle.putAll(navigator.bundle)
            this.flags = navigator.flags
            this.enterAnim = navigator.enterAnim
            this.optionsCompat = navigator.optionsCompat
            this.action = navigator.action
            this.resultLauncher = navigator.resultLauncher
        }

        fun replaceUrl(newUrl: String) = apply {
            this.path = newUrl
        }

        fun withIntent(intent: Intent?) = apply {
            this.intent = intent
        }

        fun withInt(key: String?, value: Int) = apply {
            mBundle.putInt(key, value)
        }

        fun withLong(key: String?, value: Long) = apply {
            mBundle.putLong(key, value)
        }

        fun withDouble(key: String?, value: Double) = apply {
            mBundle.putDouble(key, value)
        }

        fun withFloat(key: String?, value: Float) = apply {
            mBundle.putFloat(key, value)
        }

        fun withChar(key: String?, value: Char) = apply {
            mBundle.putChar(key, value)
        }

        fun withByte(key: String?, value: Byte) = apply {
            mBundle.putByte(key, value)
        }

        fun withBoolean(key: String?, value: Boolean) = apply {
            mBundle.putBoolean(key, value)
        }

        fun withString(key: String?, value: String?) = apply {
            mBundle.putString(key, value)
        }

        fun withSerializable(key: String?, value: Serializable?) = apply {
            mBundle.putSerializable(key, value)
        }

        fun withParcelable(key: String?, value: Parcelable?) = apply {
            mBundle.putParcelable(key, value)
        }

        fun flatBundle(bundle: Bundle?) = apply {
            mBundle.putAll(bundle)
        }

        fun withBundle(key: String?, value: Bundle?) = apply {
            mBundle.putBundle(key, value)
        }

        fun withAny(key: String, any: Any?) = apply {
            mBundle.putString(key, routerGson.toJson(any))
        }

        fun addFlags(flags: Int) = apply {
            this.flags = this.flags or flags
        }

        fun withFlags(flags: Int) = apply {
            this.flags = flags
        }

        fun withAction(action: String?) = apply {
            this.action = action
        }

        fun withTransition(enterAnim: Int, exitAnim: Int) = apply {
            this.enterAnim = enterAnim
            this.exitAnim = exitAnim
        }

        fun withOptionsCompat(compat: ActivityOptionsCompat) = apply {
            this.optionsCompat = compat
        }

        fun withActivityLaunch(resultLauncher: ActivityResultLauncher<Intent>) = apply {
            this.resultLauncher = resultLauncher
        }

        fun build() = Navigator(this)

        fun navigation(context: Context? = null, callback: NavCallback? = null) {
            val currentContext = context ?: appContext
            val navCallback = callback ?: RouterController.navCallback
            async {
                var navigator: Navigator = build()
                for (item in RouterController.routerInterceptors) {
                    val tempNavigator = item.interceptor.intercept(navigator)
                    if (tempNavigator == null) {
                        main { navCallback?.onInterrupt(navigator) }
                        return@async
                    }
                    navigator = tempNavigator
                }
                main {
                    navigator.goNavigator(currentContext!!, navCallback)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Fragment> asFragment(navCallback: NavCallback? = null): T? {
            return getFragment(navCallback) as? T
        }

        fun getFragment(navCallback: NavCallback? = null): Fragment? {
            var navigator: Navigator = build()
            for (item in RouterController.routerInterceptors) {
                navigator = item.interceptor.intercept(navigator) ?: let {
                    navCallback?.onInterrupt(navigator)
                    return null
                }
            }
            return navigator.createFragment(navCallback)
        }
    }

    companion object {

        @JvmStatic
        internal fun navBuilder(url: String?, intent: Intent?) = Builder(url.orEmpty(), intent)

    }
}