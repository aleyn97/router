package com.aleyn.router.executor

import com.aleyn.router.util.dLog
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author: Aleyn
 * @date: 2023/7/10 16:35
 */
internal class DefaultThreadFactory : ThreadFactory {

    private val poolNumber = AtomicInteger(1)

    private val threadNumber = AtomicInteger(1)

    private val namePrefix: String =
        "LRouter task pool No.${poolNumber.getAndIncrement()}, thread No."

    private val group: ThreadGroup?

    private val exceptionHandler = Thread.UncaughtExceptionHandler { t, ex ->
        "Running task appeared exception! Thread [" + t.name + "], because [" + ex.message + "]".dLog()
    }

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
    }

    override fun newThread(runnable: Runnable): Thread {
        val threadName = namePrefix + threadNumber.getAndIncrement()
        "Thread production, name is [$threadName]".dLog()
        return Thread(group, runnable, threadName, 0).apply {
            if (isDaemon) isDaemon = false
            if (priority != Thread.NORM_PRIORITY) priority = Thread.NORM_PRIORITY
            uncaughtExceptionHandler = exceptionHandler
        }
    }

}