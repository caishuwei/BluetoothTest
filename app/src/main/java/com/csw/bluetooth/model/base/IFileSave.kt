package com.csw.bluetooth.model.base

import android.net.Uri
import com.csw.bluetooth.model.storage.ExternalFileDataCache
import java.io.InputStream

/**
 * 文件存储
 */
interface IFileSave {

    /**
     * 将图片保存到本地
     */
    fun saveImage(
        fileName: String,
        mimeType: String,
        inputStream: InputStream,
        fileSize: Long
    ): Uri? {
        return ExternalFileDataCache().saveChatImage(fileName, mimeType, inputStream, fileSize)
    }

}