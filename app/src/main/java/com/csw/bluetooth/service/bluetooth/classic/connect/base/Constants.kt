package com.csw.bluetooth.service.bluetooth.classic.connect.base

import java.util.*

object Constants {
    /**
     * 通用蓝牙串口协议，这个是一个公认的UUID,方便通过蓝牙连接各种设备，若需要保证只能和特定设备连接，可以通过
     * UUID.random方法生成一个随机ID与特定设备共用
     */
    val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

}