@file:Suppress("MemberVisibilityCanBePrivate")

package com.csw.bluetooth.service.bluetooth.classic.connect.base

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage
import com.csw.bluetooth.service.bluetooth.classic.connect.message.MessageFactory

/**
 * Socket辅助类，须在有消息处理功能的子线程中初始化
 */
class BluetoothSocketHelper(
    val connectHelper: IConnectHelper,
    val bluetoothSocket: BluetoothSocket
) {
    //循环读取下一条数据
    val readNextRunnable: Runnable = object : Runnable {
        override fun run() {
            read()
            threadHandler.post(this)
        }
    }

    //post启动第一次数据读取
    val threadHandler = Handler().apply {
        post(readNextRunnable)
    }

    fun read() {
        try {
            bluetoothSocket.inputStream.run {
                MessageFactory.instanceFromInputStream(this)?.let {
                    
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            close()
        }
    }

    fun write(message: IMessage) {
        try {
            bluetoothSocket.outputStream.run {
                message.write(this)
                flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            close()
        }
    }

    fun close() {
        connectHelper.disconnect()
        try {
            bluetoothSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getConnectDevice(): BluetoothDevice {
        return bluetoothSocket.remoteDevice
    }
}