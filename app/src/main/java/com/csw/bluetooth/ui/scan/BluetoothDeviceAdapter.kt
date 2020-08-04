package com.csw.bluetooth.ui.scan

import android.bluetooth.BluetoothDevice
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csw.bluetooth.R

class BluetoothDeviceAdapter() :
    BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(R.layout.item_bluetooth_deivce, null) {
    override fun convert(helper: BaseViewHolder?, item: BluetoothDevice?) {
        if (helper == null || item == null) {
            return
        }
        helper.setText(R.id.name, item.name)
        helper.setText(
            R.id.bondStatus, when (item.bondState) {
                BluetoothDevice.BOND_NONE -> {
                    R.string.bondStateNONE
                }
                BluetoothDevice.BOND_BONDING -> {
                    R.string.bondStateBonding
                }
                BluetoothDevice.BOND_BONDED -> {
                    R.string.bondStateBonded
                }
                else->{
                    R.string.unknow
                }
            }
        )
    }
}