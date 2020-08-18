package com.csw.bluetooth.entities

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.bluetooth.database.DBUtils
import com.csw.bluetooth.database.table.Message
import com.csw.bluetooth.database.values.MessageType

/**
 * 消息Item
 */
class MessageItem(message: Message, isSendMessage: Boolean) : MultiItemEntity {
    companion object {
        const val OTHER = 0
        const val SEND_TEXT = 101
        const val SEND_IMAGE = 102
        const val RECEIVE_TEXT = 201
        const val RECEIVE_IMAGE = 202
    }

    var message = message
    val itemType1 = when (message.getMessageType()) {
        MessageType.TEXT -> {
            if (isSendMessage) {
                SEND_TEXT
            } else {
                RECEIVE_TEXT
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