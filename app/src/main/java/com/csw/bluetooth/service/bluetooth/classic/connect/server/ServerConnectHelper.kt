package com.csw.bluetooth.service.bluetooth.classic.connect.server

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.base.BaseConnectHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.base.BluetoothSocketHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage
import com.csw.quickmvp.utils.LogUtils
import java.util.*

class ServerConnectHelper(
    classicBluetoothService: ClassicBluetoothService,
    private val bluetoothAdapter: BluetoothAdapter,
    val uuid: UUID,
    val name: String
) : BaseConnectHelper(classicBluetoothService) {

    private var serverSocket: BluetoothServerSocket? = null
    private var bluetoothSocketHelper: BluetoothSocketHelper? = null

    override fun connect() {
        //开个子线程进行等待客户端连接，若serverSocket.close会导致accept过程抛出异常，所以在断开连接后线程也会结束
        Thread {
            try {
                onConnectStart()
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid)
                    .apply {
                        //阻塞至serverSocket与客户端连接上
                        bluetoothSocketHelper =
                            BluetoothSocketHelper(
                                this@ServerConnectHelper,
                                accept()
                            )
                    }
                onConnectSucceed()
            } catch (e: Exception) {
                resetState()
                LogUtils.e(this, "服务($name)等待客户端连接的过程中抛出了异常")
                e.printStackTrace()
            }
        }.start()
    }

    override fun send(message: IMessage) {
        bluetoothSocketHelper?.write(message)
    }

    override fun disconnect() {
        resetState()
        try {
            bluetoothSocketHelper?.close()
            serverSocket?.close()
        } catch (e: Exception) {
            LogUtils.e(this, "服务($name)关闭连接的过程中抛出了异常")
            e.printStackTrace()
        } finally {
            serverSocket = null
            bluetoothSocketHelper = null
        }
    }

    override fun destroy() {
        disconnect()
        super.destroy()
    }

}