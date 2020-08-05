package com.csw.bluetooth.entities

import android.bluetooth.BluetoothDevice
import com.chad.library.adapter.base.entity.MultiItemEntity

class BluetoothDeviceWrap(val bluetoothDevice: BluetoothDevice) : MultiItemEntity {
    companion object {
        const val ITEM_TYPE = 2
    }

    override fun getItemType(): Int {
        return ITEM_TYPE
    }
}