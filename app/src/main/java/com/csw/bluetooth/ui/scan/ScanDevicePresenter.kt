package com.csw.bluetooth.ui.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.IBinder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.bluetooth.IClassicBluetoothInterface
import com.csw.bluetooth.R
import com.csw.bluetooth.entities.BluetoothDeviceWrap
import com.csw.bluetooth.entities.DevicesGroup
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.quickmvp.mvp.base.BasePresenterImpl
import javax.inject.Inject


class ScanDevicePresenter @Inject constructor(
    view: ScanDeviceContract.View,
    private val context: Context
) : BasePresenterImpl<ScanDeviceContract.View>(view)
    , ScanDeviceContract.Presenter {
    private val bluetoothDevices = ArrayList<BluetoothDevice>()
    private val boundDevices =
        DevicesGroup(context.getString(R.string.bonded_device), ArrayList()).apply {
            isExpanded = true
        }
    private val otherDevices =
        DevicesGroup(context.getString(R.string.enable_device), ArrayList()).apply {
            isExpanded = true
        }
    private val uiList = ArrayList<MultiItemEntity>().apply {
        add(boundDevices)
        add(otherDevices)
    }
    private val conn = ClassicBluetoothServiceConnection()
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (intent == null || action == null) {
                return
            }
            when (action) {
                //开始扫描附近蓝牙设备
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    view.onScanStarted()
                }
                //结束扫描
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    view.onScanFinish()
                }
                //列表发生变化
                ClassicBluetoothService.ACTION_DEVICES_CHANGED -> {
                    conn.iClassicBluetoothInterface?.bluetoothDevices?.let {
                        bluetoothDevices.clear()
                        bluetoothDevices.addAll(it)
                        updateViewList()
                    }
                }
                //蓝牙设备状态发生改变
                ClassicBluetoothService.ACTION_DEVICE_STATE_CHANGED -> {
                    updateViewList()
                }
            }
        }
    }

    override fun onUICreated() {
        super.onUICreated()
        context.bindService(
            Intent(context, ClassicBluetoothService::class.java),
            conn,
            Context.BIND_AUTO_CREATE
        )
        context.registerReceiver(receiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(ClassicBluetoothService.ACTION_DEVICES_CHANGED)
            addAction(ClassicBluetoothService.ACTION_DEVICE_STATE_CHANGED)
        })
    }

    override fun onUIPause() {
        //界面退出前台，取消蓝牙扫描
        cancelDiscovery()
        super.onUIPause()
    }

    override fun onUIDestroy() {
        context.unbindService(conn)
        context.unregisterReceiver(receiver)
        super.onUIDestroy()
    }

    private fun updateViewList() {
        boundDevices.subItems.clear()
        otherDevices.subItems.clear()
        for (bd in bluetoothDevices) {
            if (bd.bondState == BluetoothDevice.BOND_BONDED) {
                boundDevices.subItems.add(BluetoothDeviceWrap(bd))
            } else {
                otherDevices.subItems.add(BluetoothDeviceWrap(bd))
            }
        }
        uiList.clear()
        uiList.add(boundDevices)
        if (boundDevices.isExpanded) {
            uiList.addAll(boundDevices.subItems)
        }
        uiList.add(otherDevices)
        if (otherDevices.isExpanded) {
            uiList.addAll(otherDevices.subItems)
        }
        view.updateDeviceList(uiList)
    }

    //presenter-------------------------------------------------------------------------------------
    override fun startDiscovery() {
        ClassicBluetoothService.beginDiscovery(context)
    }

    override fun cancelDiscovery() {
        ClassicBluetoothService.cancelDiscovery(context)
    }

    override fun connectDevice(bluetoothDevice: BluetoothDevice) {
        conn.iClassicBluetoothInterface?.connectBluetoothDevice(bluetoothDevice)
    }

    //inner class-----------------------------------------------------------------------------------
    private inner class ClassicBluetoothServiceConnection : ServiceConnection {
        var iClassicBluetoothInterface: IClassicBluetoothInterface? = null
        override fun onServiceDisconnected(name: ComponentName?) {
            iClassicBluetoothInterface = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iClassicBluetoothInterface = IClassicBluetoothInterface.Stub.asInterface(service)
            if (uiIsCreated) {
                iClassicBluetoothInterface?.bluetoothDevices?.let {
                    bluetoothDevices.clear()
                    bluetoothDevices.addAll(it)
                    updateViewList()
                }
            }
        }
    }

}