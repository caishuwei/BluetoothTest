package com.csw.bluetooth.ui.room

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.csw.bluetooth.IClassicBluetoothInterface
import com.csw.bluetooth.service.bluetooth.ConnectState
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.utils.getDisplayName
import com.csw.quickmvp.mvp.base.BasePresenterImpl
import kotlinx.android.synthetic.main.view_chat_input.*
import javax.inject.Inject

class ChatPresenter @Inject constructor(
    view: ChatContract.View,
    private val context: Context
) :
    BasePresenterImpl<ChatContract.View>(view), ChatContract.Presenter {
    private var bluetoothDevice: BluetoothDevice? = null
    private val conn = ClassicBluetoothServiceConnection()

    override fun onUICreated() {
        super.onUICreated()
        ClassicBluetoothService.startService(context)
        context.bindService(
            Intent(context, ClassicBluetoothService::class.java),
            conn,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun setBluetoothDevice(bluetoothDevice: BluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice
    }

    override fun initUIData() {
        super.initUIData()
        view.setTitle(bluetoothDevice?.getDisplayName())
        syncConnectState()
    }

    private fun syncConnectState() {
        conn.iClassicBluetoothInterface?.run {
            getConnectingDevice()?.run {
                if (this == bluetoothDevice) {
                    view.setConnectState(ConnectState.STATE_CONNECTING)
                    return
                }
            }
            getConnectedDevice()?.run {
                if (this == bluetoothDevice) {
                    view.setConnectState(ConnectState.STATE_ONLINE)
                    return
                }
            }
            view.setConnectState(ConnectState.STATE_OFFLINE)
        }
    }

    override fun connectDevice() {
        bluetoothDevice?.run {
            conn.iClassicBluetoothInterface?.connectBluetoothDevice(this)
        }
    }

    override fun sendToDevice(msg: String): Boolean {
        return conn.iClassicBluetoothInterface?.sendTextToDevice(bluetoothDevice, msg) ?: false
    }

    override fun onUIDestroy() {
        context.unbindService(conn)
        super.onUIDestroy()
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
                syncConnectState()
            }
        }
    }

}