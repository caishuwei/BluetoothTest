package com.csw.bluetooth.service.bluetooth.classic.connect.client

import android.bluetooth.BluetoothDevice
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.base.BaseConnectHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.base.BluetoothSocketHelper
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.message.TextMessage
import com.csw.quickmvp.utils.LogUtils
import java.util.*

class ClientConnectHelper(
    classicBluetoothService: ClassicBluetoothService,
    val bluetoothDevice: BluetoothDevice,
    val uuid: UUID,
    val name: String
) : BaseConnectHelper(classicBluetoothService) {

    private var bluetoothSocketHelper: BluetoothSocketHelper? = null

    override fun connect() {
        //开个子线程进行等待客户端连接，若serverSocket.close会导致accept过程抛出异常，所以在断开连接后线程也会结束
        Thread {
            try {
                onConnectStart()
                //阻塞至serverSocket与客户端连接上
                bluetoothSocketHelper =
                    BluetoothSocketHelper(
                        this@ClientConnectHelper,
                        bluetoothDevice.createRfcommSocketToServiceRecord(uuid).apply {
                            connect()
                        }
                    ).apply {
                        write(TextMessage("hi!"))
                    }
                onConnectSucceed()
            } catch (e: Exception) {
                resetState()
                LogUtils.e(this, "服务($name)与服务端连接的过程中抛出了异常")
                e.printStackTrace()
            }
        }.start()
    }

    override fun send(message: IMessage) {
        bluetoothSocketHelper?.write(message)
    }

    override fun disconnect() {
        resetState()
        bluetoothSocketHelper?.close()
    }

    override fun destroy() {
        disconnect()
        super.destroy()
    }

}