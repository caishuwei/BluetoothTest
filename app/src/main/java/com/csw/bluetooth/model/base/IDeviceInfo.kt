package com.csw.bluetooth.model.base

import com.csw.bluetooth.model.storage.ExternalFileDataCache

/**
 * kotlin接口可以提供默认方法实现，这貌似可以实现多继承了啊
 */
interface IDeviceInfo {

    /**
     * 获取设备Id
     */
    fun getDeviceId(): String {
        return ExternalFileDataCache().getDeviceId()
    }

}