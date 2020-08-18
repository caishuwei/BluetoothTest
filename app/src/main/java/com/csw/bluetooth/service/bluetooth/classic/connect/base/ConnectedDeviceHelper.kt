@file:Suppress("MemberVisibilityCanBePrivate")

package com.csw.bluetooth.service.bluetooth.classic.connect.base

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.csw.bluetooth.database.DBUtils
import com.csw.bluetooth.database.values.MessageState
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.message.MessageFactory
import com.csw.bluetooth.utils.getDisplayName
import com.csw.quickmvp.handler.ThreadWithHandler

/**
 * Socket辅助类，须在有消息处理功能的子线程中初始化
 */
class ConnectedDeviceHelper(
    val classicBluetoothService: ClassicBluetoothService,
    val bluetoothSocket: BluetoothSocket
) {
    //循环读取下一条数据
    val readNextRunnable: Runnable = object : Runnable {
        override fun run() {
            read()
            readThread.handlerProxy.post(this)
        }
    }
    private var readThread =
        ThreadWithHandler("read thread:${bluetoothSocket.remoteDevice.getDisplayName()}").apply {
            start()
        }
    private var writeThread =
        ThreadWithHandler("write thread:${bluetoothSocket.remoteDevice.getDisplayName()}").apply {
            start()
        }

    init {
        //开始循环读取输入流信息
        readThread.handlerProxy.post(readNextRunnable)
    }

    fun read() {
        readThread.handlerProxy.post {
            try {
                bluetoothSocket.inputStream.run {
                    MessageFactory.instanceFromInputStream(this)?.let {
                        DBUtils.insertMessage(it)
                        DBUtils.updateMessageState(it.getMessageId(), MessageState.RECEIVED)
                        classicBluetoothService.onNewMessage(getConnectDevice(), it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                close()
            }
        }
    }

    fun write(message: IMessage): Boolean {
        DBUtils.updateMessageState(
            message.getMessageId(),
            MessageState.WAITING
        )
        writeThread.handlerProxy.post {
            try {
                DBUtils.updateMessageState(
                    message.getMessageId(),
                    MessageState.SENDING
                )
                bluetoothSocket.outputStream.run {
                    message.write(this)
                    flush()
                }
                DBUtils.updateMessageState(
                    message.getMessageId(),
                    MessageState.SENT
                )
            } catch (e: Exception) {
                DBUtils.updateMessageState(
                    message.getMessageId(),
                    MessageState.SEND_FAIL
                )
                e.printStackTrace()
                close()
            }
        }
        return true
    }

    fun close() {
        if (bluetoothSocket.isConnected) {
            try {
                bluetoothSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //关闭线程
        readThread.quit()
        writeThread.quit()
        classicBluetoothService.onDeviceDisconnect(this)
    }

    fun getConnectDevice(): BluetoothDevice {
        return bluetoothSocket.remoteDevice
    }
}