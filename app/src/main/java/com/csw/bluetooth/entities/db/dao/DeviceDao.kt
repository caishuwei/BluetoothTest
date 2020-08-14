package com.csw.bluetooth.entities.db.dao

import androidx.room.*
import com.csw.bluetooth.entities.db.relation.DeviceWithMessageList
import com.csw.bluetooth.entities.db.table.Device

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
    @Transaction
    @Query("SELECT * FROM Device")
    fun getDeviceWithMessageList(): List<DeviceWithMessageList>

}