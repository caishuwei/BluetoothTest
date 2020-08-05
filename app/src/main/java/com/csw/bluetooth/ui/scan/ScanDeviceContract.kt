@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.bluetooth.ui.scan

import android.bluetooth.BluetoothDevice
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.quickmvp.mvp.base.IBasePresenter
import com.csw.quickmvp.mvp.base.IBaseView
import java.util.ArrayList

interface ScanDeviceContract {

    interface Presenter : IBasePresenter {
        fun startDiscovery()
        fun cancelDiscovery()
        fun connectDevice(bluetoothDevice: BluetoothDevice)
    }
    interface View : IBaseView {
        fun onDeviceNoSupportBluetooth()
        fun setScanButtonEnable(enable: Boolean)
        fun updateDeviceList(list: ArrayList<MultiItemEntity>)
        fun onScanStarted()
        fun onScanFinish()
    }

}