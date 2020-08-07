@file:Suppress("MayBeConstant", "unused")

package com.csw.bluetooth.service.bluetooth.classic.connect.message

import com.csw.bluetooth.service.bluetooth.classic.connect.message.header.Header
import java.io.OutputStream

/**
 * 消息基类，设置头部信息
 */
abstract class BaseMessage : IMessage {
    val headers = LinkedHashMap<String, String>()

    init {
        headers[Header.HeaderStart.name] = Header.HeaderStart.DESC
        headers[Header.MessageCreateTime.name] = System.currentTimeMillis().toString()
    }

    override fun write(outputStream: OutputStream) {
        writeHeaders(outputStream)
        writeBody(outputStream)
    }

    fun setMessageCreateTime(time: String) {
        headers[Header.MessageCreateTime.name] = time
    }

    private fun writeHeaders(outputStream: OutputStream) {
        val sb = StringBuilder()
        for (e in headers.entries) {
            sb.append("${e.key}${IMessage.HEADER_KEY_VALUE_SPACE}${e.value}${IMessage.HEADER_SPACE}")
        }
        sb.append(IMessage.HEADER_SPACE)
        outputStream.write(sb.toString().toByteArray())
    }

    abstract fun writeBody(outputStream: OutputStream)

    override fun getHeader(headerKey: String): String? {
        return headers[headerKey]
    }
}
