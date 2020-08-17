package com.csw.bluetooth.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.csw.bluetooth.database.dao.DeviceDao
import com.csw.bluetooth.database.dao.MessageDao
import com.csw.bluetooth.database.table.Device
import com.csw.bluetooth.database.table.ImageMessageData
import com.csw.bluetooth.database.table.Message
import com.csw.bluetooth.database.table.TextMessageData


@Database(
    entities = [
        Device::class,
        Message::class,
        ImageMessageData::class,
        TextMessageData::class
    ], version = 1, exportSchema = true
)
abstract class MyRoomDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "BluetoothCommunication"

        private val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //版本1升级到版本2数据库数据迁移
                database.execSQL("")
            }
        }

        fun getInstance(application: Application): MyRoomDatabase {
            return Room.databaseBuilder(application, MyRoomDatabase::class.java, DATABASE_NAME)
                .addMigrations(
                    migration_1_2//添加版本升级处理，这里以1到2为例子
                )
                .fallbackToDestructiveMigration()//数据库升级失败时重新创建数据库
//                .allowMainThreadQueries()//允许主线程查询
                .build()
        }

    }

    /**
     * 取得设备表数据访问对象
     */
    abstract fun getDeviceDao(): DeviceDao

    /**
     * 取得消息表数据访问对象
     */
    abstract fun getMessageDao(): MessageDao
}