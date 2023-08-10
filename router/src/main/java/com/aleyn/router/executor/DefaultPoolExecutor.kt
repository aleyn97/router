package com.aleyn.router.executor

import android.util.Log
import com.aleyn.router.util.dLog
import com.aleyn.router.util.wLog
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/**
 * @author: Aleyn
 * @date: 2023/6/9 16:58
 */
internal val CPU_COUNT = Runtime.getRuntime().availableProcessors()
internal val CORE_POOL_SIZE = max(3, min(CPU_COUNT - 1, 6))
internal const val KEEP_ALIVE_SECONDS = 30L

internal class DefaultPoolExecutor : ThreadPoolExecutor(
    CORE_POOL_SIZE,
    CORE_POOL_SIZE, KEEP_ALIVE_SECONDS,
    TimeUnit.SECONDS,
    ArrayBlockingQueue(64),
    DefaultThreadFactory()
) {

    override fun afterExecute(r: Runnable?, throwable: Throwable?) {
        super.afterExecute(r, throwable)
        var t = throwable
        if (t == null && r is Future<*>) {
            try {
                (r as Future<*>).get()
            } catch (ce: CancellationException) {
                t = ce
            } catch (ee: ExecutionException) {
                t = ee.cause
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        if (t != null) {
            """
            Running task appeared exception! Thread [${Thread.currentThread().name}], because [${t.message}]
            ${Log.getStackTraceString(t)}
            """.trimIndent().wLog()
        }
    }

}