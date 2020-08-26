package com.csw.bluetooth.ui.chat

import android.bluetooth.BluetoothDevice
import com.csw.bluetooth.entities.MessageItem
import com.csw.bluetooth.service.bluetooth.ConnectState
import com.csw.quickmvp.mvp.base.IBasePresenter
import com.csw.quickmvp.mvp.base.IBaseView
import java.util.ArrayList

interface ChatContract {

    interface Presenter : IBasePresenter {
        fun setBluetoothDevice(bluetoothDevice: BluetoothDevice)
        fun connectDevice()
        fun sendTextToDevice(msg: String): Boolean
        fun sendImageToDevice(uri: String): Boolean
        fun getImageMessageItemList(): ArrayList<MessageItem>
    }

    interface View : IBaseView {
        fun setTitle(displayName: String?)
        fun setConnectState(stateConnecting: ConnectState)
        fun updateMessageList(messageItemList: List<MessageItem>)
    }

}