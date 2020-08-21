package com.csw.quickmvp.log

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.csw.quickmvp.R
import com.csw.quickmvp.utils.LogUtils
import com.csw.quickmvp.utils.RxBus

class LogDisplayController private constructor() {

    companion object {
        const val MAX_LOG_SIZE = 20

        //开启日志显示
        const val ENABLE = true

        //单例
        val instance = LogDisplayController()
    }

    //是否展开日志，默认false
    var logExpanded: Boolean = false
        private set
    private var cursor = 0
    private val logArray = Array<LogInfo?>(MAX_LOG_SIZE) {
        null
    }
    private val callback = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activity.window?.decorView?.run {
                if (this is ViewGroup) {
                    //添加View
                    val fragmentContainer =
                        findViewById<FrameLayout>(R.id.quick_mvp_fragment_container)
                    if (fragmentContainer == null) {
                        val fc = FrameLayout(activity)
                        fc.id = R.id.quick_mvp_fragment_container
                        addView(
                            fc,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                    //添加fragment
                    if (activity is AppCompatActivity) {
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.quick_mvp_fragment_container, LogViewFragment())
                            .commitAllowingStateLoss()
                    }
                }
            }
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }
    }

    private val logListener = object : LogUtils.LogListener {
        override fun onNewLog(logInfo: LogInfo) {
            cursor = (cursor + 1) % MAX_LOG_SIZE
            logArray[cursor] = logInfo
            RxBus.getDefault().post(OnNewLogInfo(logInfo))
        }
    }

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(callback)
        LogUtils.addListener(logListener)
    }

    fun getLogList(): ArrayList<LogInfo> {
        val result = ArrayList<LogInfo>(MAX_LOG_SIZE)
        for (i in (cursor + MAX_LOG_SIZE) downTo (cursor + 1)) {
            logArray[i % MAX_LOG_SIZE]?.run {
                result.add(this)
            }
        }
        return result
    }

    fun onLogExpandChanged(expand: Boolean) {
        logExpanded = expand
        RxBus.getDefault().post(OnLogExpandChanged(expand))
    }

}