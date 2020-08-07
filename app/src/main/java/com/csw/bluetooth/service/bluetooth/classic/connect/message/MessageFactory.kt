package com.csw.bluetooth.service.bluetooth.classic.connect.message

import com.csw.bluetooth.service.bluetooth.classic.connect.message.header.Header
import java.io.InputStream
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
                        headerMap[get(0)] = get(1)
                    }
                }
            }
            //解析头，取得body长度
            headerMap[Header.ContentLength.name]?.run {
                val bodySize = try {
                    this.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                    0
                }
                if (bodySize > 0) {
                    //取得body
                    val bodyData = ByteArray(bodySize)
                    inputStream.read(bodyData)
                    //获取内容类型生成对应实例
                    headerMap[Header.ContentType.name]?.run {
                        when (this) {
                            Header.ContentType.TYPE_JSON -> {
                                return TextMessage(String(bodyData))
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
            return null
        }
    }

}