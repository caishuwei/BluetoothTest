@file:Suppress("MayBeConstant", "unused")

package com.csw.bluetooth.service.bluetooth.classic.connect.message

import com.csw.bluetooth.service.bluetooth.classic.connect.message.header.Header
import java.io.OutputStream

/**
 * 消息基类，设置头部信息
 */
abstract class BaseMessage(id: String) : IMessage {
    private var id = id
        private set
    private var createTime = System.currentTimeMillis()
        private set
    val headers = LinkedHashMap<String, String>()

    init {
        headers[Header.HeaderStart.name] = Header.HeaderStart.DESC
        headers[Header.MessageID.name] = id
        headers[Header.MessageCreateTime.name] = createTime.toString()
    }

    override fun write(outputStream: OutputStream) {
        writeHeaders(outputStream)
        writeBody(outputStream)
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

    override fun setHeaders(headerMap: Map<String, String>) {
        for (kvs in headerMap.entries) {
            headers[kvs.key] = kvs.value
        }
        headers[Header.MessageCreateTime.name]?.run {
            createTime = toLong()
        }
    }

    //参数------------------------------------------------------------------------------------------

    override fun setMessageId(messageId: String) {
        id = messageId
        headers[Header.MessageID.name] = id
    }

    override fun getMessageId(): String {
        return id
    }

    override fun setMessageCreateTime(time: Long) {
        createTime = time
        headers[Header.MessageCreateTime.name] = time.toString()
    }

    override fun getMessageCreateTime(): Long {
        return createTime
    }

    override fun setFrom(from: String) {
        headers[Header.MessageFrom.name] = from
    }

    override fun getFrom(): String? {
        return headers[Header.MessageFrom.name]
    }

    override fun setTo(to: String) {
        headers[Header.MessageTo.name] = to
    }

    override fun getTo(): String? {
        return headers[Header.MessageTo.name]
    }
}
