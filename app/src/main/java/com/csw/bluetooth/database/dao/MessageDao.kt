package com.csw.bluetooth.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.csw.bluetooth.database.table.ImageMessageData
import com.csw.bluetooth.database.table.Message
import com.csw.bluetooth.database.table.TextMessageData

@Dao
interface MessageDao {

    /**
     * 插入消息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg message: TextMessageData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg message: ImageMessageData)
}