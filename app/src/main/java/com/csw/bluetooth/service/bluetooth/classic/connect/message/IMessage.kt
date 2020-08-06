package com.csw.bluetooth.service.bluetooth.classic.connect.message

import java.io.OutputStream

interface IMessage {

    fun write(outputStream: OutputStream)

}