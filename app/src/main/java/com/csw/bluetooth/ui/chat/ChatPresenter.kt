package com.csw.bluetooth.ui.chat

import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.IBinder
import com.csw.bluetooth.IClassicBluetoothInterface
import com.csw.bluetooth.database.DBUtils
import com.csw.bluetooth.database.values.MessageState
import com.csw.bluetooth.entities.MessageItem
import com.csw.bluetooth.event.OnMessageStateChanged
import com.csw.bluetooth.service.bluetooth.ConnectState
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.utils.getDisplayName
import com.csw.quickmvp.mvp.base.BasePresenterImpl
import com.csw.quickmvp.utils.RxBus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChatPresenter @Inject constructor(
    view: ChatContract.View,
    private val context: Context
) :
    BasePresenterImpl<ChatContract.View>(view), ChatContract.Presenter {

    private val messageItemList = ArrayList<MessageItem>()
    private var bluetoothDevice: BluetoothDevice? = null
    private val conn = ClassicBluetoothServiceConnection()
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action.run {
                when (this) {
                    ClassicBluetoothService.ACTION_DEVICE_CONNECT_STATE_CHANGED -> {
                        intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                            ?.run {
                                if (this == bluetoothDevice) {
                                    intent?.getStringExtra(ClassicBluetoothService.EXTRA_DEVICE_CONNECT_STATE)
                                        ?.run {
                                            view.setConnectState(ConnectState.valueOf(this))
                                        }
                                }
                            }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onUICreated() {
        super.onUICreated()
        context.registerReceiver(receiver, IntentFilter().apply {
            addAction(ClassicBluetoothService.ACTION_DEVICE_CONNECT_STATE_CHANGED)
        })
        context.bindService(
            Intent(context, ClassicBluetoothService::class.java),
            conn,
            Context.BIND_AUTO_CREATE
        )
        RxBus.getDefault().toObservable(OnMessageStateChanged::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                bluetoothDevice?.address.let { client ->
                    it.message.run {
                        if (from == client || to == client) {
                            //来自聊天对象消息，发送给聊天对象的消息
                            when (getMessageState()) {
                                MessageState.CREATED -> {
                                    //新增讯息
                                    messageItemList.add(MessageItem(this, this.to == client))
                                    view.updateMessageList(messageItemList)
                                }
                                else -> {
                                    //倒叙遍历，因为会更新状态的基本都是最新的消息，这样可以减少循环次数
                                    for (i in (messageItemList.size - 1) downTo 0) {
                                        if (messageItemList[i].message.messageId == messageId) {
                                            messageItemList[i].message = this
                                            break
                                        }
                                    }
                                    //信息发送状态更新
                                    view.updateMessageList(messageItemList)
                                }
                            }
                        }
                    }
                }
            }
            .run {
                addRxJavaTaskRunOnUILive(this)
            }
    }

    override fun setBluetoothDevice(bluetoothDevice: BluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice
    }

    override fun initUIData() {
        super.initUIData()
        view.setTitle(bluetoothDevice?.getDisplayName())
        syncConnectState()
        requestMessageList()
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

    private fun requestMessageList() {
        val deviceAddress = bluetoothDevice?.address
        if (deviceAddress == null) {
            return
        }
        Observable.create<List<MessageItem>> {
            val messages = DBUtils.getDeviceMessageList(
                deviceAddress,
                hasReceive = true,
                hasSend = true,
                orderDescByTime = true,
                limit = 100
            )
            val size = messages.size
            val result = ArrayList<MessageItem>(size)
            if (it.isDisposed) {
                return@create
            }
            for (i in (size - 1) downTo 0) {
                messages[i].run {
                    result.add(MessageItem(this, this.to == deviceAddress))
                }
            }
            if (it.isDisposed) {
                return@create
            }
            it.onNext(result)
            if (it.isDisposed) {
                return@create
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    messageItemList.clear()
                    messageItemList.addAll(it)
                    view.updateMessageList(messageItemList)
                },
                {

                }
            ).run {
                addRxJavaTaskRunOnUILive(this)
            }
    }

    override fun connectDevice() {
        bluetoothDevice?.run {
            conn.iClassicBluetoothInterface?.connectBluetoothDevice(this)
        }
    }

    override fun sendTextToDevice(msg: String): Boolean {
        return conn.iClassicBluetoothInterface?.sendTextToDevice(bluetoothDevice, msg) ?: false
    }

    override fun sendImageToDevice(uri: String): Boolean {
        return conn.iClassicBluetoothInterface?.sendImageToDevice(bluetoothDevice, uri) ?: false
    }

    override fun getImageMessageItemList(): java.util.ArrayList<MessageItem> {
        val result = ArrayList<MessageItem>()
        for (item in messageItemList) {
            if (item.itemType == MessageItem.RECEIVE_IMAGE
                || item.itemType == MessageItem.SEND_IMAGE
            ) {
                result.add(item)
            }
        }
        return result
    }

    override fun onUIDestroy() {
        context.unregisterReceiver(receiver)
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