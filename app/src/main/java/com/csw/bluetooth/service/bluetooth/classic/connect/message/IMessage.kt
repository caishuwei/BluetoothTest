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

    /**
     * 设置头信息
     */
    fun setHeaders(headerMap: Map<String, String>)


    //参数------------------------------------------------------------------------------------------
    /**
     * 消息id
     */
    fun setMessageId(messageId: String)
    fun getMessageId(): String

    /**
     * 消息创建时间
     */
    fun setMessageCreateTime(time: Long)
    fun getMessageCreateTime(): Long

    /**
     * 消息来自哪个设备（address）
     */
    fun setFrom(from: String)
    fun getFrom(): String?

    /**
     * 消息发往哪个设备（address）
     */
    fun setTo(to: String)
    fun getTo(): String?
}