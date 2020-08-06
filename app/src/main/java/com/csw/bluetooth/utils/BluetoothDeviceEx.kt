package com.csw.bluetooth.utils

import android.bluetooth.BluetoothDevice

fun BluetoothDevice.getDisplayName(): String {
    return "$name($address,${getTypeName(type)})"
}

fun getTypeName(type: Int): String {
    return when (type) {
        BluetoothDevice.DEVICE_TYPE_CLASSIC -> {
            "经典蓝牙"
        }
        BluetoothDevice.DEVICE_TYPE_LE -> {
            "低功耗蓝牙"
        }
        BluetoothDevice.DEVICE_TYPE_DUAL -> {
            "兼容"
        }
        else -> {
            "Unknow"
        }
    }
}
