package com.csw.quickmvp.log

import java.text.SimpleDateFormat
import java.util.*

class LogInfo(val level: Int, val time: Long, val tag: String, val msg: String) {
    companion object {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
    }

    private val timeStr: String

    init {
        timeStr = sdf.format(Date(time))
    }

    fun getTime(): String {
        return timeStr
    }

    fun getContent(): String {
        return "${tag}: ${msg}"
    }
}