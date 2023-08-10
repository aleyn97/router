package com.aleyn.router.util

import com.aleyn.router.TAG

/**
 * @author: Aleyn
 * @date: 2023/6/9 17:36
 */
internal var currentLogger: ILogger = DefaultLogger()

internal var openLog = true

internal fun String.dLog() {
    if (openLog) currentLogger.debug(TAG, this)
}

internal fun String.iLog() {
    if (openLog) currentLogger.info(TAG, this)
}

internal fun String.wLog() {
    if (openLog) currentLogger.warning(TAG, this)
}

internal fun String.eLog() {
    if (openLog) currentLogger.error(TAG, this)
}

