package com.csw.bluetooth.service.bluetooth.classic.connect.server

import android.bluetooth.BluetoothAdapter
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.base.Constants
import com.csw.bluetooth.service.bluetooth.classic.connect.base.IConnectHelper

class ServerFactory {

    companion object {

        fun getSPPServerConnect(
            classicBluetoothService: ClassicBluetoothService,
            bluetoothAdapter: BluetoothAdapter
        ): IConnectHelper {
            return ServerConnectHelper(
                classicBluetoothService,
                bluetoothAdapter,
                Constants.SPP_UUID,
                "通用蓝牙串口协议服务端"
            )
        }

    }

}