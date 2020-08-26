package com.csw.bluetooth.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * 设备表，将设备地址设置为唯一
 */
@Entity(
    tableName = "Device",
    indices = [Index("address", unique = true)]
)
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "address")
    val address: String,//蓝牙设备地址
    @ColumnInfo(name = "name")
    val name: String//蓝牙设备名称
) : Serializable {

}