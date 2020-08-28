package com.csw.bluetooth.service.bluetooth.le

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.csw.bluetooth.R
import com.csw.bluetooth.notification.NotificationChannelHelper
import javax.inject.Inject

class LowEnergyBluetoothNotificationHelper @Inject constructor(private val context: Context) {

    private var notificationManager: NotificationManager? = null
    private var notificationChannelHelper =
        NotificationChannelHelper.instanceOfClassicBluetooth(context)
    val id: Int = 10002
    val notification = NotificationCompat.Builder(context, notificationChannelHelper.id)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setOnlyAlertOnce(true)
        .setContentTitle(notificationChannelHelper.name)
        .setContentText(notificationChannelHelper.desc)
        .build()

    init {
        notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(id, notification)
    }

    fun update() {
        notificationManager?.notify(id, notification)
    }

    fun cancel() {
        notificationManager?.cancel(id)
    }
}