package com.csw.bluetooth.service.bluetooth.classic.connect.message

import android.net.Uri
import android.provider.MediaStore
import com.csw.bluetooth.app.MyApplication
import com.csw.bluetooth.service.bluetooth.classic.connect.message.header.Header
import java.io.FileInputStream
import java.io.OutputStream

class ImageMessage(val id: String, val imageContentUri: Uri) : BaseMessage(id) {

    init {
        headers[Header.ContentType.name] = Header.ContentType.TYPE_IMAGE
        MyApplication.instance.contentResolver.query(
            imageContentUri,
            arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE
            ),
            null,
            null,
            null
        )?.use {
            if (it.moveToFirst()) {
                val size = it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))
                val title = it.getString(it.getColumnIndex(MediaStore.MediaColumns.TITLE))
                val filePath = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DATA))
                val mimeType = it.getString(it.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                if (size > 0) {
                    headers[Header.ContentLength.name] = size.toString()
                }
                val name = if (title != null) {
                    title
                } else if (filePath != null) {
                    filePath
                } else {
                    ""
                }
                val lastPointIndex = name.lastIndexOf(".")
                if (lastPointIndex != -1 && lastPointIndex < name.length - 1) {
                    headers[Header.FileFormat.name] =
                        filePath.substring(lastPointIndex + 1, name.length)
                }
                headers[Header.MimeType.name] = mimeType
            }
        }
    }

    override fun writeBody(outputStream: OutputStream) {
        if (headers.containsKey(Header.ContentLength.name)) {
            MyApplication.instance.contentResolver.openFileDescriptor(imageContentUri, "r")
                ?.fileDescriptor?.let {
                    FileInputStream(it).use {
                        val buffer = ByteArray(100 * 1024)
                        var len: Int
                        while ((it.read(buffer).apply { len = this }) != -1) {
                            outputStream.write(buffer, 0, len)
                        }
                    }
                }
        }
    }

}