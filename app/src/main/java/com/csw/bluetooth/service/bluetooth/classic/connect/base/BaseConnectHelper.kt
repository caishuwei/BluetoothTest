package com.csw.bluetooth.service.bluetooth.classic.connect.base

import com.csw.quickmvp.handler.MainHandler
import com.csw.quickmvp.handler.ThreadWithHandler

/**
 * 连接辅助类
 */
abstract class BaseConnectHelper(name: String) : IConnectHelper {
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

    //主线程消息处理器
    val mainHandler = MainHandler()

    //子线程消息处理器
    val threadWithHandler = ThreadWithHandler(name).apply {
        start()
    }

    var state = STATE_NONE
        private set


    /**
     * 执行连接或者等待连接的代码，本方法在子线程执行
     */
    abstract fun execConnect()

    /**
     * 执行连接或者等待连接的代码，本方法在主线程执行，因为此时子线程可能已经阻塞了
     */
    abstract fun execDisconnect()

    /**
     * 重设当前状态
     */
    protected fun resetState() {
        state = STATE_NONE
    }
    /**
     * 开始连接
     */
    protected fun onConnectStart() {
        state = STATE_CONNECTING
    }

    /**
     * 连接成功
     */
    protected fun onConnectSucceed() {
        state = STATE_CONNECTED
    }

    //IConnectHelper--------------------------------------------------------------------------------
    override fun connect() {
        threadWithHandler.handlerProxy.post {
            execConnect()
        }
    }

    override fun disconnect() {
        mainHandler.post { execDisconnect() }
    }

    override fun destroy() {
        state = STATE_DESTROYED
        threadWithHandler.quit()
    }

}