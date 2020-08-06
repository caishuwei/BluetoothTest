package com.csw.bluetooth.service.bluetooth.classic.connect.message

import java.io.OutputStream

class TextMessage(text: String) : BaseMessage() {

    private val textByteArray = text.toByteArray()

    init {
        headers[ContentType.name] = ContentType.TYPE_JSON
        headers[ContentLength.name] = textByteArray.size.toString()
    }

    override fun writeBody(outputStream: OutputStream) {
        outputStream.write(textByteArray)
    }

}