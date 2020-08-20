package com.csw.bluetooth.model.cache

import com.csw.quickmvp.utils.ExternalFileHelper
import com.csw.quickmvp.utils.Utils

/**
 * 文件数据缓存,将数据缓存到外置存储卡公开目录，有可能会被用户删除
 * <br/>
 * 低于API29，调用这个类的方法需要获取外置存储的读写权限
 */
class ExternalFileDataCache {

    /**
     * 获取设备Id
     */
    fun getDeviceId(): String {
        val filePath = ExternalFileHelper.getFilePath("device", "DeviceID.txt")
        ExternalFileHelper.getFileString(filePath)?.run {
            return this
        }
        val id = Utils.generateId()
        ExternalFileHelper.writeToFile(id.toByteArray(), filePath)
        return id
    }

}