package com.aleyn.router.core

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * @author: Aleyn
 * @date: 2023/6/9 17:48
 */

@Keep
@Parcelize
data class RouteMeta(
    val path: String = "",
    val description: String = "",
    val other: Int = 0,
    val className: String = ""
) : Parcelable {

    @IgnoredOnParcel
    var targetClass: Class<*>? = null
        get() {
            if (field == null) {
                field = Class.forName(className)
            }
            return field
        }
}