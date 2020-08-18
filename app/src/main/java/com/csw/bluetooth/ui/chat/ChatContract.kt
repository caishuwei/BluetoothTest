package com.csw.bluetooth.ui.chat

import android.bluetooth.BluetoothDevice
import com.csw.bluetooth.entities.MessageItem
import com.csw.bluetooth.service.bluetooth.ConnectState
import com.csw.quickmvp.mvp.base.IBasePresenter
import com.csw.quickmvp.mvp.base.IBaseView

interface ChatContract {

    interface Presenter : IBasePresenter {
        fun setBluetoothDevice(bluetoothDevice: BluetoothDevice)
        fun connectDevice()
        fun sendToDevice(msg: String): Boolean
    }

    interface View : IBaseView {
        fun setTitle(displayName: String?)
        fun setConnectState(stateConnecting: ConnectState)
        fun updateMessageList(messageItemList: List<MessageItem>)
    }

}