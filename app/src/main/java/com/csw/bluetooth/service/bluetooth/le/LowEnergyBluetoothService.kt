package com.csw.bluetooth.service.bluetooth.le

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.csw.bluetooth.LowEnergyBluetoothInterface
import com.csw.bluetooth.app.MyApplication
import javax.inject.Inject

class LowEnergyBluetoothService : Service() {

    companion object {
        private const val ACTION_BEGIN_DISCOVERY = "ACTION_BEGIN_DISCOVERY"
        private const val ACTION_CANCEL_DISCOVERY = "ACTION_CANCEL_DISCOVERY"
        private const val ACTION_WAIT_FOR_CLIENT_CONTENT = "ACTION_WAIT_FOR_CLIENT_CONTENT"
        fun cancelDiscovery(context: Context) {
            startService(context, ACTION_CANCEL_DISCOVERY)
        }

        fun beginDiscovery(context: Context) {
            startService(context, ACTION_BEGIN_DISCOVERY)
        }

        fun waitForClientConnect(context: Context) {
            startService(context, ACTION_WAIT_FOR_CLIENT_CONTENT)
        }

        private fun startService(context: Context, actionStr: String? = null) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, LowEnergyBluetoothService::class.java)
                    .apply {
                        if (actionStr != null) {
                            action = actionStr
                        }
                    }
            )
        }

    }

    @Inject
    lateinit var lowEnergyBluetoothNotificationHelper: LowEnergyBluetoothNotificationHelper
    private val lowEnergyBluetoothInterfaceImpl = LowEnergyBluetoothInterfaceImpl()

    override fun onCreate() {
        super.onCreate()
        MyApplication.instance.appComponent.inject(this)
        startForeground(
            lowEnergyBluetoothNotificationHelper.id,
            lowEnergyBluetoothNotificationHelper.notification
        )
        lowEnergyBluetoothNotificationHelper.update()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return lowEnergyBluetoothInterfaceImpl
    }

    override fun onDestroy() {
        lowEnergyBluetoothNotificationHelper.cancel()
        super.onDestroy()
    }

    //inner class-----------------------------------------------------------------------------------

    private inner class LowEnergyBluetoothInterfaceImpl : LowEnergyBluetoothInterface.Stub() {

    }
}