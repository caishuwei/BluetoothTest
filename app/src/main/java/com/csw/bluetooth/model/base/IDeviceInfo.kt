package com.csw.bluetooth.model.base

import android.net.Uri
import com.csw.bluetooth.model.cache.ExternalFileDataCache
import java.io.InputStream

/**
 * kotlin接口可以提供默认方法实现，这貌似可以实现多继承了啊
 */
interface IDeviceInfo {

    fun getDeviceId(): String {
        return ExternalFileDataCache().getDeviceId()
    }

    fun saveImage(
        fileName: String,
        mimeType: String,
        inputStream: InputStream,
        fileSize: Long
    ): Uri? {
        return ExternalFileDataCache().saveChatImage(fileName, mimeType, inputStream, fileSize)
    }
}