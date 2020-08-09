package com.csw.bluetooth.service.bluetooth.classic.connect.task

import com.csw.quickmvp.handler.MainHandler

abstract class ConnectTask : ICancelableConnectTask {

    val mainHandler = MainHandler()
    var connectState = ICancelableConnectTask.STATE_NONE
        private set

    override fun connect() {
        if (connectState == ICancelableConnectTask.STATE_NONE) {
            connectState = ICancelableConnectTask.STATE_STARTING
            Thread {
                connectState = ICancelableConnectTask.STATE_EXECUTING
                try {
                    doConnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                connectState = ICancelableConnectTask.STATE_COMPLETED
                mainHandler.post {
                    onTaskEnd()
                }
            }.start()
        }
    }

    override fun cancel() {
        if (connectState != ICancelableConnectTask.STATE_COMPLETED) {
            val preState = connectState
            connectState = ICancelableConnectTask.STATE_COMPLETED
            try {
                doCancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (preState == ICancelableConnectTask.STATE_NONE) {
                //取消前的状态是NONE,还未执行过connect,后面也不会执行了，这里回调一下任务结束
                mainHandler.post {
                    onTaskEnd()
                }
            }
        }
    }

    /**
     * 执行连接
     */
    abstract fun doConnect()

    /**
     * 执行取消
     */
    abstract fun doCancel()

    /**
     * 任务结束
     */
    abstract fun onTaskEnd()

}