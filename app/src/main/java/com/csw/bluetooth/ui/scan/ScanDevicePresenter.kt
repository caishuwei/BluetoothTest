package com.csw.bluetooth.ui.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.csw.quickmvp.mvp.base.BasePresenterImpl
import javax.inject.Inject


class ScanDevicePresenter @Inject constructor(
    view: ScanDeviceContract.View,
    private val context: Context
) : BasePresenterImpl<ScanDeviceContract.View>(view)
    , ScanDeviceContract.Presenter {
    private val bluetoothDevices = ArrayList<BluetoothDevice>()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (intent == null || action == null) {
                return
            }
            when (action) {
                //蓝牙设备开关
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            //正在开启蓝牙
                        }
                        BluetoothAdapter.STATE_ON -> {
                            //蓝牙已打开
                            if (uiIsResumed) {
                                //当前界面处于前台，开始扫描
                                beginScan()
                            }
                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            //正在关闭蓝牙
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            //蓝牙已关闭
                            view.setScanButtonEnable(true)
                        }
                        else -> {
                        }
                    }
                }
                //开始扫描附近蓝牙设备
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    view.onScanStarted()
                    view.setScanButtonEnable(false)
                }
                //结束扫描
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    view.onScanFinish()
                    view.setScanButtonEnable(true)
                }
                //蓝牙设备绑定状态发生改变
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    view.updateDeviceList(bluetoothDevices)
                }
                //发现新设备
                BluetoothDevice.ACTION_FOUND -> {
                    intent.getParcelableExtra<BluetoothDevice>("EXTRA_DEVICE")?.run {
                        for (bd in bluetoothDevices) {
                            if (bd.address == address) {
                                //已存在设备
                                return
                            }
                        }
                        bluetoothDevices.add(this)
                        view.updateDeviceList(bluetoothDevices)
                    }
                }
            }
        }
    }

    override fun onUICreated() {
        super.onUICreated()
        context.registerReceiver(receiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
        })
    }

    override fun initUIData() {
        super.initUIData()
        if (bluetoothAdapter == null) {
            view.onDeviceNoSupportBluetooth()
            view.setScanButtonEnable(false)
        } else {
            bluetoothDevices.clear()
            bluetoothAdapter?.bondedDevices?.run {
                bluetoothDevices.addAll(this)
            }
            view.updateDeviceList(bluetoothDevices)
        }
    }

    override fun onUIPause() {
        //界面退出前台，取消蓝牙扫描
        bluetoothAdapter?.cancelDiscovery()
        super.onUIPause()
    }

    override fun onUIDestroy() {
        context.unregisterReceiver(receiver)
        super.onUIDestroy()
    }

    //presenter-------------------------------------------------------------------------------------
    override fun beginScan() {
        bluetoothAdapter?.run {
            if (!isDiscovering) {
                //开始扫描，每次扫描大概12秒，可以调用cancelDiscovery()提前停止
                if (!startDiscovery()) {
                    //返回false表示蓝牙未开启
                    if (!isEnabled) {
                        //开启蓝牙
                        enable()
                    }
                }
            }
        }
    }

    override fun connectDevice(bluetoothDevice: BluetoothDevice) {
        //先取消扫描
        bluetoothAdapter?.cancelDiscovery()
        when (bluetoothDevice.bondState) {
            BluetoothDevice.BOND_NONE -> {
                bluetoothDevice.createBond()
            }
            BluetoothDevice.BOND_BONDING -> {
                //等待配对成功，会有广播回调
            }
            BluetoothDevice.BOND_BONDED -> {
                //连接设备
            }
        }

    }

}