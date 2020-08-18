package com.csw.bluetooth.service.bluetooth.classic.connect.message

import com.csw.bluetooth.service.bluetooth.classic.connect.message.header.Header
import java.io.OutputStream

class TextMessage(id: String, val text: String) : BaseMessage(id) {

    init {
        headers[Header.ContentType.name] = Header.ContentType.TYPE_JSON
        headers[Header.ContentLength.name] = text.toByteArray().size.toString()
    }

    override fun writeBody(outputStream: OutputStream) {
        outputStream.write(text.toByteArray())
    }

}