@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp.mvp.example.module

import com.csw.quickmvp.mvp.base.IBasePresenter
import com.csw.quickmvp.mvp.base.IBaseView

/**
 * 模块协议，这里定义View于Presenter互相暴露的方法用于调提供对方调用，
 * 所以这是定义协议的地方
 */
interface ModuleContract {

    /**
     * 定义Presenter提供给View调用的方法
     */
    interface Presenter : IBasePresenter {
        /**
         * 设置初始化参数
         */
        fun setInitParams()
    }

    /**
     * 定义View提供给Presenter调用的方法
     */
    interface View : IBaseView {
        /**
         * 更新UI
         */
        fun updateUI()
    }

}