package com.csw.bluetooth.ui.chat

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.bluetooth.R
import com.csw.bluetooth.app.MyApplication
import com.csw.bluetooth.entities.MessageItem
import com.csw.bluetooth.service.bluetooth.ConnectState
import com.csw.bluetooth.ui.fullscreen.ImageBrowseActivity
import com.csw.bluetooth.view.ChatInputView
import com.csw.quickmvp.mvp.ui.BaseMVPActivity
import com.csw.quickmvp.utils.SpaceLineDecoration
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.view_chat_input.*

class ChatActivity : BaseMVPActivity<ChatContract.Presenter>(), ChatContract.View {

    companion object {
        fun openActivity(context: Context, bluetoothDevice: BluetoothDevice) {
            val intent = Intent(context, ChatActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra("BluetoothDevice", bluetoothDevice)
            context.startActivity(intent)
        }
    }

    private val messageList = ArrayList<MultiItemEntity>()
    private var chatAdapter: ChatAdapter? = null

    override fun initInject() {
        super.initInject()
        MyApplication.instance.appComponent.getChatComponentBuilder().setView(this)
            .build()
            .inject(this)
    }

    override fun getContentViewID(): Int {
        return R.layout.activity_chat
    }

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
        super.initView(rootView, savedInstanceState)
        recyclerView?.run {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                SpaceLineDecoration.getInstanceByDp(
                    10,
                    10,
                    10,
                    10,
                    Color.TRANSPARENT
                )
            )
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        chatAdapter = ChatAdapter(messageList)?.apply {
            recyclerView?.adapter = this
        }
    }

    override fun initListener() {
        super.initListener()
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        //自定义MenuLayout,自己添加监听
        toolbar?.menu?.run {
            findItem(R.id.menu_connect_state)?.actionView?.run {
                visibility = View.GONE
                setOnClickListener {
                    presenter.connectDevice()
                }
            }
        }
        send?.setOnClickListener {
            msg?.text?.toString()?.trim()?.run {
                if (presenter.sendTextToDevice(this)) {
                    msg?.text = null
                }
            }
        }

        chatInputView?.onRequestSendMessageListener =
            object : ChatInputView.OnRequestSendMessageListener {
                override fun sendImage(uri: String) {
                    presenter.sendImageToDevice(uri)
                }
            }

        chatAdapter?.setOnItemClickListener { _, _, position ->
            chatAdapter?.getItem(position)?.run {
                if (this is MessageItem) {
                    val destId = message.messageId
                    when(itemType){
                        MessageItem.RECEIVE_IMAGE,MessageItem.SEND_IMAGE->{
                            ImageBrowseActivity.openActivity(this@ChatActivity,presenter.getImageMessageItemList(),destId)
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        val bluetoothDevice = intent.getParcelableExtra<BluetoothDevice>("BluetoothDevice")
        if (bluetoothDevice == null) {
            finish()
            return
        }
        presenter.setBluetoothDevice(bluetoothDevice)
        presenter.initUIData()
    }

    //View------------------------------------------------------------------------------------------

    override fun setTitle(displayName: String?) {
        toolbar?.title = displayName

    }

    override fun setConnectState(stateConnecting: ConnectState) {
        toolbar?.menu?.findItem(R.id.menu_connect_state)?.let { menu ->
            menu.actionView?.run {
                visibility = View.VISIBLE
                if (this is ImageView) {
                    when (stateConnecting) {
                        ConnectState.STATE_OFFLINE -> {
                            setImageResource(R.drawable.svg_ic_offline)
                            menu.isEnabled = true
                        }
                        ConnectState.STATE_CONNECTING -> {
                            setImageResource(R.drawable.svg_ic_online)
                            menu.isEnabled = false
                        }
                        ConnectState.STATE_ONLINE -> {
                            setImageResource(R.drawable.svg_ic_online)
                            menu.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    override fun updateMessageList(messageItemList: List<MessageItem>) {
        var scrollToEnd = false
        recyclerView?.layoutManager?.run {
            if (this is LinearLayoutManager) {
                val lastCompletelyVisibleItem = findLastCompletelyVisibleItemPosition()
                if (itemCount == 0) {
                    //没有item
                    scrollToEnd = true
                } else if (lastCompletelyVisibleItem == itemCount - 1) {
                    //最后一个Item完全可见
                    scrollToEnd = true
                }
            }
        }
        messageList.clear()
        messageList.addAll(messageItemList)
        chatAdapter?.notifyDataSetChanged()
        if (scrollToEnd) {
            recyclerView?.scrollToEnd()
        }
    }
}