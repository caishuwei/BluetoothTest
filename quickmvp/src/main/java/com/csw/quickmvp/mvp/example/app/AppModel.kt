package com.csw.quickmvp.mvp.example.app

import com.csw.quickmvp.utils.LogUtils
import com.csw.quickmvp.utils.Utils

/**
 * App模型，用于展示实例注入
 */
class AppModel {

    //生成唯一Id,日志输出可以看到Application ModuleView ModulePresenter注入的都是同一个实例
    private val id = Utils.generateId()

    fun log(any: Any) {
        LogUtils.d(javaClass.simpleName, "$id ${any::class.java.name}")
    }

}