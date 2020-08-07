package com.csw.bluetooth.service.bluetooth.classic.connect.message

import java.io.OutputStream

/**
 * 消息接口
 */
interface IMessage {

    companion object {
        const val HEADER_SPACE = "\r\n"
        const val HEADER_KEY_VALUE_SPACE = ":"
        val HEADER_END_FLAG = "\r\n\r\n".toByteArray()
    }

    /**
     * 将消息对象写入输出流
     */
    fun write(outputStream: OutputStream)

    /**
     * 获取头信息
     */
    fun getHeader(headerKey: String): String?
}