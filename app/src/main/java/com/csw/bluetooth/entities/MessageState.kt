package com.csw.bluetooth.entities

/**
 * 消息状态
 */
enum class MessageState {
    /**
     * 初始状态，未发送
     */
    NONE,

    /**
     * 发送中
     */
    SENDING,

    /**
     * 已发送
     */
    SENT,

    /**
     * 消息已接收
     */
    RECEIVED
}