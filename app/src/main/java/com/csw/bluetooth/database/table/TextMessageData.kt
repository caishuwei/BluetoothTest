package com.csw.bluetooth.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "TextMessageData",
    indices = [Index(value = ["messageId"])]
)
data class TextMessageData(
    @PrimaryKey
    @ColumnInfo(name = "messageId")
    val messageId: String,//消息id

    @ColumnInfo(name = "text")
    val text: String//消息内容
) : Serializable