package com.csw.bluetooth.entities.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * data class Kotlin数据类，不能被继承，不能是抽象的,
 * 构造器至少需要拥有一个成员属性，可以是var或者val
 *
 * @Entity 将实体类映射为表
 * tableName 若不设置，默认使用类名，不区分大小写
 * primaryKeys 用于可以使用多个字段值联合作为主键
 * ignoredColumns指定忽略字段名称（包括父类字段）
 * indices 可以将列值加入索引值计算，提高查询速度
 */
@Entity(
    tableName = "Message",
    indices = [Index(value = ["messageId"])]
)
data class Message(
    /**
     * @PrimaryKey 定义主键
     * @ColumnInfo 定义表的列，不指定列名，则，默认使用变量名
     * 有需要忽略的字段使用@Ignore进行注解
     */
    @PrimaryKey
    @ColumnInfo(name = "messageId")
    val messageId: String,//消息id
    @ColumnInfo(name = "createTime")
    val createTime: Long,//消息创建时间
    @ColumnInfo(name = "deviceId")
    val deviceId: Long//设备的id
)