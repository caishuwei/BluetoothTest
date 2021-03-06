package com.csw.bluetooth.service.bluetooth.classic.connect.task

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.base.ConnectedDeviceHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.base.Constants
import com.csw.quickmvp.utils.LogUtils
import java.util.*

/**
 * 服务端等待客户端连接的任务
 */
class ServerConnectTask(
    private val classicBluetoothService: ClassicBluetoothService,
    private val bluetoothAdapter: BluetoothAdapter,
    private val uuid: UUID,
    private val name: String
) : ConnectTask() {

    companion object {

        fun getSPPServerConnectTask(
            classicBluetoothService: ClassicBluetoothService,
            bluetoothAdapter: BluetoothAdapter
        ): ServerConnectTask {
            return ServerConnectTask(
                classicBluetoothService,
                bluetoothAdapter,
                Constants.SPP_UUID,
                "通用蓝牙串口协议服务端"
            )
        }

    }

    private var serverSocket: BluetoothServerSocket? = null

    override fun doConnect() {
        try {
            LogUtils.d(this@ServerConnectTask, "doConnect")
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid)
                .apply {
                    //阻塞至serverSocket与客户端连接上
                    LogUtils.d(this@ServerConnectTask, "accept()")
                    val bluetoothSocket = accept()
                    classicBluetoothService.onDeviceConnected(
                        this@ServerConnectTask,
                        ConnectedDeviceHelper(
                            bluetoothAdapter.address,
                            classicBluetoothService,
                            bluetoothSocket
                        )
                    )
                }
        } catch (e: Exception) {
            LogUtils.e(this@ServerConnectTask, "等待客户端连接的过程中抛出了异常")
            e.printStackTrace()
        }
    }

    override fun doCancel() {
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(this@ServerConnectTask, "关闭过程中抛出了异常")
        }
    }

    override fun onTaskEnd() {
        classicBluetoothService.onTaskEnd(this)
    }

    override fun getDestDevice(): BluetoothDevice? {
        return null
    }

}