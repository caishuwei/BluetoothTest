@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp.utils

import android.util.Log
import com.csw.quickmvp.BuildConfig
import com.csw.quickmvp.SDK
import com.csw.quickmvp.log.LogInfo

/**
 * 日志输出工具类，只在调试安装下才输出日志
 */
class LogUtils {
    companion object {
        /**
         * 控制哪些日志可以输出，不允许输出的可以注释掉
         */
        private val ENABLE_LOG = arrayOf(
            Log.VERBOSE,
            Log.DEBUG,//调试
            Log.INFO,//信息打印
            Log.WARN,//警告
            Log.ERROR,//错误
            Log.ASSERT
        )

        private val listeners = HashSet<LogListener>()

        fun addListener(logListener: LogListener) {
            listeners.add(logListener)
        }

        fun removeListener(logListener: LogListener) {
            listeners.remove(logListener)
        }

        fun i(tag: Any = SDK.getApplication().packageName, msg: String) {
            log(Log.INFO, tag, msg)
        }

        fun v(tag: Any = SDK.getApplication().packageName, msg: String) {
            log(Log.VERBOSE, tag, msg)
        }

        fun d(tag: Any = SDK.getApplication().packageName, msg: String) {
            log(Log.DEBUG, tag, msg)
        }

        fun w(tag: Any = SDK.getApplication().packageName, msg: String) {
            log(Log.WARN, tag, msg)
        }

        fun e(tag: Any = SDK.getApplication().packageName, msg: String) {
            log(Log.ERROR, tag, msg)
        }

        fun log(level: Int = Log.INFO, tag: Any, msg: String) {
            if (!ENABLE_LOG.contains(level)) {
                return
            }
            //若传入的是某个类的对象，这里获取类名作为tag
            var tagStr = when (tag) {
                is String -> {
                    tag
                }
                is Class<*> -> {
                    tag.simpleName
                }
                else -> {
                    tag::class.java.simpleName
                }
            }
            if (tagStr.isEmpty()) {
                tagStr = SDK.getApplication().packageName
            }
            if (listeners.isNotEmpty()) {
                val logInfo = LogInfo(level, System.currentTimeMillis(), tagStr, msg)
                for (listener in listeners) {
                    listener.onNewLog(logInfo)
                }
            }
            //仅调试状态下输出
            if (!BuildConfig.DEBUG) {
                return
            }
            when (level) {
                Log.INFO -> {
                    Log.i(tagStr, msg)
                }
                Log.VERBOSE -> {
                    Log.v(tagStr, msg)
                }
                Log.DEBUG -> {
                    Log.d(tagStr, msg)
                }
                Log.WARN -> {
                    Log.w(tagStr, msg)
                }
                Log.ERROR -> {
                    Log.e(tagStr, msg)
                }
            }
        }
    }

    interface LogListener {
        fun onNewLog(logInfo: LogInfo)
    }
}