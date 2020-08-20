package com.csw.bluetooth.model.base

import com.csw.bluetooth.model.cache.ExternalFileDataCache

/**
 * kotlin接口可以提供默认方法实现，这貌似可以实现多继承了啊
 */
interface IDeviceInfo {

    fun getDeviceId(): String {
        return ExternalFileDataCache().getDeviceId()
    }
}