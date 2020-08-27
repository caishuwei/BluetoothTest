package com.csw.bluetooth.service.bluetooth.classic.connect.message

import com.csw.bluetooth.app.MyApplication
import com.csw.bluetooth.database.DBUtils
import com.csw.bluetooth.database.table.Message
import com.csw.bluetooth.database.values.MessageType
import com.csw.bluetooth.service.bluetooth.classic.connect.message.header.Header
import java.io.InputStream
import java.net.URLDecoder
import java.util.*
import kotlin.collections.HashMap

class MessageFactory {
    companion object {

        /**
         * 从输入流读取消息
         */
        fun instanceFromInputStream(inputStream: InputStream): IMessage? {
            var byte: Byte
            //注意，除非对方关闭输出流，否则这里是不会有-1的，需要自己解析报文，读取到消息结束位置就返回
            //修改操作多，使用链表性能更好
            val headerData = LinkedList<Byte>()
            //需要查询是否到header末尾，所以需要对读取到的字节数组进行匹配
            var matchIndex = 0
            while (inputStream.read().apply { byte = this.toByte() } != -1) {
                headerData.add(byte)
                if (byte == IMessage.HEADER_END_FLAG[matchIndex]) {
                    if (matchIndex == IMessage.HEADER_END_FLAG.size - 1) {
                        //已经匹配最后一个字节，到达header末尾了
                        break
                    } else {
                        //匹配下一个
                        matchIndex++
                    }
                } else {
                    //匹配位置归零
                    matchIndex = 0
                }
            }
            val headerStr = String(headerData.toByteArray())
            val headerKVS = headerStr.split(IMessage.HEADER_SPACE)
            val headerMap = HashMap<String, String>()
            for (kv in headerKVS) {
                kv.split(IMessage.HEADER_KEY_VALUE_SPACE).run {
                    if (size == 2) {
                        headerMap[get(0)] = URLDecoder.decode(get(1), "UTF-8")
                    }
                }
            }
            val messageId = headerMap[Header.MessageID.name]
            if (messageId == null) {
                return null
            }
            val contentType = headerMap[Header.ContentType.name]
            val contentLength = headerMap[Header.ContentLength.name]
            var bodySize = 0L
            try {
                contentLength?.run {
                    bodySize = this.toLong()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return when (contentType) {
                Header.ContentType.TYPE_TEXT -> {
                    val body = if (bodySize > 0) {
                        //取得body
                        val bodyData = ByteArray(bodySize.toInt())
                        inputStream.read(bodyData)
                        bodyData
                    } else {
                        null
                    }
                    TextMessage(messageId, String(body ?: ByteArray(0)))
                }
                Header.ContentType.TYPE_IMAGE -> {
                    val mimeType = headerMap[Header.MimeType.name] ?: "image/jpeg"
                    val mimeData = mimeType.split("/")
                    val mimeFormat = if (mimeData.size == 2) {
                        mimeData[1].toLowerCase()
                    } else {
                        null
                    }
                    val format = headerMap[Header.FileFormat.name]
                    val fileFormat =
                        if (format != null) {
                            format
                        } else if (mimeFormat != null) {
                            mimeFormat
                        } else {
                            "jpg"
                        }
                    var result: ImageMessage? = null
                    if (bodySize > 0) {
                        MyApplication.instance.dataModel.saveImage(
                            "$messageId.$fileFormat",
                            mimeType,
                            inputStream,
                            bodySize
                        )?.run {
                            result = ImageMessage(messageId, this)
                        }
                    }
                    result
                }
                else -> {
                    null
                }
            }?.apply {
                setHeaders(headerMap)
            }
        }

        fun dbMessageToMessage(message: Message): IMessage? {
            return when (message.getMessageType()) {
                MessageType.TEXT -> {
                    val textData = DBUtils.getTextMessageData(message.messageId)
                    val text = textData?.text ?: ""
                    TextMessage(message.messageId, text)
                }
                else -> null
            }?.apply {
                setFrom(message.from)
                setTo(message.to)
            }
        }

    }

}