package com.csw.bluetooth.service.bluetooth.classic.connect.base

import android.bluetooth.BluetoothDevice
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.message.IMessage
import com.csw.quickmvp.handler.MainHandler

/**
 * 连接辅助类
 */
abstract class BaseConnectHelper(val classicBluetoothService: ClassicBluetoothService) :
    IConnectHelper {

    //主线程消息处理器
    val mainHandler = MainHandler()
    var connectState = IConnectHelper.STATE_NONE
        private set

    /**
     * 重设当前状态
     */
    protected fun resetState() {
        connectState = IConnectHelper.STATE_NONE
    }

    /**
     * 开始连接
     */
    protected fun onConnectStart() {
        connectState = IConnectHelper.STATE_CONNECTING
    }

    /**
     * 连接成功
     */
    protected fun onConnectSucceed() {
        connectState = IConnectHelper.STATE_CONNECTED
    }

    //IConnectHelper--------------------------------------------------------------------------------
    override fun connect() {
    }

    override fun disconnect() {
    }

    override fun destroy() {
        connectState = IConnectHelper.STATE_DESTROYED
    }

    override fun getState(): Int {
        return connectState
    }

    override fun onNewMessage(device: BluetoothDevice, it: IMessage) {
        classicBluetoothService.onNewMessage(device, it)
    }
}