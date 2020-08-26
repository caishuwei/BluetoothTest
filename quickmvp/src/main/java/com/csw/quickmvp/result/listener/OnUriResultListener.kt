package com.csw.quickmvp.result.listener

import android.net.Uri

/**
 * uri请求结果
 */
interface OnUriResultListener : BaseResultListener {
    /**
     * 用户选择后返回一个uri
     */
    fun onComplete(uri: Uri)
}