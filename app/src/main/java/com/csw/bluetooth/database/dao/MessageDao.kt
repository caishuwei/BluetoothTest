package com.csw.bluetooth.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.csw.bluetooth.database.table.ImageMessageData
import com.csw.bluetooth.database.table.Message
import com.csw.bluetooth.database.table.TextMessageData

@Dao
interface MessageDao {

    /**
     * 插入消息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(vararg message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(vararg message: TextMessageData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(vararg message: ImageMessageData)

    @Query("SELECT * FROM Message WHERE Message.`from` =:address or Message.`to` =:address ORDER BY Message.createTime DESC LIMIT 100 ")
    fun getDeviceMessageList(address: String): List<Message>

    @Query("SELECT * FROM Message WHERE Message.`from` =:from or Message.`to` =:orTo ORDER BY Message.createTime DESC LIMIT :limit ")
    fun getDescDeviceMessageList(from: String, orTo: String, limit: Int): List<Message>

    @Query("SELECT * FROM Message WHERE Message.messageId =:messageID")
    fun getMessage(messageID: String): Message?

    @Query("SELECT * FROM TextMessageData WHERE TextMessageData.messageId =:messageID")
    fun getTextMessageData(messageID: String): TextMessageData?

}