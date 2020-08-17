package com.csw.bluetooth.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ImageMessageData",
    indices = [Index(value = ["messageId"])]
)
data class ImageMessageData(
    @PrimaryKey
    @ColumnInfo(name = "messageId")
    val messageId: String,//消息id

    @ColumnInfo(name = "imageUrl")
    val imageUrl: String//本地图片地址

)