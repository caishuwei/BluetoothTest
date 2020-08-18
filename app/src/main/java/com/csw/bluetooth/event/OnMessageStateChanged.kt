package com.csw.bluetooth.event

import com.csw.bluetooth.database.table.Message

/**
 * 消息状态发生改变
 */
class OnMessageStateChanged(val message: Message)