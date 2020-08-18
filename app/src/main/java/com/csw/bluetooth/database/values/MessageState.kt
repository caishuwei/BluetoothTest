package com.csw.bluetooth.database.values

/**
 * 消息状态
 */
enum class MessageState {
    /**
     * 初始状态，未发送
     */
    NONE,

    /**
     * 等待发送中
     */
    WAITING,

    /**
     * 发送中
     */
    SENDING,

    /**
     * 发送失败
     */
    SEND_FAIL,

    /**
     * 已发送
     */
    SENT,

    /**
     * 消息已接收
     */
    RECEIVED
}