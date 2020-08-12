@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp

import android.app.Application
import android.content.Context
import com.csw.quickmvp.log.LogDisplayController

class SDK {

    companion object {
        private var application: Application? = null

        /**
         * 初始化sdk,注入Application提供应用上下文
         */
        fun init(application: Application?) {
            this.application = application
            application?.run {
                if (LogDisplayController.ENABLE) {
                    LogDisplayController.instance.init(this)
                }
            }
        }

        fun getApplication(): Application {
            application?.let {
                return it
            }
            throw RuntimeException("使用com.csw.quickmvp前，需要先调用SDK.init()进行初始化")
        }

        fun getContext(): Context {
            return getApplication()
        }
    }

}