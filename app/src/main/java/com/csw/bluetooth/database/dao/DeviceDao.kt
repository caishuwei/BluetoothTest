package com.csw.bluetooth.database.dao

import androidx.room.*
import com.csw.bluetooth.database.relation.DeviceWithMessageList
import com.csw.bluetooth.database.table.Device

/**
 * @Dao 定义数据库访问对象
 */
@Dao
interface DeviceDao {

    /**
     * 插入设备，出现冲突（如主键冲突，则替换）
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevices(vararg device: Device)

    /**
     * 查询设备及其消息
     */
    @Query("SELECT * FROM Device Where address = :address")
    fun getDeviceWithMessageList(address: String): DeviceWithMessageList

    /**
     * 查询所有设备及其消息
     * @Transaction 标记该sql需要开启事务，所有sql语句都执行成功则返回，若有部分未执行成功，则回滚操作
     */
    @Transaction
    @Query("SELECT * FROM Device")
    fun getDeviceWithMessageLists(): List<DeviceWithMessageList>

}