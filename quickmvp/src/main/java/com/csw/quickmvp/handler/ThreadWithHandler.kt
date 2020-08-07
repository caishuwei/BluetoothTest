@file:Suppress(
    "MemberVisibilityCanBePrivate", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING",
    "EqualsOrHashCode", "unused"
)

package com.csw.quickmvp.handler

import android.os.Handler
import android.os.HandlerThread
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 带Handler的子线程，必须等线程启动之后，才会初始化Handler,所以
 */
class ThreadWithHandler(
    name: String,
    priority: Int = Thread.NORM_PRIORITY,
    private val callback: Handler.Callback? = null
) :
    HandlerThread(name, priority) {
    val handlerProxy = HandlerProxy()

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        handlerProxy.handler = Handler(looper, callback)
        handlerProxy.executeActions()
    }

    override fun quit(): Boolean {
        handlerProxy.clearActions()
        return super.quit()
    }

    override fun quitSafely(): Boolean {
        //安全退出会把所有任务执行完再退出，所以不用做处理
        return super.quitSafely()
    }

    class HandlerProxy {
        //存储Handler未初始化前post的任务，模拟没有attach时view.post()
        private val actions = CopyOnWriteArrayList<HandlerAction>()
        var handler: Handler? = null

        fun executeActions() {
            handler?.run {
                val iterator = actions.iterator()
                while (iterator.hasNext()) {
                    iterator.next().let { action ->
                        postDelayed(action.r, action.delay)
                    }
                }
                clearActions()
            }
        }

        fun clearActions() {
            actions.clear()
        }

        fun post(r: () -> Unit) {
            postDelay(0, r)
        }

        fun postDelay(delayMillis: Long, r: () -> Unit) {
            postDelay(Runnable { r.invoke() }, delayMillis)
        }

        fun post(r: Runnable) {
            postDelay(r, 0)
        }

        fun postDelay(r: Runnable, delayMillis: Long) {
            handler?.run {
                postDelayed(r, delayMillis)
                return
            }
            actions.add(HandlerAction(r, delayMillis))
        }

        fun removeCallbacks(r: Runnable) {
            if (!removeAction(r)) {
                handler?.removeCallbacks(r)
            }
        }

        private fun removeAction(r: Runnable): Boolean {
            if (actions.isNotEmpty()) {
                for (action in actions) {
                    if (action.matchRunnable(r)) {
                        actions.remove(action)
                        return true
                    }
                }
            }
            return false
        }
    }

    class HandlerAction(val r: Runnable, val delay: Long) {
        fun matchRunnable(runnable: Runnable): Boolean {
            return r == runnable
        }
    }
}