@file:Suppress("MemberVisibilityCanBePrivate")

package com.csw.bluetooth.notification

import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * 通知分组
 */
class NotificationChannelGroupHelper constructor(
    context: Context,
    val id: String,
    val name: String,
    val desc: String
) {

    companion object {
        private const val BLUETOOTH_CONNECTION_ID = "BLUETOOTH_CONNECTION"
        private const val BLUETOOTH_CONNECTION_NAME = "蓝牙连接通知"
        private const val BLUETOOTH_CONNECTION_DESC = "该分组用于显示蓝牙连接相关的通知"

        fun instanceOfBluetoothConnection(context: Context): NotificationChannelGroupHelper {
            return NotificationChannelGroupHelper(
                context,
                BLUETOOTH_CONNECTION_ID,
                BLUETOOTH_CONNECTION_NAME,
                BLUETOOTH_CONNECTION_DESC
            )
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java)?.run {
                //建立分组
                val group = NotificationChannelGroup(
                    id,
                    name
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    group.description = desc
                }
                createNotificationChannelGroup(group)
            }
        }
    }

}