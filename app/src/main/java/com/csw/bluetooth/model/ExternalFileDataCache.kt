package com.csw.bluetooth.model

import android.Manifest
import androidx.annotation.RequiresPermission
import com.csw.quickmvp.utils.ExternalFileHelper
import com.csw.quickmvp.utils.Utils
import javax.inject.Inject

/**
 * 文件数据缓存,将数据缓存到外置存储卡公开目录，有可能会被用户删除
 * <br/>
 * 低于API29，调用这个类的方法需要获取外置存储的读写权限
 */
class ExternalFileDataCache @Inject constructor() {

    /**
     * 获取设备Id
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE])
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