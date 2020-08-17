package com.csw.bluetooth.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.csw.bluetooth.database.table.Device
import com.csw.bluetooth.database.table.Message

/**
 * 这个包主要用于定义表与表之间的关系，关联查询
 */
data class DeviceWithMessageList(
    /**
     * @Embedded 用于嵌套对象（DeviceWithMessageList，嵌入Device所有字段）
     */
    @Embedded
    val device: Device,

    //@Relation 定义两个表的关系 Device.id->Message.deviceId
    @Relation(
        parentColumn = "id",
        entityColumn = "deviceId"
    )
    val messageList: List<Message>
)