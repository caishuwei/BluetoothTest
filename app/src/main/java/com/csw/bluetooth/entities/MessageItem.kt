package com.csw.bluetooth.entities

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.bluetooth.database.DBUtils
import com.csw.bluetooth.database.table.Message
import com.csw.bluetooth.database.values.MessageType
import java.io.Serializable

/**
 * 消息Item
 */
class MessageItem(message: Message, isSendMessage: Boolean) : MultiItemEntity, Serializable {
    companion object {
        const val OTHER = 0
        const val SEND_TEXT = 101
        const val SEND_IMAGE = 102
        const val RECEIVE_TEXT = 201
        const val RECEIVE_IMAGE = 202
    }

    var message = message
    private val itemType1 = when (message.getMessageType()) {
        MessageType.TEXT -> {
            if (isSendMessage) {
                SEND_TEXT
            } else {
                RECEIVE_TEXT
            }
        }
        MessageType.IMAGE -> {
            if (isSendMessage) {
                SEND_IMAGE
            } else {
                RECEIVE_IMAGE
            }
        }
        else -> OTHER
    }
    private var data: Any? = null

    fun getData(): Any? {
        if (data == null) {
            when (message.getMessageType()) {
                MessageType.TEXT -> {
                    data = DBUtils.getTextMessageData(message.messageId)
                }
                MessageType.IMAGE -> {
                    data = DBUtils.getImageMessageData(message.messageId)
                }
                else -> {
                }
            }
        }
        return data
    }

    override fun getItemType(): Int {
        return itemType1
    }
}