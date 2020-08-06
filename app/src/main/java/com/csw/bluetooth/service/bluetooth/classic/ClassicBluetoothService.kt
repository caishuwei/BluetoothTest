@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.csw.bluetooth.service.bluetooth.classic

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.csw.bluetooth.IClassicBluetoothInterface
import com.csw.bluetooth.app.MyApplication
import javax.inject.Inject

/**
 * 经典蓝牙服务
 * <br/>
 * 经典蓝牙用于高带宽传输，连接范围效，数据传输速度快（相对于ble）,缺点是同一时间只能连接一个蓝牙设备
 */
class ClassicBluetoothService : Service() {

    companion object {
        const val ACTION_DEVICES_CHANGED = "ACTION_DEVICES_CHANGED"
        const val ACTION_DEVICE_STATE_CHANGED = "ACTION_DEVICE_STATE_CHANGED"
        private const val ACTION_BEGIN_DISCOVERY = "ACTION_BEGIN_DISCOVERY"
        private const val ACTION_CANCEL_DISCOVERY = "ACTION_CANCEL_DISCOVERY"

        fun cancelDiscovery(context: Context) {
            startService(context, ACTION_CANCEL_DISCOVERY)
        }

        fun beginDiscovery(context: Context) {
            startService(context, ACTION_BEGIN_DISCOVERY)
        }

        fun startService(context: Context, actionStr: String? = null) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, ClassicBluetoothService::class.java)
                    .apply {
                        if (actionStr != null) {
                            action = actionStr
                        }
                    }
            )
        }
    }

    @Inject
    lateinit var classicNotificationHelper: ClassicNotificationHelper
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var startDiscoveryWhenBluetoothOpen: Boolean = false
    private val myClassicBluetoothImpl = MyClassicBluetoothImpl()
    private val devices = ArrayList<BluetoothDevice>()
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (intent == null || action == null) {
                return
            }
            when (action) {
                //蓝牙设备开关
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            //正在开启蓝牙
                        }
                        BluetoothAdapter.STATE_ON -> {
                            //蓝牙已打开
                            if (startDiscoveryWhenBluetoothOpen) {
                                startDiscoveryWhenBluetoothOpen = false
                                //当前界面处于前台，开始扫描
                                myClassicBluetoothImpl.startDiscovery()
                            }
                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            //正在关闭蓝牙
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            //蓝牙已关闭
                        }
                        else -> {
                        }
                    }
                }
                //蓝牙设备绑定状态发生改变
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.run {
                        sendBroadcast(Intent(ACTION_DEVICE_STATE_CHANGED))
                    }
                }
                //发现新设备
                BluetoothDevice.ACTION_FOUND -> {
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.run {
                        for (bd in devices) {
                            if (bd.address == this.address) {
                                return
                            }
                        }
                        devices.add(this)
                        sendBroadcast(Intent(ACTION_DEVICES_CHANGED))
                    }
                }
                //开始扫描
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    updateBondedDevices()
                    sendBroadcast(Intent(ACTION_DEVICES_CHANGED))
                }
                //结束扫描
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {

                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        MyApplication.instance.appComponent.inject(this)
        startForeground(
            classicNotificationHelper.id,
            classicNotificationHelper.notification
        )
        classicNotificationHelper.update()

        registerReceiver(receiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        })

        updateBondedDevices()
        sendBroadcast(Intent(ACTION_DEVICES_CHANGED))
    }

    private fun updateBondedDevices() {
        devices.clear()
        bluetoothAdapter?.bondedDevices?.run {
            devices.addAll(this)
        }
    }

    /**
     * 处理服务要执行的命令
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {
            when (it) {
                ACTION_BEGIN_DISCOVERY -> {
                    myClassicBluetoothImpl.startDiscovery()
                }
                ACTION_CANCEL_DISCOVERY -> {
                    myClassicBluetoothImpl.cancelDiscovery()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myClassicBluetoothImpl
    }

    override fun onDestroy() {
        classicNotificationHelper.cancel()
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    //inner class-----------------------------------------------------------------------------------
    private inner class MyClassicBluetoothImpl : IClassicBluetoothInterface.Stub() {

        override fun cancelDiscovery() {
            bluetoothAdapter?.cancelDiscovery()
        }

        override fun startDiscovery() {
            bluetoothAdapter?.run {
                if (!isDiscovering) {
                    //开始扫描，每次扫描大概12秒，可以调用cancelDiscovery()提前停止
                    if (!startDiscovery()) {
                        //返回false表示蓝牙未开启
                        if (!isEnabled) {
                            startDiscoveryWhenBluetoothOpen = true
                            //开启蓝牙
                            enable()
                        }
                    }
                }
            }
        }

        override fun getBluetoothDevices(): Array<BluetoothDevice> {
            return Array(devices.size) {
                return@Array devices[it]
            }
        }

        override fun bondBluetoothDevice(bluetoothDevice: BluetoothDevice?) {
            bluetoothDevice?.run {
                when (bondState) {
                    BluetoothDevice.BOND_NONE -> {
                        createBond()
                    }
                    else -> {
                    }
                }
            }
        }

        override fun connectBluetoothDevice(bluetoothDevice: BluetoothDevice?) {
            bluetoothAdapter?.cancelDiscovery()
            bluetoothDevice?.run {
                when (bondState) {
                    BluetoothDevice.BOND_NONE -> {
                        createBond()
                        //connectWhenDeviceBoned
                    }
                    BluetoothDevice.BOND_BONDING -> {
                        //connectWhenDeviceBoned
                    }
                    BluetoothDevice.BOND_BONDED -> {
                        //connect
                    }
                    else -> {
                    }
                }
            }
        }


    }
}