package com.csw.bluetooth.database

import com.csw.bluetooth.app.MyApplication
import com.csw.bluetooth.database.dao.MessageDao
import com.csw.bluetooth.database.table.ImageMessageData
import com.csw.bluetooth.database.table.Message
import com.csw.bluetooth.database.table.TextMessageData
import com.csw.bluetooth.database.values.MessageState
import com.csw.bluetooth.database.values.MessageType
import com.csw.bluetooth.event.OnMessageStateChanged
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.message.ImageMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.message.TextMessage
import com.csw.quickmvp.utils.RxBus

object DBUtils {

    fun getMessageDao(): MessageDao {
        return MyApplication.instance.myRoomDatabase.getMessageDao()
    }

    /**
     * 插入一条消息
     */
    fun insertMessage(message: IMessage) {
        val messageId = message.getMessageId()
        val createTime = message.getMessageCreateTime()
        val from = message.getFrom()
        val to = message.getTo()
        if (from == null || to == null) {
            return
        }
        when (message) {
            is TextMessage -> {
                //插入文本消息数据
                getMessageDao().insertOrReplace(
                    TextMessageData(
                        messageId,
                        message.text
                    )
                )
                MessageType.TEXT
            }
            is ImageMessage -> {
                //插入图片消息数据
                getMessageDao().insertOrReplace(
                    ImageMessageData(
                        messageId,
                        message.imageContentUri.toString()
                    )
                )
                MessageType.IMAGE
            }
            else -> null
        }?.run {
            //插入消息记录
            Message(
                messageId,
                createTime,
                name,
                MessageState.CREATED.name,
                from,
                to
            ).run {
                getMessageDao().insertOrReplace(this)
                RxBus.getDefault().post(OnMessageStateChanged(this))
            }
        }
    }

    /**
     * 获取文本消息数据
     */
    fun getTextMessageData(messageId: String): TextMessageData? {
        return getMessageDao().getTextMessageData(messageId)
    }

    /**
     * 获取文本消息数据
     */
    fun getImageMessageData(messageId: String): ImageMessageData? {
        return getMessageDao().getImageMessageData(messageId)
    }

    /**
     * 更新消息状态
     */
    fun updateMessageState(messageId: String?, messageState: MessageState) {
        if (messageId == null) {
            return
        }
        getMessageDao().getMessage(messageId)?.run {
            state = messageState.name
            getMessageDao().insertOrReplace(this)
            RxBus.getDefault().post(OnMessageStateChanged(this))
        }
    }

    /**
     * 获取与设备相关的消息
     * @param deviceAddress 设备地址
     * @param hasReceive 包含从该设备接收的消息
     * @param hasSend 包含发送给该设备的消息
     * @param orderDescByTime 以消息时间倒叙排序
     * @param limit 消息数量限制
     */
    fun getDeviceMessageList(
        deviceAddress: String,
        hasReceive: Boolean = true,
        hasSend: Boolean = true,
        orderDescByTime: Boolean = false,
        limit: Int = 0
    ): List<Message> {
        if (hasReceive && hasSend && orderDescByTime && limit > 0) {
            return getMessageDao().getDescDeviceMessageList(deviceAddress, deviceAddress, limit)
        }
        return emptyList()
    }

}