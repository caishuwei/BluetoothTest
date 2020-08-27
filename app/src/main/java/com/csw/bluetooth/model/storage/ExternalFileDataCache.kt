package com.csw.bluetooth.model.storage

import android.net.Uri
import com.csw.quickmvp.utils.ExternalFileHelper
import com.csw.quickmvp.utils.Utils
import java.io.FileOutputStream
import java.io.InputStream

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

    /**
     * 将接收到的聊天图片存入本地，返回Uri
     * @param fileName 带格式的文件名
     * @param mimeType 文件类型
     * @param inputStream 输入流
     * @param fileSize 文件长度
     */
    fun saveChatImage(
        fileName: String,
        mimeType: String,
        inputStream: InputStream,
        fileSize: Long
    ): Uri? {
        val filePath = ExternalFileHelper.getFilePath("cache/chat/image", fileName)
        return ExternalFileHelper.createFile(filePath, mimeType).apply {
            ExternalFileHelper.openFileDescriptor(filePath)?.run {
                var unReadSize = fileSize
                val blockBuffer = ByteArray(100 * 1024)
                var endBuffer: ByteArray
                var len: Int
                val fos = FileOutputStream(this)
                try {
                    while (unReadSize > 0) {
                        if (blockBuffer.size <= unReadSize) {
                            //先以100kb的缓冲区读取
                            len = inputStream.read(blockBuffer)
                            if (len == -1) {
                                break
                            } else {
                                unReadSize -= len
                            }
                            writeToOutputStream(fos, blockBuffer, 0, len)
                        } else {
                            //余下内容不足100kb,创建一块大小刚好的缓冲区读取余下内容
                            endBuffer = ByteArray(unReadSize.toInt())
                            len = inputStream.read(endBuffer)
                            if (len == -1) {
                                break
                            } else {
                                unReadSize -= len
                            }
                            writeToOutputStream(fos, endBuffer, 0, len)
                        }
                    }
                } catch (e: Exception) {
                    //输入流读取时异常，向外抛出
                    throw e
                } finally {
                    endOutputStream(fos)
                }
            }
        }
    }

    private fun endOutputStream(fos: FileOutputStream) {
        try {
            fos.flush()
            fos.close()
        } catch (e: Exception) {
        }
    }

    private fun writeToOutputStream(
        fos: FileOutputStream,
        blockBuffer: ByteArray,
        offset: Int,
        len: Int
    ) {
        try {
            fos.write(blockBuffer, offset, len)
        } catch (e: Exception) {
            e.printStackTrace()
            //写入过程中出现异常，关闭输出流
            try {
                fos.close()
            } catch (e: Exception) {
            }
        }
    }

}