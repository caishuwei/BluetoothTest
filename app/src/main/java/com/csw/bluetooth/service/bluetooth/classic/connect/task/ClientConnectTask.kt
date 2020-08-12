package com.csw.bluetooth.service.bluetooth.classic.connect.task

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.base.ConnectedDeviceHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.base.Constants
import com.csw.quickmvp.utils.LogUtils
import java.util.*

/**
 * 客户端连接任务
 */
class ClientConnectTask(
    private val classicBluetoothService: ClassicBluetoothService,
    private val bluetoothDevice: BluetoothDevice,
    private val uuid: UUID,
    private val name: String
) : ConnectTask() {

    companion object {

        fun getSPPClientConnectTask(
            classicBluetoothService: ClassicBluetoothService,
            bluetoothDevice: BluetoothDevice
        ): ClientConnectTask {
            return ClientConnectTask(
                classicBluetoothService,
                bluetoothDevice,
                Constants.SPP_UUID,
                "通用蓝牙串口协议客户端"
            )
        }
    }

    private var bluetoothSocket: BluetoothSocket? = null

    override fun doConnect() {
        try {
            //阻塞至socket与服务端连接上
            LogUtils.d(this@ClientConnectTask, "doConnect")
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid).apply {
                LogUtils.d(this@ClientConnectTask, "connect()")
                connect()
                classicBluetoothService.onDeviceConnected(
                    ConnectedDeviceHelper(
                        classicBluetoothService,
                        this
                    )
                )
            }
        } catch (e: Exception) {
            LogUtils.e(this@ClientConnectTask, "与服务端连接的过程中抛出了异常")
            e.printStackTrace()
        }
    }

    override fun doCancel() {
        try {
            bluetoothSocket?.close()
            LogUtils.e(this@ClientConnectTask, "关闭过程中抛出了异常")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTaskEnd() {
        classicBluetoothService.onTaskEnd(this)
    }

    override fun getDestDevice(): BluetoothDevice? {
        return bluetoothDevice
    }
}