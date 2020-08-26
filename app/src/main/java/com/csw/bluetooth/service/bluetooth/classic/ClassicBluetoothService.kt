@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.csw.bluetooth.service.bluetooth.classic

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.csw.bluetooth.IClassicBluetoothInterface
import com.csw.bluetooth.app.MyApplication
import com.csw.bluetooth.database.DBUtils
import com.csw.bluetooth.service.bluetooth.ConnectState
import com.csw.bluetooth.service.bluetooth.classic.connect.base.ConnectedDeviceHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.message.ImageMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.message.TextMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.task.ClientConnectTask
import com.csw.bluetooth.service.bluetooth.classic.connect.task.ICancelableConnectTask
import com.csw.bluetooth.service.bluetooth.classic.connect.task.ServerConnectTask
import com.csw.bluetooth.ui.chat.ChatActivity
import com.csw.bluetooth.utils.getDisplayName
import com.csw.quickmvp.utils.LogUtils
import com.csw.quickmvp.utils.Utils
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
        const val ACTION_DEVICE_CONNECT_STATE_CHANGED = "ACTION_DEVICE_CONNECT_STATE_CHANGED"
        const val EXTRA_DEVICE_CONNECT_STATE = "EXTRA_DEVICE_CONNECT_STATE"
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
                Intent(context, ClassicBluetoothService::class.java)
                    .apply {
                        if (actionStr != null) {
                            action = actionStr
                        }
                    }
            )
        }
    }


    private var connectTask: ICancelableConnectTask? = null
    private var connectedDeviceHelper: ConnectedDeviceHelper? = null

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
                            } else {
                                if (connectedDeviceHelper == null && connectTask == null) {
                                    startServerConnectTask()
                                }
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

    private fun startServerConnectTask() {
        bluetoothAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                connectTask?.cancel()
                connectTask = ServerConnectTask.getSPPServerConnectTask(this, adapter)
                    .apply {
                        connect()
                    }
            }
        }
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
                ACTION_WAIT_FOR_CLIENT_CONTENT -> {
                    startServerConnectTask()
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

    fun onTaskEnd(cancelableConnectTask: ICancelableConnectTask) {
        if (connectTask == cancelableConnectTask) {
            connectTask = null
            if (connectedDeviceHelper == null) {
                startServerConnectTask()
            }
        }
    }

    fun onNewMessage(device: BluetoothDevice, message: IMessage) {
        //从设备接收到新的消息
        if (message is TextMessage) {
            LogUtils.d(this, "onNewMessage from[${device.getDisplayName()}] ${message.text}")
        }
    }

    fun onDeviceConnected(
        connectTask: ICancelableConnectTask,
        connectedDeviceHelper: ConnectedDeviceHelper
    ) {
        LogUtils.d(
            this,
            "onDeviceConnected ${connectedDeviceHelper.getConnectDevice().getDisplayName()}"
        )
        sendBroadcast(Intent(ACTION_DEVICE_CONNECT_STATE_CHANGED).apply {
            putExtra(EXTRA_DEVICE_CONNECT_STATE, ConnectState.STATE_ONLINE.name)
            putExtra(BluetoothDevice.EXTRA_DEVICE, connectedDeviceHelper.getConnectDevice())
        })
        this.connectedDeviceHelper?.close()
        this.connectedDeviceHelper = connectedDeviceHelper
        if (connectTask is ServerConnectTask) {
            //与客户端连接上
            ChatActivity.openActivity(this, connectedDeviceHelper.getConnectDevice())
        }
    }

    fun onDeviceDisconnect(connectedDeviceHelper: ConnectedDeviceHelper) {
        LogUtils.d(
            this,
            "onDeviceDisconnect ${connectedDeviceHelper.getConnectDevice().getDisplayName()}"
        )
        sendBroadcast(Intent(ACTION_DEVICE_CONNECT_STATE_CHANGED).apply {
            putExtra(EXTRA_DEVICE_CONNECT_STATE, ConnectState.STATE_OFFLINE.name)
            putExtra(BluetoothDevice.EXTRA_DEVICE, connectedDeviceHelper.getConnectDevice())
        })
        if (this.connectedDeviceHelper == connectedDeviceHelper) {
            this.connectedDeviceHelper = null
            if (connectTask == null) {
                startServerConnectTask()
            }
        }
    }

    /**
     * 对已连接的目标设备发出一条消息
     * @param message 消息对象
     * @param bluetoothDevice 目标设备
     */
    private fun sendMessage(
        message: IMessage,
        bluetoothDevice: BluetoothDevice
    ): Boolean {
        val from = bluetoothAdapter?.address
        val to = bluetoothDevice.address
        if (from == null) {
            return false
        }
        connectedDeviceHelper?.run {
            if (getConnectDevice() == bluetoothDevice) {
                message.setFrom(from)
                message.setTo(to)
                DBUtils.insertMessage(message)
                return write(message)
            }
        }
        return false
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
            if (bluetoothDevice == connectedDeviceHelper?.getConnectDevice()) {
                //设备已经连接
                return
            }
            if (bluetoothDevice == connectTask?.getDestDevice()) {
                //设备正在连接
                return
            }
            bluetoothAdapter?.address?.let { mDeviceAddress ->
                bluetoothDevice?.run {
                    //关闭已连接设备
                    val oldConnect = connectedDeviceHelper
                    //取消正在连接的任务
                    val oldTask = connectTask
                    //开始一个新的连接任务连接到该设备
                    connectTask = ClientConnectTask.getSPPClientConnectTask(
                        mDeviceAddress,
                        this@ClassicBluetoothService,
                        this
                    ).apply {
                        connect()
                        sendBroadcast(Intent(ACTION_DEVICE_CONNECT_STATE_CHANGED).apply {
                            putExtra(EXTRA_DEVICE_CONNECT_STATE, ConnectState.STATE_CONNECTING.name)
                            putExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                bluetoothDevice
                            )
                        })
                    }
                    oldTask?.cancel()
                    oldConnect?.close()
                }
            }
        }

        override fun getConnectingDevice(): BluetoothDevice? {
            return connectTask?.getDestDevice()
        }

        override fun getConnectedDevice(): BluetoothDevice? {
            return connectedDeviceHelper?.getConnectDevice()
        }

        override fun sendTextToDevice(bluetoothDevice: BluetoothDevice?, msg: String?): Boolean {
            if (bluetoothDevice != null && msg != null) {
                return sendMessage(TextMessage(Utils.generateId(), msg), bluetoothDevice)
            }
            return false
        }

        override fun sendImageToDevice(bluetoothDevice: BluetoothDevice?, uri: String?): Boolean {
            if (bluetoothDevice != null && uri != null) {
                try {
                    val u = Uri.parse(uri)
                    return sendMessage(ImageMessage(Utils.generateId(), u), bluetoothDevice)
                } catch (e: Exception) {
                    LogUtils.e(this@ClassicBluetoothService, "sendImageToDevice failed -> $uri")
                }
            }
            return false
        }

    }
}