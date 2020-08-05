package com.csw.bluetooth.entities

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity

class DevicesGroup(val name: String, devices: ArrayList<BluetoothDeviceWrap>) :
    AbstractExpandableItem<BluetoothDeviceWrap>(),
    MultiItemEntity {
    companion object {
        const val ITEM_TYPE = 1
    }

    init {
        subItems = devices
    }

    override fun getLevel(): Int {
        return 1
    }

    override fun getItemType(): Int {
        return ITEM_TYPE
    }
}