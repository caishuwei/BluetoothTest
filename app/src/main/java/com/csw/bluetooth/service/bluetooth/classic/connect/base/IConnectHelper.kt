package com.csw.bluetooth.service.bluetooth.classic.connect.base

import android.bluetooth.BluetoothDevice
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage

/**
 * 连接辅助类
 */
interface IConnectHelper {
    companion object {
        //未连接
        const val STATE_NONE = 0

        //连接阻塞中
        const val STATE_CONNECTING = 1

        //连接
        const val STATE_CONNECTED = 2

        //销毁
        const val STATE_DESTROYED = 3
    }

    /**
     * 开始连接
     */
    fun connect()

    /**
     * 断开连接
     */
    fun disconnect()

    /**
     * 销毁
     */
    fun destroy()

    /**
     * 获取连接状态
     */
    fun getState(): Int

    /**
     * 新消息
     */
    fun onNewMessage(device: BluetoothDevice, it: IMessage)

    /**
     * 发出一条消息
     */
    fun send(message: IMessage)
}