package com.csw.bluetooth.ui.chat

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.csw.bluetooth.R
import com.csw.bluetooth.database.table.ImageMessageData
import com.csw.bluetooth.database.table.TextMessageData
import com.csw.bluetooth.entities.MessageItem
import com.csw.quickmvp.utils.ImageLoader

class ChatAdapter(data: List<MultiItemEntity>) :
    BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(MessageItem.SEND_TEXT, R.layout.item_send_text)
        addItemType(MessageItem.RECEIVE_TEXT, R.layout.item_receive_text)
        addItemType(MessageItem.SEND_IMAGE, R.layout.item_send_image)
        addItemType(MessageItem.RECEIVE_IMAGE, R.layout.item_receive_image)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        if (helper == null || item == null) {
            return
        }
        if (item is MessageItem) {
            item.getData()?.run {
                when (this) {
                    is TextMessageData -> {
                        when (helper.itemViewType) {
                            MessageItem.SEND_TEXT -> {
                                helper.setText(R.id.text, text)
                            }
                            MessageItem.RECEIVE_TEXT -> {
                                helper.setText(R.id.text, text)
                            }
                            else -> {
                            }
                        }
                    }
                    is ImageMessageData -> {
                        when (helper.itemViewType) {
                            MessageItem.SEND_IMAGE -> {
                                ImageLoader.loadImage(null,helper.getView(R.id.image),imageUrl)
                            }
                            MessageItem.RECEIVE_IMAGE -> {
                                ImageLoader.loadImage(null,helper.getView(R.id.image),imageUrl)
                            }
                            else -> {
                            }
                        }
                    }
                    else -> {

                    }
                }
            }

        }
    }

}