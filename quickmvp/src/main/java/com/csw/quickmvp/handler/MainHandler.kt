package com.csw.quickmvp.handler

import android.os.Handler
import android.os.Looper

/**
 * 主线程消息处理器
 */
class MainHandler(callback: Callback? = null) : Handler(Looper.getMainLooper(), callback) {

}