package com.csw.bluetooth.service.bluetooth.classic.connect.client

import android.bluetooth.BluetoothDevice
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.classic.connect.base.Constants
import com.csw.bluetooth.service.bluetooth.classic.connect.base.IConnectHelper

class ClientFactory {

    companion object {

        fun getSPPClientConnect(
            classicBluetoothService: ClassicBluetoothService,
            bluetoothDevice: BluetoothDevice
        ): IConnectHelper {
            return ClientConnectHelper(
                classicBluetoothService,
                bluetoothDevice,
                Constants.SPP_UUID,
                "通用蓝牙串口协议客户端"
            )
        }

    }
}