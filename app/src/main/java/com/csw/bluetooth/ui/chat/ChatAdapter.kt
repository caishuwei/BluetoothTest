package com.csw.bluetooth.ui.chat

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.bluetooth.R
import com.csw.bluetooth.database.table.TextMessageData
import com.csw.bluetooth.entities.MessageItem

class ChatAdapter(data: List<MultiItemEntity>) :
    BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(MessageItem.SEND_TEXT, R.layout.item_send_text)
        addItemType(MessageItem.RECEIVE_TEXT, R.layout.item_receive_text)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        if (helper == null || item == null) {
            return
        }
        if (item is MessageItem) {
            item.getData()?.run {
                if (this is TextMessageData) {
                    when (helper.itemViewType) {
                        MessageItem.SEND_TEXT -> {
                            helper.setText(R.id.text, text)
                        }
                        MessageItem.RECEIVE_TEXT -> {
                            helper.setText(R.id.text, text)
                        }
                    }
                }
            }
        }
    }

}