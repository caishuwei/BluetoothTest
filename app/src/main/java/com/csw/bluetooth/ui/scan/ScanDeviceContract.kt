@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.bluetooth.ui.scan

import android.bluetooth.BluetoothDevice
import com.csw.quickmvp.mvp.base.IBasePresenter
import com.csw.quickmvp.mvp.base.IBaseView
import java.util.ArrayList

interface ScanDeviceContract {

    interface Presenter : IBasePresenter {
        fun beginScan()
        fun connectDevice(bluetoothDevice: BluetoothDevice)
    }
    interface View : IBaseView {
        fun onDeviceNoSupportBluetooth()
        fun setScanButtonEnable(enable: Boolean)
        fun updateDeviceList(bluetoothDevices: ArrayList<BluetoothDevice>)
        fun onScanStarted()
        fun onScanFinish()
    }

}