package com.csw.bluetooth.service.bluetooth.classic.connect.server

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.base.BaseConnectHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.base.BluetoothSocketHelper
import com.csw.quickmvp.utils.LogUtils
import java.util.*

class ServerConnectHelper(
    private val classicBluetoothService: ClassicBluetoothService,
    private val bluetoothAdapter: BluetoothAdapter,
    val uuid: UUID,
    val name: String
) : BaseConnectHelper(name) {

    private var serverSocket: BluetoothServerSocket? = null
    private var bluetoothSocketHelper: BluetoothSocketHelper? = null

    override fun execConnect() {
        try {
            //阻塞至serverSocket与客户端连接上
            onConnectStart()
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid)
                .apply {
                    bluetoothSocketHelper =
                        BluetoothSocketHelper(this@ServerConnectHelper, accept())
                }
            onConnectSucceed()
        } catch (e: Exception) {
            resetState()
            LogUtils.e(this, "服务($name)等待客户端连接的过程中抛出了异常")
            e.printStackTrace()
        }
    }

    override fun execDisconnect() {
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            LogUtils.e(this, "服务($name)关闭连接的过程中抛出了异常")
            e.printStackTrace()
        } finally {
            resetState()
            serverSocket = null
            bluetoothSocketHelper = null
        }
    }

}