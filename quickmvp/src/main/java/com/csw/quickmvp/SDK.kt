@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp

import android.app.Application
import android.content.Context

class SDK {

    companion object {
        private var application: Application? = null

        /**
         * 初始化sdk,注入Application提供应用上下文
         */
        fun init(application: Application?) {
            this.application = application
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