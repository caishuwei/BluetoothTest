package com.csw.bluetooth.ui.scan

import android.bluetooth.BluetoothDevice
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.bluetooth.R
import com.csw.bluetooth.entities.BluetoothDeviceWrap
import com.csw.bluetooth.entities.DevicesGroup
import com.csw.bluetooth.utils.getDisplayName

class BluetoothDeviceAdapter :
    BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(null) {
    init {
        addItemType(DevicesGroup.ITEM_TYPE, R.layout.item_bluetooth_deivce_group)
        addItemType(BluetoothDeviceWrap.ITEM_TYPE, R.layout.item_bluetooth_deivce)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        if (helper == null || item == null) {
            return
        }
        when (helper.itemViewType) {
            DevicesGroup.ITEM_TYPE -> {
                if (item is DevicesGroup) {
                    helper.setText(R.id.name, item.name)
                }
            }
            BluetoothDeviceWrap.ITEM_TYPE -> {
                if (item is BluetoothDeviceWrap) {
                    item.bluetoothDevice.run {
                        helper.setText(R.id.name, getDisplayName())
                        helper.setText(
                            R.id.bondStatus, when (bondState) {
                                BluetoothDevice.BOND_NONE -> {
                                    R.string.bond_state_none
                                }
                                BluetoothDevice.BOND_BONDING -> {
                                    R.string.bond_state_bonding
                                }
                                BluetoothDevice.BOND_BONDED -> {
                                    R.string.bond_state_bonded
                                }
                                else -> {
                                    R.string.unknow
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    fun collapseAll() {
        for ((i, item) in data.withIndex()) {
            if (item is DevicesGroup && item.isExpanded) {
                collapse(i + headerLayoutCount, true, true)
                collapseAll()
                return
            }
        }
    }
}