package com.csw.bluetooth.service.bluetooth.classic.connect.task

import android.bluetooth.BluetoothDevice

/**
 * 可取消的连接任务
 */
interface ICancelableConnectTask {
    companion object {
        //未连接
        const val STATE_NONE = 0

        //连接开始
        const val STATE_STARTING = 1

        //连接执行中
        const val STATE_EXECUTING = 2

        //连接完成
        const val STATE_COMPLETED = 3
    }

    /**
     * 连接
     */
    fun connect()

    /**
     * 取消
     */
    fun cancel()

    /**
     * 获取目标设备
     */
    fun getDestDevice(): BluetoothDevice?
}