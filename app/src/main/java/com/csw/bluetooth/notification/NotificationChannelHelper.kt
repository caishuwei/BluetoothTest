@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.csw.bluetooth.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationChannelHelper constructor(
    context: Context,
    val notificationChannelGroupHelper: NotificationChannelGroupHelper,
    val id: String,//id
    val name: String,//名称
    val desc: String,//描述
    val lightsEnable: Boolean = false,//呼吸灯
    val vibrationEnable: Boolean = false//震动
) {

    companion object {
        private const val CLASSIC_BLUETOOTH_ID: String = "CLASSIC_BLUETOOTH"
        private const val CLASSIC_BLUETOOTH_NAME: String = "经典蓝牙连接状态"
        private const val CLASSIC_BLUETOOTH_DESC: String = "该通知用于显示经典蓝牙连接状态"

        fun instanceOfClassicBluetooth(context: Context): NotificationChannelHelper {
            return NotificationChannelHelper(
                context,
                NotificationChannelGroupHelper.instanceOfBluetoothConnection(context),
                CLASSIC_BLUETOOTH_ID,
                CLASSIC_BLUETOOTH_NAME,
                CLASSIC_BLUETOOTH_DESC
            )
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java)?.run {
                //建立通知渠道
                val channel = NotificationChannel(
                    id,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = desc
                channel.group = notificationChannelGroupHelper.id
                channel.enableLights(lightsEnable)
                channel.enableVibration(vibrationEnable)
                createNotificationChannel(channel)
            }
        }
    }

}